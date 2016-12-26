import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 * The Assembler module manages the flow of the program, combining the
 * different modules.
 * @author OriB
 *
 */
public class Assembler {
	
	private static final String NAME_DELIMITER = ".";
	private static final String IN_SUFFIX = "asm";
	private static final String OUT_SUFFIX = "hack";
	
	/**
	 * The core of the program. Runs the assembler
	 * @param fileName
	 */
	private static void assemble(String fileName)
	{
		String outFileName = fileName.substring(0, fileName.lastIndexOf(NAME_DELIMITER))
				+ NAME_DELIMITER + OUT_SUFFIX;
		// Initialize tables for translation.
		Instruction.initializeTranslationTables();
		// Initialize symbol table.
		SymbolTable symbolTable = new SymbolTable();
		
		try
		{
			// Count only lines which include instructions.
			int lineNum = -1;
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			PrintWriter writer = new PrintWriter(outFileName);
			String currentLine = reader.readLine();
			Instruction currentInstruction;
			String translatedInstruction;
			// First scan of the program
			while (currentLine != null) 
			{
				// Handle whitespaces: empty lines, comments, bad lines.
				if (currentLine.isEmpty() || currentLine.matches("\\s*"))
				{
					currentLine = reader.readLine();
					continue;
				}
				if (Parser.isComment(currentLine))
				{
					currentLine = reader.readLine();
					continue;
				}
				// Not whitespace
				// Check if current "instruction" is a label
				String label = Parser.parseLabel(currentLine);
				if (label != null)
				{
					symbolTable.addLabel(label, lineNum + 1);
					currentLine = reader.readLine();
					continue;
				}
				// The line is a C or A instruction
				lineNum++;
				currentLine = reader.readLine();
			}
			reader.close();
			reader = new BufferedReader(new FileReader(fileName));
			currentLine = reader.readLine();
			// Second scan of the program
			while (currentLine != null) 
			{
				// Handle whitespaces: empty lines, comments, bad lines.
				if (currentLine.isEmpty())
				{
					currentLine = reader.readLine();
					continue;
				}
				
				if (Parser.isComment(currentLine))
				{
					currentLine = reader.readLine();
					continue;
				}
				currentInstruction = Parser.parseLine(currentLine, symbolTable);
				// Bad line
				if (currentInstruction == null)
				{
					currentLine = reader.readLine();
					continue;
				}
				// All good, write the binary code to the output file.
				translatedInstruction = currentInstruction.translateToBinary();
				writer.println(translatedInstruction);
				currentLine = reader.readLine();
			}
			reader.close();
			writer.close();
		} catch (Exception e){
			System.err.println("IO Exception");
			return;
		}
			
		
	}
	
	/**
	 * Checks if the file is asm file and performs the assembling
	 */
	private static void checkAndAssemble(File inputFile)
	{
		String fileName = inputFile.getAbsolutePath();
		String fileSuffix = fileName.substring(
				fileName.lastIndexOf(NAME_DELIMITER)+1, fileName.length());
		if (fileSuffix.equals(IN_SUFFIX))
			assemble(fileName);
	}
	
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		File inputFile = new File(args[0]);
		if (inputFile.isFile())
		{
			checkAndAssemble(inputFile);
		}
		if (inputFile.isDirectory())
		{
			File[] dirFiles = inputFile.listFiles();
			for (File file : dirFiles)
			{
				checkAndAssemble(file);
			}
		}

	}

}
