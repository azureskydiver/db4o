/* CreateNewConstructor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.bytecode.Reference;
import jode.decompiler.MethodAnalyzer;
import jode.expr.Expression;
import jode.expr.InvokeOperator;
import jode.expr.NewOperator;
import jode.expr.NopOperator;
import jode.type.Type;

public class CreateNewConstructor
{
    public static boolean transform(InstructionContainer instructioncontainer,
				    StructuredBlock structuredblock) {
	return (transformNormal(instructioncontainer, structuredblock)
		|| transformJikesString(instructioncontainer,
					structuredblock));
    }
    
    static boolean transformJikesString
	(InstructionContainer instructioncontainer,
	 StructuredBlock structuredblock) {
	if (!(structuredblock.outer instanceof SequentialBlock)
	    || !(instructioncontainer.getInstruction()
		 instanceof InvokeOperator))
	    return false;
	InvokeOperator invokeoperator
	    = (InvokeOperator) instructioncontainer.getInstruction();
	if (!invokeoperator.getClassType().equals(Type.tStringBuffer)
	    || !invokeoperator.isFreeOperator(2) || invokeoperator.isStatic()
	    || !invokeoperator.getMethodName().equals("append")
	    || invokeoperator.getMethodType().getParameterTypes().length != 1)
	    return false;
	SequentialBlock sequentialblock
	    = (SequentialBlock) structuredblock.outer;
	if (!(sequentialblock.outer instanceof SequentialBlock)
	    || !(sequentialblock.subBlocks[0] instanceof SpecialBlock))
	    return false;
	SpecialBlock specialblock
	    = (SpecialBlock) sequentialblock.subBlocks[0];
	sequentialblock = (SequentialBlock) sequentialblock.outer;
	if (specialblock.type != SpecialBlock.SWAP
	    || !(sequentialblock.subBlocks[0] instanceof InstructionBlock)
	    || !(sequentialblock.outer instanceof SequentialBlock))
	    return false;
	InstructionBlock instructionblock
	    = (InstructionBlock) sequentialblock.subBlocks[0];
	sequentialblock = (SequentialBlock) sequentialblock.outer;
	if (!(instructionblock.getInstruction() instanceof InvokeOperator)
	    || !(sequentialblock.subBlocks[0] instanceof InstructionBlock))
	    return false;
	InvokeOperator invokeoperator_0_
	    = (InvokeOperator) instructionblock.getInstruction();
	instructionblock = (InstructionBlock) sequentialblock.subBlocks[0];
	if (!invokeoperator_0_.isConstructor()
	    || !invokeoperator_0_.getClassType().equals(Type.tStringBuffer)
	    || invokeoperator_0_.isVoid()
	    || (invokeoperator_0_.getMethodType().getParameterTypes().length
		!= 0))
	    return false;
	MethodAnalyzer methodanalyzer = instructionblock.flowBlock.method;
	Expression expression = instructionblock.getInstruction();
	Type type = invokeoperator.getMethodType().getParameterTypes()[0];
	if (!type.equals(Type.tString)) {
	    InvokeOperator invokeoperator_1_
		= (new InvokeOperator
		   (methodanalyzer, 2,
		    Reference.getReference("Ljava/lang/String;", "valueOf",
					   ("(" + type.getTypeSignature()
					    + ")Ljava/lang/String;"))));
	    expression = invokeoperator_1_.addOperand(expression);
	}
	InvokeOperator invokeoperator_2_
	    = (new InvokeOperator
	       (methodanalyzer, 3,
		Reference.getReference("Ljava/lang/StringBuffer;", "<init>",
				       "(Ljava/lang/String;)V")));
	invokeoperator_2_.makeNonVoid();
	invokeoperator_2_
	    .setSubExpressions(0, invokeoperator_0_.getSubExpressions()[0]);
	invokeoperator_2_.setSubExpressions(1, expression);
	instructioncontainer.setInstruction(invokeoperator_2_);
	structuredblock.replace(sequentialblock);
	return true;
    }
    
    static boolean transformNormal(InstructionContainer instructioncontainer,
				   StructuredBlock structuredblock) {
	if (!(structuredblock.outer instanceof SequentialBlock))
	    return false;
	if (!(instructioncontainer.getInstruction() instanceof InvokeOperator))
	    return false;
	InvokeOperator invokeoperator
	    = (InvokeOperator) instructioncontainer.getInstruction();
	if (!invokeoperator.isConstructor() || !invokeoperator.isVoid())
	    return false;
	SpecialBlock specialblock = null;
	SequentialBlock sequentialblock
	    = (SequentialBlock) structuredblock.outer;
	Expression[] expressions = invokeoperator.getSubExpressions();
	int i = invokeoperator.getFreeOperandCount();
	if (expressions != null) {
	    if (!(expressions[0] instanceof NopOperator))
		return false;
	    if (invokeoperator.getFreeOperandCount() > 1) {
		if (!(sequentialblock.outer instanceof SequentialBlock)
		    || !(sequentialblock.subBlocks[0] instanceof SpecialBlock))
		    return false;
		specialblock = (SpecialBlock) sequentialblock.subBlocks[0];
		sequentialblock = (SequentialBlock) sequentialblock.outer;
		if (specialblock.type != SpecialBlock.DUP
		    || specialblock.depth == 0)
		    return false;
		int i_3_ = specialblock.count;
		do {
		    if (!(sequentialblock.outer instanceof SequentialBlock)
			|| !(sequentialblock.subBlocks[0]
			     instanceof InstructionBlock))
			return false;
		    Expression expression
			= ((InstructionBlock) sequentialblock.subBlocks[0])
			      .getInstruction();
		    sequentialblock = (SequentialBlock) sequentialblock.outer;
		    if (!expression.isVoid()) {
			i_3_ -= expression.getType().stackSize();
			i--;
		    }
		} while (i_3_ > 0 && i > 1);
		if (i_3_ != 0)
		    return false;
	    }
	}
	if (i != 1)
	    return false;
	for (/**/;
	     (sequentialblock.subBlocks[0] instanceof InstructionBlock
	      && sequentialblock.outer instanceof SequentialBlock);
	     sequentialblock = (SequentialBlock) sequentialblock.outer) {
	    Expression expression
		= ((InstructionBlock) sequentialblock.subBlocks[0])
		      .getInstruction();
	    if (!expression.isVoid() || expression.getFreeOperandCount() > 0)
		break;
	}
	SpecialBlock specialblock_4_ = null;
	if (sequentialblock.outer instanceof SequentialBlock
	    && sequentialblock.subBlocks[0] instanceof SpecialBlock) {
	    specialblock_4_ = (SpecialBlock) sequentialblock.subBlocks[0];
	    if (specialblock_4_.type != SpecialBlock.DUP
		|| specialblock_4_.count != 1 || specialblock_4_.depth != 0)
		return false;
	    sequentialblock = (SequentialBlock) sequentialblock.outer;
	    if (specialblock != null && specialblock.depth != 2)
		return false;
	} else if (specialblock != null && specialblock.depth != 1)
	    return false;
	if (!(sequentialblock.subBlocks[0] instanceof InstructionBlock))
	    return false;
	InstructionBlock instructionblock
	    = (InstructionBlock) sequentialblock.subBlocks[0];
	if (!(instructionblock.getInstruction() instanceof NewOperator))
	    return false;
	NewOperator newoperator
	    = (NewOperator) instructionblock.getInstruction();
	if (invokeoperator.getClassType() != newoperator.getType())
	    return false;
	instructionblock.removeBlock();
	if (specialblock_4_ != null)
	    specialblock_4_.removeBlock();
	if (specialblock != null)
	    specialblock.depth = 0;
	invokeoperator.setSubExpressions(0, newoperator);
	if (specialblock_4_ != null)
	    invokeoperator.makeNonVoid();
	return true;
    }
}
