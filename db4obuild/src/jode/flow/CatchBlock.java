/* CatchBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import jode.decompiler.Declarable;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;
import jode.expr.LocalLoadOperator;
import jode.expr.LocalStoreOperator;
import jode.expr.NopOperator;
import jode.expr.StoreInstruction;
import jode.type.Type;

public class CatchBlock extends StructuredBlock {
    StructuredBlock catchBlock;
    Type exceptionType;
    LocalInfo exceptionLocal;
    LocalInfo pushedLocal;

    public CatchBlock(Type type) {
        exceptionType = type;
    }

    public Type getExceptionType() {
        return exceptionType;
    }

    public LocalInfo getLocal() {
        return exceptionLocal;
    }

    public void setCatchBlock(StructuredBlock structuredblock) {
        catchBlock = structuredblock;
        structuredblock.outer = this;
        structuredblock.setFlowBlock(flowBlock);
        if (exceptionLocal == null)
            combineLocal();
    }

    public boolean replaceSubBlock(
        StructuredBlock structuredblock,
        StructuredBlock structuredblock_0_) {
        if (catchBlock == structuredblock)
            catchBlock = structuredblock_0_;
        else
            return false;
        return true;
    }

    public StructuredBlock[] getSubBlocks() {
        return new StructuredBlock[] { catchBlock };
    }

    public VariableStack mapStackToLocal(VariableStack variablestack) {
        VariableStack variablestack_1_;
        if (exceptionLocal == null) {
            pushedLocal = new LocalInfo();
            pushedLocal.setType(exceptionType);
            variablestack_1_ = variablestack.push(pushedLocal);
        } else
            variablestack_1_ = variablestack;
        return super.mapStackToLocal(variablestack_1_);
    }

    public void removePush() {
        if (pushedLocal != null)
            exceptionLocal = pushedLocal;
        super.removePush();
    }

    public Set getDeclarables() {
        if (exceptionLocal != null)
            return Collections.singleton(exceptionLocal);
        return Collections.EMPTY_SET;
    }

    public void makeDeclaration(Set set) {
        super.makeDeclaration(set);
        if (exceptionLocal != null) {
            if (declare.contains(exceptionLocal)) {
                declare.remove(exceptionLocal);
            } else {
                LocalInfo localinfo = new LocalInfo();
                Expression expression =
                    (new StoreInstruction(new LocalStoreOperator(exceptionLocal.getType(),
                        exceptionLocal))
                        .addOperand(new LocalLoadOperator(localinfo.getType(), null, localinfo)));
                StructuredBlock instructionblock = new InstructionBlock(expression);
                instructionblock.setFlowBlock(flowBlock);
                
                // xxxcr return value was ignored
                instructionblock =  instructionblock.appendBlock(catchBlock);
                catchBlock = instructionblock;
                exceptionLocal = localinfo;
                String string = localinfo.guessName();
                Iterator iterator = set.iterator();
                while (iterator.hasNext()) {
                    Declarable declarable = (Declarable) iterator.next();
                    if (string.equals(declarable.getName())) {
                        localinfo.makeNameUnique();
                        break;
                    }
                }
            }
        }
    }

    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter) throws IOException {
        tabbedprintwriter.closeBraceContinue();
        tabbedprintwriter.print("catch (");
        tabbedprintwriter.printType(exceptionType);
        tabbedprintwriter.print(
            " " + (exceptionLocal != null ? exceptionLocal.getName() : "PUSH") + ")");
        tabbedprintwriter.openBrace();
        tabbedprintwriter.tab();
        catchBlock.dumpSource(tabbedprintwriter);
        tabbedprintwriter.untab();
    }

    public boolean jumpMayBeChanged() {
        return catchBlock.jump != null || catchBlock.jumpMayBeChanged();
    }

    public boolean combineLocal() {
        StructuredBlock structuredblock =
            (catchBlock instanceof SequentialBlock ? catchBlock.getSubBlocks()[0] : catchBlock);
        if (structuredblock instanceof SpecialBlock
            && ((SpecialBlock) structuredblock).type == SpecialBlock.POP
            && ((SpecialBlock) structuredblock).count == 1) {
            exceptionLocal = new LocalInfo();
            exceptionLocal.setType(exceptionType);
            structuredblock.removeBlock();
            return true;
        }
        if (structuredblock instanceof InstructionBlock) {
            Expression expression = ((InstructionBlock) structuredblock).getInstruction();
            if (expression instanceof StoreInstruction) {
                StoreInstruction storeinstruction = (StoreInstruction) expression;
                if (storeinstruction.getOperatorIndex() == 12
                    && (storeinstruction.getSubExpressions()[1] instanceof NopOperator)
                    && (storeinstruction.getLValue() instanceof LocalStoreOperator)) {
                    exceptionLocal =
                        ((LocalStoreOperator) storeinstruction.getLValue()).getLocalInfo();
                    exceptionLocal.setType(exceptionType);
                    structuredblock.removeBlock();
                    return true;
                }
            }
        }
        return false;
    }
}
