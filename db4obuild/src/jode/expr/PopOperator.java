/* PopOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class PopOperator extends Operator
{
    Type popType;
    
    public PopOperator(Type type) {
	super(Type.tVoid, 0);
	popType = type;
	this.initOperands(1);
    }
    
    public int getPriority() {
	return 0;
    }
    
    public void updateSubTypes() {
	subExpressions[0].setType(Type.tSubType(popType));
    }
    
    public void updateType() {
	/* empty */
    }
    
    public int getBreakPenalty() {
	if (subExpressions[0] instanceof Operator)
	    return ((Operator) subExpressions[0]).getBreakPenalty();
	return 0;
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	subExpressions[0].dumpExpression(tabbedprintwriter);
    }
}
