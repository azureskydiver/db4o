/* SequentialBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;
import java.util.Set;

import jode.AssertError;
import jode.GlobalOptions;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.LocalStoreOperator;
import jode.expr.StoreInstruction;
import jode.util.SimpleSet;

public class SequentialBlock extends StructuredBlock
{
    StructuredBlock[] subBlocks = new StructuredBlock[2];
    
    public void setFirst(StructuredBlock structuredblock) {
	subBlocks[0] = structuredblock;
	structuredblock.outer = this;
	structuredblock.setFlowBlock(flowBlock);
    }
    
    public void setSecond(StructuredBlock structuredblock) {
	subBlocks[1] = structuredblock;
	structuredblock.outer = this;
	structuredblock.setFlowBlock(flowBlock);
    }
    
    public void checkConsistent() {
	super.checkConsistent();
	if (subBlocks[0].jump != null
	    || subBlocks[0] instanceof SequentialBlock || jump != null)
	    throw new AssertError("Inconsistency");
    }
    
    public VariableStack mapStackToLocal(VariableStack variablestack) {
	if (variablestack == null)
	    GlobalOptions.err.println("map stack to local called with null: "
				      + this + " in " + flowBlock);
	VariableStack variablestack_0_
	    = subBlocks[0].mapStackToLocal(variablestack);
	if (variablestack_0_ != null)
	    return subBlocks[1].mapStackToLocal(variablestack_0_);
	GlobalOptions.err.println("Dead code after Block " + subBlocks[0]);
	return null;
    }
    
    public void removeOnetimeLocals() {
	StructuredBlock structuredblock = subBlocks[1];
	if (structuredblock instanceof SequentialBlock)
	    structuredblock = ((SequentialBlock) structuredblock).subBlocks[0];
	if (subBlocks[0] instanceof InstructionBlock
	    && structuredblock instanceof InstructionContainer) {
	    InstructionBlock instructionblock
		= (InstructionBlock) subBlocks[0];
	    InstructionContainer instructioncontainer
		= (InstructionContainer) structuredblock;
	    if (instructionblock.getInstruction()
		instanceof StoreInstruction) {
		StoreInstruction storeinstruction
		    = (StoreInstruction) instructionblock.getInstruction();
		if (storeinstruction.getLValue() instanceof LocalStoreOperator
		    && ((LocalStoreOperator) storeinstruction.getLValue())
			   .getLocalInfo
			   ().getUseCount() == 2
		    && instructioncontainer.getInstruction()
			   .canCombine(storeinstruction) > 0) {
		    System.err.println("before: " + instructionblock
				       + instructioncontainer);
		    instructioncontainer.setInstruction
			(instructioncontainer.getInstruction()
			     .combine(storeinstruction));
		    System.err.println("after: " + instructioncontainer);
		    StructuredBlock structuredblock_1_ = subBlocks[1];
		    structuredblock_1_.moveDefinitions(this,
						       structuredblock_1_);
		    structuredblock_1_.replace(this);
		    structuredblock_1_.removeOnetimeLocals();
		    return;
		}
	    }
	}
	super.removeOnetimeLocals();
    }
    
    public StructuredBlock getNextBlock(StructuredBlock structuredblock) {
	if (structuredblock == subBlocks[0]) {
	    if (subBlocks[1].isEmpty())
		return subBlocks[1].getNextBlock();
	    return subBlocks[1];
	}
	return this.getNextBlock();
    }
    
    public FlowBlock getNextFlowBlock(StructuredBlock structuredblock) {
	if (structuredblock == subBlocks[0]) {
	    if (subBlocks[1].isEmpty())
		return subBlocks[1].getNextFlowBlock();
	    return null;
	}
	return this.getNextFlowBlock();
    }
    
    public boolean isSingleExit(StructuredBlock structuredblock) {
	return structuredblock == subBlocks[1];
    }
    
    public Set propagateUsage() {
	used = new SimpleSet();
	SimpleSet simpleset = new SimpleSet();
	Set set = subBlocks[0].propagateUsage();
	Set set_2_ = subBlocks[1].propagateUsage();
	used.addAll(subBlocks[0].used);
	if (subBlocks[0] instanceof LoopBlock)
	    ((LoopBlock) subBlocks[0]).removeLocallyDeclareable(used);
	simpleset.addAll(set);
	simpleset.addAll(set_2_);
	set.retainAll(set_2_);
	used.addAll(set);
	return simpleset;
    }
    
    public void makeDeclaration(Set set) {
	super.makeDeclaration(set);
	if (subBlocks[0] instanceof InstructionBlock)
	    ((InstructionBlock) subBlocks[0]).checkDeclaration(declare);
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	subBlocks[0].dumpSource(tabbedprintwriter);
	subBlocks[1].dumpSource(tabbedprintwriter);
    }
    
    public boolean replaceSubBlock(StructuredBlock structuredblock,
				   StructuredBlock structuredblock_3_) {
	for (int i = 0; i < 2; i++) {
	    if (subBlocks[i] == structuredblock) {
		subBlocks[i] = structuredblock_3_;
		return true;
	    }
	}
	return false;
    }
    
    public StructuredBlock[] getSubBlocks() {
	return subBlocks;
    }
    
    public boolean jumpMayBeChanged() {
	return subBlocks[1].jump != null || subBlocks[1].jumpMayBeChanged();
    }
}
