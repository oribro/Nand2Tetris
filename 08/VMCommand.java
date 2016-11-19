

/**
 * This class represents a command in the vm file.
 * @author OriB
 *
 */
public abstract class VMCommand {
	protected static final int MAX_MEMORY_VALUE = 32767;
	protected static final int MIN_MEMORY_VALUE = -32768;
	protected static final int UNSUPPORTED_OPERATION = Integer.MAX_VALUE+1;
	abstract public String getFirstArg();
	abstract public int getSecondArg();
	abstract public String getCommand();
}
