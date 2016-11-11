import java.io.PrintWriter;

public class C_Arithmetic extends VMCommand{
	private String operation;
	public C_Arithmetic(String operation){
		this.operation = operation;
	}
	@Override
	public String getFirstArg() {
		return operation;
	}
	@Override
	public int getSecondArg() {
		// TODO Auto-generated method stub
		return UNSUPPORTED_OPERATION;
	}
	@Override
	public void translateToAsm(PrintWriter writer) {
		switch(operation)
		{
			case "add":
			{
				int val1 = Main.stack.pop();
				int val2 = Main.stack.pop();
				int result = val1 + val2;
				int sp = Main.stack.getSP();
				writer.println("@"+result);
				writer.println("D=A");
				writer.println("@"+sp);
				writer.println("M=D");
				Main.stack.push(result);
				sp = Main.stack.getSP();
				writer.println("@"+sp);
				writer.println("D=A");
				writer.println("@SP");
				writer.println("M=D");
			}
		}
	
	}
}
