/* NewArrayOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.ArrayType;
import jode.type.Type;

public class NewArrayOperator extends Operator
{
    String baseTypeString;
    
    public NewArrayOperator(Type type, int i) {
	super(type, 0);
	this.initOperands(i);
    }
    
    public int getDimensions() {
	return subExpressions.length;
    }
    
    public int getPriority() {
	return 900;
    }
    
    public void updateSubTypes() {
	for (int i = 0; i < subExpressions.length; i++)
	    subExpressions[i].setType(Type.tUInt);
    }
    
    public void updateType() {
	/* empty */
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	Type type = this.type.getCanonic();
	int i = 0;
	while (type instanceof ArrayType) {
	    type = ((ArrayType) type).getElementType();
	    i++;
	}
	tabbedprintwriter.print("new ");
	tabbedprintwriter.printType(type.getHint());
	for (int i_0_ = 0; i_0_ < i; i_0_++) {
	    tabbedprintwriter.breakOp();
	    tabbedprintwriter.print("[");
	    if (i_0_ < subExpressions.length)
		subExpressions[i_0_].dumpExpression(tabbedprintwriter, 0);
	    tabbedprintwriter.print("]");
	}
    }
}
