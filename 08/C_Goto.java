

/**
 * Created by hadas on 16/11/2016.
 */
public class C_Goto extends C_Label{
    protected String gotoLabel;

    public C_Goto(String labelName){
        super(labelName);
    }

  
	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "goto";
	}
}
