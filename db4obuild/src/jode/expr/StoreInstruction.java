/* StoreInstruction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.AssertError;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class StoreInstruction extends Operator implements CombineableOperator
{
    boolean isOpAssign = false;
    
    public StoreInstruction(LValueExpression lvalueexpression) {
	super(Type.tVoid, 12);
	this.initOperands(2);
	this.setSubExpressions(0, (Operator) lvalueexpression);
    }
    
    public LValueExpression getLValue() {
	return (LValueExpression) subExpressions[0];
    }
    
    public void makeOpAssign(int i) {
	this.setOperatorIndex(i);
	if (subExpressions[1] instanceof NopOperator)
	    subExpressions[1].type = Type.tUnknown;
	isOpAssign = true;
    }
    
    public void makeNonVoid() {
	if (type != Type.tVoid)
	    throw new AssertError("already non void");
	type = subExpressions[0].getType();
    }
    
    public boolean lvalueMatches(Operator operator) {
	return getLValue().matches(operator);
    }
    
    public int getPriority() {
	return 100;
    }
    
    public void updateSubTypes() {
	if (!this.isVoid()) {
	    subExpressions[0].setType(type);
	    subExpressions[1].setType(Type.tSubType(type));
	}
    }
    
    public void updateType() {
	if (!isOpAssign) {
	    Type type = subExpressions[0].getType();
	    Type type_0_ = subExpressions[1].getType();
	    subExpressions[0].setType(Type.tSuperType(type_0_));
	    subExpressions[1].setType(Type.tSubType(type));
	}
	if (!this.isVoid())
	    this.updateParentType(subExpressions[0].getType());
    }
    
    public Expression simplify() {
	if (subExpressions[1] instanceof ConstOperator) {
	    ConstOperator constoperator = (ConstOperator) subExpressions[1];
	    if ((this.getOperatorIndex() == 13
		 || this.getOperatorIndex() == 14)
		&& constoperator.isOne(subExpressions[0].getType())) {
		int i = this.getOperatorIndex() == 13 ? 24 : 25;
		return new PrePostFixOperator
			   (this.getType(), i, getLValue(), this.isVoid())
			   .simplify();
	    }
	}
	return super.simplify();
    }
    
    public boolean opEquals(Operator operator) {
	return (operator instanceof StoreInstruction
		&& operator.operatorIndex == operatorIndex
		&& operator.isVoid() == this.isVoid());
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.startOp(1, 2);
	subExpressions[0].dumpExpression(tabbedprintwriter);
	tabbedprintwriter.endOp();
	tabbedprintwriter.breakOp();
	tabbedprintwriter.print(this.getOperatorString());
	subExpressions[1].dumpExpression(tabbedprintwriter, 100);
    }
}
