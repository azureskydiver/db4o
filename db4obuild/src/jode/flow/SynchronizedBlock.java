/* SynchronizedBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;
import java.util.Set;

import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;
import jode.util.SimpleSet;

public class SynchronizedBlock extends StructuredBlock
{
    Expression object;
    LocalInfo local;
    boolean isEntered;
    StructuredBlock bodyBlock;
    
    public SynchronizedBlock(LocalInfo localinfo) {
	local = localinfo;
    }
    
    public void setBodyBlock(StructuredBlock structuredblock) {
	bodyBlock = structuredblock;
	structuredblock.outer = this;
	structuredblock.setFlowBlock(flowBlock);
    }
    
    public StructuredBlock[] getSubBlocks() {
	return new StructuredBlock[] { bodyBlock };
    }
    
    public boolean replaceSubBlock(StructuredBlock structuredblock,
				   StructuredBlock structuredblock_0_) {
	if (bodyBlock == structuredblock)
	    bodyBlock = structuredblock_0_;
	else
	    return false;
	return true;
    }
    
    public Set getDeclarables() {
	SimpleSet simpleset = new SimpleSet();
	if (object != null)
	    object.fillDeclarables(simpleset);
	else
	    simpleset.add(local);
	return simpleset;
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	if (!isEntered)
	    tabbedprintwriter.println("MISSING MONITORENTER");
	tabbedprintwriter.print("synchronized (");
	if (object != null)
	    object.dumpExpression(0, tabbedprintwriter);
	else
	    tabbedprintwriter.print(local.getName());
	tabbedprintwriter.print(")");
	tabbedprintwriter.openBrace();
	tabbedprintwriter.tab();
	bodyBlock.dumpSource(tabbedprintwriter);
	tabbedprintwriter.untab();
	tabbedprintwriter.closeBrace();
    }
    
    public void simplify() {
	if (object != null)
	    object = object.simplify();
	super.simplify();
    }
    
    public boolean doTransformations() {
	StructuredBlock structuredblock = flowBlock.lastModified;
	return (!isEntered && CompleteSynchronized.enter(this, structuredblock)
		|| (isEntered && object == null
		    && CompleteSynchronized.combineObject(this,
							  structuredblock)));
    }
}
