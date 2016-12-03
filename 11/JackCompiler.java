
import java.io.File;
import java.io.IOException;
/**
 * Created by hadas on 27/11/2016.
 */
public class JackCompiler {
    private static final String NAME_DELIMITER = ".";
    private static final String IN_SUFFIX = "jack";
    private static final String OUT_SUFFIX = "xml";
    //private static void translate(File inputFile, XmlWriter writer) throws IOException, IllegalTokenException{

//        JackTokenizer tokenizer = new JackTokenizer(inputFile);
//        while(tokenizer.hasMoreTokens())
//        {
//            if (tokenizer.checkComment())
//                continue;
//            if (tokenizer.checkEmpty())
//                continue;
//            tokenizer.advance();
//            writer.writeToken(tokenizer);
//        }
//        tokenizer.close();
    //}
        public static void main(String[] args) {
            try {
                File inputFile = new File(args[0]);
                if (inputFile.isFile()) {
                    String fileName = inputFile.getAbsolutePath();
                    String fileSuffix = fileName.substring(fileName.lastIndexOf(NAME_DELIMITER)+1, fileName.length());
                    if (fileSuffix.equals(IN_SUFFIX)) {
                        String outFileName;
                        outFileName = fileName.substring(0,
                                fileName.indexOf(NAME_DELIMITER)) + NAME_DELIMITER + OUT_SUFFIX;
                        SymbolTable symbolTable = new SymbolTable();
                        CompilationEngine compiler = new CompilationEngine(inputFile,
                        		outFileName, symbolTable);
                        compiler.compileClass();
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