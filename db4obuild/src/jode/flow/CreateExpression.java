/* CreateExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.GlobalOptions;
import jode.expr.CombineableOperator;
import jode.expr.Expression;

public class CreateExpression
{
    public static boolean transform(InstructionContainer instructioncontainer,
				    StructuredBlock structuredblock) {
	int i = instructioncontainer.getInstruction().getFreeOperandCount();
	if (i == 0)
	    return false;
	if (!(structuredblock.outer instanceof SequentialBlock))
	    return false;
	SequentialBlock sequentialblock
	    = (SequentialBlock) structuredblock.outer;
	Expression expression = instructioncontainer.getInstruction();
	for (;;) {
	    if (!(sequentialblock.subBlocks[0] instanceof InstructionBlock))
		return false;
	    Expression expression_0_
		= ((InstructionBlock) sequentialblock.subBlocks[0])
		      .getInstruction();
	    if (!expression_0_.isVoid())
		break;
	    if (expression_0_.getFreeOperandCount() > 0
		|| !(expression_0_ instanceof CombineableOperator)
		|| (expression.canCombine((CombineableOperator) expression_0_)
		    <= 0))
		return false;
	    SequentialBlock sequentialblock_1_ = sequentialblock;
	    while (sequentialblock_1_ != structuredblock.outer) {
		sequentialblock_1_
		    = (SequentialBlock) sequentialblock_1_.subBlocks[1];
		if (((InstructionBlock) sequentialblock_1_.subBlocks[0])
			.getInstruction
			().hasSideEffects(expression_0_))
		    return false;
	    }
	    if (!(sequentialblock.outer instanceof SequentialBlock))
		return false;
	    sequentialblock = (SequentialBlock) sequentialblock.outer;
	}
	sequentialblock = (SequentialBlock) structuredblock.outer;
	expression = instructioncontainer.getInstruction();
	for (;;) {
	    Expression expression_2_
		= ((InstructionBlock) sequentialblock.subBlocks[0])
		      .getInstruction();
	    if (!expression_2_.isVoid()) {
		expression = expression.addOperand(expression_2_);
		break;
	    }
	    expression
		= expression.combine((CombineableOperator) expression_2_);
	    sequentialblock = (SequentialBlock) sequentialblock.outer;
	}
	if (GlobalOptions.verboseLevel > 0
	    && expression.getFreeOperandCount() == 0)
	    GlobalOptions.err.print('x');
	instructioncontainer.setInstruction(expression);
	instructioncontainer.moveDefinitions(sequentialblock, structuredblock);
	structuredblock.replace(sequentialblock);
	return true;
    }
}
