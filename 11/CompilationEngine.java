import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Created by hadas on 29/11/2016.
 */
public class CompilationEngine{
	private static final String IDENTIFIER_REGEX = "([^\\d]\\w*)";
	private static final String CLASS_REGEX = "^[^\\d]\\w*";
	private static final String METHOD_REGEX = IDENTIFIER_REGEX + "\\." + IDENTIFIER_REGEX;
	private static final String CONST = "constant", ARG = "argument", LOCAL = "local",
			STATIC = "static", FIELD = "field", THIS = "this", THAT = "that", POINTER = "pointer",
			TEMP = "temp";
    private static final String ILLEGAL_FILE_FORMAT= "File not in Jack format", COMMA = ",",
    		END_OF_STATEMENT=";",
            END_OF_BLOCK = "\\}", BLOCK_BEGINNING = "\\{", ARRAY_OPENER="\\[",
            ARRAY_CLOSER = "\\]",
            PARENTHESIS_LEFT = "\\(", PARENTHESIS_RIGHT = "\\)",
            PRIMITIVE_TYPES = "int|boolean|char",
            CLASS_VAR_TYPE = "static|field",
            SUBROUTINE_TYPE = "constructor|function|method",
            STATEMENTS_TYPES = "let|if|while|do|return",
            OP = "\\+|\\-|\\*|\\/|\\&amp;|\\||\\&lt;|\\&gt;|\\=",
            UNARY_OP = "-|~";
    private static final String WHILE_START = "WHILE_EXP",  WHILE_END = "WHILE_END",
    		IF_COND = "IF_TRUE", ELSE_COND = "IF_FALSE", END_COND = "IF_END";
    private int whileCounter = 0, ifCounter = 0;
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
    private boolean checkNextSymbol(String symbol) throws IOException,
    IllegalTokenException{
        JackTokenizer.TokenType type = tokenizer.tokenType();
        if (type == null || type != JackTokenizer.TokenType.SYMBOL || 
        		!tokenizer.symbol().matches(symbol)) {
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
        while (tokenizer.hasMoreTokens() && (tokenizer.checkComment() || 
        		tokenizer.checkEmpty()));
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
    private void compileSubroutineBody(String name, String funcType) throws IOException,
    IllegalTokenException {
        writer.beginBlock("subroutineBody");
        advance();
        if (!checkNextSymbol(BLOCK_BEGINNING)) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        compileVarDec();
        writer.writeFunction(name, symbolTable.varCount(SymbolTable.VarKind.VAR));
        // Handle method: set the base address of this to the given object
        if (funcType.equals("method")){
        	writer.writePush(ARG, "0");
        	writer.writePop(POINTER, "0");
        }
        // Handle constructor: Allocate new memory space for object
        if (funcType.equals("constructor"))
        {
        	int fieldCount = symbolTable.varCount(SymbolTable.VarKind.FIELD);
        	writer.writePush(CONST, Integer.toString(fieldCount));
        	writer.writeCall("Memory.alloc", 1);
        	writer.writePop(POINTER, "0");
        }
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
        symbolTable.startSubroutine();
        ifCounter = 0;
        whileCounter = 0;
        writer.beginBlock("subroutineDec");
        String funcType = tokenizer.getToken();
        writer.writeToken(tokenizer);
        advance();
        if (!checkNextKeyword("void")) {
            checkType();
        } 
        if (funcType.equals("method"))
        	symbolTable.setMethodBaseIndex();
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
        compileSubroutineBody(name, funcType);
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
        writer.writePop(TEMP, "0");
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
        String kind = null;
        String index = null;
        boolean isArray = false;
        writer.writeIdentifier(name, "used", tokenizer);
        advance();
        if (!checkNextSymbol(ARRAY_OPENER)) {
            if(!checkNextSymbol("="))
                throw new IllegalTokenException(tokenizer.getToken());
        }
        // array
        else {
        	isArray = true;
             if (symbolTable.typeOf(name).matches(CLASS_REGEX))
         	{
         		kind = symbolTable.kindOf(name);
                 index = symbolTable.indexOf(name);
                 if (kind.equals("variable")){
                 	writer.writePush(LOCAL, index);
                 }
                 if (kind.equals(ARG)){
                 	writer.writePush(ARG, index);;
                 }
                 if (kind.equals(STATIC)){
                 	writer.writePush(STATIC, index);;
                 }
                 if (kind.equals(FIELD)){
                 	writer.writePush(THIS, index);;
                 }
                 
         	}
            writer.writeToken(tokenizer);
            advance();
            compileExpression();
            if (!checkNextSymbol(ARRAY_CLOSER)) {
                throw new IllegalTokenException(tokenizer.getToken());
            }
            writer.writeArithmetic("+");
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
        if (!isArray)
        {
	        kind = symbolTable.kindOf(name);
	        index = symbolTable.indexOf(name);
	        if (kind.equals("variable")){
	        	writer.writePop(LOCAL, index);
	        }
	        if (kind.equals(ARG)){
	        	writer.writePop(ARG, index);;
	        }
	        if (kind.equals(STATIC)){
	        	writer.writePop(STATIC, index);;
	        }
	        if (kind.equals(FIELD)){
	        	writer.writePop(THIS, index);;
	        }
        }
        // Pop to array is handled differently
        else{
        	writer.writePop(TEMP, "0");
        	writer.writePop(POINTER, "1");
        	writer.writePush(TEMP, "0");
        	writer.writePop(THAT, "0");
        }
        writer.endBlock("letStatement");
    }

    public void compileWhile() throws IllegalTokenException, IOException{
        writer.beginBlock("whileStatement");
        writer.writeToken(tokenizer);
        int localCount = whileCounter++;
        writer.writeLabel(WHILE_START, Integer.toString(localCount));
        advance();
        if(!checkNextSymbol(PARENTHESIS_LEFT))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
        advance();
        compileExpression();
        if(!checkNextSymbol(PARENTHESIS_RIGHT))
            throw new IllegalTokenException(tokenizer.getToken());
        writer.writeToken(tokenizer);
        writer.writeArithmetic("~");
        writer.writeIf(WHILE_END, Integer.toString(localCount));
        checkConditionBody();
        writer.writeGoto(WHILE_START, Integer.toString(localCount));
        writer.writeLabel(WHILE_END, Integer.toString(localCount));
        writer.endBlock("whileStatement");
    }
    public void compileReturn() throws IOException, IllegalTokenException{
        writer.beginBlock("returnStatement");
        writer.writeToken(tokenizer);
        advance();
        // Non-void function.
        if(!checkNextSymbol(END_OF_STATEMENT)){
            compileExpression();
            if(!checkNextSymbol(END_OF_STATEMENT)){
                throw new IllegalTokenException(tokenizer.getToken());
            }
        }
        // Return 0 for void function.
        else{
        	writer.writePush(CONST, "0");
        }
        writer.writeToken(tokenizer);
        writer.writeReturn();
        writer.endBlock("returnStatement");
    }
    
    public void compileIf() throws IOException, IllegalTokenException {
        writer.beginBlock("ifStatement");
        writer.writeToken(tokenizer);
        boolean hasElse = false;
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
        int localCount = ifCounter++;
        writer.writeToken(tokenizer);
        writer.writeIf(IF_COND, Integer.toString(localCount));
        writer.writeGoto(ELSE_COND, Integer.toString(localCount));
        writer.writeLabel(IF_COND, Integer.toString(localCount));
        checkConditionBody();
        advance();
        if(checkNextKeyword("else")){
        	hasElse = true;
        	writer.writeGoto(END_COND, Integer.toString(localCount));
            writer.writeToken(tokenizer);
            writer.writeLabel(ELSE_COND, Integer.toString(localCount));
            checkConditionBody();
            advance();
        }
        if(hasElse)
        	writer.writeLabel(END_COND, Integer.toString(localCount));
        else
        	writer.writeLabel(ELSE_COND, Integer.toString(localCount));
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
    
    void compileTerm() throws IOException, IllegalTokenException {
        writer.beginBlock("term");
        JackTokenizer.TokenType type = tokenizer.tokenType();
        switch (type) {
            case STRING_CONST:
                writer.writeToken(tokenizer);
                String string = tokenizer.getToken();
                int length = string.length();
                writer.writePush(CONST, Integer.toString(length));
                writer.writeCall("String.new", 1);
                int asciiVal = 0;
                for (int i = 0; i < length; i++){
                	asciiVal = (int) string.charAt(i);
                	writer.writePush(CONST, Integer.toString(asciiVal));
                	writer.writeCall("String.appendChar", 2);
                }
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
                String value = tokenizer.getToken();
                writer.writeToken(tokenizer);
                if (value.matches("true")){
                	writer.writePush(CONST, "0");
                	writer.writeArithmetic("~");
                }
                if (value.matches("false|null"))
                	writer.writePush(CONST, "0");
                if (value.matches(THIS))
                	writer.writePush(POINTER, "0");
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
                    String op = tokenizer.getToken();
                    advance();
                    compileTerm();
                    if (op.equals("-"))
                    	writer.writeArithmetic("\\-");
                    if (op.equals("~"))
                    	writer.writeArithmetic(op);
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
                        writer.writeArithmetic("+");
                        writer.writePop(POINTER, "1");
                        writer.writePush(THAT, "0");
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
        String kind = symbolTable.kindOf(name);
        String index = symbolTable.indexOf(name);
        int nArgs = 0;
        advance();
        /**
         *  Check regular vars - not methods.
         */
        if (!checkNextSymbol("\\(|\\.")) {
        	 if (symbolTable.typeOf(name).matches(CLASS_REGEX))
          	{
        		 
          	}
        	writer.writeIdentifier(name, "used", tokenizer);
        	if (kind.equals("variable"))
        		writer.writePush(LOCAL, index);
        	if (kind.equals(ARG))
        		writer.writePush(ARG, index);
        	if (kind.equals(STATIC))
        		writer.writePush(STATIC, index);
        	if (kind.equals(FIELD))
        		writer.writePush(THIS, index);
            return false;
        }
        if (tokenizer.symbol().equals(".")){
                advance();
                checkNextIdentifier();
                
                // handle object method call
                if (!symbolTable.typeOf(name).matches(PRIMITIVE_TYPES+"|none")){
                	name = symbolTable.typeOf(name) + "." + tokenizer.getToken();
                	if (kind.equals("variable"))
                 		writer.writePush(LOCAL, index);
                 	if (kind.equals(ARG))
                 		writer.writePush(ARG, index);
                 	if (kind.equals(STATIC))
                 		writer.writePush(STATIC, index);
                 	if (kind.equals(FIELD))
                		writer.writePush(THIS, index);
                 	nArgs++;
                }
                else{
                	name = name + "." + tokenizer.getToken();	
                }
                writer.writeIdentifier(name, "used", tokenizer);
                advance();
                if (!checkNextSymbol(PARENTHESIS_LEFT)) {
                    throw new IllegalTokenException(tokenizer.getToken());
                }
                writer.writeToken(tokenizer);
        }
        
        // It's a method from this class
        if (!name.matches(METHOD_REGEX)){
        	writer.writePush(POINTER, "0");
        	name = tokenizer.getClassName() + "." + name;
        	nArgs++;
        }
        
        writer.beginBlock("expressionList");
        advance();
        
        
        if (!checkNextSymbol(PARENTHESIS_RIGHT)) {
            nArgs += compileExpressionList();
        }
        
        
        writer.endBlock("expressionList");
        if (!checkNextSymbol(PARENTHESIS_RIGHT)) {
            throw new IllegalTokenException(tokenizer.getToken());
        }
        writer.writeToken(tokenizer);
        
        // pass this object to the method
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




