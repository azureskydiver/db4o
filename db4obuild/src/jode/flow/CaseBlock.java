/* CaseBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.AssertError;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.ConstOperator;
import jode.type.Type;

public class CaseBlock extends StructuredBlock
{
    StructuredBlock subBlock;
    int value;
    boolean isDefault = false;
    boolean isFallThrough = false;
    boolean isLastBlock = false;
    
    public CaseBlock(int i) {
	value = i;
	subBlock = null;
    }
    
    public CaseBlock(int i, Jump jump) {
	value = i;
	subBlock = new EmptyBlock(jump);
	subBlock.outer = this;
    }
    
    public void checkConsistent() {
	if (!(outer instanceof SwitchBlock))
	    throw new AssertError("Inconsistency");
	super.checkConsistent();
    }
    
    public boolean replaceSubBlock(StructuredBlock structuredblock,
				   StructuredBlock structuredblock_0_) {
	if (subBlock == structuredblock)
	    subBlock = structuredblock_0_;
	else
	    return false;
	return true;
    }
    
    protected boolean wantBraces() {
	StructuredBlock structuredblock = subBlock;
	if (structuredblock == null)
	    return false;
	for (;;) {
	    if (structuredblock.declare != null
		&& !structuredblock.declare.isEmpty())
		return true;
	    if (!(structuredblock instanceof SequentialBlock)) {
		if (structuredblock instanceof InstructionBlock
		    && ((InstructionBlock) structuredblock).isDeclaration)
		    return true;
		return false;
	    }
	    StructuredBlock[] structuredblocks
		= structuredblock.getSubBlocks();
	    if (structuredblocks[0] instanceof InstructionBlock
		&& ((InstructionBlock) structuredblocks[0]).isDeclaration)
		return true;
	    structuredblock = structuredblocks[1];
	}
    }
    
    public StructuredBlock[] getSubBlocks() {
	return (subBlock != null ? new StructuredBlock[] { subBlock }
		: new StructuredBlock[0]);
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	if (isDefault) {
	    if (isLastBlock && subBlock instanceof EmptyBlock
		&& subBlock.jump == null)
		return;
	    if (subBlock instanceof BreakBlock
		&& ((BreakBlock) subBlock).breaksBlock == this) {
		if (isFallThrough) {
		    tabbedprintwriter.tab();
		    subBlock.dumpSource(tabbedprintwriter);
		    tabbedprintwriter.untab();
		}
		return;
	    }
	    if (isFallThrough) {
		tabbedprintwriter.tab();
		tabbedprintwriter.println("/* fall through */");
		tabbedprintwriter.untab();
	    }
	    tabbedprintwriter.print("default:");
	} else {
	    if (isFallThrough) {
		tabbedprintwriter.tab();
		tabbedprintwriter.println("/* fall through */");
		tabbedprintwriter.untab();
	    }
	    ConstOperator constoperator
		= new ConstOperator(new Integer(value));
	    Type type = ((SwitchBlock) outer).getInstruction().getType();
	    constoperator.setType(type);
	    constoperator.makeInitializer(type);
	    tabbedprintwriter.print("case " + constoperator.toString() + ":");
	}
	if (subBlock != null) {
	    boolean bool = wantBraces();
	    if (bool)
		tabbedprintwriter.openBrace();
	    else
		tabbedprintwriter.println();
	    if (subBlock != null) {
		tabbedprintwriter.tab();
		subBlock.dumpSource(tabbedprintwriter);
		tabbedprintwriter.untab();
	    }
	    if (bool)
		tabbedprintwriter.closeBrace();
	} else
	    tabbedprintwriter.println();
    }
    
    public boolean jumpMayBeChanged() {
	return subBlock.jump != null || subBlock.jumpMayBeChanged();
    }
}
