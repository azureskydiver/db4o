/* PutFieldOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import jode.bytecode.Reference;
import jode.decompiler.MethodAnalyzer;

public class PutFieldOperator extends FieldOperator implements LValueExpression
{
    public PutFieldOperator(MethodAnalyzer methodanalyzer, boolean bool,
			    Reference reference) {
	super(methodanalyzer, bool, reference);
    }
    
    public boolean matches(Operator operator) {
	return (operator instanceof GetFieldOperator
		&& ((GetFieldOperator) operator).ref.equals(ref));
    }
    
    public boolean opEquals(Operator operator) {
	return (operator instanceof PutFieldOperator
		&& ((PutFieldOperator) operator).ref.equals(ref));
    }
}
