/* CompareToIntOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class CompareToIntOperator extends Operator
{
    boolean allowsNaN;
    boolean greaterOnNaN;
    Type compareType;
    
    public CompareToIntOperator(Type type, boolean bool) {
	super(Type.tInt, 0);
	compareType = type;
	allowsNaN = type == Type.tFloat || type == Type.tDouble;
	greaterOnNaN = bool;
	this.initOperands(2);
    }
    
    public int getPriority() {
	return 499;
    }
    
    public void updateSubTypes() {
	subExpressions[0].setType(Type.tSubType(compareType));
	subExpressions[1].setType(Type.tSubType(compareType));
    }
    
    public void updateType() {
	/* empty */
    }
    
    public boolean opEquals(Operator operator) {
	return operator instanceof CompareToIntOperator;
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	subExpressions[0].dumpExpression(tabbedprintwriter, 550);
	tabbedprintwriter.breakOp();
	tabbedprintwriter.print(" <=>");
	if (allowsNaN)
	    tabbedprintwriter.print(greaterOnNaN ? "g" : "l");
	tabbedprintwriter.print(" ");
	subExpressions[1].dumpExpression(tabbedprintwriter, 551);
    }
}
