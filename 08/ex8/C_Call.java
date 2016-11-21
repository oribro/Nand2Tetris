package ex8;

public class C_Call extends VMCommand{

	private String name;
	private int argsNum;
	private int VMFileLineNum;

	C_Call(String name, int argsNum, int VMFileLineNum)
	{
		this.name = name;
		this.argsNum = argsNum;
		this.VMFileLineNum = VMFileLineNum;
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

	public int getVMFileLineNum() {
		// TODO Auto-generated method stub
		return this.VMFileLineNum;
	}

}
