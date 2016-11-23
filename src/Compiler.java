import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 23.11.2016.
 */
public class Compiler {

    private List<String> flags;
    private String compiler;

    public Compiler(String compiler) {
        this.compiler = compiler;
        this.flags = new ArrayList<>();
    }

    public void compile(String source) {
        System.out.println(source);
        System.out.println(this.flags);
    }

    public List<String> getFlags() {
        return flags;
    }
}
