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
	public void translateToAsm(PrintWriter writer, Memory memory) {
		// TODO Auto-generated method stub
		int address = memory.getSP() + memory.getSPBase();
		int addrToPush = address;
		int valToPush = 0;
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
		// The value to push is the first value
		if (segment.equals("constant"))
			valToPush = value;
		// The value to push is in the section that is determined by the given value.
		else
			valToPush = memory.getValueAt(address);
		addrToPush = memory.getSP() + memory.getSPBase();
		writeMemory(writer, memory, valToPush, addrToPush);
		memory.push(valToPush);
		writeSP(writer, memory);
	}

}
