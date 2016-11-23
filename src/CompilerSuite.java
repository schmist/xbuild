import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stefan on 23.11.2016.
 */
public class CompilerSuite {

    Map<String,Compiler> compilerMap;

    public CompilerSuite() {
        this.compilerMap = new HashMap<>();
    }

    public Compiler addCompiler(String extension, String compilerString) {
        Compiler compiler = new Compiler(compilerString);
        this.compilerMap.put(extension, compiler);
        return compiler;
    }

    public void compile(String source) {
        String extension = source.split("\\.(?=[^\\.]+$)")[1];
        Compiler compiler = this.compilerMap.get(extension);
        if (compiler!=null) {
            compiler.compile(source);
        } else {
            System.out.println("No compiler found for this file type!");
        }
    }

}
