/* SwitchBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;

public class SwitchBlock extends InstructionContainer implements BreakableBlock
{
    CaseBlock[] caseBlocks;
    VariableStack exprStack;
    VariableStack breakedStack;
    boolean isBreaked = false;
    static int serialno = 0;
    String label = null;
    
    public SwitchBlock(Expression expression, int[] is,
		       FlowBlock[] flowblocks) {
	super(expression);
	int i = flowblocks.length;
	FlowBlock flowblock = flowblocks[is.length];
	for (int i_0_ = 0; i_0_ < is.length; i_0_++) {
	    if (flowblocks[i_0_] == flowblock) {
		flowblocks[i_0_] = null;
		i--;
	    }
	}
	caseBlocks = new CaseBlock[i];
	FlowBlock flowblock_1_ = null;
	for (int i_2_ = i - 1; i_2_ >= 0; i_2_--) {
	    int i_3_ = 0;
	    for (int i_4_ = 1; i_4_ < flowblocks.length; i_4_++) {
		if (flowblocks[i_4_] != null
		    && (flowblocks[i_3_] == null
			|| (flowblocks[i_4_].getAddr()
			    >= flowblocks[i_3_].getAddr())))
		    i_3_ = i_4_;
	    }
	    int i_5_;
	    if (i_3_ == is.length)
		i_5_ = -1;
	    else
		i_5_ = is[i_3_];
	    if (flowblocks[i_3_] == flowblock_1_)
		caseBlocks[i_2_] = new CaseBlock(i_5_);
	    else
		caseBlocks[i_2_]
		    = new CaseBlock(i_5_, new Jump(flowblocks[i_3_]));
	    caseBlocks[i_2_].outer = this;
	    flowblock_1_ = flowblocks[i_3_];
	    flowblocks[i_3_] = null;
	    if (i_3_ == is.length)
		caseBlocks[i_2_].isDefault = true;
	}
	caseBlocks[i - 1].isLastBlock = true;
	jump = null;
	isBreaked = false;
    }
    
    public VariableStack mapStackToLocal(VariableStack variablestack) {
	int i = instr.getFreeOperandCount();
	VariableStack variablestack_6_;
	if (i > 0) {
	    exprStack = variablestack.peek(i);
	    variablestack_6_ = variablestack.pop(i);
	} else
	    variablestack_6_ = variablestack;
	VariableStack variablestack_7_ = variablestack_6_;
	for (int i_8_ = 0; i_8_ < caseBlocks.length; i_8_++) {
	    if (variablestack_7_ != null)
		variablestack_6_.merge(variablestack_7_);
	    variablestack_7_
		= caseBlocks[i_8_].mapStackToLocal(variablestack_6_);
	}
	if (variablestack_7_ != null)
	    mergeBreakedStack(variablestack_7_);
	if (jump != null) {
	    jump.stackMap = breakedStack;
	    return null;
	}
	return breakedStack;
    }
    
    public void mergeBreakedStack(VariableStack variablestack) {
	if (breakedStack != null)
	    breakedStack.merge(variablestack);
	else
	    breakedStack = variablestack;
    }
    
    public void removePush() {
	if (exprStack != null)
	    instr = exprStack.mergeIntoExpression(instr);
	super.removePush();
    }
    
    public StructuredBlock findCase(FlowBlock flowblock) {
	for (int i = 0; i < caseBlocks.length; i++) {
	    if (caseBlocks[i].subBlock != null
		&& caseBlocks[i].subBlock instanceof EmptyBlock
		&& caseBlocks[i].subBlock.jump != null
		&& caseBlocks[i].subBlock.jump.destination == flowblock)
		return caseBlocks[i].subBlock;
	}
	return null;
    }
    
    public StructuredBlock prevCase(StructuredBlock structuredblock) {
	for (int i = caseBlocks.length - 1; i >= 0; i--) {
	    if (caseBlocks[i].subBlock == structuredblock) {
		for (i--; i >= 0; i--) {
		    if (caseBlocks[i].subBlock != null)
			return caseBlocks[i].subBlock;
		}
	    }
	}
	return null;
    }
    
    public StructuredBlock getNextBlock(StructuredBlock structuredblock) {
	for (int i = 0; i < caseBlocks.length - 1; i++) {
	    if (structuredblock == caseBlocks[i])
		return caseBlocks[i + 1];
	}
	return this.getNextBlock();
    }
    
    public FlowBlock getNextFlowBlock(StructuredBlock structuredblock) {
	for (int i = 0; i < caseBlocks.length - 1; i++) {
	    if (structuredblock == caseBlocks[i])
		return null;
	}
	return this.getNextFlowBlock();
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	if (label != null) {
	    tabbedprintwriter.untab();
	    tabbedprintwriter.println(label + ":");
	    tabbedprintwriter.tab();
	}
	tabbedprintwriter.print("switch (");
	instr.dumpExpression(0, tabbedprintwriter);
	tabbedprintwriter.print(")");
	tabbedprintwriter.openBrace();
	for (int i = 0; i < caseBlocks.length; i++)
	    caseBlocks[i].dumpSource(tabbedprintwriter);
	tabbedprintwriter.closeBrace();
    }
    
    public StructuredBlock[] getSubBlocks() {
	return caseBlocks;
    }
    
    public String getLabel() {
	if (label == null)
	    label = "switch_" + serialno++ + "_";
	return label;
    }
    
    public void setBreaked() {
	isBreaked = true;
    }
    
    public boolean jumpMayBeChanged() {
	return (!isBreaked
		&& (caseBlocks[caseBlocks.length - 1].jump != null
		    || caseBlocks[caseBlocks.length - 1].jumpMayBeChanged()));
    }
}
