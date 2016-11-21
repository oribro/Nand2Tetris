package ex8;
public class C_Return extends VMCommand{

    private String nameToRet;

    public C_Return(String nameToRet)
    {
        this.nameToRet = nameToRet;
    }

    @Override
    public String getFirstArg() {
        // TODO Auto-generated method stub
        return nameToRet;
    }

    @Override
    public int getSecondArg() {
        // TODO Auto-generated method stub
        return UNSUPPORTED_OPERATION;
    }

    @Override
    public String getCommand() {
        // TODO Auto-generated method stub
        return "return";
    }

}
