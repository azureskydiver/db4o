/* CompareUnaryOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class CompareUnaryOperator extends Operator
{
    boolean objectType;
    Type compareType;
    
    public CompareUnaryOperator(Type type, int i) {
	super(Type.tBoolean, i);
	compareType = type;
	objectType = type.isOfType(Type.tUObject);
	this.initOperands(1);
    }
    
    public int getPriority() {
	switch (this.getOperatorIndex()) {
	case 26:
	case 27:
	    return 500;
	case 28:
	case 29:
	case 30:
	case 31:
	    return 550;
	default:
	    throw new RuntimeException("Illegal operator");
	}
    }
    
    public Type getCompareType() {
	return compareType;
    }
    
    public void updateSubTypes() {
	subExpressions[0].setType(Type.tSubType(compareType));
    }
    
    public void updateType() {
	/* empty */
    }
    
    public Expression simplify() {
	if (subExpressions[0] instanceof CompareToIntOperator) {
	    CompareToIntOperator comparetointoperator
		= (CompareToIntOperator) subExpressions[0];
	    boolean bool = false;
	    int i = this.getOperatorIndex();
	    if (comparetointoperator.allowsNaN && this.getOperatorIndex() > 27
		&& comparetointoperator.greaterOnNaN == (i == 29 || i == 30)) {
		bool = true;
		i ^= 0x1;
	    }
	    Expression expression
		= new CompareBinaryOperator
		      (comparetointoperator.compareType, i,
		       comparetointoperator.allowsNaN)
		      .addOperand
		      (comparetointoperator.subExpressions[1])
		      .addOperand(comparetointoperator.subExpressions[0]);
	    if (bool)
		return expression.negate().simplify();
	    return expression.simplify();
	}
	if (subExpressions[0].getType().isOfType(Type.tBoolean)) {
	    if (this.getOperatorIndex() == 26)
		return subExpressions[0].negate().simplify();
	    if (this.getOperatorIndex() == 27)
		return subExpressions[0].simplify();
	}
	return super.simplify();
    }
    
    public Expression negate() {
	if (this.getType() != Type.tFloat && this.getType() != Type.tDouble
	    || this.getOperatorIndex() <= 27) {
	    this.setOperatorIndex(this.getOperatorIndex() ^ 0x1);
	    return this;
	}
	return super.negate();
    }
    
    public boolean opEquals(Operator operator) {
	return (operator instanceof CompareUnaryOperator
		&& operator.getOperatorIndex() == this.getOperatorIndex());
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	subExpressions[0].dumpExpression(tabbedprintwriter, getPriority() + 1);
	tabbedprintwriter.breakOp();
	tabbedprintwriter.print(this.getOperatorString());
	tabbedprintwriter.print(objectType ? "null" : "0");
    }
}
