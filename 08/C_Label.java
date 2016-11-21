

/**
 * Created by hadas on 16/11/2016.
 */
public class C_Label extends VMCommand{
    private String labelName;
    public C_Label(String labelName){
        this.labelName = labelName;
    }
    public String getFirstArg() {
        return labelName;
    }
    public int getSecondArg() {
        return UNSUPPORTED_OPERATION;
    }

 
	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "label";
	}
}