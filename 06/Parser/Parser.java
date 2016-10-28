import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	
	private static final String WHITESPACE = "\\s*";
	private static final String COMMENT_REGEX = "(//+.*)";
	private static final String A_INSTRUCTION_REGEX = WHITESPACE + "@([0-9]+)" +
								WHITESPACE + COMMENT_REGEX + "?";
	private static final String DEST_REGEX = "(M|D|MD|A|AM|AD|AMD)?";
	private static final String COMP_REGEX = "(0|1|-1|D|A|!D|!A|-D|-A|D\\+1" +
			"|A\\+1|D-1|A-1|D\\+A|D-A|A-D|D&A|D\\|A|M|!M|-M|M\\+1" +
			"|M-1|D\\+M|D-M|M-D|D&M|D\\|M)";
	private static final String JMP_REGEX = "(JGT|JEQ|JGE|JLT|JNE|JLE|JMP)?";
	private static final String C_INSTRUCTION_REGEX =  WHITESPACE + DEST_REGEX +
								WHITESPACE + "(=?)" + WHITESPACE + COMP_REGEX +
								WHITESPACE + "(;?)" + WHITESPACE +
								JMP_REGEX + WHITESPACE + COMMENT_REGEX + "?";
	
	// Only deal with comments and instructions here.
	// Empty lines are dealt in the main program.
	public static Instruction parseLine(String line)
	{
	
		
		//Comment handling
		if (line.matches(WHITESPACE + COMMENT_REGEX))
			return null;
		
		// Handle A_Instruction
		// Pattern for the C_Instruction
		Pattern pattern = Pattern.compile(A_INSTRUCTION_REGEX);
		Matcher matcher = pattern.matcher(line);
		
		if (matcher.matches())
		{
			String value = matcher.group(1);
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
	
}
