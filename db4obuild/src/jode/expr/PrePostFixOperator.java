/* PrePostFixOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class PrePostFixOperator extends Operator
{
    boolean postfix;
    
    public PrePostFixOperator
	(Type type, int i, LValueExpression lvalueexpression, boolean bool) {
	super(type);
	postfix = bool;
	this.setOperatorIndex(i);
	this.initOperands(1);
	this.setSubExpressions(0, (Operator) lvalueexpression);
    }
    
    public int getPriority() {
	return postfix ? 800 : 700;
    }
    
    public void updateSubTypes() {
	if (!this.isVoid())
	    subExpressions[0].setType(type);
    }
    
    public void updateType() {
	if (!this.isVoid())
	    this.updateParentType(subExpressions[0].getType());
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	if (!postfix)
	    tabbedprintwriter.print(this.getOperatorString());
	tabbedprintwriter.startOp(1, 2);
	subExpressions[0].dumpExpression(tabbedprintwriter);
	tabbedprintwriter.endOp();
	if (postfix)
	    tabbedprintwriter.print(this.getOperatorString());
    }
}
