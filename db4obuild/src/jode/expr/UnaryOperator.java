/* UnaryOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.Options;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class UnaryOperator extends Operator
{
    public UnaryOperator(Type type, int i) {
	super(type, i);
	this.initOperands(1);
    }
    
    public int getPriority() {
	return 700;
    }
    
    public Expression negate() {
	if (this.getOperatorIndex() == 34) {
	    if (subExpressions != null)
		return subExpressions[0];
	    return new NopOperator(Type.tBoolean);
	}
	return super.negate();
    }
    
    public void updateSubTypes() {
	subExpressions[0].setType(Type.tSubType(type));
    }
    
    public void updateType() {
	this.updateParentType(Type.tSuperType(subExpressions[0].getType()));
    }
    
    public boolean opEquals(Operator operator) {
	return (operator instanceof UnaryOperator
		&& operator.operatorIndex == operatorIndex);
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.print(this.getOperatorString());
	if ((Options.outputStyle & 0x40) != 0)
	    tabbedprintwriter.print(" ");
	subExpressions[0].dumpExpression(tabbedprintwriter, 700);
    }
}
