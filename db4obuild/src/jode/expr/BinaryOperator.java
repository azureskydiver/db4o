/* BinaryOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class BinaryOperator extends Operator
{
    public BinaryOperator(Type type, int i) {
	super(type, i);
	this.initOperands(2);
    }
    
    public int getPriority() {
	switch (operatorIndex) {
	case 1:
	case 2:
	    return 610;
	case 3:
	case 4:
	case 5:
	    return 650;
	case 6:
	case 7:
	case 8:
	    return 600;
	case 9:
	    return 450;
	case 10:
	    return 410;
	case 11:
	    return 420;
	case 12:
	case 13:
	case 14:
	case 15:
	case 16:
	case 17:
	case 18:
	case 19:
	case 20:
	case 21:
	case 22:
	case 23:
	    return 100;
	case 33:
	    return 310;
	case 32:
	    return 350;
	default:
	    throw new RuntimeException("Illegal operator");
	}
    }
    
    public void updateSubTypes() {
	subExpressions[0].setType(Type.tSubType(type));
	subExpressions[1].setType(Type.tSubType(type));
    }
    
    public void updateType() {
	Type type = Type.tSuperType(subExpressions[0].getType());
	Type type_0_ = Type.tSuperType(subExpressions[1].getType());
	subExpressions[0].setType(Type.tSubType(type_0_));
	subExpressions[1].setType(Type.tSubType(type));
	this.updateParentType(type.intersection(type_0_));
    }
    
    public Expression negate() {
	if (this.getOperatorIndex() == 32 || this.getOperatorIndex() == 33) {
	    this.setOperatorIndex(this.getOperatorIndex() ^ 0x1);
	    for (int i = 0; i < 2; i++) {
		subExpressions[i] = subExpressions[i].negate();
		subExpressions[i].parent = this;
	    }
	    return this;
	}
	return super.negate();
    }
    
    public boolean opEquals(Operator operator) {
	return (operator instanceof BinaryOperator
		&& operator.operatorIndex == operatorIndex);
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	subExpressions[0].dumpExpression(tabbedprintwriter, getPriority());
	tabbedprintwriter.breakOp();
	tabbedprintwriter.print(this.getOperatorString());
	subExpressions[1].dumpExpression(tabbedprintwriter, getPriority() + 1);
    }
}
