/* CreateForInitializer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.GlobalOptions;
import jode.expr.CombineableOperator;

public class CreateForInitializer
{
    public static boolean transform(LoopBlock loopblock,
				    StructuredBlock structuredblock) {
	if (!(structuredblock.outer instanceof SequentialBlock))
	    return false;
	SequentialBlock sequentialblock
	    = (SequentialBlock) structuredblock.outer;
	if (!(sequentialblock.subBlocks[0] instanceof InstructionBlock))
	    return false;
	InstructionBlock instructionblock
	    = (InstructionBlock) sequentialblock.subBlocks[0];
	if (!instructionblock.getInstruction().isVoid()
	    || !(instructionblock.getInstruction()
		 instanceof CombineableOperator)
	    || !loopblock.conditionMatches((CombineableOperator)
					   instructionblock.getInstruction()))
	    return false;
	if (GlobalOptions.verboseLevel > 0)
	    GlobalOptions.err.print('f');
	loopblock.setInit((InstructionBlock) sequentialblock.subBlocks[0]);
	return true;
    }
}
