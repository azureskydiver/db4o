/* IfThenElseOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.decompiler.FieldAnalyzer;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class IfThenElseOperator extends Operator
{
    public IfThenElseOperator(Type type) {
	super(type, 0);
	this.initOperands(3);
    }
    
    public int getPriority() {
	return 200;
    }
    
    public void updateSubTypes() {
	subExpressions[0].setType(Type.tBoolean);
	subExpressions[1].setType(Type.tSubType(type));
	subExpressions[2].setType(Type.tSubType(type));
    }
    
    public void updateType() {
	Type type
	    = Type.tSuperType(subExpressions[1].getType())
		  .intersection(Type.tSuperType(subExpressions[2].getType()));
	this.updateParentType(type);
    }
    
    public Expression simplify() {
	if (this.getType().isOfType(Type.tBoolean)
	    && subExpressions[1] instanceof ConstOperator
	    && subExpressions[2] instanceof ConstOperator) {
	    ConstOperator constoperator = (ConstOperator) subExpressions[1];
	    ConstOperator constoperator_0_ = (ConstOperator) subExpressions[2];
	    if (constoperator.getValue().equals(new Integer(1))
		&& constoperator_0_.getValue().equals(new Integer(0)))
		return subExpressions[0].simplify();
	    if (constoperator_0_.getValue().equals(new Integer(1))
		&& constoperator.getValue().equals(new Integer(0)))
		return subExpressions[0].negate().simplify();
	}
	if (subExpressions[0] instanceof CompareUnaryOperator
	    && (((CompareUnaryOperator) subExpressions[0]).getOperatorIndex()
		& ~0x1) == 26) {
	    CompareUnaryOperator compareunaryoperator
		= (CompareUnaryOperator) subExpressions[0];
	    int i = compareunaryoperator.getOperatorIndex() & 0x1;
	    if (subExpressions[2 - i] instanceof GetFieldOperator
		&& subExpressions[1 + i] instanceof StoreInstruction) {
		GetFieldOperator getfieldoperator
		    = (GetFieldOperator) subExpressions[2 - i];
		StoreInstruction storeinstruction
		    = (StoreInstruction) subExpressions[1 + i];
		int i_1_ = compareunaryoperator.getOperatorIndex();
		FieldAnalyzer fieldanalyzer;
		if (storeinstruction.getLValue() instanceof PutFieldOperator
		    && (fieldanalyzer
			= ((PutFieldOperator) storeinstruction.getLValue())
			      .getField()) != null
		    && fieldanalyzer.isSynthetic()
		    && storeinstruction.lvalueMatches(getfieldoperator)
		    && (compareunaryoperator.subExpressions[0]
			instanceof GetFieldOperator)
		    && storeinstruction.lvalueMatches((GetFieldOperator)
						      (compareunaryoperator
						       .subExpressions[0]))
		    && (storeinstruction.subExpressions[1]
			instanceof InvokeOperator)) {
		    InvokeOperator invokeoperator
			= (InvokeOperator) storeinstruction.subExpressions[1];
		    if (invokeoperator.isGetClass()
			&& (invokeoperator.subExpressions[0]
			    instanceof ConstOperator)
			&& invokeoperator.subExpressions[0].getType()
			       .equals(Type.tString)) {
			String string
			    = (String) ((ConstOperator)
					invokeoperator.subExpressions[0])
					   .getValue();
			if (fieldanalyzer.setClassConstant(string))
			    return (new ClassFieldOperator
				    (string.charAt(0) == '['
				     ? (Type) Type.tType(string)
				     : Type.tClass(string)));
		    }
		}
	    }
	}
	return super.simplify();
    }
    
    public boolean opEquals(Operator operator) {
	return operator instanceof IfThenElseOperator;
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	subExpressions[0].dumpExpression(tabbedprintwriter, 201);
	tabbedprintwriter.breakOp();
	tabbedprintwriter.print(" ? ");
	int i = 0;
	if (!subExpressions[1].getType().getHint()
		 .isOfType(subExpressions[2].getType())) {
	    tabbedprintwriter.startOp(2, 2);
	    tabbedprintwriter.print("(");
	    tabbedprintwriter.printType(this.getType().getHint());
	    tabbedprintwriter.print(") ");
	    i = 700;
	}
	subExpressions[1].dumpExpression(tabbedprintwriter, i);
	if (i == 700)
	    tabbedprintwriter.endOp();
	tabbedprintwriter.breakOp();
	tabbedprintwriter.print(" : ");
	subExpressions[2].dumpExpression(tabbedprintwriter, 200);
    }
}
