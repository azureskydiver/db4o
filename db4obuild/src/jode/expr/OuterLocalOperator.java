/* OuterLocalOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.GlobalOptions;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;

public class OuterLocalOperator extends Operator
{
    LocalInfo local;
    
    public OuterLocalOperator(LocalInfo localinfo) {
	super(localinfo.getType());
	local = localinfo;
	this.initOperands(0);
    }
    
    public boolean isConstant() {
	return true;
    }
    
    public int getPriority() {
	return 1000;
    }
    
    public LocalInfo getLocalInfo() {
	return local.getLocalInfo();
    }
    
    public void updateSubTypes() {
	if ((GlobalOptions.debuggingFlags & 0x4) != 0)
	    GlobalOptions.err.println("setType of " + local.getName() + ": "
				      + local.getType());
	local.setType(type);
    }
    
    public void updateType() {
	/* empty */
    }
    
    public boolean opEquals(Operator operator) {
	return (operator instanceof OuterLocalOperator
		&& (((OuterLocalOperator) operator).local.getSlot()
		    == local.getSlot()));
    }
    
    public Expression simplify() {
	return super.simplify();
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.print(local.getName());
    }
}
