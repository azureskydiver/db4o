/* CreateConstantArray - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.GlobalOptions;
import jode.expr.ArrayStoreOperator;
import jode.expr.ConstOperator;
import jode.expr.ConstantArrayOperator;
import jode.expr.Expression;
import jode.expr.NewArrayOperator;
import jode.expr.NopOperator;
import jode.expr.StoreInstruction;

public class CreateConstantArray
{
    public static boolean transform(InstructionContainer instructioncontainer,
				    StructuredBlock structuredblock) {
	if (structuredblock.outer instanceof SequentialBlock) {
	    SequentialBlock sequentialblock
		= (SequentialBlock) structuredblock.outer;
	    if (!(instructioncontainer.getInstruction()
		  instanceof StoreInstruction)
		|| (instructioncontainer.getInstruction().getFreeOperandCount()
		    != 1)
		|| !(sequentialblock.subBlocks[0] instanceof SpecialBlock)
		|| !(sequentialblock.outer instanceof SequentialBlock))
		return false;
	    StoreInstruction storeinstruction
		= (StoreInstruction) instructioncontainer.getInstruction();
	    if (!(storeinstruction.getLValue() instanceof ArrayStoreOperator))
		return false;
	    ArrayStoreOperator arraystoreoperator
		= (ArrayStoreOperator) storeinstruction.getLValue();
	    if (!(arraystoreoperator.getSubExpressions()[0]
		  instanceof NopOperator)
		|| !(arraystoreoperator.getSubExpressions()[1]
		     instanceof ConstOperator))
		return false;
	    Expression expression = storeinstruction.getSubExpressions()[1];
	    ConstOperator constoperator
		= (ConstOperator) arraystoreoperator.getSubExpressions()[1];
	    SpecialBlock specialblock
		= (SpecialBlock) sequentialblock.subBlocks[0];
	    sequentialblock = (SequentialBlock) sequentialblock.outer;
	    if (specialblock.type != SpecialBlock.DUP
		|| specialblock.depth != 0 || specialblock.count != 1
		|| !(constoperator.getValue() instanceof Integer)
		|| !(sequentialblock.subBlocks[0] instanceof InstructionBlock))
		return false;
	    int i = ((Integer) constoperator.getValue()).intValue();
	    InstructionBlock instructionblock
		= (InstructionBlock) sequentialblock.subBlocks[0];
	    if (instructionblock.getInstruction()
		instanceof NewArrayOperator) {
		NewArrayOperator newarrayoperator
		    = (NewArrayOperator) instructionblock.getInstruction();
		if (newarrayoperator.getDimensions() != 1
		    || !(newarrayoperator.getSubExpressions()[0]
			 instanceof ConstOperator))
		    return false;
		ConstOperator constoperator_0_
		    = (ConstOperator) newarrayoperator.getSubExpressions()[0];
		if (!(constoperator_0_.getValue() instanceof Integer))
		    return false;
		int i_1_ = ((Integer) constoperator_0_.getValue()).intValue();
		if (i_1_ <= i)
		    return false;
		if (GlobalOptions.verboseLevel > 0)
		    GlobalOptions.err.print('a');
		ConstantArrayOperator constantarrayoperator
		    = new ConstantArrayOperator(newarrayoperator.getType(),
						i_1_);
		constantarrayoperator.setValue(i, expression);
		instructioncontainer.setInstruction(constantarrayoperator);
		instructioncontainer.moveDefinitions(sequentialblock,
						     structuredblock);
		structuredblock.replace(sequentialblock);
		return true;
	    }
	    if (instructionblock.getInstruction()
		instanceof ConstantArrayOperator) {
		ConstantArrayOperator constantarrayoperator
		    = ((ConstantArrayOperator)
		       instructionblock.getInstruction());
		if (constantarrayoperator.setValue(i, expression)) {
		    instructioncontainer.setInstruction(constantarrayoperator);
		    instructioncontainer.moveDefinitions(sequentialblock,
							 structuredblock);
		    structuredblock.replace(sequentialblock);
		    return true;
		}
	    }
	}
	return false;
    }
}
