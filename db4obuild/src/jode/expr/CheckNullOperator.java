/* CheckNullOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;
import java.util.Collection;

import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class CheckNullOperator extends Operator
{
    LocalInfo local;
    
    public CheckNullOperator(Type type, LocalInfo localinfo) {
	super(type, 0);
	local = localinfo;
	this.initOperands(1);
    }
    
    public int getPriority() {
	return 200;
    }
    
    public void updateSubTypes() {
	local.setType(type);
	subExpressions[0].setType(Type.tSubType(type));
    }
    
    public void updateType() {
	Type type = Type.tSuperType(subExpressions[0].getType())
			.intersection(this.type);
	local.setType(type);
	this.updateParentType(type);
    }
    
    public void removeLocal() {
	local.remove();
    }
    
    public void fillInGenSet(Collection collection, Collection collection_0_) {
	if (collection_0_ != null)
	    collection_0_.add(local);
	super.fillInGenSet(collection, collection_0_);
    }
    
    public void fillDeclarables(Collection collection) {
	collection.add(local);
	super.fillDeclarables(collection);
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.print("(" + local.getName() + " = ");
	subExpressions[0].dumpExpression(tabbedprintwriter, 0);
	tabbedprintwriter
	    .print(").getClass() != null ? " + local.getName() + " : null");
    }
    
    public boolean opEquals(Operator operator) {
	return operator instanceof CheckNullOperator;
    }
}
