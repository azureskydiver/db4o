/* GetFieldOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import jode.bytecode.Reference;
import jode.decompiler.FieldAnalyzer;
import jode.decompiler.MethodAnalyzer;

public class GetFieldOperator extends FieldOperator
{
    public GetFieldOperator(MethodAnalyzer methodanalyzer, boolean bool,
			    Reference reference) {
	super(methodanalyzer, bool, reference);
    }
    
    public Expression simplify() {
	if (!staticFlag) {
	    subExpressions[0] = subExpressions[0].simplify();
	    subExpressions[0].parent = this;
	    if (subExpressions[0] instanceof ThisOperator) {
		FieldAnalyzer fieldanalyzer = this.getField();
		if (fieldanalyzer != null && fieldanalyzer.isSynthetic()) {
		    Expression expression = fieldanalyzer.getConstant();
		    if (expression instanceof ThisOperator
			|| expression instanceof OuterLocalOperator)
			return expression;
		}
	    }
	}
	return this;
    }
    
    public boolean opEquals(Operator operator) {
	return (operator instanceof GetFieldOperator
		&& ((GetFieldOperator) operator).ref.equals(ref));
    }
}
