/* CompleteSynchronized - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import jode.GlobalOptions;
import jode.expr.LocalLoadOperator;
import jode.expr.LocalStoreOperator;
import jode.expr.MonitorEnterOperator;
import jode.expr.StoreInstruction;

public class CompleteSynchronized {
    public static boolean enter(
        SynchronizedBlock synchronizedblock,
        StructuredBlock structuredblock) {
        if (!(structuredblock.outer instanceof SequentialBlock))
            return false;
        SequentialBlock sequentialblock = (SequentialBlock) synchronizedblock.outer;
        if (!(sequentialblock.subBlocks[0] instanceof InstructionBlock))
            return false;
        jode.expr.Expression expression =
            ((InstructionBlock) sequentialblock.subBlocks[0]).getInstruction();
        if (!(expression instanceof MonitorEnterOperator))
            return false;
        jode.expr.Expression expression_0_ =
            ((MonitorEnterOperator) expression).getSubExpressions()[0];

        //  xxxcr this one was funny

//        	if (!(expression_0_ instanceof LocalLoadOperator)
//        	    || (((LocalLoadOperator) expression_0_).getLocalInfo()
//        		!= synchronizedblock.local.getLocalInfo()))
//        	    return false;

        // xxxcr my replacement:

        if (expression_0_ instanceof LocalLoadOperator) {
            ((LocalLoadOperator) expression_0_).getLocalInfo();
			synchronizedblock.local.getLocalInfo();
        }

        if (expression_0_ instanceof StoreInstruction) {
            StoreInstruction storeinstruction = (StoreInstruction) expression_0_;
            if (!(storeinstruction.getLValue() instanceof LocalStoreOperator))
                return false;
            LocalStoreOperator localstoreoperator =
                (LocalStoreOperator) storeinstruction.getLValue();
            if ((localstoreoperator.getLocalInfo() != synchronizedblock.local.getLocalInfo())
                || storeinstruction.getSubExpressions()[1] == null)
                return false;
            synchronizedblock.object = storeinstruction.getSubExpressions()[1];
            synchronizedblock.moveDefinitions(structuredblock.outer, structuredblock);
        }
        

        // xxxcr replaced until here

        if (GlobalOptions.verboseLevel > 0)
            GlobalOptions.err.print('s');
        synchronizedblock.isEntered = true;
		
		synchronizedblock.moveDefinitions(structuredblock, structuredblock.outer);
        structuredblock.replace(structuredblock.outer);
        return true;
    }

    public static boolean combineObject(
        SynchronizedBlock synchronizedblock,
        StructuredBlock structuredblock) {
        if (!(structuredblock.outer instanceof SequentialBlock))
            return false;
        SequentialBlock sequentialblock = (SequentialBlock) structuredblock.outer;
        if (!(sequentialblock.subBlocks[0] instanceof InstructionBlock))
            return false;
        InstructionBlock instructionblock = (InstructionBlock) sequentialblock.subBlocks[0];
        if (!(instructionblock.getInstruction() instanceof StoreInstruction))
            return false;
        StoreInstruction storeinstruction = (StoreInstruction) instructionblock.getInstruction();
        if (!(storeinstruction.getLValue() instanceof LocalStoreOperator))
            return false;
        LocalStoreOperator localstoreoperator = (LocalStoreOperator) storeinstruction.getLValue();
        if ((localstoreoperator.getLocalInfo() != synchronizedblock.local.getLocalInfo())
            || storeinstruction.getSubExpressions()[1] == null)
            return false;
        synchronizedblock.object = storeinstruction.getSubExpressions()[1];
        synchronizedblock.moveDefinitions(structuredblock.outer, structuredblock);
        structuredblock.replace(structuredblock.outer);
        return true;
    }
}
