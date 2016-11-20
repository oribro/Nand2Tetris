package ex8;

import java.io.PrintWriter;
import java.util.Set;

/**
 * Created by hadas on 16/11/2016.
 */
public class C_Goto extends VMCommand{
    protected String gotoLabel;

    public C_Goto(String labelName){
        gotoLabel = labelName;
    }
    @Override
    public String getFirstArg() {
        return null;
    }

    @Override
    public int getSecondArg() {
        return 0;
    }

    @Override
    public void translateToAsm(PrintWriter writer, Memory memory) {
        writer.println("@"+ gotoLabel + "\n0;JMP" );
    }
}
