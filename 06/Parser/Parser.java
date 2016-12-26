import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The parser module receives a line from the Main module and parse
 * it to distinguish between A_Instruction, C_Instruction and lines to ignore.
 * @author OriB
 *
 */
public class Parser {
	
	private static final String WHITESPACE = "\\s*";
	private static final String NAME_REGEX = "([^\\d](\\w|\\.|\\:|\\_|\\$)+)";
	private static final String COMMENT_REGEX = "(//+.*)";
	private static final String LABEL_REGEX = WHITESPACE + "\\(" + WHITESPACE + NAME_REGEX + WHITESPACE + "\\)" +
								WHITESPACE + COMMENT_REGEX + "?";
	private static final String NON_SYMBOL_REGEX = "\\d+";
	private static final String A_INSTRUCTION_REGEX = WHITESPACE + "@" + WHITESPACE + NAME_REGEX +
								WHITESPACE + COMMENT_REGEX + "?";
	private static final String DEST_REGEX = "(M|D|MD|A|AM|AD|AMD)?";
	private static final String COMP_REGEX = "(0|1|-1|D|A|!D|!A|-D|-A|D\\+1" +
			"|A\\+1|D-1|A-1|D\\+A|D-A|A-D|D&A|D\\|A|M|!M|-M|M\\+1" +
			"|M-1|D\\+M|D-M|M-D|D&M|D\\|M|D<<|D>>|A<<|A>>|M<<|M>>)";
	private static final String JMP_REGEX = "(JGT|JEQ|JGE|JLT|JNE|JLE|JMP)?";
	private static final String C_INSTRUCTION_REGEX =  WHITESPACE + DEST_REGEX +
								WHITESPACE + "(=?)" + WHITESPACE + COMP_REGEX +
								WHITESPACE + "(;?)" + WHITESPACE +
								JMP_REGEX + WHITESPACE + COMMENT_REGEX + "?";
	
	// Only deal with comments and instructions here.
	// Empty lines are dealt in the main program.
	public static Instruction parseLine(String line, SymbolTable symbolTable)
	{
					
		// Handle A_Instruction
		// Pattern for the A_Instruction
		Pattern pattern = Pattern.compile(A_INSTRUCTION_REGEX);
		Matcher matcher = pattern.matcher(line);
		
		if (matcher.matches())
		{
			String value = matcher.group(1);
			// Check if value is a symbol
			if (!value.matches(NON_SYMBOL_REGEX))
			{
				// Translate the symbol to it's numeric meaning.
				if (symbolTable.contains(value))
				{
					int symbolAddress = symbolTable.getAddress(value);
					return new A_Instruction(symbolAddress);
				}
				else
				{
					// The symbol is a variable, add it to the table.
					symbolTable.addVariable(value);
					return new A_Instruction(symbolTable.getAddress(value));
				}
			}
			// The value is a number
			return new A_Instruction(Integer.parseInt(value));
		}
		
		// Pattern for the C_Instruction
		pattern = Pattern.compile(C_INSTRUCTION_REGEX);
		matcher = pattern.matcher(line);
		// Handle C_Instruction
		if (matcher.matches())
		{
			String dest = matcher.group(1);
			String destDelimiter = matcher.group(2);
			String comp = matcher.group(3);
			String jumpDelimiter = matcher.group(4);
			String jump = matcher.group(5);
			// Handle illegal cases.
			if ((dest == null && !destDelimiter.isEmpty()) ||
					(dest != null && destDelimiter.isEmpty())
					|| (jump == null && !jumpDelimiter.isEmpty())
					|| (jump != null && jumpDelimiter.isEmpty()))
				return null;
			// Create the object according to the visible fields.
			if (dest == null && jump == null)
				return new C_Instruction(comp);
			else if (dest == null)
				return new C_Instruction(comp, jump, true);
			else if (jump == null)
				return new C_Instruction(dest, comp, false);
			else
				return new C_Instruction(dest, comp, jump);
		}
			// Bad line.
		else
			return null;
			
	}
	
	public static boolean isComment(String line)
	{
		//Comment handling
		if (line.matches(WHITESPACE + COMMENT_REGEX))
			return true;
		return false;
	}
	
	public static String parseLabel(String line)
	{
		// Handle L_Instruction
		// Pattern for the L_Instruction
		Pattern pattern = Pattern.compile(LABEL_REGEX);
		Matcher matcher = pattern.matcher(line);
		
		if (matcher.matches())
		{
			String label = matcher.group(1);
			return label;
		}
		return null;
		/*// Not an L_Instruction
		else
			return null;*/
	}
	
}
