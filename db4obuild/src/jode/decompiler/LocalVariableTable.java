/* LocalVariableTable - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import jode.bytecode.LocalVariableInfo;
import jode.type.Type;

public class LocalVariableTable
{
    LocalVariableRangeList[] locals;
    
    public LocalVariableTable(int i, LocalVariableInfo[] localvariableinfos) {
	locals = new LocalVariableRangeList[i];
	for (int i_0_ = 0; i_0_ < i; i_0_++)
	    locals[i_0_] = new LocalVariableRangeList();
	for (int i_1_ = 0; i_1_ < localvariableinfos.length; i_1_++)
	    locals[localvariableinfos[i_1_].slot].addLocal
		(localvariableinfos[i_1_].start.getAddr(),
		 localvariableinfos[i_1_].end.getAddr(),
		 localvariableinfos[i_1_].name,
		 Type.tType(localvariableinfos[i_1_].type));
    }
    
    public LocalVarEntry getLocal(int i, int i_2_)
	throws ArrayIndexOutOfBoundsException {
	return locals[i].getInfo(i_2_);
    }
}
