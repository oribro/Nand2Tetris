import java.io.PrintWriter;

/**
 * This class represents a command in the vm file.
 * @author OriB
 *
 */
public abstract class VMCommand {
	protected static final int UNSUPPORTED_OPERATION = Integer.MAX_VALUE+1;
	abstract public String getFirstArg();
	abstract public int getSecondArg();
	/**
	 * This will perform the translation by any concrete command.
	 */
	abstract public void translateToAsm(PrintWriter writer, Memory memory);
	
	/**
	 * Write a value to the memory.
	 */
	protected void writeMemory(PrintWriter writer, Memory stack,
			int result, int address)
	{
		// Handle the case where there result is a negative number
		if (result < 0)
		{
			result = result * (-1);
			writer.println("@"+Integer.toString(result));
			writer.println("D=A");
			writer.println("@"+Integer.toString(address));
			writer.println("M=-D");
		}
		else
		{
			writer.println("@"+Integer.toString(result));
			writer.println("D=A");
			writer.println("@"+Integer.toString(address));
			writer.println("M=D");
		}
	}
	
	/**
	 * Write the position of the stack pointer to the RAM.
	 */
	protected void writeSP(PrintWriter writer, Memory stack)
	{
		writer.println("@"+Integer.toString(stack.getSP()+
				stack.getSPBase()));
		writer.println("D=A");
		writer.println("@SP");
		writer.println("M=D");
	}
}
