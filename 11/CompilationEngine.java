import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Created by hadas on 29/11/2016.
 */
public class CompilationEngine{
	private static final String CONST = "constant", ARG = "argument", LOCAL = "local",
			STATIC = "static", THIS = "this", THAT = "that", POINTER = "pointer", TEMP = "temp",
			ADD = "add", SUB = "sub", NEG = "neg", EQ = "eq", GT = "gt", LT = "lt", AND = "and",
			OR = "or", NOT = "not";
    private static final String ILLEGAL_FILE_FORMAT= "File not in Jack format", COMMA = ",", END_OF_STATEMENT=";",
            END_OF_BLOCK = "\\}", BLOCK_BEGINNING = "\\{", ARRAY_OPENER="\\[", ARRAY_CLOSER = "\\]",
            PARENTHESIS_LEFT = "\\(", PARENTHESIS_RIGHT = "\\)", PRIMITIVE_TYPES = "int|boolean|char",
            CLASS_VAR_TYPE = "static|field", SUBROUTINE_TYPE = "constructor|function|method",
            STATEMENTS_TYPES = "let|if|while|do|return", OP = "\\+|\\-|\\*|\\/|\\&amp;|\\||\\&lt;|\\&gt;|\\=",
            UNARY_OP = "-|~";
    private VMWriter writer;
    private SymbolTable symbolTable;
    private JackTokenizer tokenizer;
    public CompilationEngine(File inputFile, String outputFilename, SymbolTable symbolTable)
    		throws FileNotFoundException, IOException{
        tokenizer = new JackTokenizer(inputFile);
        writer = new VMWriter(outputFilename, symbolTable);
        this.symbolTable = symbolTable;
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
        String name = tokenizer.getToken();
        writer.writeIdentifier(name, "defined", tokenizer);
        advance();
        if (!checkNextSymbol(BLOCK_BEGINNING)) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        compileClassVarDec();
        compileSubroutine();

        if (!checkNextSymbol(END_OF_BLOCK)) {
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
        if (!checkNextKeyword(PRIMITIVE_TYPES)) {
            checkNextIdentifier();
        }
    }
    public void compileClassVarDec()throws IOException, IllegalTokenException{
        if (!tokenizer.hasMoreTokens()) {
            throw new IOException(ILLEGAL_FILE_FORMAT);
        }
        advance();
        if (!checkNextKeyword(CLASS_VAR_TYPE)) {
            return;
        }
        writer.beginBlock("classVarDec");
        String name, type = null;
        String kindsel = tokenizer.getToken();
        SymbolTable.VarKind kind;
        if (kindsel.matches("static"))
        	kind = SymbolTable.VarKind.STATIC;
        else
        	kind = SymbolTable.VarKind.FIELD;
        writer.writeToken(tokenizer);
        advance();
        checkType();
        type = tokenizer.getToken();
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        name = tokenizer.getToken();
        symbolTable.define(name, type, kind);
        writer.writeIdentifier(name, "defined", tokenizer);
        checkAdditionalVars(type, kind);
        if (!checkNextSymbol(END_OF_STATEMENT)) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        writer.endBlock("classVarDec");
        compileClassVarDec();
    }
    private void checkAdditionalVars(String type, SymbolTable.VarKind kind)
    		throws IOException, IllegalTokenException {
        advance();
        String name = null;
        while (checkNextSymbol(COMMA)) {
            writer.writeToken(tokenizer);
            advance();
            checkNextIdentifier();
            name = tokenizer.getToken();
            symbolTable.define(name, type, kind);
            writer.writeIdentifier(name, "defined", tokenizer);
            advance();
        }
    }
    private void compileSubroutineBody(String name) throws IOException, IllegalTokenException {
        writer.beginBlock("subroutineBody");
        advance();
        if (!checkNextSymbol(BLOCK_BEGINNING)) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        compileVarDec();
        writer.writeFunction(name, symbolTable.varCount(SymbolTable.VarKind.VAR));
       // advance();
        if (!checkNextSymbol(END_OF_BLOCK)) {
            compileStatements();
            if (!checkNextSymbol(END_OF_BLOCK)) {
                throw new IllegalTokenException(tokenizer.getToken());
            }
        }
        writer.writeToken(tokenizer);
        writer.endBlock("subroutineBody");
    }

    public void compileSubroutine() throws IOException, IllegalTokenException {
        if (!checkNextKeyword(SUBROUTINE_TYPE)) {
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
        String name = tokenizer.getClassName() + "." + tokenizer.getToken();
        writer.writeIdentifier(name, "defined", tokenizer);
        advance();
        if (!checkNextSymbol(PARENTHESIS_LEFT)){
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        advance();
        writer.beginBlock("parameterList");
        if (!checkNextSymbol(PARENTHESIS_RIGHT)) {
            compileParameterList();
            if (!checkNextSymbol(PARENTHESIS_RIGHT)) {
                throw new IllegalTokenException(tokenizer.getToken());
            }
        }
        writer.endBlock("parameterList");
        writer.writeToken(tokenizer);
        compileSubroutineBody(name);
        advance();
        writer.endBlock("subroutineDec");
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
        writer.writeIdentifier(name, "defined", tokenizer);
        advance();
        while (checkNextSymbol(COMMA)) {
            writer.writeToken(tokenizer);
            advance();
            checkType();
            writer.writeToken(tokenizer);
            advance();
            checkNextIdentifier();
            name = tokenizer.getToken();
            symbolTable.define(name, type, kind);
            writer.writeIdentifier(name, "defined", tokenizer);
            advance();
        }
    }

    public void compileVarDec() throws IOException, IllegalTokenException {
        advance();
        if (!checkNextKeyword("var")) {
            return;
        }
        writer.beginBlock("varDec");
        String name, type = null;
        SymbolTable.VarKind kind = SymbolTable.VarKind.VAR;
        writer.writeToken(tokenizer);
        advance();
        checkType();
        type = tokenizer.getToken();
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        name = tokenizer.getToken();
        symbolTable.define(name, type, kind);
        writer.writeIdentifier(name, "defined", tokenizer);
        checkAdditionalVars(type, kind);
        if (!checkNextSymbol(END_OF_STATEMENT)) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        writer.endBlock("varDec");
        compileVarDec();
    }
    public void compileStatements() throws IOException, IllegalTokenException {
        writer.beginBlock("statements");
        while (checkNextKeyword(STATEMENTS_TYPES)) {
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

        if(!checkNextSymbol(END_OF_STATEMENT))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
        writer.endBlock("doStatement");
    }
    private void checkConditionBody() throws IOException, IllegalTokenException
    {
        advance();
        if(!checkNextSymbol(BLOCK_BEGINNING))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
        advance();
        compileStatements();
        if(!checkNextSymbol(END_OF_BLOCK))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
    }
    public void compileLet() throws IOException, IllegalTokenException{
        writer.beginBlock("letStatement");
        writer.writeToken(tokenizer);
        advance();
        checkNextIdentifier();
        String name = tokenizer.getToken();
        writer.writeIdentifier(name, "used", tokenizer);
        advance();
        if (!checkNextSymbol(ARRAY_OPENER)) {
            if(!checkNextSymbol("="))
                throw new IllegalTokenException(tokenizer.getToken());
        }
        // array
        else {
            writer.writeToken(tokenizer);
            advance();
            compileExpression();
            if (!checkNextSymbol(ARRAY_CLOSER)) {
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
        if (!checkNextSymbol(END_OF_STATEMENT)) {
            throw new IllegalTokenException(tokenizer.getToken());
        }

        writer.writeToken(tokenizer);
        writer.endBlock("letStatement");
    }

    public void compileWhile() throws IllegalTokenException, IOException{
        writer.beginBlock("whileStatement");
        writer.writeToken(tokenizer);
        advance();
        if(!checkNextSymbol(PARENTHESIS_LEFT))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
        advance();
        compileExpression();
        if(!checkNextSymbol(PARENTHESIS_RIGHT))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
        checkConditionBody();
        writer.endBlock("whileStatement");
    }
    public void compileReturn() throws IOException, IllegalTokenException{
        writer.beginBlock("returnStatement");
        writer.writeToken(tokenizer);
        advance();
        if(!checkNextSymbol(END_OF_STATEMENT)){
            compileExpression();
            if(!checkNextSymbol(END_OF_STATEMENT)){
                throw new IllegalTokenException(tokenizer.getToken());
            }
        }
        writer.writeToken(tokenizer);
        writer.writeReturn();
        writer.endBlock("returnStatement");
    }
    public void compileIf() throws IOException, IllegalTokenException {
        writer.beginBlock("ifStatement");
        writer.writeToken(tokenizer);
        advance();
        if(!checkNextSymbol(PARENTHESIS_LEFT)){
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        advance();
        compileExpression();
        if(!checkNextSymbol(PARENTHESIS_RIGHT)){
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
        String op = null;
        while (checkNextSymbol(OP)) {
            writer.writeToken(tokenizer);
            op = tokenizer.getToken();
            advance();
            compileTerm();
            writer.writeArithmetic(op);
            if (checkNextSymbol(END_OF_STATEMENT)) {
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
                writer.writePush(CONST, tokenizer.getToken());
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
                if (checkNextSymbol(PARENTHESIS_LEFT)) {
                    writer.writeToken(tokenizer);
                    advance();
                    compileExpression();
                    if (!checkNextSymbol(PARENTHESIS_RIGHT)) {
                        throw new IllegalTokenException(tokenizer.getToken());
                    }
                    writer.writeToken(tokenizer);
                    advance();
                }
                else if (checkNextSymbol(UNARY_OP)) {
                    writer.writeToken(tokenizer);
                    advance();
                    compileTerm();
                    writer.writeArithmetic("\\-" + tokenizer.getToken());
                }
                break;
            case IDENTIFIER:
                if (!subroutineCall()) {
                    if (!checkNextSymbol(ARRAY_OPENER)) {
                        break;
                    }
                    // array
                    else {
                        writer.writeToken(tokenizer);
                        advance();
                        compileExpression();
                        if (!checkNextSymbol(ARRAY_CLOSER)) {
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
        String name = tokenizer.getToken();
        writer.writeIdentifier(name, "used", tokenizer);
        advance();
        if (!checkNextSymbol("\\(|\\.")) {
            return false;
        }
        writer.writeToken(tokenizer);
        if (tokenizer.symbol().equals(".")){
                advance();
                checkNextIdentifier();
                name = name + "." + tokenizer.getToken();
                writer.writeIdentifier(name, "used", tokenizer);
                advance();
                if (!checkNextSymbol(PARENTHESIS_LEFT)) {
                    throw new IllegalTokenException(tokenizer.getToken());
                }
                writer.writeToken(tokenizer);
        }
        writer.beginBlock("expressionList");
        advance();
        int nArgs = 0;
        if (!checkNextSymbol(PARENTHESIS_RIGHT)) {
            nArgs = compileExpressionList();
        }
        writer.endBlock("expressionList");
        if (!checkNextSymbol(PARENTHESIS_RIGHT)) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        writer.writeCall(name, nArgs);
        advance();
        return true;
    }
    public int compileExpressionList() throws IOException, IllegalTokenException {
    	int nArgs = 0;
        compileExpression();
        nArgs++;
        while (checkNextSymbol(COMMA)) {
            writer.writeToken(tokenizer);
            advance();
            compileExpression();
            nArgs++;
        }
        return nArgs;
    }
}




