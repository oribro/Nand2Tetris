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
	
	/**
	 * Writes the assembly code that effects the VM initialization,
	 * also called bootstrap code. This code must be placed at the beginning
	 * of the output file.
	 */
	public void writeInit()
	{
		writer.println("@SP");
		writer.println("256");
		writeCall(new C_Call("Sys.init",0));
	}
	
	/**
	 * Translate the concrete command obtained from the parser.
	 * @param command
	 */
	
	public void writeCommand(VMCommand command) throws IllegalArgumentException
	{
		if (command.getCommand().equals("arithmetic"))
			writeArithmetic((C_Arithmetic) command);
		else if (command.getCommand().equals("push"))
			writePush((C_Push) command);
		else if (command.getCommand().equals("pop"))
			writePop((C_Pop) command);
		else if (command.getCommand().equals("function"))
			writeFunction((C_Function) command);
		else if (command.getCommand().equals("call"))
			writeCall((C_Call) command);
		else if (command.getCommand().equals("return"))
			writeReturn((C_Return) command);
		else if (command.getCommand().equals("label"))
			writeLabel((C_Label) command);
		else if (command.getCommand().equals("goto"))
			writeGoto((C_Goto) command);
		else if (command.getCommand().equals("if-goto"))
			writeIf((C_If) command);
		else
		{
			throw new IllegalArgumentException();
		}
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
				writer.println("D=M-D");    // if (a==b)
				writer.println("@EQUAL" + eqJumpCounter);
				writer.println("D;JEQ");
				writer.println("@SP");      // (a!=b)
				writer.println("A=M-1");
				writer.println("M=0");
				writer.println("@DONEEQ" + eqJumpCounter);
				writer.println("0;JMP");
				writer.println("(EQUAL" +eqJumpCounter+")");  // (a==b)
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
				writer.println("D=M-D");    // if (a>b)
				writer.println("@GREATER" + gtJumpCounter);
				writer.println("D;JGT");
				writer.println("@SP");    // (a<=b)
				writer.println("A=M-1");
				writer.println("M=0");
				writer.println("@DONEGT" + gtJumpCounter);
				writer.println("0;JMP");
				writer.println("(GREATER" +gtJumpCounter+")");
				writer.println("@SP");    // (a>b)
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
		writer.println("AM=M-1");  //Keep second var
		writer.println("D=M");
		writer.println("A=A-1");   //Go to the first var
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
				pushUpdater();
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
				writer.println("D=A");   //Decide if pointer is that or this
				writer.println("@"+value);
				writer.println("A=A+D");
				writer.println("D=M");
				pushUpdater();
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
	
	private void writeFunction(C_Function command)
	{
		/**
		 * NEED TO DECLARE A LABEL (f) HERE
		 */
		int argsNum = command.getSecondArg();
		for (int i=1; i <= argsNum; i++)
			writePush(new C_Push("constant", 0));
	}
	
	private void writeCall(C_Call command)
	{
		int argsNum = command.getSecondArg();
		
		writer.println("@LCL");  // Push calling function's relevant sections
		writer.println("D=M");
		pushUpdater();
		writer.println("@ARG");
		writer.println("D=M");
		pushUpdater();
		writer.println("@THIS");
		writer.println("D=M");
		pushUpdater();
		writer.println("@THAT");
		writer.println("D=M");
		pushUpdater();
		writer.println("@5");     // ARG = SP-5-argsNum
		writer.println("D=A");
		writer.println("@SP");
		writer.println("D=M-D");
		writer.println("@"+argsNum);
		writer.println("D=D-A");
		writer.println("@ARG");
		writer.println("M=D");
		writer.println("@SP");   // LCL = SP
		writer.println("D=M");
		writer.println("@LCL");
		writer.println("M=D");
		
	}
	
	private void writeReturn(C_Return command)
	{
		//Use R13 for FRAME temp variable (switch between functions' LCL)
		//Use R14 for RET - return address temp variable
		writer.println("@LCL"); // FRAME=LCL
		writer.println("D=M");
		writer.println("@R13");
		writer.println("M=D");
		writer.println("@5");  // RET = *(FRAME-5)
		writer.println("D=A");
		writer.println("@R13");
		writer.println("D=M-D");
		writer.println("A=D");
		writer.println("D=M");
		writer.println("@R14");
		writer.println("M=D");
		writePop(new C_Pop("argument", 0)); // *ARG=pop()
		writer.println("@R2");      //SP=ARG+1
		writer.println("D=M");
		writer.println("@SP"); 
		writer.println("M=D+1");
		for (int i = 4; i >= 1; i--)
		{
			writer.println("@R13");     //Restore sections of the caller function
			writer.println("AM=M-1");
			writer.println("D=M");
			writer.println("@R"+i);
			writer.println("M=D");
		}
		
		/**
		 *  NEED TO ADD "goto RET" HERE
		 */
		
	}
	
	private void writeLabel(C_Label command)
	{
		String functionName = command.getFuncName();
		String labelName = command.getFirstArg();
		writer.println("(" + functionName + "$" + labelName + ")");
	}
	
	private void writeGoto(C_Goto command)
	{
		String functionName = command.getFuncName();
		String labelName = command.getFirstArg();
		writer.println("@"+ functionName + "$" + labelName + "\n0;JMP");
	}
	private void writeIf(C_If command) 
	{
		String functionName = command.getFuncName();
		String labelName = command.getFirstArg();
		writer.println("@SP\nAM=M-1\nD=M\n@" + functionName + "$" + labelName + "\nD;JNE");
	}
	private void pushTranslateCode(int value)
	{
		writer.println("D=M");
		writer.println("@"+value);
		writer.println("A=A+D");
		writer.println("D=M");
		pushUpdater();
	}
	
	private void pushUpdater()
	{
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
