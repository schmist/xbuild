import java.nio.file.Path;
import java.util.List;

/**
 * Created by Stefan on 11/25/2016.
 */
public abstract class Compiler {

    public enum CompilerType {GCC}

    public abstract Path compile(Path src, Flags flags, Path output);

    public abstract void archive(List<Path> objs, Path output);

    public abstract void link(List<Path> objs, List<Dependency> deps, List<Path> lfls, Flags flags, Path output);

    public abstract void output(Binary.OutputType type, Path output);

    public static Compiler createCompiler(CompilerType type) {
        return createCompiler(null,type);
    }

    public static Compiler createCompiler(Toolchain toolchain, CompilerType type) {
        switch(type) {
            case GCC:
                if (toolchain==null) {
                    //TODO log error
                    return null;
                } else {
                    return new GccCompiler(toolchain);
                }
            default:
                //should not happen
                return null;
        }
    }

}
