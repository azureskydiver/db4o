/* LocalLoadOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import jode.decompiler.LocalInfo;
import jode.decompiler.MethodAnalyzer;
import jode.type.Type;

public class LocalLoadOperator extends LocalVarOperator
{
    MethodAnalyzer methodAnalyzer;
    
    public LocalLoadOperator(Type type, MethodAnalyzer methodanalyzer,
			     LocalInfo localinfo) {
	super(type, localinfo);
	methodAnalyzer = methodanalyzer;
    }
    
    public boolean isRead() {
	return true;
    }
    
    public boolean isWrite() {
	return false;
    }
    
    public boolean isConstant() {
	return false;
    }
    
    public void setMethodAnalyzer(MethodAnalyzer methodanalyzer) {
	methodAnalyzer = methodanalyzer;
    }
    
    public boolean opEquals(Operator operator) {
	return (operator instanceof LocalLoadOperator
		&& (((LocalLoadOperator) operator).local.getSlot()
		    == local.getSlot()));
    }
    
    public Expression simplify() {
	if (local.getExpression() != null)
	    return local.getExpression().simplify();
	return super.simplify();
    }
}
