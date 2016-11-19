import java.io.File;
import java.io.IOException;

public class VMtranslator {
	
	private static final String NAME_DELIMITER = ".";
	private static final String IN_SUFFIX = "vm";
	private static final String OUT_SUFFIX = "asm";
	
	
	private static CodeWriter translate(File inputFile, CodeWriter writer)
			throws IOException{
		
		// Initialize input and output files for reading and writing.
		Parser reader = new Parser(inputFile);
		// Read and write commands from vm to asm.
		// Ignore empty lines and comments.
		while(reader.hasMoreCommands())
		{
			if (reader.checkEmpty())
				continue;
			if (reader.checkComment())
				continue;
			VMCommand command = reader.getCommandType();
			writer.writeCommand(command);
			reader.advance();
		}
		return writer;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			
			File inputFile = new File(args[0]);
			CodeWriter writer = null;
			if (inputFile.isFile())
			{
				String fileName = inputFile.getAbsolutePath();
				String fileSuffix = fileName.substring(
						fileName.lastIndexOf(NAME_DELIMITER)+1, fileName.length());
				if (fileSuffix.equals(IN_SUFFIX))
				{
					// Create an asm file for the input vm file
					String outFileName = null;
					outFileName = fileName.substring(0,
							fileName.lastIndexOf(NAME_DELIMITER)) +
							NAME_DELIMITER + OUT_SUFFIX;
					writer = new CodeWriter(outFileName);
					translate(inputFile, writer);
					writer.close();
				}
			}
			if (inputFile.isDirectory())
			{
				// Create an asm file for the directory. All vm files will be in it,
				// translated to asm.
				String outFileName = inputFile.getAbsolutePath() + "\\" +
				inputFile.getName()	+ NAME_DELIMITER + OUT_SUFFIX;
				writer = new CodeWriter(outFileName);
				File[] dirFiles = inputFile.listFiles();
				for (File file : dirFiles)
				{
					String fileName = file.getName();
					String fileSuffix = fileName.substring(
							fileName.lastIndexOf(NAME_DELIMITER)+1, fileName.length());
					if (fileSuffix.equals(IN_SUFFIX))
						writer = translate(file, writer);
				}
			}
			writer.close();
		}catch(IOException e)
		{
			System.err.println("I/O ERROR");
			return;
		}
		catch(IllegalArgumentException e)
		{
			System.err.println("Illegal argument ERROR");
			return;
		}
	}

}