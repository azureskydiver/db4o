/* LocalVarOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.util.Collection;

import jode.GlobalOptions;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public abstract class LocalVarOperator extends Operator
{
    LocalInfo local;
    
    public LocalVarOperator(Type type, LocalInfo localinfo) {
	super(type);
	local = localinfo;
	localinfo.setOperator(this);
	this.initOperands(0);
    }
    
    public abstract boolean isRead();
    
    public abstract boolean isWrite();
    
    public void updateSubTypes() {
	if (parent != null && (GlobalOptions.debuggingFlags & 0x4) != 0)
	    GlobalOptions.err.println("local type changed in: " + parent);
	local.setType(type);
    }
    
    public void updateType() {
	this.updateParentType(local.getType());
    }
    
    public void fillDeclarables(Collection collection) {
	collection.add(local);
	super.fillDeclarables(collection);
    }
    
    public LocalInfo getLocalInfo() {
	return local.getLocalInfo();
    }
    
    public void setLocalInfo(LocalInfo localinfo) {
	local = localinfo;
	updateType();
    }
    
    public int getPriority() {
	return 1000;
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter) {
	tabbedprintwriter.print(local.getName());
    }
}
