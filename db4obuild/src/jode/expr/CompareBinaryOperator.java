/* CompareBinaryOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class CompareBinaryOperator extends Operator
{
    boolean allowsNaN = false;
    Type compareType;
    
    public CompareBinaryOperator(Type type, int i) {
	super(Type.tBoolean, i);
	compareType = type;
	this.initOperands(2);
    }
    
    public CompareBinaryOperator(Type type, int i, boolean bool) {
	super(Type.tBoolean, i);
	compareType = type;
	allowsNaN = bool;
	this.initOperands(2);
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
	subExpressions[1].setType(Type.tSubType(compareType));
    }
    
    public void updateType() {
	Type type = Type.tSuperType(subExpressions[0].getType());
	Type type_0_ = Type.tSuperType(subExpressions[1].getType());
	compareType = compareType.intersection(type).intersection(type_0_);
	subExpressions[0].setType(Type.tSubType(type_0_));
	subExpressions[1].setType(Type.tSubType(type));
    }
    
    public Expression negate() {
	if (!allowsNaN || this.getOperatorIndex() <= 27) {
	    this.setOperatorIndex(this.getOperatorIndex() ^ 0x1);
	    return this;
	}
	return super.negate();
    }
    
    public boolean opEquals(Operator operator) {
	return (operator instanceof CompareBinaryOperator
		&& operator.operatorIndex == operatorIndex);
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	subExpressions[0].dumpExpression(tabbedprintwriter, getPriority() + 1);
	tabbedprintwriter.breakOp();
	tabbedprintwriter.print(this.getOperatorString());
	subExpressions[1].dumpExpression(tabbedprintwriter, getPriority() + 1);
    }
}
