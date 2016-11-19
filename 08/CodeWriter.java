import java.io.IOException;
import java.io.PrintWriter;

public class CodeWriter {
	private final static int POINTER_BASE = 3;
	private final static int TEMP_BASE = 5;
	private final static int STATIC_BASE = 16;
	
	private PrintWriter writer;
	// Variables to help us not to repeat jumps in the ASM file
	private int eqJumpCounter;
	private int gtJumpCounter;
	private int ltJumpCounter;
	/**
	 * Opens the output file/stream and gets ready to write into it. 
	 * @throws IOException
	 */
	CodeWriter(String outputFile) throws IOException
	{
		this.writer = new PrintWriter(outputFile);
		this.eqJumpCounter = 0;
		this.gtJumpCounter = 0;
		this.ltJumpCounter = 0;
	}
	
	public void writeCommand(VMCommand command)
	{
		if (command.getCommand().equals("arithmetic"))
			writeArithmetic((C_Arithmetic) command);
		if (command.getCommand().equals("push"))
			writePush((C_Push) command);
		if (command.getCommand().equals("pop"))
			writePop((C_Pop) command);
	}
	/**
	 * Writes the assembly code that is the translation of the given
	 * arithmetic command. 
	 */
	private void writeArithmetic(C_Arithmetic command)
	{
		String operation = command.getFirstArg();
		switch(operation)
		{
			case "add":
			{
				binaryOpSet();
				writer.println("M=D+M");
				break;
			}
			case "sub":
			{
				binaryOpSet();
				writer.println("M=M-D");
				break;
			}
			case "neg":
			{
				writer.println("@SP");
				writer.println("A=M-1");
				writer.println("M=-M");
				break;
			}
			case "eq":
			{
				binaryOpSet();
				writer.println("D=M-D");
				writer.println("@EQUAL" + eqJumpCounter);
				writer.println("D;JEQ");
				writer.println("@SP");
				writer.println("A=M-1");
				writer.println("M=0");
				writer.println("@DONEEQ" + eqJumpCounter);
				writer.println("0;JMP");
				writer.println("(EQUAL" +eqJumpCounter+")");
				writer.println("@SP");
				writer.println("A=M-1");
				writer.println("M=-1");
				writer.println("(DONEEQ" +eqJumpCounter+ ")");
				eqJumpCounter++;
				break;
			}
			case "gt":
			{
				binaryOpSet();
				writer.println("D=M-D");
				writer.println("@GREATER" + gtJumpCounter);
				writer.println("D;JGT");
				writer.println("@SP");
				writer.println("A=M-1");
				writer.println("M=0");
				writer.println("@DONEGT" + gtJumpCounter);
				writer.println("0;JMP");
				writer.println("(GREATER" +gtJumpCounter+")");
				writer.println("@SP");
				writer.println("A=M-1");
				writer.println("M=-1");
				writer.println("(DONEGT" +gtJumpCounter+ ")");
				gtJumpCounter++;
				break;
			}
			case "lt":
			{
				binaryOpSet();
				writer.println("D=M-D");
				writer.println("@LESS" + ltJumpCounter);
				writer.println("D;JLT");
				writer.println("@SP");
				writer.println("A=M-1");
				writer.println("M=0");
				writer.println("@DONELT" + ltJumpCounter);
				writer.println("0;JMP");
				writer.println("(LESS" +ltJumpCounter+")");
				writer.println("@SP");
				writer.println("A=M-1");
				writer.println("M=-1");
				writer.println("(DONELT" +ltJumpCounter+ ")");
				ltJumpCounter++;
				break;
			}
			case "and":
			{
				binaryOpSet();
				writer.println("M=D&M");
				break;
			}
			case "or":
			{
				binaryOpSet();
				writer.println("M=D|M");
				break;
			}
			case "not":
			{
				writer.println("@SP");
				writer.println("A=M-1");
				writer.println("M=!M");
				break;
			}
		}
	}
	
	/**
	 * Sets the stack address to the value to change for binary op.
	 */
	private void binaryOpSet()
	{
		writer.println("@SP");
		writer.println("AM=M-1");
		writer.println("D=M");
		writer.println("A=A-1");
	}
	
	
	private void writePush(C_Push command)
	{
		String segment = command.getFirstArg();
		int value = command.getSecondArg();
		switch(segment)
		{
			case "constant":
			{
				writer.println("@"+value);
				writer.println("D=A");
				writer.println("@SP");
				writer.println("A=M");
				writer.println("M=D");
				writer.println("@SP");
				writer.println("M=M+1");
				break;
			}
			case "local":
			{
				writer.println("@LCL");
				pushTranslateCode(value);
				break;
			}
			case "argument":
			{
				writer.println("@ARG");
				pushTranslateCode(value);
				break;
			}
			case "this":
			{
				writer.println("@THIS");
				pushTranslateCode(value);
				break;
			}
			case "that":
			{
				writer.println("@THAT");
				pushTranslateCode(value);
				break;
			}
			case "temp":
			{
				writer.println("@R5");
				pushTranslateCode(value + TEMP_BASE);
				break;
			}
			case "pointer":
			{
				writer.println("@R3");
				writer.println("D=A");
				writer.println("@"+value);
				writer.println("A=A+D");
				writer.println("D=M");
				writer.println("@SP");
				writer.println("A=M");
				writer.println("M=D");
				writer.println("@SP");
				writer.println("M=M+1");
				break;
			}
			case "static":
			{
				writer.println("@16");
				pushTranslateCode(value + STATIC_BASE);
				break;
			}
		}
	}
	
	private void writePop(C_Pop command)
	{
		String segment = command.getFirstArg();
		int value = command.getSecondArg();
		switch(segment)
		{
			case "local":
			{
				writer.println("@LCL");
				popTranslateCode(value);
				break;
			}
			case "argument":
			{
				writer.println("@ARG");
				popTranslateCode(value);
				break;
			}
			case "this":
			{
				writer.println("@THIS");
				popTranslateCode(value);
				break;
			}
			case "that":
			{
				writer.println("@THAT");
				popTranslateCode(value);
				break;
			}
			case "temp":
			{
				writer.println("@R5");
				popTranslateCode(value + TEMP_BASE);
				break;
			}
			case "pointer":
			{
				writer.println("@R3");
				writer.println("D=A");
				writer.println("@"+value);
				writer.println("D=A+D");
				setValueAtAddress();
				break;
			}
			case "static":
			{
				writer.println("@16");
				popTranslateCode(value + STATIC_BASE);
				break;
			}
		}
	}
	
	private void pushTranslateCode(int value)
	{
		writer.println("D=M");
		writer.println("@"+value);
		writer.println("A=A+D");
		writer.println("D=M");
		writer.println("@SP");
		writer.println("A=M");
		writer.println("M=D");
		writer.println("@SP");
		writer.println("M=M+1");
	}
	
	
	private void popTranslateCode(int value)
	{
		writer.println("D=M");
		writer.println("@"+value);
		writer.println("D=D+A");
		setValueAtAddress();
	}
	
	/**
	 * Use R15 special register for keeping the address to pop to
	 * (like the usage of a temp variable)
	 */
	private void setValueAtAddress()
	{
		writer.println("@R15");
		writer.println("M=D");
		writer.println("@SP");
		writer.println("AM=M-1");
		writer.println("D=M");
		writer.println("@R15");
		writer.println("A=M");
		writer.println("M=D");
	}
	
	public void close()
	{
		writer.close();
	}
	
}
