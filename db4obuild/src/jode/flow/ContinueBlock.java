/* ContinueBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;

public class ContinueBlock extends StructuredBlock
{
    LoopBlock continuesBlock;
    String continueLabel;
    
    public ContinueBlock(LoopBlock loopblock, boolean bool) {
	continuesBlock = loopblock;
	if (bool)
	    continueLabel = loopblock.getLabel();
	else
	    continueLabel = null;
    }
    
    public void checkConsistent() {
	super.checkConsistent();
	for (StructuredBlock structuredblock = outer;
	     structuredblock != continuesBlock;
	     structuredblock = structuredblock.outer) {
	    if (structuredblock == null)
		throw new RuntimeException("Inconsistency");
	}
    }
    
    public boolean isEmpty() {
	return true;
    }
    
    public StructuredBlock getNextBlock() {
	return continuesBlock;
    }
    
    public FlowBlock getNextFlowBlock() {
	return null;
    }
    
    public VariableStack mapStackToLocal(VariableStack variablestack) {
	continuesBlock.mergeContinueStack(variablestack);
	return null;
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.println("continue" + (continueLabel == null ? ""
						: " " + continueLabel) + ";");
    }
    
    public boolean needsBraces() {
	return false;
    }
    
    public boolean jumpMayBeChanged() {
	return true;
    }
}
