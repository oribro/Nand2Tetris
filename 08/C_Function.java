

/**
 * This object represents a function to translate to asm.
 * @author OriB
 */
public class C_Function extends VMCommand {

	private String name;
	private int localNum;
	
	C_Function(String name, int localNum)
	{
		this.name = name;
		this.localNum = localNum;
	}
	
	@Override
	public String getFirstArg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSecondArg() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "function";
	}

	

}
