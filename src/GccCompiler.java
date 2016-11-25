import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;

/**
 * Created by Stefan on 11/25/2016.
 */
public class GccCompiler extends Compiler {

    private String prefix;
    private List<String> cFlags;
    private List<String> cxxFlags;

    public GccCompiler(Toolchain toolchain) {
        this.prefix = toolchain.getPrefix();
        this.cFlags = toolchain.getcFlags();
        this.cxxFlags = toolchain.getCxxFlags();
    }

    @Override
    public void compile(Path src) {
        PathMatcher cFileMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.c");
        PathMatcher cppFileMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.cpp");
        if (cFileMatcher.matches(src)) {
            System.out.println(prefix+"gcc -c "+src.toString()+" -o "+src.toString().replace(".c",".o"));
        } else if (cppFileMatcher.matches(src)) {
            System.out.println(prefix+"g++ -c "+src.toString()+" -o");
        }
    }
}
