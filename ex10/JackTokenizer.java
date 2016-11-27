package ex10;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by hadas on 27/11/2016.
 */
public class JackTokenizer {
    private static final String WHITESPACE = "\\s*";
    private static final String COMMENT_REGEX = "(\\/\\/)|(\\/\\*)|(\\*\\/)|(\\/\\*\\*)";
    public enum TokenType{KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST}
    public enum KeyWord{CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN, CHAR, VOID,
        VAR, STATIC, FIELD, LET, DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE, NULL, THIS}
    private static final String NULL_STR = "";
    private BufferedReader fileReader = null;
    private String currInstruction = null;
    public JackTokenizer(String filename) {

    }
    public boolean hasMoreTokens() {
        return false;
    }
    public boolean checkEmpty() throws IOException
    {
        if (currInstruction.isEmpty())
        {
            currInstruction = fileReader.readLine();
            return true;
        }
        return false;
    }

    public boolean checkComment() throws IOException
    {
        //TODO: match the project's instructions
        if (currInstruction.matches(WHITESPACE + COMMENT_REGEX))
        {
            currInstruction = fileReader.readLine();
            return true;
        }
        return false;
    }
    public void advance() throws IOException
    {
        currInstruction = fileReader.readLine();
    }
    public TokenType tokenType() {
        return TokenType.IDENTIFIER;
    }
    public KeyWord keyWord() {
        return KeyWord.BOOLEAN;
    }
    public char symbol() {
        return 'a';
    }
    public String identifier() {
        return "";
    }
    public int intVal() {
        return 0;
    }
    public String stringVal() {
        return "";
    }
    public void close () throws IOException{ fileReader.close();}
}
