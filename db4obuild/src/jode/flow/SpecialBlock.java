/* SpecialBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.expr.CompareUnaryOperator;
import jode.expr.Expression;
import jode.expr.InvokeOperator;
import jode.expr.PopOperator;
import jode.expr.StoreInstruction;

public class SpecialBlock extends StructuredBlock
{
    public static int DUP = 0;
    public static int SWAP = 1;
    public static int POP = 2;
    private static String[] output = { "DUP", "SWAP", "POP" };
    int type;
    int count;
    int depth;
    
    public SpecialBlock(int i, int i_0_, int i_1_, Jump jump) {
	type = i;
	count = i_0_;
	depth = i_1_;
	this.setJump(jump);
    }
    
    public VariableStack mapStackToLocal(VariableStack variablestack) {
	VariableStack variablestack_2_ = variablestack.executeSpecial(this);
	return super.mapStackToLocal(variablestack_2_);
    }
    
    public void removePush() {
	this.removeBlock();
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.println(output[type] + (count == 1 ? "" : "2")
				  + (depth == 0 ? "" : "_X" + depth));
    }
    
    public boolean doTransformations() {
	return (type == SWAP && removeSwap(flowBlock.lastModified)
		|| type == POP && removePop(flowBlock.lastModified));
    }
    
    public boolean removeSwap(StructuredBlock structuredblock) {
	if (structuredblock.outer instanceof SequentialBlock
	    && structuredblock.outer.outer instanceof SequentialBlock
	    && (structuredblock.outer.getSubBlocks()[0]
		instanceof InstructionBlock)
	    && (structuredblock.outer.outer.getSubBlocks()[0]
		instanceof InstructionBlock)) {
	    InstructionBlock instructionblock
		= ((InstructionBlock)
		   structuredblock.outer.outer.getSubBlocks()[0]);
	    InstructionBlock instructionblock_3_
		= (InstructionBlock) structuredblock.outer.getSubBlocks()[0];
	    Expression expression = instructionblock.getInstruction();
	    Expression expression_4_ = instructionblock_3_.getInstruction();
	    if (expression.isVoid() || expression_4_.isVoid()
		|| expression.getFreeOperandCount() != 0
		|| expression_4_.getFreeOperandCount() != 0
		|| expression.hasSideEffects(expression_4_)
		|| expression_4_.hasSideEffects(expression))
		return false;
	    structuredblock.outer.replace(instructionblock.outer);
	    instructionblock.replace(this);
	    instructionblock.moveJump(jump);
	    instructionblock.flowBlock.lastModified = instructionblock;
	    return true;
	}
	return false;
    }
    
    public boolean removePop(StructuredBlock structuredblock) {
	if (structuredblock.outer instanceof SequentialBlock
	    && (structuredblock.outer.getSubBlocks()[0]
		instanceof InstructionBlock)) {
	    if (jump != null && jump.destination == null)
		return false;
	    InstructionBlock instructionblock
		= (InstructionBlock) structuredblock.outer.getSubBlocks()[0];
	    Expression expression = instructionblock.getInstruction();
	    if (expression.getType().stackSize() == count) {
		StructuredBlock structuredblock_5_;
		if (expression instanceof InvokeOperator
		    || expression instanceof StoreInstruction) {
		    Expression expression_6_
			= new PopOperator(expression.getType())
			      .addOperand(expression);
		    instructionblock.setInstruction(expression_6_);
		    structuredblock_5_ = instructionblock;
		} else {
		    Expression expression_7_
			= new CompareUnaryOperator
			      (expression.getType(), 27)
			      .addOperand(expression);
		    IfThenElseBlock ifthenelseblock
			= new IfThenElseBlock(expression_7_);
		    ifthenelseblock.setThenBlock(new EmptyBlock());
		    structuredblock_5_ = ifthenelseblock;
		}
		structuredblock_5_.moveDefinitions(structuredblock.outer,
						   structuredblock);
		structuredblock_5_.moveJump(jump);
		if (this == structuredblock) {
		    structuredblock_5_.replace(structuredblock.outer);
		    flowBlock.lastModified = structuredblock_5_;
		} else {
		    structuredblock_5_.replace(this);
		    structuredblock.replace(structuredblock.outer);
		}
		return true;
	    }
	}
	return false;
    }
}
