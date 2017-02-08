import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Stefan on 23.11.2016.
 */
public class Main {

    static Path workingDir;

    public static void main(String[] args) throws FileNotFoundException {
        String cpu = "";
        switch(args.length) {
            case 1:
                String split[] = args[0].split("=");
                if (split.length==2) {
                    cpu = split[1];
                }
                break;
        }
        workingDir = Paths.get("");
        Path xbuildPath = workingDir.resolve("x.build");
        if (!Files.exists(xbuildPath)) {
            System.out.println("No x.build file found!");
            System.exit(2);
        }
        Path toolchainsPath = workingDir.resolve("x.toolchains");
        if (!Files.exists(toolchainsPath)) {
            System.out.println("No x.toolchains file found!");
            System.exit(3);
        }
        Gson gson = new GsonBuilder().registerTypeAdapter(Binary.class, new BinaryAdapter()).create();
        Binary[] binaries = gson.fromJson(new JsonReader(new FileReader(xbuildPath.toString())),Binary[].class);
        Toolchain[] toolchains = gson.fromJson(new JsonReader(new FileReader(toolchainsPath.toString())),Toolchain[].class);
        Path flagsPath = workingDir.resolve("x.flags");
        Flags[] flags = null;
        if (Files.exists(flagsPath)) {
            flags = gson.fromJson(new JsonReader(new FileReader(flagsPath.toString())),Flags[].class);
        }
        int numThreads = Runtime.getRuntime().availableProcessors();
        for (Binary binary : binaries) {
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            binary.assignCompiler(toolchains, cpu);
            binary.assignFlags(flags);
            binary.resolveDependencies(binaries);
            binary.compile(executor);
        }
        System.out.println("All done!");
    }
}
