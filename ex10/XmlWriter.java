package ex10;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by hadas on 28/11/2016.
 */
public class XmlWriter {
    private static final String OPENER_LEFT = "<", OPENER_RIGHT = "</",
            CLOSER = ">", WHITESPACE = "  ";
    private static final int WHITESPACE_LENGTH = 2;
    private PrintWriter writer;
    private String space;
    public XmlWriter(String filename) throws FileNotFoundException{
        writer = new PrintWriter(filename);
        space = "";
    }
    public void close() {
        writer.close();
    }
    public void beginBlock(String blockName) {
        writer.println(space + OPENER_LEFT+blockName+CLOSER);
        space+= WHITESPACE;
    }
    public void endBlock(String blockName) {
        space= space.substring(WHITESPACE_LENGTH, space.length());
        writer.println(space + OPENER_RIGHT + blockName + CLOSER);
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
        writer.println(space + OPENER_LEFT + typeString + CLOSER +WHITESPACE+ terminal +WHITESPACE+OPENER_RIGHT+typeString + CLOSER);
    }
}
