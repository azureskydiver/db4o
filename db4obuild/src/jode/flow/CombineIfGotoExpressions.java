/* CombineIfGotoExpressions - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.expr.BinaryOperator;
import jode.expr.CombineableOperator;
import jode.expr.Expression;
import jode.type.Type;

public class CombineIfGotoExpressions
{
    public static boolean transform(ConditionalBlock conditionalblock,
				    StructuredBlock structuredblock) {
	if (conditionalblock.jump == null
	    || !(structuredblock.outer instanceof SequentialBlock))
	    return false;
	SequentialBlock sequentialblock
	    = (SequentialBlock) conditionalblock.outer;
	Expression expression = conditionalblock.getInstruction();
	Expression expression_0_ = expression;
	for (/**/; sequentialblock.subBlocks[0] instanceof InstructionBlock;
	     sequentialblock = (SequentialBlock) sequentialblock.outer) {
	    InstructionBlock instructionblock
		= (InstructionBlock) sequentialblock.subBlocks[0];
	    if (!(sequentialblock.outer instanceof SequentialBlock))
		return false;
	    Expression expression_1_ = instructionblock.getInstruction();
	    if (!(expression_1_ instanceof CombineableOperator)
		|| (expression_0_
			.canCombine((CombineableOperator) expression_1_)
		    + expression.canCombine((CombineableOperator)
					    expression_1_)) <= 0)
		return false;
	    expression_0_ = expression_1_;
	}
	if (sequentialblock.subBlocks[0] instanceof ConditionalBlock) {
	    ConditionalBlock conditionalblock_2_
		= (ConditionalBlock) sequentialblock.subBlocks[0];
	    Jump jump = conditionalblock_2_.trueBlock.jump;
	    int i;
	    Expression expression_3_;
	    if (jump.destination == conditionalblock.jump.destination) {
		i = 32;
		expression_3_ = conditionalblock_2_.getInstruction().negate();
	    } else if (jump.destination
		       == conditionalblock.trueBlock.jump.destination) {
		i = 33;
		expression_3_ = conditionalblock_2_.getInstruction();
	    } else
		return false;
	    for (sequentialblock = (SequentialBlock) conditionalblock.outer;
		 sequentialblock.subBlocks[0] instanceof InstructionBlock;
		 sequentialblock = (SequentialBlock) sequentialblock.outer) {
		InstructionBlock instructionblock
		    = (InstructionBlock) sequentialblock.subBlocks[0];
		Expression expression_4_ = instructionblock.getInstruction();
		expression
		    = expression.combine((CombineableOperator) expression_4_);
	    }
	    conditionalblock.flowBlock.removeSuccessor(jump);
	    jump.prev.removeJump();
	    Expression expression_5_
		= new BinaryOperator(Type.tBoolean, i).addOperand
		      (expression).addOperand(expression_3_);
	    conditionalblock.setInstruction(expression_5_);
	    conditionalblock.moveDefinitions(sequentialblock, structuredblock);
	    structuredblock.replace(sequentialblock);
	    return true;
	}
	return false;
    }
}
