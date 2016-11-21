package ex8;

/**
 * Manage the RAM of the computer by dividing it to the specified
 * sub sections.
 * @author OriB
 */
public class Memory {
	private static final int POINTER_SIZE = 2;
	private static final int TEMP_SIZE = 8;
	private static final int STATIC_SIZE = 240;
	private static final int STACK_SIZE = 1792;
	private static final int HEAP_SIZE = 14436;
	private static final int TEMP_BASE = 5;
	private static final int TEMP_TOP = 12;
	private static final int POINTER_BASE = 3;
	private static final int STATIC_BASE = 16;
	private static final int HEAP_BASE = 2048;
	private static final int HEAP_TOP = 16483;
	private static final int SP_BASE = 256;
	private static final int LOCAL_BASE = 300;
	private static final int ARGUMENT_BASE = 400;
	private static final int THIS_BASE = 3000;
	private static final int THAT_BASE = 3010;
	private static final int THIS_INDEX = 0;
	private static final int THAT_INDEX = 1;
	
	private int[] stack;
	private int[] heap;
	private int[] staticVars;
	private int[] tempVars;
	private int[] pointerSeg;
	
	private int sp = 0;
	
	Memory()
	{
		stack = new int[STACK_SIZE];
		heap = new int[HEAP_SIZE];
		staticVars = new int[STATIC_SIZE];
		tempVars = new int[TEMP_SIZE];
		pointerSeg = new int[POINTER_SIZE];
	}
	
	public void push(int num)
	{
		stack[sp] = num;
		sp++;
	}
	public int pop()
	{
		sp--;
		return stack[sp];
	}
	public int getSP(){
		return sp;
	}
	
	public int getSPBase(){
		return SP_BASE;
	}
	/**
	 * Get the value at the specified address in the memory.
	 * Used for push operations.
	 */
	public int getValueAt(int address){
		int value = 0;
		if (address >= STATIC_BASE && address <= SP_BASE-1)
		{
			value = staticVars[address-STATIC_BASE];
		}
		else if (address == POINTER_BASE || address == POINTER_BASE+1)
		{
			value = pointerSeg[address-POINTER_BASE];
		}
		else if (address >= TEMP_BASE && address <= TEMP_TOP)
		{
			value = tempVars[address-TEMP_BASE];
		}
		else if (address >= SP_BASE && address <= HEAP_BASE-1)
		{
			value = stack[address-SP_BASE];
		}
		else
		{
			value = heap[address-HEAP_BASE];
		}
		return value;
	}
	
	/**
	 * Set the value at the specified address in the memory.
	 * Used for pop operations.
	 */
	public void setValueAt(int address, int value){
		if (address >= STATIC_BASE && address <= SP_BASE-1)
		{
			staticVars[address-STATIC_BASE] = value;
		}
		else if (address == POINTER_BASE || address == POINTER_BASE+1)
		{
			pointerSeg[address-POINTER_BASE] = value;
		}
		else if (address >= TEMP_BASE && address <= TEMP_TOP)
		{
			tempVars[address-TEMP_BASE] = value;
		}
		else if (address >= SP_BASE && address <= HEAP_BASE-1)
		{
			stack[address-SP_BASE] = value;
		}
		else
		{
			heap[address-HEAP_BASE] = value;
		}
	}
	
	public int getLocalBase() {
		return LOCAL_BASE;
	}

	public int getArgumentBase() {
		return ARGUMENT_BASE;
	}

	public int getThisBase() {
		// Check if the pointer is set to some address to change the base to.
		if (pointerSeg[THIS_INDEX] != 0)
			return pointerSeg[THIS_INDEX];
		return THIS_BASE;
	}

	public int getThatBase() {
		// Check if the pointer is set to some address to change the base to.
		if (pointerSeg[THAT_INDEX] != 0)
			return pointerSeg[THAT_INDEX];
		return THAT_BASE;
	}
	public int getTempBase() {
		return TEMP_BASE;
	}
	public int getStaticBase() {
		return STATIC_BASE;
	}

	public int getPointerBase() {
		// TODO Auto-generated method stub
		return POINTER_BASE;
	}
	
	
}
