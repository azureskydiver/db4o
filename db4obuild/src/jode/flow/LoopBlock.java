/* LoopBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;
import java.util.Set;
import java.util.Stack;

import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.CombineableOperator;
import jode.expr.ConstOperator;
import jode.expr.Expression;
import jode.expr.LocalStoreOperator;
import jode.expr.StoreInstruction;
import jode.util.SimpleSet;

public class LoopBlock extends StructuredBlock implements BreakableBlock {
    public static final int WHILE = 0;
    public static final int DOWHILE = 1;
    public static final int FOR = 2;
    public static final int POSSFOR = 3;
    public static final Expression TRUE = new ConstOperator(Boolean.TRUE);
    public static final Expression FALSE = new ConstOperator(Boolean.FALSE);
    Expression cond;
    VariableStack condStack;
    InstructionBlock initBlock;
    InstructionBlock incrBlock;
    Expression initInstr;
    Expression incrInstr;
    boolean isDeclaration;
    int type;
    StructuredBlock bodyBlock;
    VariableStack breakedStack;
    VariableStack continueStack;
    boolean mayChangeJump = true;
    static int serialno = 0;
    String label = null;

    public StructuredBlock getNextBlock(StructuredBlock structuredblock) {
        return this;
    }

    public FlowBlock getNextFlowBlock(StructuredBlock structuredblock) {
        return null;
    }

    public LoopBlock(int i, Expression expression) {
        type = i;
        cond = expression;
        mayChangeJump = expression == TRUE;
    }

    public void setBody(StructuredBlock structuredblock) {
        bodyBlock = structuredblock;
        bodyBlock.outer = this;
        structuredblock.setFlowBlock(flowBlock);
    }

    public void setInit(InstructionBlock instructionblock) {
        if (type == 3)
            initBlock = instructionblock;
        else if (type == 2) {
            initInstr = instructionblock.getInstruction();
            instructionblock.removeBlock();
        }
    }

    public boolean conditionMatches(CombineableOperator combineableoperator) {
        return type == 3 || cond.containsMatchingLoad(combineableoperator);
    }

    public Expression getCondition() {
        return cond;
    }

    public void setCondition(Expression expression) {
        cond = expression;
        if (type == 3) {
            if (expression
                .containsMatchingLoad((CombineableOperator) incrBlock.getInstruction())) {
                type = 2;
                incrInstr = incrBlock.getInstruction();
                incrBlock.removeBlock();
                if (initBlock != null
                    && (expression
                        .containsMatchingLoad((CombineableOperator) initBlock.getInstruction()))) {
                    initInstr = initBlock.getInstruction();
                    initBlock.removeBlock();
                }
            } else
                type = 0;
            initBlock = incrBlock = null;
        }
        mayChangeJump = false;
    }

    public int getType() {
        return type;
    }

    public void setType(int i) {
        type = i;
    }

    public boolean replaceSubBlock(
        StructuredBlock structuredblock,
        StructuredBlock structuredblock_0_) {
        if (bodyBlock == structuredblock)
            bodyBlock = structuredblock_0_;
        else
            return false;
        return true;
    }

    public StructuredBlock[] getSubBlocks() {
        return new StructuredBlock[] { bodyBlock };
    }

    public void removeLocallyDeclareable(Set set) {
        if (type == 2 && initInstr instanceof StoreInstruction) {
            StoreInstruction storeinstruction = (StoreInstruction) initInstr;
            if (storeinstruction.getLValue() instanceof LocalStoreOperator) {
                LocalInfo localinfo =
                    ((LocalStoreOperator) storeinstruction.getLValue()).getLocalInfo();
                set.remove(localinfo);
            }
        }
    }

    public Set getDeclarables() {
        SimpleSet simpleset = new SimpleSet();
        if (type == 2) {
            incrInstr.fillDeclarables(simpleset);
            if (initInstr != null)
                initInstr.fillDeclarables(simpleset);
        }
        cond.fillDeclarables(simpleset);
        return simpleset;
    }

    public void checkDeclaration(Set set) {
        if (initInstr instanceof StoreInstruction
            && (((StoreInstruction) initInstr).getLValue() instanceof LocalStoreOperator)) {
            StoreInstruction storeinstruction = (StoreInstruction) initInstr;
            LocalInfo localinfo =
                ((LocalStoreOperator) storeinstruction.getLValue()).getLocalInfo();
            if (set.contains(localinfo)) {
                isDeclaration = true;
                set.remove(localinfo);
            }
        }
    }

    public void makeDeclaration(Set set) {
        if (type == 2) {
            if (initInstr != null)
                initInstr.makeDeclaration(set);
            incrInstr.makeDeclaration(set);
        }
        cond.makeDeclaration(set);
        super.makeDeclaration(set);
        if (type == 2 && initInstr != null)
            checkDeclaration(declare);
    }

    public void dumpSource(TabbedPrintWriter tabbedprintwriter) throws IOException {
        super.dumpSource(tabbedprintwriter);
    }

    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter) throws IOException {
        if (label != null) {
            tabbedprintwriter.untab();
            tabbedprintwriter.println(label + ":");
            tabbedprintwriter.tab();
        }
        boolean bool = bodyBlock.needsBraces();
        switch (type) {
            case 0 :
            case 3 :
                if (cond == TRUE)
                    tabbedprintwriter.print("for (;;)");
                else {
                    tabbedprintwriter.print("while (");
                    cond.dumpExpression(0, tabbedprintwriter);
                    tabbedprintwriter.print(")");
                }
                break;
            case 1 :
                tabbedprintwriter.print("do");
                break;
            case 2 :
                tabbedprintwriter.print("for (");
                tabbedprintwriter.startOp(0, 0);
                if (initInstr != null) {
                    if (isDeclaration) {
                        StoreInstruction storeinstruction = (StoreInstruction) initInstr;
                        LocalInfo localinfo =
                            ((LocalStoreOperator) storeinstruction.getLValue()).getLocalInfo();
                        tabbedprintwriter.startOp(1, 1);
                        localinfo.dumpDeclaration(tabbedprintwriter);
                        tabbedprintwriter.breakOp();
                        tabbedprintwriter.print(" = ");
                        storeinstruction.getSubExpressions()[1].makeInitializer(
                            localinfo.getType());
                        storeinstruction.getSubExpressions()[1].dumpExpression(
                            tabbedprintwriter,
                            100);
                        tabbedprintwriter.endOp();
                    } else
                        initInstr.dumpExpression(1, tabbedprintwriter);
                } else
                    tabbedprintwriter.print("/**/");
                tabbedprintwriter.print("; ");
                tabbedprintwriter.breakOp();
                cond.dumpExpression(2, tabbedprintwriter);
                tabbedprintwriter.print("; ");
                tabbedprintwriter.breakOp();
                incrInstr.dumpExpression(1, tabbedprintwriter);
                tabbedprintwriter.endOp();
                tabbedprintwriter.print(")");
                break;
        }
        if (bool)
            tabbedprintwriter.openBrace();
        else
            tabbedprintwriter.println();
        tabbedprintwriter.tab();
        bodyBlock.dumpSource(tabbedprintwriter);
        tabbedprintwriter.untab();
        if (type == 1) {
            if (bool)
                tabbedprintwriter.closeBraceContinue();
            tabbedprintwriter.print("while (");
            cond.dumpExpression(0, tabbedprintwriter);
            tabbedprintwriter.println(");");
        } else if (bool)
            tabbedprintwriter.closeBrace();
    }

    public String getLabel() {
        if (label == null)
            label = "while_" + serialno++ +"_";
        return label;
    }

    public void setBreaked() {
        mayChangeJump = false;
    }

    public VariableStack mapStackToLocal(VariableStack variablestack) {
        if (type == 1) {
            VariableStack variablestack_1_ = bodyBlock.mapStackToLocal(variablestack);
            if (variablestack_1_ != null)
                mergeContinueStack(variablestack_1_);
            if (continueStack != null) {
                int i = cond.getFreeOperandCount();
                VariableStack variablestack_2_;
                if (i > 0) {
                    condStack = continueStack.peek(i);
                    variablestack_2_ = continueStack.pop(i);
                } else
                    variablestack_2_ = continueStack;
                if (cond != TRUE)
                    mergeBreakedStack(variablestack_2_);
                if (cond != FALSE)
                    variablestack.merge(variablestack_2_);
            }
        } else {
            continueStack = variablestack;
            int i = cond.getFreeOperandCount();
            VariableStack variablestack_3_;
            if (i > 0) {
                condStack = variablestack.peek(i);
                variablestack_3_ = variablestack.pop(i);
            } else
                variablestack_3_ = variablestack;
            if (cond != TRUE)
                breakedStack = variablestack_3_;
            VariableStack variablestack_4_ = bodyBlock.mapStackToLocal(variablestack_3_);
            if (variablestack_4_ != null)
                mergeContinueStack(variablestack_4_);
        }
        return breakedStack;
    }

    public void mergeContinueStack(VariableStack variablestack) {
        if (continueStack == null)
            continueStack = variablestack;
        else
            continueStack.merge(variablestack);
    }

    public void mergeBreakedStack(VariableStack variablestack) {
        if (breakedStack != null)
            breakedStack.merge(variablestack);
        else
            breakedStack = variablestack;
    }

    public void removePush() {
        if (condStack != null)
            cond = condStack.mergeIntoExpression(cond);
        bodyBlock.removePush();
    }

    public void removeOnetimeLocals() {
        cond = cond.removeOnetimeLocals();
        if (type == 2) {
            if (initInstr != null)
                initInstr.removeOnetimeLocals();
            incrInstr.removeOnetimeLocals();
        }
        super.removeOnetimeLocals();
    }

    public void replaceBreakContinue(BreakableBlock breakableblock) {
        Stack stack = new Stack();
        stack.push(breakableblock);
        while (!stack.isEmpty()) {
            StructuredBlock[] structuredblocks = ((StructuredBlock) stack.pop()).getSubBlocks();
            for (int i = 0; i < structuredblocks.length; i++) {
                if (structuredblocks[i] instanceof BreakBlock) {
                    BreakBlock breakblock = (BreakBlock) structuredblocks[i];
                    if (breakblock.breaksBlock == breakableblock)
                        new ContinueBlock(this, breakblock.label != null).replace(breakblock);
                }
                stack.push(structuredblocks[i]);
            }
        }
    }

    public boolean jumpMayBeChanged() {
        return mayChangeJump;
    }

    public void simplify() {
        cond = cond.simplify();
        if (type == 2) {
            incrInstr = incrInstr.simplify();
            if (initInstr != null)
                initInstr = initInstr.simplify();
        }
        super.simplify();
    }

    public boolean doTransformations() {
        return (
            (initBlock == null && type == 3 || initInstr == null && type == 2)
                && CreateForInitializer.transform(this, flowBlock.lastModified));
    }
}
