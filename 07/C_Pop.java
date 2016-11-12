import java.io.PrintWriter;

public class C_Pop extends VMCommand{

	private String segment;
	private int value;
	
	C_Pop(String segment, int value)
	{
		this.segment = segment;
		this.value = value;
	}
	
	@Override
	public String getFirstArg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSecondArg() {
		// TODO Auto-generated method stub
		return UNSUPPORTED_OPERATION;
	}

	@Override
	public void translateToAsm(PrintWriter writer, Memory memory) {
		// TODO Auto-generated method stub
		int address = 0;
		int valToPop = 0;
		switch(segment)
		{
			case "constant":
			{
				break;
			}
			case "local":
			{
				address = memory.getLocalBase() + value;
				break;
			}
			case "argument":
			{
				address = memory.getArgumentBase() + value;
				break;
			}
			case "this":
			{
				address = memory.getThisBase() + value;
				break;
			}
			case "that":
			{
				address = memory.getThatBase() + value;
				break;
			}
			case "temp":
			{
				address = memory.getTempBase() + value;
				break;
			}
			case "pointer":
			{
				address = memory.getPointerBase() + value;
				break;
			}
			case "static":
			{
				address = memory.getStaticBase() + value;
				break;
			}
		}
		valToPop = memory.pop();
		writeMemory(writer, memory, valToPop, address);
		memory.setValueAt(address, valToPop);
		writeSP(writer, memory);
	}


}
