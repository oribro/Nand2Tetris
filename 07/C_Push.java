import java.io.PrintWriter;

public class C_Push extends VMCommand{
	
	private String segment;
	private int value;
	
	C_Push(String segment, int value)
	{
		this.segment = segment;
		this.value = value;
	}
	
	@Override
	public String getFirstArg() {
		// TODO Auto-generated method stub
		return this.segment;
	}

	@Override
	public int getSecondArg() {
		// TODO Auto-generated method stub
		return this.value;
	}

	@Override
	public void translateToAsm(PrintWriter writer) {
		// TODO Auto-generated method stub
		int sp = Main.stack.getSP();
		writer.println("@"+value);
		writer.println("D=A");
		writer.println("@"+sp);
		writer.println("M=D");
		Main.stack.push(value);
		sp = Main.stack.getSP();
		writer.println("@"+sp);
		writer.println("D=A");
		writer.println("@SP");
		writer.println("M=D");
	}

}
