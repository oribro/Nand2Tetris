package ex10;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
/**
 * Created by hadas on 27/11/2016.
 */
public class JackAnalyzer {
    private static final String NAME_DELIMITER = ".";
    private static final String IN_SUFFIX = "jack";
    private static final String OUT_SUFFIX = "xml";
    private static void translateFile(File inputFile)
            throws FileNotFoundException, IOException, IllegalTokenException
    {
        String fileName = inputFile.getAbsolutePath();
        String fileSuffix = fileName.substring(fileName.lastIndexOf(NAME_DELIMITER)+1, fileName.length());
        if (fileSuffix.equals(IN_SUFFIX)) {
            String outFileName;
            outFileName = fileName.substring(0,
                    fileName.indexOf(NAME_DELIMITER)) + NAME_DELIMITER + OUT_SUFFIX;
            CompilationEngine compiler = new CompilationEngine(inputFile, outFileName);
            compiler.compileClass();
        }
    }

    public static void main(String[] args) {
        try {
            File inputFile = new File(args[0]);
            if (inputFile.isFile()) {
                translateFile(inputFile);
            }
            if (inputFile.isDirectory()) {
                File[] dirFiles = inputFile.listFiles();
                for (File file : dirFiles)
                {
                    translateFile(file);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (IllegalTokenException e) {
            System.err.println(e.getMessage());
        }
    }

}
