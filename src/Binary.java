import com.google.gson.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

/**
 * Created by Stefan on 11/25/2016.
 */
public class Binary {
    public enum BinaryType {CC_EXEC, CC_LIB}
    public enum OutputType {ELF, HEX, BIN}
    private List<String> srcs = new ArrayList<>();
    private List<String> incl = new ArrayList<>();
    private List<Dependency> deps = new ArrayList<>();
    private List<String> lfls = new ArrayList<>();
    private String name;
    private BinaryType type;
    private OutputType out = OutputType.ELF;
    private List<Path> fileList;
    private List<Path> objList;
    private Compiler compiler;
    private String flags = "";
    private Flags flagsContainer;
    private Path output;

    public Binary() {
        fileList = new ArrayList<>();
        objList = new ArrayList<>();
    }

    public List<String> getSrcs() {
        return srcs;
    }

    public String getName() {
        return name;
    }

    public List<Path> getFileList() {
        return fileList;
    }

    public void assignCompiler(Toolchain[] toolchains, String cpu) {
        switch(this.type) {
            case CC_LIB:
            case CC_EXEC:
                this.compiler = Compiler.createCompiler(Toolchain.getToolchain(toolchains, cpu),Compiler.CompilerType.GCC);
        }
    }

    public void assignFlags(Flags[] flags) {
        this.flagsContainer = Arrays.stream(flags).filter(item->item.getName().equals(this.flags)).findFirst().orElse(null);
        if (!this.flags.isEmpty() && this.flagsContainer==null) {
            System.out.println("Specified flags could not be found.");
            System.exit(4);
        }
        this.prepareIncludes();
    }

    private void findFiles() {
        for(String src: this.srcs) {
            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:./"+src);
            try (Stream<Path> stream = Files.walk(Paths.get("."))) {
                stream.filter(pathMatcher::matches).forEach(_src->this.fileList.add(_src.normalize()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareIncludes() {
        if (this.flagsContainer==null) {
            this.flagsContainer = new Flags();
        }
        this.incl.forEach(item->{
            Path include = Paths.get(item);
            if (!include.isAbsolute()) {
                include = Main.workingDir.resolve(include);
            }
            this.flagsContainer.addCFlag("-I"+include.toString());
        });
    }

    public void compile(ExecutorService executor) {
        System.out.println("Building ["+this.name+"]:");
        this.findFiles();
        if (!Files.isDirectory(output)) {
            try {
                Files.createDirectories(output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.fileList.forEach((src)->executor.execute(()->this.objList.add(this.compiler.compile(src,this.flagsContainer,output))));
        executor.shutdown();
        while (!executor.isTerminated());
        switch(this.type) {
            case CC_EXEC:
                List<Path> linkerFiles = new ArrayList<>();
                this.lfls.forEach(lfl->linkerFiles.add(Paths.get(lfl)));
                this.compiler.link(this.objList,this.deps,linkerFiles,this.flagsContainer,this.output.resolve(this.name));
                this.compiler.output(this.out,this.output.resolve(this.name));
                break;
            case CC_LIB:
                this.compiler.archive(this.objList,output.resolve("lib"+this.name+".a"));
                break;
        }
    }

    public void resolveDependencies(Binary[] binaries) {
        this.deps.forEach(dep -> {
            String depName = dep.getName();
            Arrays.stream(binaries).forEach(binary -> {
                if (depName.equals(binary.getName())) {
                    String path = binary.getOutput().toString();
                    dep.setPath(path);
                    System.out.println("Dependency ["+depName+"->"+path+"] resolved.");
                }
            });
            if (dep.getPath().isEmpty()) {
                System.out.println("Missing dependency: " + depName+".");
            }
        });
    }

    public void applyOutputPath() {
        this.output = Paths.get("xbuild/"+this.name);
    }

    public Path getOutput() {
        return output;
    }

    public List<Dependency> getDeps() {
        return deps;
    }
}

