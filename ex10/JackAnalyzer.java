package ex10;
import java.io.File;
import java.io.IOException;
/**
 * Created by hadas on 27/11/2016.
 */
public class JackAnalyzer {
    private static final String NAME_DELIMITER = ".";
    private static final String IN_SUFFIX = ".jack";
    private static final String OUT_SUFFIX = "T.xml";
    private static void translate(File inputFile, XmlWriter writer) throws IOException, IllegalTokenException{
        JackTokenizer tokenizer = new JackTokenizer(inputFile);
        // Read and write commands from vm to asm.
        // Ignore empty lines and comments.
        while(tokenizer.hasMoreTokens())
        {
            if (tokenizer.checkComment())
                continue;
            if (tokenizer.checkEmpty())
                continue;
            String tokenString = "";
            tokenizer.advance();
            writer.writeToken(tokenizer);

        }
        tokenizer.close();
    }
        public static void main(String[] args) {
            try {
                File inputFile = new File(args[0]);
                XmlWriter writer = null;
                if (inputFile.isFile()) {
                    String fileName = inputFile.getAbsolutePath();
                    String fileSuffix = fileName.substring(fileName.lastIndexOf(NAME_DELIMITER), fileName.length());
                    if (fileSuffix.equals(IN_SUFFIX)) {
                        String outFileName = null;
                        outFileName = fileName.substring(0,
                                fileName.indexOf(IN_SUFFIX)) + OUT_SUFFIX;
                        writer = new XmlWriter(outFileName);
                        translate(inputFile, writer);
                        writer.close();
                    }
                }
                if (inputFile.isDirectory()) {
                    //TODO
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
