import java.util.HashMap;

/**
 * This is the module that manages the program's symbol table.
 * The symbols are: Predefined symbols, labels and variables.
 * The table will be created for the translation process and will be
 * destroyed when it's done.
 * @author OriB
 *
 */
public class SymbolTable {
	
	private static final String REGISTER_CHAR = "R";
	private static final int START_ADDRESS = 16;
	private HashMap<String, Integer> table;
	private int freeAddressCount = START_ADDRESS;
	
	/**
	 * Construct the table with predefined symbols.
	 */
	public SymbolTable()
	{
		// Add predefined symbols
		table = new HashMap<String, Integer>();
		String registerSymbol;
		for (int i=0; i<16; i++)
		{
			registerSymbol = REGISTER_CHAR + Integer.toString(i);
			table.put(registerSymbol, i);
		}
		table.put("SCREEN", 16384);
		table.put("KBD", 24576);
		table.put("SP", 0);
		table.put("LCL", 1);
		table.put("ARG", 2);
		table.put("THIS", 3);
		table.put("THAT", 4);
	}
	/**
	 * These methods will be called by the Main module when
	 * performing table operations.
	 */
	public void addLabel(String label, int address)
	{
		table.put(label, address);
	}
	
	public void addVariable(String var)
	{
		table.put(var, freeAddressCount);
		freeAddressCount++;
	}
	
	public boolean contains(String symbol)
	{
		return table.containsKey(symbol);
	}
	
	public int getAddress(String symbol)
	{
		return table.get(symbol);
	}

	
	
	
}
