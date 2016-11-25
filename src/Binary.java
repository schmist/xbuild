import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Stefan on 11/25/2016.
 */
public class Binary {
    public enum BinaryType {CC_EXEC, CC_LIB}
    private List<String> srcs;
    private String name;
    private BinaryType type;
    private List<Path> fileList;
    private Compiler compiler;

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

    public void compile() {
        this.findFiles();
        this.fileList.forEach(this.compiler::compile);
    }
}
