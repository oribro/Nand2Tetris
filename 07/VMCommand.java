import java.io.PrintWriter;

/**
 * This class represents a command in the vm file.
 * @author OriB
 *
 */
public abstract class VMCommand {
	protected static final int MAX_MEMORY_VALUE = 32767;
	protected static final int MIN_MEMORY_VALUE = -32768;
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
			int result, int address) throws IllegalArgumentException
	{
		// Handle the case where there result is a negative number
		if (result < 0 && result >= MIN_MEMORY_VALUE)
		{
			int temp = result;
			result = result * (-1);
			if (temp == MIN_MEMORY_VALUE)
				result--;
			writer.println("@"+Integer.toString(result));
			writer.println("D=A");
			writer.println("@"+Integer.toString(address));
			writer.println("M=-D");
			// Handle edge case of 2nd complement
			if (temp == MIN_MEMORY_VALUE)
				writer.println("M=M-1");
		}
		
		else if (result >= 0 && result <= MAX_MEMORY_VALUE)
		{
			writer.println("@"+Integer.toString(result));
			writer.println("D=A");
			writer.println("@"+Integer.toString(address));
			writer.println("M=D");
		}

		// The output is not a 16-bit value
		else
		{
			throw new IllegalArgumentException();
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
