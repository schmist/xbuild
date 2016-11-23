import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 23.11.2016.
 */
public abstract class Binary {

    private List<String> sources;
    private String root;
    private CompilerSuite compilerSuite;

    public Binary(CompilerSuite compilerSuite) {
        this.sources = new ArrayList<>();
        this.compilerSuite = compilerSuite;
    }

    public void addSource(String source) {
        this.sources.add(source);
    }

    public void addSources(List<String> sources) {
        this.sources.addAll(sources);
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public void compile() {
        for (String source : this.sources) {
            compilerSuite.compile(source);
        }
    }

    public abstract void link();

}
