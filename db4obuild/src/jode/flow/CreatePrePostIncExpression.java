/* CreatePrePostIncExpression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.expr.BinaryOperator;
import jode.expr.ConstOperator;
import jode.expr.Expression;
import jode.expr.IIncOperator;
import jode.expr.LocalLoadOperator;
import jode.expr.NopOperator;
import jode.expr.Operator;
import jode.expr.PrePostFixOperator;
import jode.expr.StoreInstruction;
import jode.type.Type;

public class CreatePrePostIncExpression
{
    public static boolean transform(InstructionContainer instructioncontainer,
				    StructuredBlock structuredblock) {
	return (createLocalPrePostInc(instructioncontainer, structuredblock)
		|| createPostInc(instructioncontainer, structuredblock));
    }
    
    public static boolean createLocalPrePostInc
	(InstructionContainer instructioncontainer,
	 StructuredBlock structuredblock) {
	if (!(structuredblock.outer instanceof SequentialBlock)
	    || !(structuredblock.outer.getSubBlocks()[0]
		 instanceof InstructionBlock))
	    return false;
	Expression expression
	    = ((InstructionBlock) structuredblock.outer.getSubBlocks()[0])
		  .getInstruction();
	Expression expression_0_ = instructioncontainer.getInstruction();
	LocalLoadOperator localloadoperator;
	IIncOperator iincoperator;
	boolean bool;
	if (expression instanceof IIncOperator
	    && expression_0_ instanceof LocalLoadOperator) {
	    iincoperator = (IIncOperator) expression;
	    localloadoperator = (LocalLoadOperator) expression_0_;
	    bool = false;
	} else if (expression instanceof LocalLoadOperator
		   && expression_0_ instanceof IIncOperator) {
	    localloadoperator = (LocalLoadOperator) expression;
	    iincoperator = (IIncOperator) expression_0_;
	    bool = true;
	} else
	    return false;
	int i;
	if (iincoperator.getOperatorIndex() == 1 + 12)
	    i = 24;
	else if (iincoperator.getOperatorIndex() == 2 + 12)
	    i = 25;
	else
	    return false;
	if (iincoperator.getValue() == -1)
	    i ^= 0x1;
	else if (iincoperator.getValue() != 1)
	    return false;
	if (!iincoperator.lvalueMatches(localloadoperator))
	    return false;
	Type type = localloadoperator.getType().intersection(Type.tUInt);
	iincoperator.makeNonVoid();
	PrePostFixOperator prepostfixoperator
	    = new PrePostFixOperator(type, i, iincoperator.getLValue(), bool);
	instructioncontainer.setInstruction(prepostfixoperator);
	instructioncontainer.moveDefinitions(structuredblock.outer,
					     structuredblock);
	structuredblock.replace(structuredblock.outer);
	return true;
    }
    
    public static boolean createPostInc
	(InstructionContainer instructioncontainer,
	 StructuredBlock structuredblock) {
	if (!(instructioncontainer.getInstruction()
	      instanceof StoreInstruction))
	    return false;
	StoreInstruction storeinstruction
	    = (StoreInstruction) instructioncontainer.getInstruction();
	Expression expression = storeinstruction.getSubExpressions()[0];
	int i = expression.getFreeOperandCount();
	if (!((Operator) expression).isFreeOperator()
	    || !storeinstruction.isVoid()
	    || !(storeinstruction.getSubExpressions()[1]
		 instanceof BinaryOperator))
	    return false;
	BinaryOperator binaryoperator
	    = (BinaryOperator) storeinstruction.getSubExpressions()[1];
	if (binaryoperator.getSubExpressions() == null
	    || !(binaryoperator.getSubExpressions()[0] instanceof NopOperator)
	    || !(binaryoperator.getSubExpressions()[1]
		 instanceof ConstOperator))
	    return false;
	ConstOperator constoperator
	    = (ConstOperator) binaryoperator.getSubExpressions()[1];
	int i_1_;
	if (binaryoperator.getOperatorIndex() == 1)
	    i_1_ = 24;
	else if (binaryoperator.getOperatorIndex() == 2)
	    i_1_ = 25;
	else
	    return false;
	if (!constoperator.isOne(expression.getType()))
	    return false;
	if (!(structuredblock.outer instanceof SequentialBlock))
	    return false;
	SequentialBlock sequentialblock
	    = (SequentialBlock) structuredblock.outer;
	if (!(sequentialblock.subBlocks[0] instanceof SpecialBlock))
	    return false;
	SpecialBlock specialblock
	    = (SpecialBlock) sequentialblock.subBlocks[0];
	if (specialblock.type != SpecialBlock.DUP
	    || specialblock.count != expression.getType().stackSize()
	    || specialblock.depth != i)
	    return false;
	if (!(sequentialblock.outer instanceof SequentialBlock))
	    return false;
	sequentialblock = (SequentialBlock) sequentialblock.outer;
	if (!(sequentialblock.subBlocks[0] instanceof InstructionBlock))
	    return false;
	InstructionBlock instructionblock
	    = (InstructionBlock) sequentialblock.subBlocks[0];
	if (!(instructionblock.getInstruction() instanceof Operator)
	    || !storeinstruction.lvalueMatches((Operator)
					       instructionblock
						   .getInstruction()))
	    return false;
	if (i > 0) {
	    if (!(sequentialblock.outer instanceof SequentialBlock))
		return false;
	    sequentialblock = (SequentialBlock) sequentialblock.outer;
	    if (!(sequentialblock.subBlocks[0] instanceof SpecialBlock))
		return false;
	    SpecialBlock specialblock_2_
		= (SpecialBlock) sequentialblock.subBlocks[0];
	    if (specialblock_2_.type != SpecialBlock.DUP
		|| specialblock_2_.count != i || specialblock_2_.depth != 0)
		return false;
	}
	instructioncontainer.setInstruction
	    (new PrePostFixOperator(expression.getType(), i_1_,
				    storeinstruction.getLValue(), true));
	instructioncontainer.moveDefinitions(sequentialblock, structuredblock);
	structuredblock.replace(sequentialblock);
	return true;
    }
}
