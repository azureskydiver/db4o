/* StructuredBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import jode.AssertError;
import jode.GlobalOptions;
import jode.decompiler.ClassAnalyzer;
import jode.decompiler.Declarable;
import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.util.SimpleSet;

public abstract class StructuredBlock {
    Set used;
    Set declare;
    Set done;
    StructuredBlock outer;
    FlowBlock flowBlock;
    Jump jump;

    public StructuredBlock getNextBlock() {
        if (jump != null)
            return null;
        if (outer != null)
            return outer.getNextBlock(this);
        return null;
    }

    public void setJump(Jump jump) {
        this.jump = jump;
        jump.prev = this;
    }

    public FlowBlock getNextFlowBlock() {
        if (jump != null)
            return jump.destination;
        if (outer != null)
            return outer.getNextFlowBlock(this);
        return null;
    }

    public StructuredBlock getNextBlock(StructuredBlock structuredblock_0_) {
        return getNextBlock();
    }

    public FlowBlock getNextFlowBlock(StructuredBlock structuredblock_1_) {
        return getNextFlowBlock();
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean isSingleExit(StructuredBlock structuredblock_2_) {
        return false;
    }

    public boolean replaceSubBlock(
        StructuredBlock structuredblock_3_,
        StructuredBlock structuredblock_4_) {
        return false;
    }

    public StructuredBlock[] getSubBlocks() {
        return new StructuredBlock[0];
    }

    public boolean contains(StructuredBlock structuredblock_5_) {
            for (/**/;
                structuredblock_5_ != this && structuredblock_5_ != null;
                structuredblock_5_ = structuredblock_5_.outer) {
            /* empty */
        }
        return structuredblock_5_ == this;
    }

    public final void removeJump() {
        if (jump != null) {
            jump.prev = null;
            jump = null;
        }
    }

    void moveDefinitions(StructuredBlock structuredblock_6_, StructuredBlock structuredblock_7_) {
        /* empty */
    }

    public void replace(StructuredBlock structuredblock_8_) {
        outer = structuredblock_8_.outer;
        setFlowBlock(structuredblock_8_.flowBlock);
        if (outer != null)
            outer.replaceSubBlock(structuredblock_8_, this);
        else
            flowBlock.block = this;
    }

    public void swapJump(StructuredBlock structuredblock_9_) {
        Jump jump = structuredblock_9_.jump;
        structuredblock_9_.jump = this.jump;
        this.jump = jump;
        this.jump.prev = this;
        structuredblock_9_.jump.prev = structuredblock_9_;
    }

    public void moveJump(Jump jump) {
        if (this.jump != null)
            throw new AssertError("overriding with moveJump()");
        this.jump = jump;
        if (jump != null) {
            jump.prev.jump = null;
            jump.prev = this;
        }
    }

    public void copyJump(Jump jump) {
        if (this.jump != null)
            throw new AssertError("overriding with moveJump()");
        if (jump != null) {
            this.jump = new Jump(jump);
            this.jump.prev = this;
        }
    }

    public StructuredBlock appendBlock(StructuredBlock structuredblock_10_) {
        if (structuredblock_10_ instanceof EmptyBlock) {
            moveJump(structuredblock_10_.jump);
            return this;
        }
        SequentialBlock sequentialblock = new SequentialBlock();
        sequentialblock.replace(this);
        sequentialblock.setFirst(this);
        sequentialblock.setSecond(structuredblock_10_);
        return sequentialblock;
    }

    public StructuredBlock prependBlock(StructuredBlock structuredblock_11_) {
        SequentialBlock sequentialblock = new SequentialBlock();
        sequentialblock.replace(this);
        sequentialblock.setFirst(structuredblock_11_);
        sequentialblock.setSecond(this);
        return sequentialblock;
    }

    public final void removeBlock() {
        if (outer instanceof SequentialBlock) {
            if (outer.getSubBlocks()[1] == this) {
                if (jump != null)
                    outer.getSubBlocks()[0].moveJump(jump);
                outer.getSubBlocks()[0].replace(outer);
            } else
                outer.getSubBlocks()[1].replace(outer);
        } else {
            EmptyBlock emptyblock = new EmptyBlock();
            emptyblock.moveJump(jump);
            emptyblock.replace(this);
        }
    }

    public boolean flowMayBeChanged() {
        return jump != null || jumpMayBeChanged();
    }

    public boolean jumpMayBeChanged() {
        return false;
    }

    public Set getDeclarables() {
        return Collections.EMPTY_SET;
    }

    public Set propagateUsage() {
        used = new SimpleSet();
        used.addAll(getDeclarables());
        StructuredBlock[] structuredblocks = getSubBlocks();
        SimpleSet simpleset = new SimpleSet();
        simpleset.addAll(used);
        for (int i = 0; i < structuredblocks.length; i++) {
            Set set = structuredblocks[i].propagateUsage();
            SimpleSet simpleset_12_ = new SimpleSet();
            simpleset_12_.addAll(set);
            simpleset_12_.retainAll(simpleset);
            used.addAll(simpleset_12_);
            simpleset.addAll(set);
        }
        return simpleset;
    }

    public VariableStack mapStackToLocal(VariableStack variablestack) {
        StructuredBlock[] structuredblocks = getSubBlocks();
        VariableStack variablestack_13_;
        if (structuredblocks.length == 0)
            variablestack_13_ = variablestack;
        else {
            variablestack_13_ = null;
            for (int i = 0; i < structuredblocks.length; i++)
                variablestack_13_ =
                    VariableStack.merge(
                        variablestack_13_,
                        structuredblocks[i].mapStackToLocal(variablestack));
        }
        if (jump != null) {
            jump.stackMap = variablestack_13_;
            return null;
        }
        return variablestack_13_;
    }

    public void removePush() {
        StructuredBlock[] structuredblocks = getSubBlocks();
        for (int i = 0; i < structuredblocks.length; i++)
            structuredblocks[i].removePush();
    }

    public void removeOnetimeLocals() {
        StructuredBlock[] structuredblocks = getSubBlocks();
        for (int i = 0; i < structuredblocks.length; i++)
            structuredblocks[i].removeOnetimeLocals();
    }

    public void makeDeclaration(Set set) {
        done = new SimpleSet();
        done.addAll(set);
        declare = new SimpleSet();
        Iterator iterator = used.iterator();
        while_18_ : while (iterator.hasNext()) {
            Declarable declarable = (Declarable) iterator.next();
            if (!set.contains(declarable)) {
                if (declarable instanceof LocalInfo) {
                    LocalInfo localinfo = (LocalInfo) declarable;
                    String string = localinfo.guessName();
                    Iterator iterator_14_ = set.iterator();
                    while (iterator_14_.hasNext()) {
                        Declarable declarable_15_ = (Declarable) iterator_14_.next();
                        if (declarable_15_ instanceof LocalInfo) {
                            LocalInfo localinfo_16_ = (LocalInfo) declarable_15_;
                            if ((localinfo_16_.getMethodAnalyzer()
                                == localinfo.getMethodAnalyzer())
                                && (localinfo_16_.getSlot() == localinfo.getSlot())
                                && localinfo_16_.getType().isOfType(localinfo.getType())
                                && (localinfo_16_.isNameGenerated()
                                    || localinfo.isNameGenerated()
                                    || string.equals(localinfo_16_.getName()))
                                && !localinfo_16_.isFinal()
                                && !localinfo.isFinal()
                                && localinfo_16_.getExpression() == null
                                && localinfo.getExpression() == null) {
                                localinfo.combineWith(localinfo_16_);
                                continue while_18_;
                            }
                        }
                    }
                }
                if (declarable.getName() != null) {
                    Iterator iterator_17_ = set.iterator();
                    while (iterator_17_.hasNext()) {
                        Declarable declarable_18_ = (Declarable) iterator_17_.next();
                        if (declarable.getName().equals(declarable_18_.getName())) {
                            declarable.makeNameUnique();
                            break;
                        }
                    }
                }
                set.add(declarable);
                declare.add(declarable);
                if (declarable instanceof ClassAnalyzer){
                    ((ClassAnalyzer) declarable).makeDeclaration(set);
                }
            }
        }
        StructuredBlock[] structuredblocks = getSubBlocks();
        for (int i = 0; i < structuredblocks.length; i++){
			structuredblocks[i].makeDeclaration(set);
        }
       	set.removeAll(declare);
    }

    public void checkConsistent() {
        StructuredBlock[] structuredblocks = getSubBlocks();
        for (int i = 0; i < structuredblocks.length; i++) {
            if (structuredblocks[i].outer != this || structuredblocks[i].flowBlock != flowBlock)
                throw new AssertError("Inconsistency");
            structuredblocks[i].checkConsistent();
        }
        if (this.jump != null && this.jump.destination != null) {
            for (Jump jump = flowBlock.getJumps(this.jump.destination);
                jump != this.jump;
                jump = jump.next) {
                if (jump == null)
                    throw new AssertError("Inconsistency");
            }
        }
    }

    public void setFlowBlock(FlowBlock flowblock) {
        if (flowBlock != flowblock) {
            flowBlock = flowblock;
            StructuredBlock[] structuredblocks = getSubBlocks();
            for (int i = 0; i < structuredblocks.length; i++) {
                if (structuredblocks[i] != null)
                    structuredblocks[i].setFlowBlock(flowblock);
            }
        }
    }

    public boolean needsBraces() {
        return true;
    }

    public void fillInGenSet(Set set, Set set_19_) {
        /* empty */
    }

    public void fillSuccessors() {
        if (jump != null)
            flowBlock.addSuccessor(jump);
        StructuredBlock[] structuredblocks = getSubBlocks();
        for (int i = 0; i < structuredblocks.length; i++)
            structuredblocks[i].fillSuccessors();
    }

    public void dumpSource(TabbedPrintWriter tabbedprintwriter) throws IOException {
        if ((GlobalOptions.debuggingFlags & 0x100) != 0) {
            if (declare != null)
                tabbedprintwriter.println("declaring: " + declare);
            if (done != null)
                tabbedprintwriter.println("done: " + done);
            tabbedprintwriter.println("using: " + used);
        }
        if (declare != null) {
            Iterator iterator = declare.iterator();
            while (iterator.hasNext()) {
                Declarable declarable = (Declarable) iterator.next();
                declarable.dumpDeclaration(tabbedprintwriter);
                tabbedprintwriter.println(";");
            }
        }
        dumpInstruction(tabbedprintwriter);
        if (jump != null)
            jump.dumpSource(tabbedprintwriter);
    }

    public abstract void dumpInstruction(TabbedPrintWriter tabbedprintwriter) throws IOException;

    public String toString() {
        try {
            StringWriter stringwriter = new StringWriter();
            TabbedPrintWriter tabbedprintwriter = new TabbedPrintWriter(stringwriter);
            tabbedprintwriter.println(super.toString());
            tabbedprintwriter.tab();
            dumpSource(tabbedprintwriter);
            return stringwriter.toString();
        } catch (IOException ioexception) {
            return super.toString();
        }
    }

    public void simplify() {
        StructuredBlock[] structuredblocks = getSubBlocks();
        for (int i = 0; i < structuredblocks.length; i++)
            structuredblocks[i].simplify();
    }

    public boolean doTransformations() {
        return false;
    }
}
