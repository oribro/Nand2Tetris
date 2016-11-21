package ex8;

/**
 * This object represents a function to translate to asm.
 * @author OriB
 */
public class C_Function extends VMCommand {

    private String name;
    private int localsNum;

    C_Function(String name, int localNum)
    {
        this.name = name;
        this.localsNum = localNum;
    }

    @Override
    public String getFirstArg() {
        // TODO Auto-generated method stub
        return name;
    }

    @Override
    public int getSecondArg() {
        // TODO Auto-generated method stub
        return localsNum;
    }

    @Override
    public String getCommand() {
        // TODO Auto-generated method stub
        return "function";
    }



}
