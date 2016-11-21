package ex8;


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
	public String getCommand() {
		// TODO Auto-generated method stub
		return "push";
	}

}
