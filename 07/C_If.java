package ex8;

import java.io.PrintWriter;
import java.util.Set;

/**
 * Created by hadas on 16/11/2016.
 */
public class C_If extends C_Goto {
    public C_If(String labelName) {
        super(labelName);
    }
    public String getFirstArg() {
        return "";
    }
    public int getSecondArg() {
        return 0;
    }
    public void translateToAsm(PrintWriter writer, Memory memory) {
        writer.println("@"+ (memory.getSP()+memory.getSPBase())+ "\nD=M\n@" + gotoLabel + "\nD;JGT");
    }
}
