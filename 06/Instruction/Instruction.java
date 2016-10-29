import java.util.HashMap;

/**
 * This class represents a general instruction.
 * The class and it's inherited subclasses compose the Code module
 * that translates the fields to binary, using an abstract method.
 * @author OriB
 *
 */
public abstract class Instruction {
	// Tables for translation.
	protected static HashMap<String, String> destTable;
	protected static HashMap<String, String> compTable;
	protected static HashMap<String, String> jumpTable;
	
	/**
	 * Initialize the translation tables for the C-Instruction.
	 * Will be performed by the Main upon starting the process.
	 */
	public static void initializeTranslationTables()
	{
		initializeDestTable();
		initializeCompTable();
		initializeJumpTable();
	}
	
	private static void initializeDestTable()
	{
		destTable = new HashMap<String, String>();
		destTable.put(null, "000");
		destTable.put("M", "001");
		destTable.put("D", "010");
		destTable.put("MD", "011");
		destTable.put("A", "100");
		destTable.put("AM", "101");
		destTable.put("AD", "110");
		destTable.put("AMD", "111");
	}
	private static void initializeCompTable()
	{
		// A register
		
		compTable = new HashMap<String, String>();
		compTable.put("0", "0101010");
		compTable.put("1", "0111111");
		compTable.put("-1", "0111010");
		compTable.put("D", "0001100");
		compTable.put("A", "0110000");
		compTable.put("!D", "0001101");
		compTable.put("!A", "0110001");
		compTable.put("-D", "0001111");
		compTable.put("-A", "0110011");
		compTable.put("D+1", "0011111");
		compTable.put("A+1", "0110111");
		compTable.put("D-1", "0001110");
		compTable.put("A-1", "0110010");
		compTable.put("D+A", "0000010");
		compTable.put("D-A", "0010011");
		compTable.put("A-D", "0000111");
		compTable.put("D&A", "0000000");
		compTable.put("D|A", "0010101");
		
		// M register
		
		compTable.put("M", "1110000");
		compTable.put("!M", "1110001");
		compTable.put("-M", "1110011");
		compTable.put("M+1", "1110111");
		compTable.put("M-1", "1110010");
		compTable.put("D+M", "1000010");
		compTable.put("D-M", "1010011");
		compTable.put("M-D", "1000111");
		compTable.put("D&M", "1000000");
		compTable.put("D|M", "1010101");
		
		// Shift operations
		
		compTable.put("D<<", "101011");
		compTable.put("D>>", "101001");
		compTable.put("A<<", "101010");
		compTable.put("A>>", "101000");
		compTable.put("M<<", "101110");
		compTable.put("M>>", "101100");
	}
	
	private static void initializeJumpTable()
	{
		jumpTable = new HashMap<String, String>();
		jumpTable.put(null, "000");
		jumpTable.put("JGT", "001");
		jumpTable.put("JEQ", "010");
		jumpTable.put("JGE", "011");
		jumpTable.put("JLT", "100");
		jumpTable.put("JNE", "101");
		jumpTable.put("JLE", "110");
		jumpTable.put("JMP", "111");
	}
	
	
	/**
	 * This function performs the translation from human language 
	 * to computer language.
	 * @return The translated instruction as a binary string.
	 */
	protected abstract String translateToBinary();
}
