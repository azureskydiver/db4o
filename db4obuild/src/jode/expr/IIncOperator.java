/* IIncOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.AssertError;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class IIncOperator extends Operator implements CombineableOperator
{
    int value;
    
    public IIncOperator(LocalStoreOperator localstoreoperator, int i,
			int i_0_) {
	super(Type.tVoid, i_0_);
	value = i;
	this.initOperands(1);
	this.setSubExpressions(0, localstoreoperator);
    }
    
    public LValueExpression getLValue() {
	return (LValueExpression) subExpressions[0];
    }
    
    public int getValue() {
	return value;
    }
    
    public int getPriority() {
	return 100;
    }
    
    public void updateSubTypes() {
	subExpressions[0].setType(type != Type.tVoid ? type : Type.tInt);
    }
    
    public void updateType() {
	if (type != Type.tVoid)
	    this.updateParentType(subExpressions[0].getType());
    }
    
    public void makeNonVoid() {
	if (type != Type.tVoid)
	    throw new AssertError("already non void");
	type = subExpressions[0].getType();
    }
    
    public boolean lvalueMatches(Operator operator) {
	return getLValue().matches(operator);
    }
    
    public Expression simplify() {
	if (value == 1) {
	    int i = this.getOperatorIndex() == 13 ? 24 : 25;
	    return new PrePostFixOperator
		       (this.getType(), i, getLValue(), this.isVoid())
		       .simplify();
	}
	return super.simplify();
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.startOp(1, 2);
	subExpressions[0].dumpExpression(tabbedprintwriter);
	tabbedprintwriter.endOp();
	tabbedprintwriter.print(this.getOperatorString() + value);
    }
}
