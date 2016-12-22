import java.io.IOException;
import java.io.PrintWriter;

public class CodeWriter {
	private final static int POINTER_BASE = 3;
	private final static int TEMP_BASE = 5;
	private final static int STATIC_BASE = 16;
	private static final String NAME_DELIMITER = ".";

	private PrintWriter writer;
	// Variables to help us not to repeat jumps in the ASM file
	private int eqJumpCounter;
	private int gtJumpCounter;
	private int ltJumpCounter;
	private int funcRetJumpCounter;
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
		this.funcRetJumpCounter = 0;
	}

	/**
	 * Writes the assembly code that effects the VM initialization,
	 * also called bootstrap code. This code must be placed at the beginning
	 * of the output file.
	 */
	public void writeInit()
	{
		writer.println("@256");
		writer.println("D=A");
		writer.println("@SP");
		writer.println("M=D");
		writeCall(new C_Call("Sys.init",0, 0));
	}

	/**
	 * Translate the concrete command obtained from the parser.
	 * @param command
	 */

	public void writeCommand(VMCommand command, Parser parser) throws IllegalArgumentException
	{
		if (command.getCommand().equals("arithmetic"))
			writeArithmetic((C_Arithmetic) command);
		else if (command.getCommand().equals("push"))
			writePush((C_Push) command, parser);
		else if (command.getCommand().equals("pop"))
			writePop((C_Pop) command, parser);
		else if (command.getCommand().equals("function"))
			writeFunction((C_Function) command, parser);
		else if (command.getCommand().equals("call"))
			writeCall((C_Call) command);
		else if (command.getCommand().equals("return"))
			writeReturn((C_Return) command, parser);
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
				saveRegisters(true);
				checkSigns();
				writer.println("@GREATERGT" + gtJumpCounter);
				writer.println("D;JLT");
				writer.println("@LESSGT" + gtJumpCounter);
				writer.println("D;JGT");
				writer.println("@SP");
				writer.println("AM=M+1");
				binaryOpSet();
				writer.println("D=M-D");    // if (a>b)
				writer.println("@GREATERGT" + gtJumpCounter);
				writer.println("D;JGT");
				writer.println("(LESSGT" + gtJumpCounter+ ")");
				writer.println("@SP");    // (a<=b)
				writer.println("A=M-1");
				writer.println("M=0");
				writer.println("@DONEGT" + gtJumpCounter);
				writer.println("0;JMP");
				writer.println("(GREATERGT" +gtJumpCounter+")");
				writer.println("@SP");    // (a>b)
				writer.println("A=M-1");
				writer.println("M=-1");
				writer.println("(DONEGT" +gtJumpCounter+ ")");
				gtJumpCounter++;
				break;
			}
			case "lt":
			{
				saveRegisters(false);
				checkSigns();
				writer.println("@GREATERLT" + ltJumpCounter);
				writer.println("D;JLT");
				writer.println("@LESSLT" + ltJumpCounter);
				writer.println("D;JGT");
				writer.println("@SP");
				writer.println("AM=M+1");
				binaryOpSet();
				writer.println("D=M-D");
				writer.println("@LESSLT" + ltJumpCounter);
				writer.println("D;JLT");
				writer.println("(GREATERLT" + ltJumpCounter+ ")");
				writer.println("@SP");
				writer.println("A=M-1");
				writer.println("M=0");
				writer.println("@DONELT" + ltJumpCounter);
				writer.println("0;JMP");
				writer.println("(LESSLT" +ltJumpCounter+")");
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
	private void saveRegisters(boolean flag)
	{
		writer.println("@SP");
		writer.println("AM=M-1");  //Keep second var
		saveRegister("R14", flag);
		writer.println("@SP");
		writer.println("A=M-1"); //Go to the first var
		saveRegister("R13", flag);
		
	}
	private void binaryOpSet()
	{
		writer.println("@SP");
		writer.println("AM=M-1");  //Keep second var
		writer.println("D=M");
		writer.println("A=A-1"); //Go to the first var
		
		
	}
	private void saveRegister(String registerName, boolean flag){
		int counter;
		String kind;
		if (flag){
			counter = gtJumpCounter;
			kind = "GT";
		}
		else{
			counter = ltJumpCounter;
			kind = "LT";
		}
		String labelName = "save"+ registerName + "positive" +kind + counter;
		writer.println("D=M");
		writer.println("@" + labelName);
		writer.println("D;JGT");
		writer.println("@" + registerName);
		writer.println("M=0");
		writer.println("@DONEREG" +registerName+ kind + counter);
		writer.println("0;JMP");
		writer.println("(" + labelName + ")");
		writer.println("@" + registerName);
		writer.println("M=-1");
		writer.println("(DONEREG" + registerName+ kind + counter + ")");
	}
	private void checkSigns() {
		writer.println("@R14");
		writer.println("D=M");
		writer.println("@R13");
		writer.println("D=M-D");
	}

	private void writePush(C_Push command, Parser parser)
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
				//writer.println("@R5");
				//pushTranslateCode(value + TEMP_BASE);
				writer.println("@R5");
				writer.println("D=A");  
				writer.println("@"+value);
				writer.println("A=A+D");
				writer.println("D=M");
				pushUpdater();
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
				String curFuncName = parser.getCurrFunctionName();
				writer.println("@" + curFuncName.substring(0,
						curFuncName.lastIndexOf(NAME_DELIMITER)+1) + value);
				writer.println("D=M");
				pushUpdater();
				break;
			}
		}
	}

	private void writePop(C_Pop command, Parser parser)
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
				//writer.println("@R5");
				//popTranslateCode(value + TEMP_BASE);
				writer.println("@R5");
				writer.println("D=A");
				writer.println("@"+value);
				writer.println("D=A+D");
				setValueAtAddress();
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
				String curFuncName = parser.getCurrFunctionName();
				writer.println("@" + curFuncName.substring(0,
						curFuncName.lastIndexOf(NAME_DELIMITER)+1) + value);
				writer.println("D=A");
				setValueAtAddress();
				break;
			}
		}
	}

	private void writeFunction(C_Function command, Parser parser)
	{
		// Label for function call
		writeLabel(new C_Label(command.getFirstArg() + "$BEGIN"));
		int argsNum = command.getSecondArg();
		for (int i=1; i <= argsNum; i++)
			writePush(new C_Push("constant", 0), parser);
	}

	private void writeCall(C_Call command)
	{
		//Use R14 for RET - return address temp variable
		int argsNum = command.getSecondArg();
		String retLabel = command.getFirstArg() + "$RET" + funcRetJumpCounter;
		funcRetJumpCounter++;
		//writer.println("@"+command.getVMFileLineNum()); // Push return addr
		writer.println("@" + retLabel);
		writer.println("D=A");
		writer.println("@R14");
		writer.println("M=D");
		pushUpdater();
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
		// Call the function
		writeGoto(new C_Goto(command.getFirstArg() + "$BEGIN"));
		// Declare label to return to.	
		writeLabel(new C_Label(retLabel));
	}

	private void writeReturn(C_Return command, Parser parser)
	{
		//Use R13 for FRAME temp variable (switch between functions' LCL)
		//Use R14 for RET - return address temp variable
		writer.println("@LCL"); // FRAME=LCL
		writer.println("D=M");
		writer.println("@R13");
		writer.println("M=D");
		writer.println("@5");  // RET = *(FRAME-5)
		writer.println("A=D-A");
		writer.println("D=M");
		writer.println("@R14");
		writer.println("M=D");
		writePop(new C_Pop("argument", 0), parser); // *ARG=pop()
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
		// Goto return address
		//writeGoto(new C_Goto(command.getFirstArg() + "$RET"));
		writer.println("@R14");
		writer.println("A=M");
		writer.println("0;JMP");
	}

	private void writeLabel(C_Label command)
	{
		String labelName = command.getFirstArg();
		writer.println("(" + labelName + ")");
	}

	private void writeGoto(C_Goto command)
	{
		String labelName = command.getFirstArg();
		writer.println("@"+ labelName + "\n0;JMP");
	}
	private void writeIf(C_If command)
	{
		String labelName = command.getFirstArg();
		writer.println("@SP\nAM=M-1\nD=M\n@" + labelName + "\nD;JNE");
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