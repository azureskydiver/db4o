/* CreateCheckNull - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.decompiler.LocalInfo;
import jode.expr.CheckNullOperator;
import jode.expr.CompareUnaryOperator;
import jode.expr.InvokeOperator;
import jode.expr.Operator;
import jode.expr.PopOperator;
import jode.type.Type;

public class CreateCheckNull
{
    public static boolean transformJavac
	(InstructionContainer instructioncontainer,
	 StructuredBlock structuredblock) {
	if (!(structuredblock.outer instanceof SequentialBlock)
	    || !(instructioncontainer.getInstruction() instanceof Operator)
	    || !(structuredblock.outer.getSubBlocks()[0]
		 instanceof SpecialBlock))
	    return false;
	SpecialBlock specialblock
	    = (SpecialBlock) structuredblock.outer.getSubBlocks()[0];
	if (specialblock.type != SpecialBlock.DUP || specialblock.count != 1
	    || specialblock.depth != 0)
	    return false;
	Operator operator = (Operator) instructioncontainer.getInstruction();
	if (!(operator.getOperator() instanceof PopOperator)
	    || !(operator.getSubExpressions()[0] instanceof InvokeOperator))
	    return false;
	InvokeOperator invokeoperator
	    = (InvokeOperator) operator.getSubExpressions()[0];
	if (!invokeoperator.getMethodName().equals("getClass")
	    || !invokeoperator.getMethodType().toString()
		    .equals("()Ljava/lang/Class;"))
	    return false;
	LocalInfo localinfo = new LocalInfo();
	instructioncontainer
	    .setInstruction(new CheckNullOperator(Type.tUObject, localinfo));
	structuredblock.replace(structuredblock.outer);
	return true;
    }
    
    public static boolean transformJikes(IfThenElseBlock ifthenelseblock,
					 StructuredBlock structuredblock) {
	if (!(structuredblock.outer instanceof SequentialBlock)
	    || !(structuredblock.outer.getSubBlocks()[0]
		 instanceof SpecialBlock)
	    || ifthenelseblock.elseBlock != null
	    || !(ifthenelseblock.thenBlock instanceof ThrowBlock))
	    return false;
	SpecialBlock specialblock
	    = (SpecialBlock) structuredblock.outer.getSubBlocks()[0];
	if (specialblock.type != SpecialBlock.DUP || specialblock.count != 1
	    || specialblock.depth != 0)
	    return false;
	if (!(ifthenelseblock.cond instanceof CompareUnaryOperator))
	    return false;
	CompareUnaryOperator compareunaryoperator
	    = (CompareUnaryOperator) ifthenelseblock.cond;
	if (compareunaryoperator.getOperatorIndex() != 26
	    || !compareunaryoperator.getCompareType().isOfType(Type.tUObject))
	    return false;
	LocalInfo localinfo = new LocalInfo();
	InstructionBlock instructionblock
	    = new InstructionBlock(new CheckNullOperator(Type.tUObject,
							 localinfo));
	ifthenelseblock.flowBlock
	    .removeSuccessor(ifthenelseblock.thenBlock.jump);
	instructionblock.moveJump(ifthenelseblock.jump);
	if (structuredblock == ifthenelseblock) {
	    instructionblock.replace(structuredblock.outer);
	    InstructionBlock instructionblock_0_ = instructionblock;
	} else {
	    instructionblock.replace(ifthenelseblock);
	    structuredblock.replace(structuredblock.outer);
	}
	return true;
    }
}
