

/**
 * This class represents a C-Instruction
 * @author OriB
 *
 */
public class C_Instruction extends Instruction{
	
	private static final String C_INSTRUCTION_OP_CODE = "1";
	private static final String UNUSED_BITS = "11";
	private String dest;
	private String comp;
	private String jump;
	
	/*
	 * Different constructors depends on the given instruction.
	 */
	public C_Instruction(String dest, String comp, String jump)
	{
		this.dest = dest;
		this.comp = comp;
		this.jump = jump;
	}
	
	public C_Instruction(String value1, String value2, boolean flag)
	{
		// if (flag) jump, else dest. Comp is always used.
		if (flag)
		{
			this.dest = null;
			this.comp = value1;
			this.jump = value2;
		}
		else
		{
			this.dest = value1;
			this.comp = value2;
			this.jump = null;
		}
	}
	
	public C_Instruction(String comp)
	{
		this(null, comp, null);
	}

	public String getDest() {
		return dest;
	}

	public String getComp() {
		return comp;
	}

	public String getJump() {
		return jump;
	}
	
	
	protected String translateToBinary()
	{
		// Get the binary values in O(1).
		String binDest = destTable.get(this.dest);
		String binComp = compTable.get(this.comp);
		String binJump = jumpTable.get(this.jump);
		return C_INSTRUCTION_OP_CODE + UNUSED_BITS + binComp +
				binDest + binJump;
	}

}
