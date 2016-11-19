

public class C_Call extends VMCommand{
	
	private String name;
	private int argsNum;
	
	C_Call(String name, int argsNum)
	{
		this.name = name;
		this.argsNum = argsNum;
	}
	
	@Override
	public String getFirstArg() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public int getSecondArg() {
		// TODO Auto-generated method stub
		return argsNum;
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "call";
	}


}
