package ex10;

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
    private void advance() throws IOException {
        if (tokenizer.hasMoreTokens()) {
            while (tokenizer.checkComment() || tokenizer.checkEmpty()) {
                if (!tokenizer.hasMoreTokens()) {
                    return;
                }
            }
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
        if (type == null || type != JackTokenizer.TokenType.SYMBOL || !tokenizer.symbol().matches(symbol)) {
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
        writer.beginBlock("class");
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        advance();
        if (!checkNextSymbol("\\{")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        compileClassVarDec();
        compileSubroutine();

        if (!checkNextSymbol("\\}")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        advance();
        while (tokenizer.hasMoreTokens() && (tokenizer.checkComment() || tokenizer.checkEmpty()));
        if (tokenizer.hasMoreTokens()) {
            throw new IllegalArgumentException(ILLEGAL_FILE_FORMAT);
        }
        writer.endBlock("class");
        writer.close();
        tokenizer.close();
    }
    private void checkType() throws IOException, IllegalTokenException {
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
        writer.beginBlock("classVarDec");
        writer.writeToken(tokenizer);
        advance();
        checkType();
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        checkAdditionalVars();
        if (!checkNextSymbol(";")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        writer.endBlock("classVarDec");
        compileClassVarDec();
    }
    private void checkAdditionalVars() throws IOException, IllegalTokenException {
        advance();
        while (checkNextSymbol(",")) {
            writer.writeToken(tokenizer);
            advance();
            checkNextIdentifier();
            writer.writeToken(tokenizer);
            advance();
        }
    }
    private void compileSubroutineBody() throws IOException, IllegalTokenException {
        writer.beginBlock("subroutineBody");
        advance();
        if (!checkNextSymbol("\\{")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        compileVarDec();
       // advance();
        if (!checkNextSymbol("\\}")) {
            compileStatements();
            if (!checkNextSymbol("\\}")) {
                throw new IllegalTokenException(tokenizer.getToken());
            }
        }
        writer.writeToken(tokenizer);
        writer.endBlock("subroutineBody");
    }

    public void compileSubroutine() throws IOException, IllegalTokenException {
        if (!checkNextKeyword("constructor|function|method")) {
            return;
        }
        writer.beginBlock("subroutineDec");
        writer.writeToken(tokenizer);
        advance();
        if (!checkNextKeyword("void")) {
            checkType();
        }
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        advance();
        if (!checkNextSymbol("\\(")){
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        advance();
        writer.beginBlock("parameterList");
        if (!checkNextSymbol("\\)")) {
            compileParameterList();
            if (!checkNextSymbol("\\)")) {
                throw new IllegalTokenException(tokenizer.getToken());
            }
        }
        writer.endBlock("parameterList");
        writer.writeToken(tokenizer);
        compileSubroutineBody();
        advance();
        writer.endBlock("subroutineDec");
        compileSubroutine();
    }
    public void compileParameterList() throws IOException, IllegalTokenException {
        checkType();
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        advance();
        while (checkNextSymbol(",")) {
            writer.writeToken(tokenizer);
            advance();
            checkType();
            writer.writeToken(tokenizer);
            advance();
            checkNextIdentifier();
            writer.writeToken(tokenizer);
            advance();
        }
    }

    public void compileVarDec() throws IOException, IllegalTokenException {
        advance();
        if (!checkNextKeyword("var")) {
            return;
        }
        writer.beginBlock("varDec");
        writer.writeToken(tokenizer);
        advance();
        checkType();
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        checkAdditionalVars();
        if (!checkNextSymbol(";")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        writer.endBlock("varDec");
        compileVarDec();
    }
    public void compileStatements() throws IOException, IllegalTokenException {
        writer.beginBlock("statements");
        while (checkNextKeyword("let|if|while|do|return")) {
            String token = tokenizer.getToken();
            switch (token){
                case "let":
                    compileLet();
                    advance();
                    break;
                case "if": compileIf();
                    break;
                case "while": compileWhile();
                    advance();
                    break;
                case "do": compileDo();
                    advance();
                    break;
                case "return": compileReturn();
                    advance();
                    break;
            }
        }
        writer.endBlock("statements");
    }

    public void compileDo() throws IOException, IllegalTokenException {
        writer.beginBlock("doStatement");
        writer.writeToken(tokenizer);
        advance();
        if (!subroutineCall()) {
            throw new IllegalTokenException(tokenizer.getToken());
        }

        if(!checkNextSymbol(";"))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
        writer.endBlock("doStatement");
    }
    private void checkConditionBody() throws IOException, IllegalTokenException
    {
        advance();
        if(!checkNextSymbol("\\{"))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
        advance();
        compileStatements();
        if(!checkNextSymbol("\\}"))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
    }
    public void compileLet() throws IOException, IllegalTokenException{
        writer.beginBlock("letStatement");
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        advance();
        if (!checkNextSymbol("\\[")) {
            if(!checkNextSymbol("="))
                throw new IllegalTokenException(tokenizer.getToken());
        }
        // array
        else {
            writer.writeToken(tokenizer);
            advance();
            compileExpression();
            if (!checkNextSymbol("\\]")) {
                throw new IllegalTokenException(tokenizer.getToken());
            }
            writer.writeToken(tokenizer);
            advance();
            if(!checkNextSymbol("="))
                throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        advance();
        compileExpression();
        if (!checkNextSymbol(";")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }

        writer.writeToken(tokenizer);
        writer.endBlock("letStatement");
    }

    public void compileWhile() throws IllegalTokenException, IOException{
        writer.beginBlock("whileStatement");
        writer.writeToken(tokenizer);
        advance();
        if(!checkNextSymbol("\\("))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
        advance();
        compileExpression();
        if(!checkNextSymbol("\\)"))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
        checkConditionBody();
        writer.endBlock("whileStatement");
    }
    public void compileReturn() throws IOException, IllegalTokenException{
        writer.beginBlock("returnStatement");
        writer.writeToken(tokenizer);
        advance();
        if(!checkNextSymbol(";")){
            compileExpression();
            if(!checkNextSymbol(";")){
                throw new IllegalTokenException(tokenizer.getToken());
            }
        }
        writer.writeToken(tokenizer);
        writer.endBlock("returnStatement");
    }
    public void compileIf() throws IOException, IllegalTokenException {
        writer.beginBlock("ifStatement");
        writer.writeToken(tokenizer);
        advance();
        if(!checkNextSymbol("\\(")){
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        advance();
        compileExpression();
        if(!checkNextSymbol("\\)")){
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        checkConditionBody();
        advance();
        if(checkNextKeyword("else")){
            writer.writeToken(tokenizer);
            checkConditionBody();
            advance();
        }
        writer.endBlock("ifStatement");
    }
    public void compileExpression() throws IOException, IllegalTokenException {
        writer.beginBlock("expression");
        compileTerm();
        while (checkNextSymbol("\\+|\\-|\\*|\\/|\\&amp;|\\||\\&lt;|\\&gt;|\\=")) {
            writer.writeToken(tokenizer);
            advance();
            compileTerm();
            if (checkNextSymbol(";")) {
                break;
            }
        }
        writer.endBlock("expression");
        return;
    }
    public void compileTerm() throws IOException, IllegalTokenException {
        writer.beginBlock("term");
        JackTokenizer.TokenType type = tokenizer.tokenType();
        switch (type) {
            case STRING_CONST:
                writer.writeToken(tokenizer);
                advance();
                break;
            case INT_CONST:
                writer.writeToken(tokenizer);
                advance();
                break;
            case KEYWORD:
                if (!checkNextKeyword("true|false|null|this")) {
                    throw new IllegalTokenException(tokenizer.getToken());
                }
                writer.writeToken(tokenizer);
                advance();
                break;
            case SYMBOL:
                if (checkNextSymbol("\\(")) {
                    writer.writeToken(tokenizer);
                    advance();
                    compileExpression();
                    if (!checkNextSymbol("\\)")) {
                        throw new IllegalTokenException(tokenizer.getToken());
                    }
                    writer.writeToken(tokenizer);
                    advance();
                }
                else if (checkNextSymbol("-|~")) {
                    writer.writeToken(tokenizer);
                    advance();
                    compileTerm();
                }
                break;
            case IDENTIFIER:
                if (!subroutineCall()) {
                    if (!checkNextSymbol("\\[")) {
                        break;
                    }
                    // array
                    else {
                        writer.writeToken(tokenizer);
                        advance();
                        compileExpression();
                        if (!checkNextSymbol("\\]")) {
                            throw new IllegalTokenException(tokenizer.getToken());
                        }
                        writer.writeToken(tokenizer);
                        advance();
                        break;
                    }
                }
                break;

            default: throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.endBlock("term");
        return;
    }
    private boolean subroutineCall() throws IllegalTokenException, IOException {
        checkNextIdentifier();
        writer.writeToken(tokenizer);
        advance();
        if (!checkNextSymbol("\\(|\\.")) {
            return false;
        }
        writer.writeToken(tokenizer);
        if (tokenizer.symbol().equals(".")){
                advance();
                checkNextIdentifier();
                writer.writeToken(tokenizer);
                advance();
                if (!checkNextSymbol("\\(")) {
                    throw new IllegalTokenException(tokenizer.getToken());
                }
                writer.writeToken(tokenizer);
        }
        writer.beginBlock("expressionList");
        advance();
        if (!checkNextSymbol("\\)")) {
            compileExpressionList();
        }
        writer.endBlock("expressionList");
        if (!checkNextSymbol("\\)")) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        advance();
        return true;
    }
    public void compileExpressionList() throws IOException, IllegalTokenException {
        compileExpression();
        while (checkNextSymbol(",")) {
            writer.writeToken(tokenizer);
            advance();
            compileExpression();
        }
    }
}
