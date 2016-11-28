import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 11/28/2016.
 */
public class Flags {
    private String name;
    private List<String> cflags;
    private List<String> conlyflags;
    private List<String> cxxflags;
    private List<String> lflags;

    public Flags() {
        this.cflags = new ArrayList<>();
        this.conlyflags = new ArrayList<>();
        this.cxxflags = new ArrayList<>();
        this.lflags = new ArrayList<>();
    }

    public List<String> getCFlags() {
        return cflags;
    }

    public List<String> getConlyFlags() {
        return conlyflags;
    }

    public List<String> getCxxFlags() {
        return cxxflags;
    }

    public List<String> getLFlags() {
        return lflags;
    }

    public String getName() {
        return name;
    }

    public void addCFlag(String flag) {
        this.cflags.add(flag);
    }
}
