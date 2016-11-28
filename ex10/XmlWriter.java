package ex10;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by hadas on 28/11/2016.
 */
public class XmlWriter {
    private PrintWriter writer;
    public static final String BEGIN_FILE = "<tokens>", END_FILE = "</tokens>", OPENER_LEFT = "<", OPENER_RIGHT = "</",
            CLOSER = ">", WHITESPACE = " ";
    public XmlWriter(String filename) throws FileNotFoundException{
        writer = new PrintWriter(filename);
        writer.println(BEGIN_FILE);
    }
    public void close() {
        writer.println(END_FILE);
        writer.close();
    }
    public void writeToken(JackTokenizer tokenizer) throws IllegalTokenException{
        String typeString="", terminal="";
        if (tokenizer.tokenType() == null) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        switch (tokenizer.tokenType()) {
            case KEYWORD:
                typeString = "keyword";
                terminal = tokenizer.getToken();
                break;
            case SYMBOL:
                typeString = "symbol";
                terminal = tokenizer.symbol();
                break;
            case IDENTIFIER:
                typeString = "identifier";
                terminal = tokenizer.getToken();
                break;
            case INT_CONST:
                typeString = "integerConstant";
                terminal = tokenizer.getToken();
                break;
            case STRING_CONST:
                typeString = "stringConstant";
                terminal = tokenizer.getToken();
                break;
        }
        writer.println(OPENER_LEFT + typeString + CLOSER +WHITESPACE+ terminal +WHITESPACE+OPENER_RIGHT+typeString + CLOSER);
    }
}
