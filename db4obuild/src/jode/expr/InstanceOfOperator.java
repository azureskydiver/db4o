/* InstanceOfOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class InstanceOfOperator extends Operator
{
    Type instanceType;
    
    public InstanceOfOperator(Type type) {
	super(Type.tBoolean, 0);
	instanceType = type;
	this.initOperands(1);
    }
    
    public int getPriority() {
	return 550;
    }
    
    public void updateSubTypes() {
	subExpressions[0].setType(Type.tUObject);
    }
    
    public void updateType() {
	/* empty */
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	Type type = instanceType.getCastHelper(subExpressions[0].getType());
	if (type != null) {
	    tabbedprintwriter.startOp(2, 2);
	    tabbedprintwriter.print("(");
	    tabbedprintwriter.printType(type);
	    tabbedprintwriter.print(") ");
	    tabbedprintwriter.breakOp();
	    subExpressions[0].dumpExpression(tabbedprintwriter, 700);
	    tabbedprintwriter.endOp();
	} else
	    subExpressions[0].dumpExpression(tabbedprintwriter, 550);
	tabbedprintwriter.breakOp();
	tabbedprintwriter.print(" instanceof ");
	tabbedprintwriter.printType(instanceType);
    }
}
