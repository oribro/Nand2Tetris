/**
 * This class represents an A-Instruction
 * @author OriB
 *
 */
public class A_Instruction extends Instruction {
	
	private static final String Padder = "0";
	private static final int SIXTEEN_BIT = 16;
	private int value;
	
	public A_Instruction(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return this.value;
	}
	
	protected String translateToBinary()
	{
		String binaryRes = Integer.toBinaryString(this.value);
		// Padding with zeroes to get 16 bit number.
		int zeroesToAdd = SIXTEEN_BIT - binaryRes.length();
		String padding = "";
		for (int i = 1; i <= zeroesToAdd; i++)
			padding += Padder;
		return padding + binaryRes;
	}
}
