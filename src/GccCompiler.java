import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan on 11/25/2016.
 */
public class GccCompiler extends Compiler {

    private String prefix;

    public GccCompiler(Toolchain toolchain) {
        this.prefix = toolchain.getPrefix();
    }

    @Override
    public Path compile(Path src, Flags flags, Path output) {
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
            cmdList.add(compilerCmd);
            cmdList.addAll(flags.getConlyFlags());
            objectFile += output.resolve(src.getFileName()).toString().replace(cExtension,".o");
        } else if (cppFileMatcher.matches(src)) {
            compilerCmd += "g++";
            cmdList.add(compilerCmd);
            cmdList.addAll(flags.getCxxFlags());
            objectFile += output.resolve(src.getFileName()).toString().replace(cppExtension,".o");
        }
        cmdList.addAll(flags.getCFlags());
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
        return Paths.get(objectFile);
    }

    @Override
    public void archive(List<Path> objs, Path output) {
        List<String> cmdList = new ArrayList<>();
        cmdList.add(this.prefix+"ar");
        cmdList.add("rcs");
        cmdList.add(output.toString());
        objs.forEach(obj->cmdList.add(obj.toString()));
        ProcessBuilder builder = new ProcessBuilder(cmdList);
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        try {
            Process process = builder.start();
            process.waitFor();
            System.out.println("Archiving done!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void link(List<Path> objs, List<Dependency> deps, Path output) {
        List<String> cmdList = new ArrayList<>();
        objs.forEach(obj->cmdList.add(obj.toString()));
        deps.forEach(dep->{
            cmdList.add("-L"+dep.getPath());
            cmdList.add("-l"+dep.getName());
        });
        System.out.println(cmdList);
    }
}
