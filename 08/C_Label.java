

/**
 * Created by hadas on 16/11/2016.
 */
public class C_Label extends VMCommand{
	private String funcName;
    private String labelName;
    public C_Label(String funcName, String labelName){
        this.labelName = labelName;
        this.funcName = funcName;
    }
    public String getFirstArg() {
        return labelName;
    }
    public int getSecondArg() {
        return UNSUPPORTED_OPERATION;
    }
    public String getFuncName() {
        return funcName;
    }
 
	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "label";
	}
}