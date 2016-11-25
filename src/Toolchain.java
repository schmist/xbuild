import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Stefan on 11/25/2016.
 */
public class Toolchain {
    private String prefix;
    private List<String> cpus = new ArrayList<>();
    private List<String> cFlags = new ArrayList<>();
    private List<String> cxxFlags = new ArrayList<>();

    private Toolchain(String cpu) {
        this.cpus.add(cpu);
    }

    public String getPrefix() {
        return prefix;
    }

    public List<String> getcFlags() {
        return cFlags;
    }

    public List<String> getCxxFlags() {
        return cxxFlags;
    }

    public static Toolchain createDefault() {
        return new Toolchain("x64");
    }

    public static Toolchain getToolchain(Toolchain[] toolchains, String cpu) {
        return Arrays.stream(toolchains).filter(toolchain -> toolchain.cpus.contains(cpu)).findFirst().orElse(Toolchain.createDefault());
    }
}


