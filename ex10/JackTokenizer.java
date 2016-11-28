package ex10;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by hadas on 27/11/2016.
 */
public class JackTokenizer {

    public enum TokenType{KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST}
    private static final String WHITESPACE = "\\s*", COMMENT_REGEX = "\\/\\/.*|\\/\\*.*|\\/\\*\\*.*|.*\\*\\/",
    COMMENT_HALF_LINE = "(\\/\\*\\*|\\/\\*).*\\*\\/",
    KEYWORD_REGEX="(class|constructor|function|method|field|static|var|int|char|boolean|void|true|false|null|" +
            "this|let|do|if|else|while|return)", SYMBOL_REGEX = "\\{|\\}|\\(|\\)|\\[|\\]|\\.|\\," +
            "|\\;|\\+|\\-|\\*|\\/|\\&|\\||\\<|\\>|\\=|\\~",
            IDENTIFIER_REGEX = "([^\\d]\\w*)", INT_REGEX= "\\d+", STRING_REGEX = "(\\w|\\s)*";
    private BufferedReader fileReader;
    private String currToken, currLine;
    public JackTokenizer(File inputFile) throws IOException{
        this.fileReader = new BufferedReader(new FileReader(inputFile));
        currLine = fileReader.readLine();
        currToken = "";
       // advance(); //do we need it?
    }
    public boolean hasMoreTokens() throws IOException{
        if (currLine == null)
        {
            fileReader.close();
            return false;
        }
        return true;
    }
    public boolean checkEmpty() throws IOException
    {
        if (currLine.isEmpty())
        {
            currLine = fileReader.readLine();
            return true;
        }
        return false;
    }

    public boolean checkComment() throws IOException
    {
        Pattern commentPattern = Pattern.compile(COMMENT_HALF_LINE);
        Matcher commentMatcher = commentPattern.matcher(currLine);
        if (commentMatcher.find()) {
            currLine = commentMatcher.replaceAll("");
            return false;
        }
        if (currLine.matches(WHITESPACE + COMMENT_REGEX))
        {
            currLine = fileReader.readLine();
            return true;
        }
        //Pattern commentPattern = Pattern.compile("")
//        if (currLine.contains("//")) {
//            currLine = currLine.substring(0, currLine.indexOf("//"));
//            if (currLine.isEmpty()) {
//                currLine = fileReader.readLine();
//                return true;
//            }
//        }
        return false;
    }
    public void advance() throws IOException
    {
        currLine = currLine.trim();
        String[] parts = currLine.split(" ");
        currToken = parts[0];
        if (currToken.charAt(0) == '"') {
            currLine = currLine.substring(1, currLine.length());
            currToken = currLine.substring(0, currLine.indexOf('"'));
            currLine = currLine.replaceFirst(currToken + "\"", "");
            return;
        }
        if (tokenType() == null) {
            Matcher matcher = Pattern.compile(SYMBOL_REGEX).matcher(currToken);
            if (matcher.find()) {
                int symbolIndex = matcher.start();
                if (symbolIndex == 0) {
                    currToken = Character.toString(currToken.charAt(0));
                }
                else {
                    currToken = currToken.substring(0, symbolIndex);
                }

            }
            //else?
        }
        if (currToken.toCharArray().length == 1) {
            currLine = currLine.substring(1, currLine.length());
            return;
        }
        currLine = currLine.replaceFirst(currToken, "");
    }

    public TokenType tokenType() {
        Pattern pattern = Pattern.compile(SYMBOL_REGEX);
        Matcher matcher = pattern.matcher(currToken);
        if (matcher.matches()) {
            return TokenType.SYMBOL;
        }
        pattern = Pattern.compile(KEYWORD_REGEX);
        matcher = pattern.matcher(currToken);
        if (matcher.matches())
        {
            return TokenType.KEYWORD;
        }

        pattern = Pattern.compile(IDENTIFIER_REGEX);
        matcher = pattern.matcher(currToken);
        if (matcher.matches()) {
            return TokenType.IDENTIFIER;
        }
        pattern = Pattern.compile(INT_REGEX);
        matcher = pattern.matcher(currToken);
        if (matcher.matches()) {
            return TokenType.INT_CONST;
        }
        pattern = Pattern.compile(STRING_REGEX);
        matcher = pattern.matcher(currToken);
        if (matcher.matches()) {
            return TokenType.STRING_CONST;
        }
        return null;
    }
    public String getToken() { return currToken; }
    public String symbol() throws IllegalTokenException{
        if (tokenType() != TokenType.SYMBOL) {
            throw new IllegalTokenException(currToken); //make it a unique exception
        }
        if (currToken.equals("<")) {
            return "&lt;";
        }
        if (currToken.equals(">")) {
            return "&gt;";
        }
        if (currToken.equals("\"")) {
            return "&quot;";
        }
        if (currToken.equals("&")) {
            return "&amp;";
        }
        return currToken;
    }
    public void close () throws IOException{ fileReader.close();}
}
