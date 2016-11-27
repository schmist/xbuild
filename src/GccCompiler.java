import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
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
        System.out.println("Compiling: "+src);
        String cExtension = ".c";
        String cppExtension = ".cpp";
        PathMatcher cFileMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*"+cExtension);
        PathMatcher cppFileMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*"+cppExtension);
        List<String> cmdList = new ArrayList<>();
        String compilerCmd = prefix;
        String objectFile ="";
        if (cFileMatcher.matches(src)) {
            compilerCmd += "gcc";
            objectFile += src.toString().replace(cExtension,".o");
        } else if (cppFileMatcher.matches(src)) {
            compilerCmd += "g++";
            objectFile += src.toString().replace(cppExtension,".o");
        }

        cmdList.add(compilerCmd);
        //cmdList.add("-v");
        if (compilerCmd.endsWith("g++")) {
            cmdList.add("-std=c++11");
        }
        cmdList.add("-DSTM32F0");
        cmdList.add("-ID:\\workspace\\vlc_apps\\libvlc\\include");
        cmdList.add("-IC:\\Tools\\libopencm3\\include");
        cmdList.add("-mcpu=cortex-m0");
        cmdList.add("-mthumb");
        cmdList.add("-c");
        cmdList.add(src.toString());
        cmdList.add("-o");
        cmdList.add(objectFile);
        ProcessBuilder builder = new ProcessBuilder(cmdList);
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        try {
            Process process = builder.start();
            process.waitFor();
            System.out.println("Compiling: "+src+" done!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
