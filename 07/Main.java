import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

public class Main {
	private static final String NAME_DELIMITER = ".";
	private static final String IN_SUFFIX = "vm";
	private static final String OUT_SUFFIX = "asm";
	
	public static GlobalStack stack;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			stack = new GlobalStack();
			File inputFile = new File(args[0]);
			Parser reader = new Parser(inputFile);
			String fileName = inputFile.getName();
			String fileSuffix = fileName.substring(
					fileName.lastIndexOf(NAME_DELIMITER)+1, fileName.length());
			String inFileName, outFileName = null;
			if (fileSuffix.equals(IN_SUFFIX))
			{
				 inFileName = fileName.substring(0,
						fileName.lastIndexOf(NAME_DELIMITER));
				 outFileName = inputFile.getParent() + "\\" + inFileName + NAME_DELIMITER + OUT_SUFFIX;
			}
			PrintWriter writer = new PrintWriter(outFileName);
			
			while(reader.hasMoreCommands())
			{
				if (reader.checkEmpty())
					continue;
				if (reader.checkComment())
					continue;
				VMCommand command = reader.getCommandType();
				command.translateToAsm(writer);
			}
			writer.close();
		}catch (Exception e){
			System.err.println("ERROR");
			return;
		}
	}

}
