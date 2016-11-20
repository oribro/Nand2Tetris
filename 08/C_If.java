

/**
 * Created by hadas on 16/11/2016.
 */
public class C_If extends C_Goto {
    public C_If(String funcName, String labelName) {
        super(funcName, labelName);
    }
    
	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "if-goto";
	}
    
}
