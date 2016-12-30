import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;


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
			writer.writeCommand(command, reader);
			reader.advance();
		}
		return writer;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			File inputFile = new File(args[0]);
			CodeWriter asmWriter = null;
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
					asmWriter = new CodeWriter(outFileName);
					translate(inputFile, asmWriter);
					asmWriter.close();
				}
			}
			if (inputFile.isDirectory())
			{
	
				String outFileName = inputFile.getAbsolutePath() + "/" +
						inputFile.getName()	+ NAME_DELIMITER + OUT_SUFFIX;
				asmWriter = new CodeWriter(outFileName);
				PrintWriter vmFileWriter =  new PrintWriter(
						inputFile.getAbsolutePath() +"/"+ "AllVMFiles.vm");
				File[] dirFiles = inputFile.listFiles();
				for (File file : dirFiles)
				{
					String fileName = file.getName();
					String fileSuffix = fileName.substring(
							fileName.lastIndexOf(NAME_DELIMITER)+1, fileName.length());
					if (!fileName.equals("AllVMFiles.vm") && fileSuffix.equals(IN_SUFFIX))
					{
						// We need bootstrap
						/*if (fileName.equals("Sys.vm"))
							asmWriter.writeInit();*/
						Parser reader = new Parser(file);
						reader.vmFilesProcessing(vmFileWriter);
						reader.close();
					}
				}
				File vmFile = new File(inputFile.getAbsolutePath() +
						"/"+ "AllVMFiles.vm");
				vmFileWriter.close();
				asmWriter = translate(vmFile, asmWriter);

				if (vmFile.isFile()) {
					try {
						Files.deleteIfExists(FileSystems.getDefault().getPath(
								vmFile.getAbsolutePath()));
					} catch (Exception e) {
						throw new IOException();
					}
				}

			}

			asmWriter.close();
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