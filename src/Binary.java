import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

/**
 * Created by Stefan on 11/25/2016.
 */
public class Binary {
    public enum BinaryType {CC_EXEC, CC_LIB}
    private List<String> srcs;
    private List<String> incl;
    private String name;
    private BinaryType type;
    private List<Path> fileList;
    private Compiler compiler;
    private String flags;
    private Flags flagsContainer;
    private Path output = Paths.get("xbuild");

    public Binary() {
        fileList = new ArrayList<>();
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
            this.flagsContainer.addCFlag("-I"+include.toString());
        });
    }

    public void compile(Executor executor) {
        this.findFiles();
        System.out.println(this.fileList);
        if (!Files.isDirectory(output)) {
            try {
                Files.createDirectory(output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.fileList.forEach((src)->executor.execute(()->this.compiler.compile(src,this.flagsContainer,output)));
    }
}
