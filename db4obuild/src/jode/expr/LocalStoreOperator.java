/* LocalStoreOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import jode.decompiler.LocalInfo;
import jode.type.Type;

public class LocalStoreOperator extends LocalVarOperator
    implements LValueExpression
{
    public LocalStoreOperator(Type type, LocalInfo localinfo) {
	super(type, localinfo);
    }
    
    public boolean isRead() {
	return parent != null && parent.getOperatorIndex() != 12;
    }
    
    public boolean isWrite() {
	return true;
    }
    
    public boolean matches(Operator operator) {
	return (operator instanceof LocalLoadOperator
		&& (((LocalLoadOperator) operator).getLocalInfo().getSlot()
		    == local.getSlot()));
    }
}
