import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

/**
 * Created by Stefan on 23.11.2016.
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Gson gson = new Gson();
        Binary[] binaries = gson.fromJson(new JsonReader(new FileReader("src/x.build")),Binary[].class);
        Toolchain[] toolchains = gson.fromJson(new JsonReader(new FileReader("src/x.toolchains")),Toolchain[].class);
        Arrays.stream(binaries).forEach(binary->{
            binary.assignCompiler(toolchains,"stm32f0");
            binary.compile();
        });
    }
}
