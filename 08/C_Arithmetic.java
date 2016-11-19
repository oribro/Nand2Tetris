


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
	public String getCommand() {
		// TODO Auto-generated method stub
		return "arithmetic";
	}
	
}
