/* CreateClassField - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.expr.ClassFieldOperator;
import jode.expr.CompareUnaryOperator;
import jode.expr.ConstOperator;
import jode.expr.GetFieldOperator;
import jode.expr.InvokeOperator;
import jode.expr.Operator;
import jode.expr.PutFieldOperator;
import jode.expr.StoreInstruction;
import jode.type.Type;

public class CreateClassField
{
    public static boolean transform(IfThenElseBlock ifthenelseblock,
				    StructuredBlock structuredblock) {
	if (!(ifthenelseblock.cond instanceof CompareUnaryOperator)
	    || ((Operator) ifthenelseblock.cond).getOperatorIndex() != 26
	    || !(ifthenelseblock.thenBlock instanceof InstructionBlock)
	    || ifthenelseblock.elseBlock != null)
	    return false;
	if (ifthenelseblock.thenBlock.jump != null
	    && (ifthenelseblock.jump == null
		|| (ifthenelseblock.jump.destination
		    != ifthenelseblock.thenBlock.jump.destination)))
	    return false;
	CompareUnaryOperator compareunaryoperator
	    = (CompareUnaryOperator) ifthenelseblock.cond;
	jode.expr.Expression expression
	    = ((InstructionBlock) ifthenelseblock.thenBlock).getInstruction();
	if (!(compareunaryoperator.getSubExpressions()[0]
	      instanceof GetFieldOperator)
	    || !(expression instanceof StoreInstruction))
	    return false;
	StoreInstruction storeinstruction = (StoreInstruction) expression;
	if (!(storeinstruction.getLValue() instanceof PutFieldOperator))
	    return false;
	PutFieldOperator putfieldoperator
	    = (PutFieldOperator) storeinstruction.getLValue();
	if (putfieldoperator.getField() == null
	    || !putfieldoperator.matches((GetFieldOperator)
					 compareunaryoperator
					     .getSubExpressions()[0])
	    || !(storeinstruction.getSubExpressions()[1]
		 instanceof InvokeOperator))
	    return false;
	InvokeOperator invokeoperator
	    = (InvokeOperator) storeinstruction.getSubExpressions()[1];
	if (!invokeoperator.isGetClass())
	    return false;
	jode.expr.Expression expression_0_
	    = invokeoperator.getSubExpressions()[0];
	if (expression_0_ instanceof ConstOperator
	    && ((ConstOperator) expression_0_).getValue() instanceof String) {
	    String string
		= (String) ((ConstOperator) expression_0_).getValue();
	    if (putfieldoperator.getField().setClassConstant(string)) {
		compareunaryoperator.setSubExpressions
		    (0, new ClassFieldOperator(string.charAt(0) == '['
					       ? (Type) Type.tType(string)
					       : Type.tClass(string)));
		EmptyBlock emptyblock = new EmptyBlock();
		emptyblock.moveJump(ifthenelseblock.thenBlock.jump);
		ifthenelseblock.setThenBlock(emptyblock);
		return true;
	    }
	}
	return false;
    }
}
