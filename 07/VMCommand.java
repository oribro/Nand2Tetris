import java.io.PrintWriter;

public abstract class VMCommand {
	protected static final int UNSUPPORTED_OPERATION = Integer.MAX_VALUE+1;
	abstract public String getFirstArg();
	abstract public int getSecondArg();
	abstract public void translateToAsm(PrintWriter writer);
}
