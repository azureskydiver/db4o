/* CreateAssignExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.expr.BinaryOperator;
import jode.expr.ConvertOperator;
import jode.expr.Expression;
import jode.expr.LocalLoadOperator;
import jode.expr.LocalStoreOperator;
import jode.expr.Operator;
import jode.expr.StoreInstruction;
import jode.expr.StringAddOperator;
import jode.type.Type;

public class CreateAssignExpression
{
    public static boolean transform(InstructionContainer instructioncontainer,
				    StructuredBlock structuredblock) {
	if (!(structuredblock.outer instanceof SequentialBlock)
	    || !(instructioncontainer.getInstruction()
		 instanceof StoreInstruction)
	    || !instructioncontainer.getInstruction().isVoid())
	    return false;
	return (createAssignOp(instructioncontainer, structuredblock)
		|| createAssignExpression(instructioncontainer,
					  structuredblock));
    }
    
    public static boolean createAssignOp
	(InstructionContainer instructioncontainer,
	 StructuredBlock structuredblock) {
	SequentialBlock sequentialblock
	    = (SequentialBlock) structuredblock.outer;
	StoreInstruction storeinstruction
	    = (StoreInstruction) instructioncontainer.getInstruction();
	if (!storeinstruction.isFreeOperator())
	    return false;
	Expression expression = storeinstruction.getSubExpressions()[0];
	int i = expression.getFreeOperandCount();
	boolean bool = false;
	if (sequentialblock.subBlocks[0] instanceof SpecialBlock) {
	    SpecialBlock specialblock
		= (SpecialBlock) sequentialblock.subBlocks[0];
	    if (specialblock.type != SpecialBlock.DUP
		|| specialblock.depth != i
		|| specialblock.count != expression.getType().stackSize()
		|| !(sequentialblock.outer instanceof SequentialBlock))
		return false;
	    sequentialblock = (SequentialBlock) sequentialblock.outer;
	    bool = true;
	}
	if (!(sequentialblock.subBlocks[0] instanceof InstructionBlock))
	    return false;
	InstructionBlock instructionblock
	    = (InstructionBlock) sequentialblock.subBlocks[0];
	if (!(instructionblock.getInstruction() instanceof Operator))
	    return false;
	Operator operator = (Operator) instructionblock.getInstruction();
	if (operator.getFreeOperandCount() != i)
	    return false;
	Type type = operator.getType();
	SpecialBlock specialblock = null;
	if (i > 0) {
	    if (!(sequentialblock.outer instanceof SequentialBlock)
		|| !(sequentialblock.outer.getSubBlocks()[0]
		     instanceof SpecialBlock))
		return false;
	    SequentialBlock sequentialblock_0_
		= (SequentialBlock) sequentialblock.outer;
	    specialblock = (SpecialBlock) sequentialblock_0_.subBlocks[0];
	    if (specialblock.type != SpecialBlock.DUP
		|| specialblock.depth != 0 || specialblock.count != i)
		return false;
	}
	if (operator instanceof ConvertOperator
	    && operator.getSubExpressions()[0] instanceof Operator
	    && operator.getType().isOfType(expression.getType())) {
	    for (operator = (Operator) operator.getSubExpressions()[0];
		 (operator instanceof ConvertOperator
		  && operator.getSubExpressions()[0] instanceof Operator);
		 operator = (Operator) operator.getSubExpressions()[0]) {
		/* empty */
	    }
	}
	Expression expression_1_;
	int i_2_;
	if (operator instanceof BinaryOperator) {
	    i_2_ = operator.getOperatorIndex();
	    if (i_2_ < 1 || i_2_ >= 12)
		return false;
	    if (!(operator.getSubExpressions()[0] instanceof Operator))
		return false;
	    Operator operator_3_;
	    for (operator_3_ = (Operator) operator.getSubExpressions()[0];
		 (operator_3_ instanceof ConvertOperator
		  && operator_3_.getSubExpressions()[0] instanceof Operator);
		 operator_3_ = (Operator) operator_3_.getSubExpressions()[0]) {
		/* empty */
	    }
	    if (!storeinstruction.lvalueMatches(operator_3_)
		|| !operator_3_.isFreeOperator(i))
		return false;
	    if (expression instanceof LocalStoreOperator)
		((LocalLoadOperator) operator_3_).getLocalInfo().combineWith
		    (((LocalStoreOperator) expression).getLocalInfo());
	    expression_1_ = operator.getSubExpressions()[1];
	} else {
	    Expression expression_4_ = operator.simplifyString();
	    expression_1_ = expression_4_;
	    Operator operator_5_ = null;
	    Operator operator_6_ = null;
	    for (/**/; expression_4_ instanceof StringAddOperator;
		 expression_4_ = operator_5_.getSubExpressions()[0]) {
		operator_6_ = operator_5_;
		operator_5_ = (Operator) expression_4_;
	    }
	    if (operator_5_ == null || !(expression_4_ instanceof Operator)
		|| !storeinstruction.lvalueMatches((Operator) expression_4_)
		|| !((Operator) expression_4_).isFreeOperator(i))
		return false;
	    if (expression instanceof LocalStoreOperator)
		((LocalLoadOperator) expression_4_).getLocalInfo().combineWith
		    (((LocalStoreOperator) expression).getLocalInfo());
	    if (operator_6_ != null)
		operator_6_
		    .setSubExpressions(0, operator_5_.getSubExpressions()[1]);
	    else
		expression_1_ = operator_5_.getSubExpressions()[1];
	    i_2_ = 1;
	}
	if (specialblock != null)
	    specialblock.removeBlock();
	instructionblock.setInstruction(expression_1_);
	expression.setType(type);
	storeinstruction.makeOpAssign(12 + i_2_);
	if (bool)
	    storeinstruction.makeNonVoid();
	structuredblock.replace(sequentialblock.subBlocks[1]);
	return true;
    }
    
    public static boolean createAssignExpression
	(InstructionContainer instructioncontainer,
	 StructuredBlock structuredblock) {
	SequentialBlock sequentialblock
	    = (SequentialBlock) structuredblock.outer;
	StoreInstruction storeinstruction
	    = (StoreInstruction) instructioncontainer.getInstruction();
	if (sequentialblock.subBlocks[0] instanceof SpecialBlock
	    && storeinstruction.isFreeOperator()) {
	    Expression expression = storeinstruction.getSubExpressions()[0];
	    SpecialBlock specialblock
		= (SpecialBlock) sequentialblock.subBlocks[0];
	    if (specialblock.type != SpecialBlock.DUP
		|| specialblock.depth != expression.getFreeOperandCount()
		|| specialblock.count != expression.getType().stackSize())
		return false;
	    specialblock.removeBlock();
	    storeinstruction.makeNonVoid();
	    return true;
	}
	return false;
    }
}
