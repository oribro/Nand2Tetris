import java.util.Stack;

public class GlobalStack {
	private int[] stack;
	private int sp;
	GlobalStack()
	{
		stack = new int[2048];
		sp = 256;
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
}
