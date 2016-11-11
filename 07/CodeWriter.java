import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

public class CodeWriter {
	private PrintWriter writer;
	/**
	 * Opens the output file/stream and gets ready to write into it. 
	 * @throws IOException
	 */
	CodeWriter(File outputFile) throws IOException
	{
		this.writer = new PrintWriter(outputFile);
	}
	
	/**
	 * Informs the code writer that the translation of a new VM file is started. 
	 * @throws IOException
	 */
	public void setFileName(String fileName) throws IOException
	{
		writer.close();
		writer = new PrintWriter(fileName);
	}
	
	/**
	 * Writes the assembly code that is the translation of the given
	 * arithmetic command. 
	 */
	public void writeArithmetic(C_Arithmetic command)
	{
	
	}
	
	public void writePushTop(C_Push command)
	{
		
	}
	
	
	
}
