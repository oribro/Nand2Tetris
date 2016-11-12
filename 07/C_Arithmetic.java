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
	public void translateToAsm(PrintWriter writer, Memory memory) {
		int val1;
		int val2;
		int result;
		int address = memory.getSP() + memory.getSPBase();
		switch(operation)
		{
			case "add":
			{
				val1 = memory.pop();
				val2 = memory.pop();
				result = val1 + val2;
				address -= 2;
				break;
			}
			case "sub":
			{
				val1 = memory.pop();
				val2 = memory.pop();
				result = val2 - val1;
				address -= 2;
				break;
			}
			case "neg":
			{
				val1 = memory.pop();
				result = val1 * (-1);
				address -= 1;
				break;
			}
			case "eq":
			{
				val1 = memory.pop();
				val2 = memory.pop();
				if (val1 == val2)
					result = -1;
				else
					result = 0;
				address -= 2;
				break;
			}
			case "gt":
			{
				val1 = memory.pop();
				val2 = memory.pop();
				if (val1 < val2)
					result = -1;
				else
					result = 0;
				address -= 2;
				break;
			}
			case "lt":
			{
				val1 = memory.pop();
				val2 = memory.pop();
				if (val1 > val2)
					result = -1;
				else
					result = 0;
				address -= 2;
				break;
			}
			case "and":
			{
				val1 = memory.pop();
				val2 = memory.pop();
				result = val2 & val1;
				address -= 2;
				break;
			}
			case "or":
			{
				val1 = memory.pop();
				val2 = memory.pop();
				result = val2 | val1;
				address -= 2;
				break;
			}
			case "not":
			{
				val1 = memory.pop();
				result = ~val1;
				address -= 1;
				break;
			}
			default: result = 0;
		}
		writeMemory(writer, memory, result, address);
		memory.push(result);
		writeSP(writer, memory);
	}
}
