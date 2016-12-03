
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Created by hadas on 29/11/2016.
 */
public class CompilationEngine{
    private static String ILLEGAL_FILE_FORMAT= "File not in Jack format";
    private VMWriter writer;
    private JackTokenizer tokenizer;
    private SymbolTable symbolTable;
    
    public CompilationEngine(File inputFile, String outputFilename, SymbolTable symbolTable)
    		throws FileNotFoundException, IOException{
        tokenizer = new JackTokenizer(inputFile);
        writer = new VMWriter(outputFilename, symbolTable);
        this.symbolTable = symbolTable;
    }
    private void advance() throws IOException {
        if (tokenizer.hasMoreTokens()) {
            while (tokenizer.checkComment() || tokenizer.checkEmpty());
            tokenizer.advance();
        }
        else {
            throw new IllegalArgumentException(ILLEGAL_FILE_FORMAT);
        }
    }
    private boolean checkNextKeyword(String keyword) throws IOException {
        JackTokenizer.TokenType type = tokenizer.tokenType();
        String token = tokenizer.getToken();
        if (type==null || type != JackTokenizer.TokenType.KEYWORD || !token.matches(keyword)) {
            return false;
        }
        return true;
    }
    private void checkNextIdentifier() throws IOException, IllegalTokenException{
        JackTokenizer.TokenType type = tokenizer.tokenType();
        String token = tokenizer.getToken();
        if (type == null || type != JackTokenizer.TokenType.IDENTIFIER) {
            throw new IllegalTokenException(token);
        }
    }
    private boolean checkNextSymbol(String symbol) throws IOException, IllegalTokenException{
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
        advance();
        if (!checkNextKeyword("class")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        advance();
        if (!checkNextSymbol("{")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        compileClassVarDec();
        compileSubroutine();
      /*  advance();
        if (!checkNextSymbol("}")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        if (tokenizer.hasMoreTokens()) {
            throw new IllegalArgumentException(ILLEGAL_FILE_FORMAT);
        }*/
        writer.close();
        tokenizer.close();
    }
    private void checkType() throws IOException, IllegalTokenException {
        advance();
        if (!checkNextKeyword("int|char|boolean")) {
            checkNextIdentifier();
        }
    }
    public void compileClassVarDec()throws IOException, IllegalTokenException{
        if (!tokenizer.hasMoreTokens()) {
            throw new IOException(ILLEGAL_FILE_FORMAT);
        }
        advance();
        if (!checkNextKeyword("static|field")) {
            return;
        }
        String name, type = null;
        String kindsel = tokenizer.getToken();
        SymbolTable.VarKind kind;
        if (kindsel.matches("static"))
        	kind = SymbolTable.VarKind.STATIC;
        else
        	kind = SymbolTable.VarKind.FIELD;
        writer.writeToken(tokenizer);
        checkType();
        type = tokenizer.getToken();
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        name = tokenizer.getToken();
        symbolTable.define(name, type, kind);
        writer.writeToken(tokenizer);
        checkAdditionalVars(type, kind);
        if (!checkNextSymbol(";")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        compileClassVarDec();
    }
    private void checkAdditionalVars(String type, SymbolTable.VarKind kind)
    		throws IOException, IllegalTokenException {
        advance();
        String name = null;
        while (checkNextSymbol(",")) {
            writer.writeToken(tokenizer);
            advance();
            checkNextIdentifier();
            name = tokenizer.getToken();
            symbolTable.define(name, type, kind);
            writer.writeToken(tokenizer);
            advance();
        }
    }
    private void compileSubroutineBody() throws IOException, IllegalTokenException {
        advance();
        if (!checkNextSymbol("{")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        compileVarDec();
       // advance();
        if (!checkNextSymbol("}")) {
            compileStatements();
        }
        //add check for } after returning from statements
        writer.writeToken(tokenizer);
    }

    public void compileSubroutine() throws IOException, IllegalTokenException {
        if (!checkNextKeyword("constructor|function|method")) {
            return;
        }
        symbolTable.startSubroutine();
        writer.writeToken(tokenizer);
        advance();
        if (!checkNextKeyword("void")) {
            checkType();
        }
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        tokenizer.setToken(tokenizer.getClassName() + "." + tokenizer.getToken());
        writer.writeToken(tokenizer);
        advance();
        if (!checkNextSymbol("(")){
            throw new IllegalTokenException(tokenizer.getToken());
        }
        advance();
        if (!checkNextSymbol(")")) {
            compileParameterList();
        }
        else {
            writer.writeToken(tokenizer);
        }
        compileSubroutineBody();
        if (!checkNextSymbol("}")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        advance();
        compileSubroutine();
    }
    public void compileParameterList() throws IOException, IllegalTokenException {
    	String name, type = null;
        SymbolTable.VarKind kind = SymbolTable.VarKind.ARG;
        checkType();
        type = tokenizer.getToken();
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        name = tokenizer.getToken();
        symbolTable.define(name, type, kind);
        writer.writeToken(tokenizer);
        checkAdditionalVars(type, kind);
    }
    public void compileVarDec() throws IOException, IllegalTokenException {
        advance();
        if (!checkNextKeyword("var")) {
            return;
        }
        String name, type = null;
        SymbolTable.VarKind kind = SymbolTable.VarKind.VAR;
        checkType();
        type = tokenizer.getToken();
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        name = tokenizer.getToken();
        symbolTable.define(name, type, kind);
        writer.writeToken(tokenizer);
        checkAdditionalVars(type, kind);
        if (!checkNextSymbol(";")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        compileVarDec();
    }
    public void compileStatements() throws IOException, IllegalTokenException {
        if (!checkNextKeyword("let|if|while|do|return")) {
            return;
        }
        writer.writeToken(tokenizer);
        String token = tokenizer.getToken();
        switch (token){
            case "let": compileLet();
                break;
            case "if": compileIf();
                break;
            case "while": compileWhile();
                break;
            case "do": compileDo();
                break;
            case "return": compileReturn();
                break;
        }
        advance();
        compileStatements();
    }

    public void compileDo() throws IOException, IllegalTokenException {
        advance();
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        // TODO: Expression list
        advance();
        if(!checkNextSymbol(".")){
            advance();
            if(!checkNextSymbol("("))
                throw new IllegalTokenException(tokenizer.getToken());
            writer.writeToken(tokenizer);
            advance();
            if(!checkNextSymbol(")"))
                throw new IllegalTokenException(tokenizer.getToken());
            writer.writeToken(tokenizer);
        }
        else {
            advance();
            checkNextIdentifier();
            writer.writeToken(tokenizer);
            advance();
            if(!checkNextSymbol("("))
                throw new IllegalTokenException(tokenizer.getToken());
            writer.writeToken(tokenizer);
            advance();
            if(!checkNextSymbol(")"))
                throw new IllegalTokenException(tokenizer.getToken());
            writer.writeToken(tokenizer);
        }
        advance();
        if(!checkNextSymbol(";"))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
    }
    private void checkConditionBody() throws IOException, IllegalTokenException
    {
        advance();
        if(!checkNextSymbol("{"))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
        advance();
        compileStatements();
        if(!checkNextSymbol("}"))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
    }
    public void compileLet() throws IOException, IllegalTokenException{
        advance();
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        advance();
        if (!checkNextSymbol("[")) {
            if(!checkNextSymbol("="))
                throw new IllegalTokenException(tokenizer.getToken());
        }
        // array
        else {
            advance();
            checkNextIdentifier();
            writer.writeToken(tokenizer);
            advance();
            if (!checkNextSymbol("]")) {
                throw new IllegalTokenException(tokenizer.getToken());
            }
            writer.writeToken(tokenizer);
            advance();
            if(!checkNextSymbol("="))
                throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        advance();
        if(!checkNextSymbol(";"))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
    }

    public void compileWhile() throws IllegalTokenException, IOException{
        advance();
        if(!checkNextSymbol("("))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        advance();
        if(!checkNextSymbol(")"))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
        checkConditionBody();
    }
    public void compileReturn() throws IOException, IllegalTokenException{
        advance();
        if(!checkNextSymbol(";")){
            checkNextIdentifier();
            writer.writeToken(tokenizer);
            advance();
            if(!checkNextSymbol(";")){
                throw new IllegalTokenException(tokenizer.getToken());
            }
        }
        writer.writeToken(tokenizer);
    }
    public void compileIf() throws IOException, IllegalTokenException {
        advance();
        if(!checkNextSymbol("(")){
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        advance();
        if(!checkNextSymbol(")")){
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        checkConditionBody();
        advance();
        if(checkNextKeyword("else")){
            writer.writeToken(tokenizer);
            checkConditionBody();
        }
    }
    public void compileExpression(){

    }
    public void compileTerm(){

    }
    public void compileExpressionList(){

    }
}