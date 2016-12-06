import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by hadas on 28/11/2016.
 */
public class VMWriter {
	private static final String CONST = "constant", ARG = "argument", LOCAL = "local",
			STATIC = "static", THIS = "this", THAT = "that", POINTER = "pointer", TEMP = "temp",
			ADD = "add", SUB = "sub", NEG = "neg", EQ = "eq", GT = "gt", LT = "lt", AND = "and",
			OR = "or", NOT = "not";
	private static final String NAME_DELIMITER = ".";
	private static final String IDENTIFIER_REGEX = "([^\\d]\\w*)";
	private static final String CLASS_REGEX = "[^\\d]\\w*";
	private static final String METHOD_REGEX = IDENTIFIER_REGEX + "\\." + IDENTIFIER_REGEX;
    private static final String OPENER_LEFT = "<", OPENER_RIGHT = "</",
            CLOSER = ">", WHITESPACE = "  ";
    private static final int WHITESPACE_LENGTH = 2;
    private PrintWriter writer;
    private PrintWriter vmWriter;
    private SymbolTable symbolTable;
    private String space;
    public VMWriter(String filename, SymbolTable symbolTable) throws FileNotFoundException{
        writer = new PrintWriter(filename);
        vmWriter = new PrintWriter(filename.replace("xml", "vm"));
        this.symbolTable = symbolTable;
        space = "";
    }
    public void close() {
        writer.close();
        vmWriter.close();
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
		default:
			break;
        }
        writer.println(space + OPENER_LEFT + typeString + CLOSER +WHITESPACE+ terminal +WHITESPACE+OPENER_RIGHT+typeString + CLOSER);
    }
    
    // Use the symbol table to extend identifier
    public void writeIdentifier(String identifier, String varState, JackTokenizer tokenizer){
    	 writer.println(space + OPENER_LEFT + "identifier" + CLOSER);
    	 writer.println(space + OPENER_LEFT + "name" + CLOSER +WHITESPACE+ identifier +WHITESPACE+OPENER_RIGHT+"name" + CLOSER);
    	 //String className = tokenizer.getClassName();
    	 String category = null;
    	 String runningIndex = null;
    	 if (identifier.matches((IDENTIFIER_REGEX + "\\" + NAME_DELIMITER + IDENTIFIER_REGEX)))
    		 category = "subroutine";
    	 else if (identifier.matches(IDENTIFIER_REGEX))
    		 category = "class";
    	 else{
    		 category = symbolTable.kindOf(identifier);
    		 runningIndex = symbolTable.indexOf(identifier);
    		 writer.println(space + OPENER_LEFT + "index" + CLOSER +WHITESPACE+ runningIndex +WHITESPACE+OPENER_RIGHT+ "index" + CLOSER);
    	 } 
    	 writer.println(space + OPENER_LEFT + "category" + CLOSER +WHITESPACE+ category +WHITESPACE+OPENER_RIGHT+ "category" + CLOSER);
    	 // TODO: Handle used variables in expressions
    	 writer.println(space + OPENER_LEFT + "state" + CLOSER +WHITESPACE+ varState +WHITESPACE+OPENER_RIGHT+ "state" + CLOSER);
    	 writer.println(space + OPENER_RIGHT+ "identifier" + CLOSER);
    }
    
    public void writePush(String segment, String index)
    {
    	vmWriter.println("push " +segment+ " " + index);
    }
    
    public void writePop(String segment, String index)
    {
    	vmWriter.println("pop " +segment+ " " + index);
    }
    
    public void writeArithmetic(String command)
    {
    	switch(command){
    	case "+": vmWriter.println(ADD);
    				break;
    	case "-": vmWriter.println(SUB);
					break;
    	case "*": writeCall("Math.multiply", 2);
					break;
    	case "/": writeCall("Math.divide", 2);
					break;
    	case "|": vmWriter.println(OR);
					break;
    	case "&": vmWriter.println(AND);
					break;
    	case "<": vmWriter.println(LT);
					break;
    	case ">": vmWriter.println(GT);
					break;
    	case "=": vmWriter.println(EQ);
					break;
    	case "\\-": vmWriter.println(NEG);
					break;
    	case "~": vmWriter.println(NOT);
					break;
    	}
    }
    
    public void writeLabel(String label, String labelCounter)
    {
    	vmWriter.println("label " + label + labelCounter);
    }
    
    public void writeGoto(String label, String labelCounter)
    {
    	vmWriter.println("goto " + label + labelCounter);
    }
    
    public void writeIf(String label, String labelCounter)
    {
    	vmWriter.println("if-goto " + label + labelCounter);
    }
    
    public void writeCall(String name, int nArgs)
    {
    	vmWriter.println("call " + name + " " + Integer.toString(nArgs));
    }
    
    public void writeFunction(String name, int nLocals)
    {
    	vmWriter.println("function " + name + " " + Integer.toString(nLocals));
    }
    
    public void writeReturn()
    {
    	vmWriter.println("return");
    }
}






