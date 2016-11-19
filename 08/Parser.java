import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	
	private static final int MAX_MEMORY_VALUE = 32767;
	private static final String WHITESPACE = "\\s*";
	private static final String COMMENT_REGEX = "(//+.*)";
	private static final String SEGMENT = "(argument|local|static|constant|"
			+ "this|that|pointer|temp)";
	private static final String NAME_REGEX = "([^\\d](\\w|\\_|\\d|\\.|\\:)+)";
	private static final String C_ARITHMETIC_REGEX = WHITESPACE + "(add|sub|neg|eq|"
			+ "gt|lt|and|or|not)" + WHITESPACE + COMMENT_REGEX + "?";
	private static final String C_PUSH_REGEX = WHITESPACE + "push" + WHITESPACE 
						+ SEGMENT + WHITESPACE + "(\\d+)" + WHITESPACE +
						COMMENT_REGEX + "?";
	private static final String C_POP_REGEX = WHITESPACE + "pop" + WHITESPACE 
			+ SEGMENT + WHITESPACE + "(\\d+)" + WHITESPACE + COMMENT_REGEX + "?";
	private static final String C_FUNCTION_REGEX = WHITESPACE + "function" + WHITESPACE 
			+ NAME_REGEX + WHITESPACE + "(\\d+)" + WHITESPACE + COMMENT_REGEX + "?";
	
	private BufferedReader reader;
	private String currentLine;
	
	/**
	 * Opens the input file/stream and gets ready to parse it.
	 * @param inputFile
	 */
	Parser(File inputFile) throws IOException
	{
		this.reader = new BufferedReader(new FileReader(inputFile));
		advance();
	}
	
	/**
	 * Are there more commands in the input? 
	 * @throws IOException 
	 */
	public boolean hasMoreCommands() throws IOException{
		if (currentLine == null)
		{
			reader.close();
			return false;
		}
		return true;
	}
	
	// Handle whitespaces: empty lines, comments, bad lines.
	public boolean checkEmpty() throws IOException
	{
		if (currentLine.isEmpty())
		{
			currentLine = reader.readLine();
			return true;
		}
		return false;
	}
	
	public boolean checkComment() throws IOException
	{
		if (currentLine.matches(WHITESPACE + COMMENT_REGEX))
		{
			currentLine = reader.readLine();
			return true;
		}
		return false;
	}
	/**
	 *  Reads the next command from the input and makes it the current command.
	 *  Should be called only if hasMoreCommands() is true.
		Initially there is no current command. 
	 */
	public void advance() throws IOException
	{
		currentLine = reader.readLine();	
	}
	
	/**
	 * @return Returns the type of the current VM command. 
	 */
	public VMCommand getCommandType() throws IllegalArgumentException
	{
		Pattern pattern = Pattern.compile(C_ARITHMETIC_REGEX);
		Matcher matcher = pattern.matcher(currentLine);
		if (matcher.matches())
		{
			String operation = matcher.group(1);
			return new C_Arithmetic(operation);
		}
		pattern = Pattern.compile(C_PUSH_REGEX);
		matcher = pattern.matcher(currentLine);
		if (matcher.matches())
		{
			String segment = matcher.group(1);
			int value = Integer.parseInt(matcher.group(2));
			if (value >= MAX_MEMORY_VALUE+1)
				throw new IllegalArgumentException();
			return new C_Push(segment, value);
		}
		pattern = Pattern.compile(C_POP_REGEX);
		matcher = pattern.matcher(currentLine);
		if (matcher.matches())
		{
			String segment = matcher.group(1);
			int value = Integer.parseInt(matcher.group(2));
			if (value >= MAX_MEMORY_VALUE+1)
				throw new IllegalArgumentException();
			return new C_Pop(segment, value);
		}
		pattern = Pattern.compile(C_FUNCTION_REGEX);
		matcher = pattern.matcher(currentLine);
		if (matcher.matches())
		{
			String funcName = matcher.group(1);
			int localsNum = Integer.parseInt(matcher.group(2));
			return new C_Function(funcName, localsNum);
		}
		return null;
		
	}
	
}
