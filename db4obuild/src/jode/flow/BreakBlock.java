/* BreakBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;

public class BreakBlock extends StructuredBlock
{
    StructuredBlock breaksBlock;
    String label;
    
    public BreakBlock(BreakableBlock breakableblock, boolean bool) {
	breaksBlock = (StructuredBlock) breakableblock;
	breakableblock.setBreaked();
	if (bool)
	    label = breakableblock.getLabel();
	else
	    label = null;
    }
    
    public void checkConsistent() {
	super.checkConsistent();
	for (StructuredBlock structuredblock = outer;
	     structuredblock != breaksBlock;
	     structuredblock = structuredblock.outer) {
	    if (structuredblock == null)
		throw new RuntimeException("Inconsistency");
	}
    }
    
    public boolean isEmpty() {
	return true;
    }
    
    public StructuredBlock getNextBlock() {
	return breaksBlock.getNextBlock();
    }
    
    public FlowBlock getNextFlowBlock() {
	return breaksBlock.getNextFlowBlock();
    }
    
    public VariableStack mapStackToLocal(VariableStack variablestack) {
	((BreakableBlock) breaksBlock).mergeBreakedStack(variablestack);
	return null;
    }
    
    public boolean needsBraces() {
	return false;
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter
	    .println("break" + (label == null ? "" : " " + label) + ";");
    }
    
    public boolean jumpMayBeChanged() {
	return true;
    }
}
