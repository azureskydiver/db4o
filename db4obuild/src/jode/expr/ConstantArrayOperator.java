/* ConstantArrayOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.type.ArrayType;
import jode.type.Type;

public class ConstantArrayOperator extends Operator
{
    boolean isInitializer;
    ConstOperator empty;
    Type argType;
    
    public ConstantArrayOperator(Type type, int i) {
	super(type);
	argType = (type instanceof ArrayType
		   ? Type.tSubType(((ArrayType) type).getElementType())
		   : Type.tError);
	Number number;
	if (argType == Type.tError || argType.isOfType(Type.tUObject))
	    number = null;
	else if (argType.isOfType(Type.tBoolUInt))
	    number = new Integer(0);
	else if (argType.isOfType(Type.tLong))
	    number = new Long(0L);
	else if (argType.isOfType(Type.tFloat))
	    number = new Float(0.0F);
	else if (argType.isOfType(Type.tDouble))
	    number = new Double(0.0);
	else
	    throw new IllegalArgumentException("Illegal Type: " + argType);
	empty = new ConstOperator(number);
	empty.setType(argType);
	empty.makeInitializer(argType);
	this.initOperands(i);
	for (int i_0_ = 0; i_0_ < subExpressions.length; i_0_++)
	    this.setSubExpressions(i_0_, empty);
    }
    
    public void updateSubTypes() {
	argType = (type instanceof ArrayType
		   ? Type.tSubType(((ArrayType) type).getElementType())
		   : Type.tError);
	for (int i = 0; i < subExpressions.length; i++) {
	    if (subExpressions[i] != null)
		subExpressions[i].setType(argType);
	}
    }
    
    public void updateType() {
	/* empty */
    }
    
    public boolean setValue(int i, Expression expression) {
	if (i < 0 || i > subExpressions.length || subExpressions[i] != empty)
	    return false;
	expression.setType(argType);
	this.setType(Type.tSuperType(Type.tArray(expression.getType())));
	subExpressions[i] = expression;
	expression.parent = this;
	expression.makeInitializer(argType);
	return true;
    }
    
    public int getPriority() {
	return 200;
    }
    
    public void makeInitializer(Type type) {
	if (type.getHint().isOfType(this.getType()))
	    isInitializer = true;
    }
    
    public Expression simplify() {
	for (int i = 0; i < subExpressions.length; i++) {
	    if (subExpressions[i] != null)
		subExpressions[i] = subExpressions[i].simplify();
	}
	return this;
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	if (!isInitializer) {
	    tabbedprintwriter.print("new ");
	    tabbedprintwriter.printType(type.getHint());
	    tabbedprintwriter.breakOp();
	    tabbedprintwriter.print(" ");
	}
	tabbedprintwriter.print("{ ");
	tabbedprintwriter.startOp(0, 0);
	for (int i = 0; i < subExpressions.length; i++) {
	    if (i > 0) {
		tabbedprintwriter.print(", ");
		tabbedprintwriter.breakOp();
	    }
	    if (subExpressions[i] != null)
		subExpressions[i].dumpExpression(tabbedprintwriter, 0);
	    else
		empty.dumpExpression(tabbedprintwriter, 0);
	}
	tabbedprintwriter.endOp();
	tabbedprintwriter.print(" }");
    }
}
