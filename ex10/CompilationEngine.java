package ex10;

import interfaces.IllegalCommandException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by hadas on 29/11/2016.
 */
public class CompilationEngine{
    private static String ILLEGAL_FILE_FORMAT= "File not in Jack format";
    private XmlWriter writer;
    private JackTokenizer tokenizer;
    public CompilationEngine(File inputFile, String outputFilename) throws FileNotFoundException, IOException{
        tokenizer = new JackTokenizer(inputFile);
        writer = new XmlWriter(outputFilename);
    }
    private boolean checkNextKeyword(String keyword) throws IOException {

        tokenizer.advance();
        JackTokenizer.TokenType type = tokenizer.tokenType();
        String token = tokenizer.getToken();
        if (type==null || type != JackTokenizer.TokenType.KEYWORD || !token.matches(keyword)) {
            return false;
        }
        return true;
    }
    private void checkNextIdentifier() throws IOException, IllegalTokenException{
        tokenizer.advance();
        JackTokenizer.TokenType type = tokenizer.tokenType();
        String token = tokenizer.getToken();
        if (type == null || type != JackTokenizer.TokenType.IDENTIFIER) {
            throw new IllegalTokenException(token);
        }
    }
    private boolean checkNextSymbol(String symbol) throws IOException, IllegalTokenException{
        tokenizer.advance();
        JackTokenizer.TokenType type = tokenizer.tokenType();
        if (type == null || type != JackTokenizer.TokenType.SYMBOL || !tokenizer.symbol().equals(symbol)) {
            return false;
        }
        return true;
    }
    public void compileClass() throws IOException, IllegalTokenException {
        if (!tokenizer.hasMoreTokens()) {
            return;
        }
        if (!checkNextKeyword("class")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        if (!checkNextSymbol("{")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        compileClassVarDec();
        compileSubroutine();
        if (!checkNextSymbol("}")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        if (tokenizer.hasMoreTokens()) {
            throw new IllegalArgumentException(ILLEGAL_FILE_FORMAT);
        }
        writer.close();
        tokenizer.close();
    }
    private void checkType() throws IOException, IllegalTokenException {
        tokenizer.advance();
        if (!checkNextKeyword("int|char|boolean")) {
            checkNextIdentifier();
        }
    }
    public void compileClassVarDec()throws IOException, IllegalTokenException{
        if (!tokenizer.hasMoreTokens()) {
            throw new IOException(ILLEGAL_FILE_FORMAT);
        }
        if (!checkNextKeyword("static|field")) {
            return;
        }
        writer.writeToken(tokenizer);
        checkType();
        writer.writeToken(tokenizer);
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        checkAdditionalVars();
        if (!checkNextSymbol(";")){
            throw new IllegalTokenException(tokenizer.getToken());
        }
        compileClassVarDec();
    }
    private void checkAdditionalVars() throws IOException, IllegalTokenException {
        tokenizer.advance();
        JackTokenizer.TokenType type = tokenizer.tokenType();
        while (type != null && type == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol().equals(",")) {
            writer.writeToken(tokenizer);
            checkNextIdentifier();
            writer.writeToken(tokenizer);
            tokenizer.advance();
            type = tokenizer.tokenType();
        }
    }
    private void compileSubroutineBody() throws IOException, IllegalTokenException {
        if (!checkNextSymbol("{")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        compileVarDec();
        compileStatements();
        if (!checkNextSymbol("}")){
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
    }
    public void compileSubroutine() throws IOException, IllegalTokenException {
        if (!checkNextKeyword("constructor|function|method")) {
            return;
        }
        writer.writeToken(tokenizer);
        if (!checkNextKeyword("void")) {
            checkType();
        }
        writer.writeToken(tokenizer);
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        if (!checkNextSymbol("(")){
            throw new IllegalTokenException(tokenizer.getToken());
        }
        if (!checkNextSymbol(")")) {
            compileParameterList();
        }
        else {
            writer.writeToken(tokenizer);
        }
        compileSubroutineBody();
        compileSubroutine();
    }
    public void compileParameterList() throws IOException, IllegalTokenException {
        checkType();
        writer.writeToken(tokenizer);
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        checkAdditionalVars();
    }
    public void compileVarDec() throws IOException, IllegalTokenException {
        checkNextKeyword("var");
        checkType();
        checkNextIdentifier();
        checkAdditionalVars();
    }
    public void compileStatements() {

    }
    public void compileDo() {

    }
    public void compileLet(){

    }
    public void compileWhile(){

    }
    public void compileReturn(){

    }
    public void compileIf() {

    }
    public void compileExpression(){

    }
    public void compileTerm(){

    }
    public void compileExpressionList(){

    }
}
