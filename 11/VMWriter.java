
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by hadas on 28/11/2016.
 */
public class VMWriter {
    private PrintWriter writer;
    private SymbolTable symbolTable;
    
    private static final String NAME_DELIMITER = ".";
    private static final String IDENTIFIER_REGEX = "([^\\d]\\w*)";
    public static final String BEGIN_FILE = "<tokens>", END_FILE = "</tokens>", OPENER_LEFT = "<", OPENER_RIGHT = "</",
            CLOSER = ">", WHITESPACE = " ";
    public VMWriter(String filename, SymbolTable symbolTable) throws FileNotFoundException{
        writer = new PrintWriter(filename);
        this.symbolTable = symbolTable;
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
                writeIdentifier(typeString, terminal, tokenizer);
                return;
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
    
    // Use the symbol table to extend identifier
    private void writeIdentifier(String typeString, String identifier, JackTokenizer tokenizer){
    	 writer.println(OPENER_LEFT + typeString + CLOSER);
    	 writer.println(identifier);
    	 String className = tokenizer.getClassName();
    	 String category = null;
    	 String varState = "none";
    	 String runningIndex = null;
    	 if (identifier.equals(className))
    		 category = "class";
    	 else if (identifier.matches((className + "\\" + NAME_DELIMITER + IDENTIFIER_REGEX)))
    		 category = "subroutine";
    	 else{
    		 category = symbolTable.kindOf(identifier);
    		 runningIndex = symbolTable.indexOf(identifier);
    		 writer.println(OPENER_LEFT + "index" + CLOSER +WHITESPACE+ runningIndex +WHITESPACE+OPENER_RIGHT+ "index" + CLOSER);
    	 } 
    	 writer.println(OPENER_LEFT + "category" + CLOSER +WHITESPACE+ category +WHITESPACE+OPENER_RIGHT+ "category" + CLOSER);
    	 if (symbolTable.kindOf(identifier).equals("variable"))
    		 varState = "defined";
    	 // TODO: Handle used variables in expressions
    	 writer.println(OPENER_LEFT + "state" + CLOSER +WHITESPACE+ varState +WHITESPACE+OPENER_RIGHT+ "state" + CLOSER);
    	 writer.println(OPENER_RIGHT+typeString + CLOSER);
    }
}