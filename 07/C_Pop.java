

public class C_Pop extends VMCommand{

	private String segment;
	private int value;
	private String newName;
	
	C_Pop(String segment, int value, String newName)
	{
		this.segment = segment;
		this.value = value;
		this.newName = newName;
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
		return "pop";
	}
	public String getNewName(){
		return this.newName;
	}


}
