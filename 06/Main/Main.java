import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

public class Main {
	private static final String nameDelimiter = ".";
	private static final String outSuffix = "hack";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inFileName = args[0].substring(0, args[0].lastIndexOf(nameDelimiter));
		String outFileName = inFileName + nameDelimiter + outSuffix;
		Instruction.initializeTranslationTables();
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));
			PrintWriter writer = new PrintWriter(outFileName);
			String currentLine = reader.readLine();
			Instruction currentInstruction;
			String translatedInstruction;
			while (currentLine != null) 
			{
				// Handle whitespaces: empty lines, comments, bad lines.
				if (currentLine.isEmpty())
				{
					currentLine = reader.readLine();
					continue;
				}
				currentInstruction = Parser.parseLine(currentLine);
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
			
		
		//System.out.println(parseLine("    D = M ; JLT    ").translateToBinary());
	}

}
