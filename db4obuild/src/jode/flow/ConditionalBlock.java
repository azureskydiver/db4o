/* ConditionalBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.AssertError;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;

public class ConditionalBlock extends InstructionContainer
{
    VariableStack stack;
    EmptyBlock trueBlock;
    
    public void checkConsistent() {
	super.checkConsistent();
	if (trueBlock.jump == null || !(trueBlock instanceof EmptyBlock))
	    throw new AssertError("Inconsistency");
    }
    
    public ConditionalBlock(Expression expression, Jump jump, Jump jump_0_) {
	super(expression, jump_0_);
	trueBlock = new EmptyBlock(jump);
	trueBlock.outer = this;
    }
    
    public ConditionalBlock(Expression expression) {
	super(expression);
	trueBlock = new EmptyBlock();
	trueBlock.outer = this;
    }
    
    public StructuredBlock[] getSubBlocks() {
	return new StructuredBlock[] { trueBlock };
    }
    
    public boolean replaceSubBlock(StructuredBlock structuredblock,
				   StructuredBlock structuredblock_1_) {
	throw new AssertError("replaceSubBlock on ConditionalBlock");
    }
    
    public VariableStack mapStackToLocal(VariableStack variablestack) {
	int i = instr.getFreeOperandCount();
	VariableStack variablestack_2_;
	if (i > 0) {
	    stack = variablestack.peek(i);
	    variablestack_2_ = variablestack.pop(i);
	} else
	    variablestack_2_ = variablestack;
	trueBlock.jump.stackMap = variablestack_2_;
	if (jump != null) {
	    jump.stackMap = variablestack_2_;
	    return null;
	}
	return variablestack_2_;
    }
    
    public void removePush() {
	if (stack != null)
	    instr = stack.mergeIntoExpression(instr);
	trueBlock.removePush();
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.print("IF (");
	instr.dumpExpression(0, tabbedprintwriter);
	tabbedprintwriter.println(")");
	tabbedprintwriter.tab();
	trueBlock.dumpSource(tabbedprintwriter);
	tabbedprintwriter.untab();
    }
    
    public boolean doTransformations() {
	StructuredBlock structuredblock = flowBlock.lastModified;
	return (super.doTransformations()
		|| CombineIfGotoExpressions.transform(this, structuredblock)
		|| CreateIfThenElseOperator.createFunny(this,
							structuredblock));
    }
}
