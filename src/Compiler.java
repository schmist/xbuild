import java.nio.file.Path;

/**
 * Created by Stefan on 11/25/2016.
 */
public abstract class Compiler {

    public enum CompilerType {GCC}

    public abstract void compile(Path src, Flags flags, Path output);

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
