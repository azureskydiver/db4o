/* FlowBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jode.AssertError;
import jode.GlobalOptions;
import jode.decompiler.LocalInfo;
import jode.decompiler.MethodAnalyzer;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.CombineableOperator;
import jode.expr.Expression;
import jode.util.SimpleMap;

public class FlowBlock
{
    public static FlowBlock END_OF_METHOD = new FlowBlock(null, 2147483647);
    public static FlowBlock NEXT_BY_ADDR;
    MethodAnalyzer method;
    private SlotSet in = new SlotSet();
    VariableSet gen = new VariableSet();
    private int addr;
    private int length;
    StructuredBlock block;
    StructuredBlock lastModified;
    private Map successors = new SimpleMap();
    List predecessors = new ArrayList();
    FlowBlock nextByAddr;
    FlowBlock prevByAddr;
    VariableStack stackMap;
    static int serialno;
    String label = null;
    
    static class SuccessorInfo
    {
	SlotSet kill;
	VariableSet gen;
	Jump jumps;
    }
    
    public FlowBlock(MethodAnalyzer methodanalyzer, int i) {
	method = methodanalyzer;
	addr = i;
    }
    
    public final int getNextAddr() {
	return addr + length;
    }
    
    public boolean hasNoJumps() {
	return successors.size() == 0 && predecessors.size() == 0;
    }
    
    public Jump resolveSomeJumps(Jump jump, FlowBlock flowblock_0_) {
	Jump jump_1_ = null;
	if (lastModified.jump == null) {
	    Jump jump_2_ = new Jump(flowblock_0_);
	    lastModified.setJump(jump_2_);
	    jump_1_ = jump_2_;
	}
	for (Jump jump_3_ = jump; jump_3_ != null; jump_3_ = jump_3_.next) {
	    if (jump_3_.prev.outer instanceof ConditionalBlock
		&& jump_3_.prev.outer.jump != null) {
		StructuredBlock structuredblock = jump_3_.prev;
		ConditionalBlock conditionalblock
		    = (ConditionalBlock) structuredblock.outer;
		Expression expression = conditionalblock.getInstruction();
		conditionalblock.setInstruction(expression.negate());
		conditionalblock.swapJump(structuredblock);
	    }
	}
	while (jump != null) {
	    Jump jump_4_ = jump;
	    jump = jump.next;
	    if (jump_4_.prev == lastModified) {
		jump_4_.next = jump_1_;
		jump_1_ = jump_4_;
	    } else {
		if (jump_4_.prev.outer instanceof ConditionalBlock) {
		    StructuredBlock structuredblock = jump_4_.prev;
		    ConditionalBlock conditionalblock
			= (ConditionalBlock) structuredblock.outer;
		    Expression expression = conditionalblock.getInstruction();
		    if (conditionalblock.jump != null) {
			structuredblock.removeJump();
			IfThenElseBlock ifthenelseblock
			    = new IfThenElseBlock(conditionalblock
						      .getInstruction
						      ().negate());
			ifthenelseblock.moveDefinitions(conditionalblock,
							structuredblock);
			ifthenelseblock.replace(conditionalblock);
			ifthenelseblock.moveJump(conditionalblock.jump);
			ifthenelseblock.setThenBlock(structuredblock);
			if (conditionalblock == lastModified)
			    lastModified = ifthenelseblock;
			continue;
		    }
		    if (conditionalblock.outer instanceof LoopBlock
			|| (conditionalblock.outer instanceof SequentialBlock
			    && (conditionalblock.outer.getSubBlocks()[0]
				== conditionalblock)
			    && (conditionalblock.outer.outer
				instanceof LoopBlock))) {
			LoopBlock loopblock
			    = (conditionalblock.outer instanceof LoopBlock
			       ? (LoopBlock) conditionalblock.outer
			       : (LoopBlock) conditionalblock.outer.outer);
			if (loopblock.getCondition() == LoopBlock.TRUE
			    && loopblock.getType() != 1
			    && (loopblock.jumpMayBeChanged()
				|| (loopblock.getNextFlowBlock()
				    == flowblock_0_))) {
			    if (loopblock.jump == null) {
				loopblock.moveJump(jump_4_);
				jump = jump_4_;
			    } else
				jump_4_.prev.removeJump();
			    loopblock.setCondition(expression.negate());
			    loopblock.moveDefinitions(conditionalblock, null);
			    conditionalblock.removeBlock();
			    continue;
			}
		    } else if ((conditionalblock.outer
				instanceof SequentialBlock)
			       && (conditionalblock.outer.getSubBlocks()[1]
				   == conditionalblock)) {
			StructuredBlock structuredblock_5_;
			for (structuredblock_5_ = conditionalblock.outer.outer;
			     structuredblock_5_ instanceof SequentialBlock;
			     structuredblock_5_ = structuredblock_5_.outer) {
			    /* empty */
			}
			if (structuredblock_5_ instanceof LoopBlock) {
			    LoopBlock loopblock
				= (LoopBlock) structuredblock_5_;
			    if (loopblock.getCondition() == LoopBlock.TRUE
				&& loopblock.getType() == 0
				&& (loopblock.jumpMayBeChanged()
				    || (loopblock.getNextFlowBlock()
					== flowblock_0_))) {
				if (loopblock.jump == null) {
				    loopblock.moveJump(jump_4_);
				    jump = jump_4_;
				} else
				    jump_4_.prev.removeJump();
				loopblock.setType(1);
				loopblock.setCondition(expression.negate());
				loopblock.moveDefinitions(conditionalblock,
							  null);
				conditionalblock.removeBlock();
				continue;
			    }
			}
		    }
		    if (conditionalblock.outer instanceof SequentialBlock
			&& (conditionalblock.outer.getSubBlocks()[0]
			    == conditionalblock)
			&& ((conditionalblock.outer.getNextFlowBlock()
			     == flowblock_0_)
			    || conditionalblock.outer.jumpMayBeChanged())) {
			SequentialBlock sequentialblock
			    = (SequentialBlock) conditionalblock.outer;
			IfThenElseBlock ifthenelseblock
			    = new IfThenElseBlock(expression.negate());
			StructuredBlock structuredblock_6_
			    = sequentialblock.getSubBlocks()[1];
			ifthenelseblock.moveDefinitions(sequentialblock,
							structuredblock_6_);
			ifthenelseblock.replace(sequentialblock);
			ifthenelseblock.setThenBlock(structuredblock_6_);
			if (structuredblock_6_.contains(lastModified)) {
			    if (lastModified.jump.destination
				== flowblock_0_) {
				ifthenelseblock.moveJump(lastModified.jump);
				lastModified = ifthenelseblock;
				jump_4_.prev.removeJump();
				continue;
			    }
			    lastModified = ifthenelseblock;
			}
			ifthenelseblock.moveJump(jump_4_);
			jump = jump_4_;
			continue;
		    }
		} else {
		    if (jump_4_.destination
			== jump_4_.prev.outer.getNextFlowBlock(jump_4_.prev)) {
			jump_4_.prev.removeJump();
			continue;
		    }
		    StructuredBlock structuredblock;
		    for (structuredblock = jump_4_.prev.outer;
			 structuredblock instanceof SequentialBlock;
			 structuredblock = structuredblock.outer) {
			/* empty */
		    }
		    if (structuredblock instanceof IfThenElseBlock) {
			IfThenElseBlock ifthenelseblock
			    = (IfThenElseBlock) structuredblock;
			if (ifthenelseblock.elseBlock == null
			    && ifthenelseblock.jump != null) {
			    ifthenelseblock.setElseBlock(new EmptyBlock());
			    ifthenelseblock.elseBlock
				.moveJump(ifthenelseblock.jump);
			    ifthenelseblock.moveJump(jump_4_);
			    jump = jump_4_;
			    continue;
			}
		    }
		    if (structuredblock instanceof IfThenElseBlock
			&& structuredblock.outer instanceof SequentialBlock
			&& (structuredblock.outer.getSubBlocks()[0]
			    == structuredblock)) {
			IfThenElseBlock ifthenelseblock
			    = (IfThenElseBlock) structuredblock;
			SequentialBlock sequentialblock
			    = (SequentialBlock) structuredblock.outer;
			StructuredBlock structuredblock_7_
			    = sequentialblock.subBlocks[1];
			if (ifthenelseblock.elseBlock == null
			    && ((structuredblock_7_.getNextFlowBlock()
				 == flowblock_0_)
				|| structuredblock_7_.jump != null
				|| structuredblock_7_.jumpMayBeChanged())) {
			    ifthenelseblock.replace(sequentialblock);
			    ifthenelseblock.setElseBlock(structuredblock_7_);
			    if (structuredblock_7_.contains(lastModified)) {
				if (lastModified.jump.destination
				    == flowblock_0_) {
				    ifthenelseblock
					.moveJump(lastModified.jump);
				    lastModified = ifthenelseblock;
				    jump_4_.prev.removeJump();
				    continue;
				}
				lastModified = ifthenelseblock;
			    }
			    ifthenelseblock.moveJump(jump_4_);
			    jump = jump_4_;
			    continue;
			}
		    }
		}
		for (StructuredBlock structuredblock = jump_4_.prev.outer;
		     structuredblock != null;
		     structuredblock = structuredblock.outer) {
		    if (structuredblock instanceof BreakableBlock) {
			if (structuredblock.getNextFlowBlock() == flowblock_0_)
			    break;
			if (structuredblock.jumpMayBeChanged()) {
			    structuredblock.setJump(new Jump(flowblock_0_));
			    structuredblock.jump.next = jump;
			    jump = structuredblock.jump;
			    break;
			}
			if (flowblock_0_ == END_OF_METHOD)
			    break;
		    }
		}
		jump_4_.next = jump_1_;
		jump_1_ = jump_4_;
	    }
	}
	return jump_1_;
    }
    
    void resolveRemaining(Jump jump) {
	LoopBlock loopblock = null;
	StructuredBlock structuredblock = lastModified;
	boolean bool = false;
	for (/**/; jump != null; jump = jump.next) {
	    StructuredBlock structuredblock_8_ = jump.prev;
	    if (structuredblock_8_ == lastModified)
		bool = true;
	    else {
		int i = 0;
		BreakableBlock breakableblock = null;
		for (StructuredBlock structuredblock_9_
			 = structuredblock_8_.outer;
		     structuredblock_9_ != null;
		     structuredblock_9_ = structuredblock_9_.outer) {
		    if (structuredblock_9_ instanceof BreakableBlock) {
			i++;
			if (structuredblock_9_.getNextFlowBlock()
			    == jump.destination) {
			    breakableblock
				= (BreakableBlock) structuredblock_9_;
			    break;
			}
		    }
		}
		structuredblock_8_.removeJump();
		if (breakableblock == null) {
		    if (loopblock == null)
			loopblock = new LoopBlock(1, LoopBlock.FALSE);
		    for (/**/; !structuredblock.contains(structuredblock_8_);
			 structuredblock = structuredblock.outer) {
			/* empty */
		    }
		    structuredblock_8_.appendBlock(new BreakBlock(loopblock,
								  i > 0));
		} else
		    structuredblock_8_
			.appendBlock(new BreakBlock(breakableblock, i > 1));
	    }
	}
	if (bool)
	    lastModified.removeJump();
	if (loopblock != null) {
	    loopblock.replace(structuredblock);
	    loopblock.setBody(structuredblock);
	    lastModified = loopblock;
	}
    }
    
    void mergeSuccessors(FlowBlock flowblock_10_) {
	Iterator iterator = flowblock_10_.successors.entrySet().iterator();
	while (iterator.hasNext()) {
	    Map.Entry entry = (Map.Entry) iterator.next();
	    FlowBlock flowblock_11_ = (FlowBlock) entry.getKey();
	    SuccessorInfo successorinfo = (SuccessorInfo) entry.getValue();
	    SuccessorInfo successorinfo_12_
		= (SuccessorInfo) successors.get(flowblock_11_);
	    if (flowblock_11_ != END_OF_METHOD)
		flowblock_11_.predecessors.remove(flowblock_10_);
	    if (successorinfo_12_ == null) {
		if (flowblock_11_ != END_OF_METHOD)
		    flowblock_11_.predecessors.add(this);
		successors.put(flowblock_11_, successorinfo);
	    } else {
		successorinfo_12_.gen.addAll(successorinfo.gen);
		successorinfo_12_.kill.retainAll(successorinfo.kill);
		Jump jump;
		for (jump = successorinfo_12_.jumps; jump.next != null;
		     jump = jump.next) {
		    /* empty */
		}
		jump.next = successorinfo.jumps;
	    }
	}
    }
    
    public void mergeAddr(FlowBlock flowblock_13_) {
	if (flowblock_13_.nextByAddr == this
	    || flowblock_13_.prevByAddr == null) {
	    flowblock_13_.nextByAddr.addr = flowblock_13_.addr;
	    flowblock_13_.nextByAddr.length += flowblock_13_.length;
	    flowblock_13_.nextByAddr.prevByAddr = flowblock_13_.prevByAddr;
	    if (flowblock_13_.prevByAddr != null)
		flowblock_13_.prevByAddr.nextByAddr = flowblock_13_.nextByAddr;
	} else {
	    flowblock_13_.prevByAddr.length += flowblock_13_.length;
	    flowblock_13_.prevByAddr.nextByAddr = flowblock_13_.nextByAddr;
	    if (flowblock_13_.nextByAddr != null)
		flowblock_13_.nextByAddr.prevByAddr = flowblock_13_.prevByAddr;
	}
    }
    
    void updateInOut(FlowBlock flowblock_14_, SuccessorInfo successorinfo) {
	SlotSet slotset = successorinfo.kill;
	VariableSet variableset = successorinfo.gen;
	flowblock_14_.in.merge(variableset);
	SlotSet slotset_15_ = (SlotSet) flowblock_14_.in.clone();
	slotset_15_.removeAll(slotset);
	Iterator iterator = flowblock_14_.successors.values().iterator();
	while (iterator.hasNext()) {
	    SuccessorInfo successorinfo_16_ = (SuccessorInfo) iterator.next();
	    successorinfo_16_.gen.mergeGenKill(variableset,
					       successorinfo_16_.kill);
	    if (flowblock_14_ != this)
		successorinfo_16_.kill.mergeKill(slotset);
	}
	in.addAll(slotset_15_);
	gen.addAll(flowblock_14_.gen);
	if ((GlobalOptions.debuggingFlags & 0x10) != 0) {
	    GlobalOptions.err.println("UpdateInOut: gens : " + variableset);
	    GlobalOptions.err.println("             kills: " + slotset);
	    GlobalOptions.err
		.println("             s.in : " + flowblock_14_.in);
	    GlobalOptions.err.println("             in   : " + in);
	}
    }
    
    public void updateInOutCatch(FlowBlock flowblock_17_) {
	VariableSet variableset = ((TryBlock) block).gen;
	flowblock_17_.in.merge(variableset);
	Iterator iterator = flowblock_17_.successors.values().iterator();
	while (iterator.hasNext()) {
	    SuccessorInfo successorinfo = (SuccessorInfo) iterator.next();
	    successorinfo.gen.mergeGenKill(variableset, successorinfo.kill);
	}
	in.addAll(flowblock_17_.in);
	gen.addAll(flowblock_17_.gen);
	if ((GlobalOptions.debuggingFlags & 0x10) != 0) {
	    GlobalOptions.err
		.println("UpdateInOutCatch: gens : " + variableset);
	    GlobalOptions.err
		.println("                  s.in : " + flowblock_17_.in);
	    GlobalOptions.err.println("                  in   : " + in);
	}
    }
    
    public void checkConsistent() {
	if ((GlobalOptions.debuggingFlags & 0x80) != 0) {
	    try {
		if (block.outer != null || block.flowBlock != this)
		    throw new AssertError("Inconsistency");
		block.checkConsistent();
		Iterator iterator = predecessors.iterator();
		while (iterator.hasNext()) {
		    FlowBlock flowblock_18_ = (FlowBlock) iterator.next();
		    if (flowblock_18_ != null
			&& !flowblock_18_.successors.containsKey(this))
			throw new AssertError("Inconsistency");
		}
		StructuredBlock structuredblock;
		for (structuredblock = lastModified;
		     (structuredblock.outer instanceof SequentialBlock
		      || structuredblock.outer instanceof TryBlock
		      || structuredblock.outer instanceof FinallyBlock);
		     structuredblock = structuredblock.outer) {
		    /* empty */
		}
		if (structuredblock.outer != null)
		    throw new AssertError("Inconsistency");
		Iterator iterator_19_ = successors.entrySet().iterator();
		while (iterator_19_.hasNext()) {
		    Map.Entry entry = (Map.Entry) iterator_19_.next();
		    FlowBlock flowblock_20_ = (FlowBlock) entry.getKey();
		    if (flowblock_20_.predecessors.contains(this)
			== (flowblock_20_ == END_OF_METHOD))
			throw new AssertError("Inconsistency");
		    Jump jump = ((SuccessorInfo) entry.getValue()).jumps;
		    if (jump == null)
			throw new AssertError("Inconsistency");
		    for (/**/; jump != null; jump = jump.next) {
			if (jump.destination != flowblock_20_)
			    throw new AssertError("Inconsistency");
			if (jump.prev == null || jump.prev.flowBlock != this
			    || jump.prev.jump != jump)
			    throw new AssertError("Inconsistency");
		    while_24_:
			for (StructuredBlock structuredblock_21_ = jump.prev;
			     structuredblock_21_ != block;
			     structuredblock_21_ = structuredblock_21_.outer) {
			    if (structuredblock_21_.outer == null)
				throw new RuntimeException("Inconsistency");
			    StructuredBlock[] structuredblocks
				= structuredblock_21_.outer.getSubBlocks();
			    for (int i = 0; i < structuredblocks.length; i++) {
				if (structuredblocks[i] == structuredblock_21_)
				    continue while_24_;
			    }
			    throw new AssertError("Inconsistency");
			}
		    }
		}
	    } catch (AssertError asserterror) {
		GlobalOptions.err.println("Inconsistency in: " + this);
		throw asserterror;
	    }
	}
    }
    
    public void appendBlock(StructuredBlock structuredblock, int i) {
	SlotSet slotset = new SlotSet();
	SlotSet slotset_22_ = new SlotSet();
	VariableSet variableset = new VariableSet();
	structuredblock.fillInGenSet(slotset, slotset_22_);
	variableset.addAll(slotset_22_);
	if (block == null) {
	    block = structuredblock;
	    lastModified = structuredblock;
	    structuredblock.setFlowBlock(this);
	    structuredblock.fillSuccessors();
	    length = i;
	    in = slotset;
	    gen = variableset;
	    Iterator iterator = successors.values().iterator();
	    while (iterator.hasNext()) {
		SuccessorInfo successorinfo = (SuccessorInfo) iterator.next();
		successorinfo.gen = new VariableSet();
		successorinfo.kill = new SlotSet();
		successorinfo.gen.addAll(variableset);
		successorinfo.kill.addAll(slotset_22_);
	    }
	} else if (!(structuredblock instanceof EmptyBlock)) {
	    checkConsistent();
	    if ((GlobalOptions.debuggingFlags & 0x8) != 0)
		GlobalOptions.err
		    .println("appending Block: " + structuredblock);
	    SuccessorInfo successorinfo
		= (SuccessorInfo) successors.get(NEXT_BY_ADDR);
	    slotset.merge(successorinfo.gen);
	    slotset.removeAll(successorinfo.kill);
	    variableset.mergeGenKill(successorinfo.gen, slotset_22_);
	    slotset_22_.mergeKill(successorinfo.kill);
	    in.addAll(slotset);
	    gen.addAll(slotset_22_);
	    removeSuccessor(lastModified.jump);
	    lastModified.removeJump();
	    lastModified = lastModified.appendBlock(structuredblock);
	    structuredblock.fillSuccessors();
	    successorinfo = (SuccessorInfo) successors.get(NEXT_BY_ADDR);
	    successorinfo.gen = variableset;
	    successorinfo.kill = slotset_22_;
	    length += i;
	    checkConsistent();
	    doTransformations();
	}
	checkConsistent();
    }
    
    public void setNextByAddr(FlowBlock flowblock_23_) {
	if (flowblock_23_ == END_OF_METHOD || flowblock_23_ == NEXT_BY_ADDR)
	    throw new IllegalArgumentException
		      ("nextByAddr mustn't be special");
	SuccessorInfo successorinfo
	    = (SuccessorInfo) successors.remove(NEXT_BY_ADDR);
	SuccessorInfo successorinfo_24_
	    = (SuccessorInfo) successors.get(flowblock_23_);
	if (successorinfo != null) {
	    NEXT_BY_ADDR.predecessors.remove(this);
	    Jump jump = successorinfo.jumps;
	    jump.destination = flowblock_23_;
	    while (jump.next != null) {
		jump = jump.next;
		jump.destination = flowblock_23_;
	    }
	    successors.put(flowblock_23_, successorinfo);
	    if (successorinfo_24_ != null) {
		successorinfo.gen.addAll(successorinfo_24_.gen);
		successorinfo.kill.retainAll(successorinfo_24_.kill);
		jump.next = successorinfo_24_.jumps;
	    } else
		flowblock_23_.predecessors.add(this);
	}
	checkConsistent();
	nextByAddr = flowblock_23_;
	flowblock_23_.prevByAddr = this;
    }
    
    public boolean doT2(FlowBlock flowblock_25_) {
	if (flowblock_25_.predecessors.size() != 1
	    || flowblock_25_.predecessors.get(0) != this)
	    return false;
	checkConsistent();
	flowblock_25_.checkConsistent();
	if ((GlobalOptions.debuggingFlags & 0x20) != 0)
	    GlobalOptions.err.println("T2([" + addr + "," + getNextAddr()
				      + "],[" + flowblock_25_.addr + ","
				      + flowblock_25_.getNextAddr() + "])");
	SuccessorInfo successorinfo
	    = (SuccessorInfo) successors.remove(flowblock_25_);
	updateInOut(flowblock_25_, successorinfo);
	if ((GlobalOptions.debuggingFlags & 0x8) != 0)
	    GlobalOptions.err.println("before Resolve: " + this);
	Jump jump = resolveSomeJumps(successorinfo.jumps, flowblock_25_);
	if ((GlobalOptions.debuggingFlags & 0x8) != 0)
	    GlobalOptions.err.println("before Remaining: " + this);
	resolveRemaining(jump);
	if ((GlobalOptions.debuggingFlags & 0x8) != 0)
	    GlobalOptions.err.println("after Resolve: " + this);
	lastModified = lastModified.appendBlock(flowblock_25_.block);
	mergeSuccessors(flowblock_25_);
	doTransformations();
	mergeAddr(flowblock_25_);
	checkConsistent();
	return true;
    }
    
    public void mergeEndBlock() {
	checkConsistent();
	SuccessorInfo successorinfo
	    = (SuccessorInfo) successors.remove(END_OF_METHOD);
	if (successorinfo != null) {
	    Jump jump = successorinfo.jumps;
	    Jump jump_26_ = null;
	    while (jump != null) {
		Jump jump_27_ = jump;
		jump = jump.next;
		if (jump_27_.prev instanceof ReturnBlock)
		    jump_27_.prev.removeJump();
		else {
		    jump_27_.next = jump_26_;
		    jump_26_ = jump_27_;
		}
	    }
	    for (jump_26_ = resolveSomeJumps(jump_26_, END_OF_METHOD);
		 jump_26_ != null; jump_26_ = jump_26_.next) {
		StructuredBlock structuredblock = jump_26_.prev;
		if (lastModified != structuredblock) {
		    BreakableBlock breakableblock = null;
		    for (StructuredBlock structuredblock_28_
			     = structuredblock.outer;
			 structuredblock_28_ != null;
			 structuredblock_28_ = structuredblock_28_.outer) {
			if (structuredblock_28_ instanceof BreakableBlock) {
			    if (structuredblock_28_.getNextFlowBlock()
				== END_OF_METHOD)
				breakableblock
				    = (BreakableBlock) structuredblock_28_;
			    break;
			}
		    }
		    structuredblock.removeJump();
		    if (breakableblock == null)
			structuredblock.appendBlock(new ReturnBlock());
		    else
			structuredblock.appendBlock
			    (new BreakBlock(breakableblock, false));
		}
	    }
	    if (lastModified.jump.destination == END_OF_METHOD)
		lastModified.removeJump();
	    doTransformations();
	    checkConsistent();
	}
    }
    
    public boolean doT1(int i, int i_29_) {
	if (!predecessors.contains(this))
	    return false;
	Iterator iterator = predecessors.iterator();
	while (iterator.hasNext()) {
	    FlowBlock flowblock_30_ = (FlowBlock) iterator.next();
	    if (flowblock_30_ != null && flowblock_30_ != this
		&& flowblock_30_.addr >= i && flowblock_30_.addr < i_29_)
		return false;
	}
	checkConsistent();
	if ((GlobalOptions.debuggingFlags & 0x20) != 0)
	    GlobalOptions.err
		.println("T1([" + addr + "," + getNextAddr() + "])");
	SuccessorInfo successorinfo = (SuccessorInfo) successors.remove(this);
	updateInOut(this, successorinfo);
	Jump jump = successorinfo.jumps;
	StructuredBlock structuredblock = block;
	boolean bool = false;
	if (jump.next == null && jump.prev == lastModified
	    && lastModified instanceof InstructionBlock
	    && ((InstructionBlock) lastModified).getInstruction().isVoid()) {
	    if (lastModified.outer instanceof SequentialBlock
		&& lastModified.outer.getSubBlocks()[0] instanceof LoopBlock) {
		LoopBlock loopblock
		    = (LoopBlock) lastModified.outer.getSubBlocks()[0];
		if (loopblock.cond == LoopBlock.FALSE && loopblock.type == 1) {
		    lastModified.removeJump();
		    LoopBlock loopblock_31_ = new LoopBlock(2, LoopBlock.TRUE);
		    loopblock_31_.replace(structuredblock);
		    loopblock_31_.setBody(structuredblock);
		    loopblock_31_.incrInstr
			= ((InstructionBlock) lastModified).getInstruction();
		    loopblock_31_.replaceBreakContinue(loopblock);
		    loopblock.bodyBlock.replace(lastModified.outer);
		    bool = true;
		}
	    }
	    if (!bool && (((InstructionBlock) lastModified).getInstruction()
			  instanceof CombineableOperator)) {
		lastModified.removeJump();
		LoopBlock loopblock = new LoopBlock(3, LoopBlock.TRUE);
		loopblock.replace(structuredblock);
		loopblock.setBody(structuredblock);
		loopblock.incrBlock = (InstructionBlock) lastModified;
		bool = true;
	    }
	}
	if (!bool) {
	    jump = resolveSomeJumps(jump, this);
	    LoopBlock loopblock = new LoopBlock(0, LoopBlock.TRUE);
	    structuredblock = block;
	    loopblock.replace(structuredblock);
	    loopblock.setBody(structuredblock);
	    for (/**/; jump != null; jump = jump.next) {
		if (jump.prev != lastModified) {
		    StructuredBlock structuredblock_32_ = jump.prev;
		    int i_33_ = 0;
		    int i_34_ = 0;
		    BreakableBlock breakableblock = null;
		    for (StructuredBlock structuredblock_35_
			     = structuredblock_32_.outer;
			 structuredblock_35_ != loopblock;
			 structuredblock_35_ = structuredblock_35_.outer) {
			if (structuredblock_35_ instanceof BreakableBlock) {
			    if (structuredblock_35_ instanceof LoopBlock)
				i_34_++;
			    i_33_++;
			    if (structuredblock_35_.getNextFlowBlock()
				== this) {
				breakableblock
				    = (BreakableBlock) structuredblock_35_;
				break;
			    }
			}
		    }
		    structuredblock_32_.removeJump();
		    if (breakableblock == null)
			structuredblock_32_.appendBlock
			    (new ContinueBlock(loopblock, i_34_ > 0));
		    else
			structuredblock_32_.appendBlock
			    (new BreakBlock(breakableblock, i_33_ > 1));
		}
	    }
	    if (lastModified.jump.destination == this)
		lastModified.removeJump();
	}
	predecessors.remove(this);
	lastModified = block;
	doTransformations();
	checkConsistent();
	return true;
    }
    
    public void doTransformations() {
	if ((GlobalOptions.debuggingFlags & 0x8) != 0)
	    GlobalOptions.err.println("before Transformation: " + this);
	while (lastModified instanceof SequentialBlock) {
	    if (!lastModified.getSubBlocks()[0].doTransformations())
		lastModified = lastModified.getSubBlocks()[1];
	}
	while (lastModified.doTransformations()) {
	    /* empty */
	}
	if ((GlobalOptions.debuggingFlags & 0x8) != 0)
	    GlobalOptions.err.println("after Transformation: " + this);
    }
    
    FlowBlock getSuccessor(int i, int i_36_) {
	Iterator iterator = successors.keySet().iterator();
	FlowBlock flowblock_37_ = null;
	while (iterator.hasNext()) {
	    FlowBlock flowblock_38_ = (FlowBlock) iterator.next();
	    if (flowblock_38_.addr >= i && flowblock_38_.addr < i_36_
		&& flowblock_38_ != this
		&& (flowblock_37_ == null
		    || flowblock_38_.addr < flowblock_37_.addr))
		flowblock_37_ = flowblock_38_;
	}
	return flowblock_37_;
    }
    
    public void analyze() {
	analyze(0, 2147483647);
	mergeEndBlock();
    }
    
    public boolean analyze(int i, int i_39_) {
	if ((GlobalOptions.debuggingFlags & 0x20) != 0)
	    GlobalOptions.err.println("analyze(" + i + ", " + i_39_ + ")");
	checkConsistent();
	boolean bool = false;
	for (;;) {
	    if (lastModified instanceof SwitchBlock)
		analyzeSwitch(i, i_39_);
	    if (doT1(i, i_39_)) {
		if ((GlobalOptions.debuggingFlags & 0x8) != 0)
		    GlobalOptions.err.println("after T1: " + this);
		if (addr != 0)
		    return true;
	    }
	    FlowBlock flowblock_40_ = getSuccessor(i, i_39_);
	    for (;;) {
		if (flowblock_40_ == null) {
		    if ((GlobalOptions.debuggingFlags & 0x20) != 0)
			GlobalOptions.err.println
			    ("No more successors applicable: " + i + " - "
			     + i_39_ + "; " + addr + " - " + getNextAddr());
		    return bool;
		}
		if ((nextByAddr == flowblock_40_
		     || flowblock_40_.nextByAddr == this)
		    && doT2(flowblock_40_)) {
		    bool = true;
		    if ((GlobalOptions.debuggingFlags & 0x8) != 0)
			GlobalOptions.err.println("after T2: " + this);
		    break;
		}
		Iterator iterator = flowblock_40_.predecessors.iterator();
		while (iterator.hasNext()) {
		    int i_41_ = ((FlowBlock) iterator.next()).addr;
		    if (i_41_ < i || i_41_ >= i_39_) {
			if ((GlobalOptions.debuggingFlags & 0x20) != 0)
			    GlobalOptions.err.println("breaking analyze(" + i
						      + ", " + i_39_ + "); "
						      + addr + " - "
						      + getNextAddr());
			return bool;
		    }
		}
		int i_42_ = flowblock_40_.addr > addr ? getNextAddr() : i;
		int i_43_ = flowblock_40_.addr > addr ? i_39_ : addr;
		if (flowblock_40_.analyze(i_42_, i_43_))
		    break;
		flowblock_40_ = getSuccessor(flowblock_40_.addr + 1, i_39_);
	    }
	}
    }
    
    public boolean analyzeSwitch(int i, int i_44_) {
	if ((GlobalOptions.debuggingFlags & 0x20) != 0)
	    GlobalOptions.err
		.println("analyzeSwitch(" + i + ", " + i_44_ + ")");
	SwitchBlock switchblock = (SwitchBlock) lastModified;
	boolean bool = false;
	int i_45_ = -1;
	FlowBlock flowblock_46_ = null;
	for (int i_47_ = 0; i_47_ < switchblock.caseBlocks.length; i_47_++) {
	    if (switchblock.caseBlocks[i_47_].subBlock instanceof EmptyBlock
		&& switchblock.caseBlocks[i_47_].subBlock.jump != null) {
		FlowBlock flowblock_48_
		    = switchblock.caseBlocks[i_47_].subBlock.jump.destination;
		if (flowblock_48_.addr >= i_44_)
		    break;
		if (flowblock_48_.addr >= i) {
		    while (flowblock_48_.analyze(getNextAddr(), i_44_))
			bool = true;
		    if (flowblock_48_.addr != getNextAddr()
			|| flowblock_48_.predecessors.size() > 2
			|| (flowblock_48_.predecessors.size() > 1
			    && (flowblock_46_ == null
				|| !flowblock_48_.predecessors
					.contains(flowblock_46_)))
			|| (((SuccessorInfo) successors.get(flowblock_48_))
			    .jumps.next) != null)
			break;
		    checkConsistent();
		    SuccessorInfo successorinfo
			= (SuccessorInfo) successors.remove(flowblock_48_);
		    if (flowblock_48_.predecessors.size() == 2) {
			SuccessorInfo successorinfo_49_
			    = ((SuccessorInfo)
			       flowblock_46_.successors.remove(flowblock_48_));
			successorinfo.kill.retainAll(successorinfo_49_.kill);
			successorinfo.gen.addAll(successorinfo_49_.gen);
			Jump jump
			    = flowblock_46_.resolveSomeJumps((successorinfo_49_
							      .jumps),
							     flowblock_48_);
			flowblock_46_.resolveRemaining(jump);
			switchblock.caseBlocks[i_45_ + 1].isFallThrough = true;
		    }
		    updateInOut(flowblock_48_, successorinfo);
		    if (flowblock_46_ != null) {
			flowblock_46_.block
			    .replace(switchblock.caseBlocks[i_45_].subBlock);
			mergeSuccessors(flowblock_46_);
		    }
		    switchblock.caseBlocks[i_47_].subBlock.removeJump();
		    mergeAddr(flowblock_48_);
		    flowblock_46_ = flowblock_48_;
		    i_45_ = i_47_;
		    checkConsistent();
		    bool = true;
		}
	    }
	}
	if (flowblock_46_ != null) {
	    flowblock_46_.block
		.replace(switchblock.caseBlocks[i_45_].subBlock);
	    mergeSuccessors(flowblock_46_);
	}
	if ((GlobalOptions.debuggingFlags & 0x8) != 0)
	    GlobalOptions.err.println("after analyzeSwitch: " + this);
	if ((GlobalOptions.debuggingFlags & 0x20) != 0)
	    GlobalOptions.err.println("analyzeSwitch done: " + i + " - "
				      + i_44_ + "; " + addr + " - "
				      + getNextAddr());
	checkConsistent();
	return bool;
    }
    
    public void makeStartBlock() {
	predecessors.add(null);
    }
    
    public void removeSuccessor(Jump jump) {
	SuccessorInfo successorinfo
	    = (SuccessorInfo) successors.get(jump.destination);
	Jump jump_50_ = null;
	Jump jump_51_;
	for (jump_51_ = successorinfo.jumps;
	     jump_51_ != jump && jump_51_ != null; jump_51_ = jump_51_.next)
	    jump_50_ = jump_51_;
	if (jump_51_ == null)
	    throw new IllegalArgumentException
		      (addr + ": removing non existent jump: " + jump);
	if (jump_50_ != null)
	    jump_50_.next = jump_51_.next;
	else if (jump_51_.next == null) {
	    successors.remove(jump.destination);
	    jump.destination.predecessors.remove(this);
	} else
	    successorinfo.jumps = jump_51_.next;
    }
    
    public Jump getJumps(FlowBlock flowblock_52_) {
	return ((SuccessorInfo) successors.get(flowblock_52_)).jumps;
    }
    
    public Jump removeJumps(FlowBlock flowblock_53_) {
	if (flowblock_53_ != END_OF_METHOD)
	    flowblock_53_.predecessors.remove(this);
	return ((SuccessorInfo) successors.remove(flowblock_53_)).jumps;
    }
    
    public Set getSuccessors() {
	return successors.keySet();
    }
    
    public void addSuccessor(Jump jump) {
	SuccessorInfo successorinfo
	    = (SuccessorInfo) successors.get(jump.destination);
	if (successorinfo == null) {
	    successorinfo = new SuccessorInfo();
	    successorinfo.jumps = jump;
	    if (jump.destination != END_OF_METHOD)
		jump.destination.predecessors.add(this);
	    successors.put(jump.destination, successorinfo);
	} else {
	    jump.next = successorinfo.jumps;
	    successorinfo.jumps = jump;
	}
    }
    
    public final boolean mapStackToLocal() {
	mapStackToLocal(VariableStack.EMPTY);
	return true;
    }
    
    public void mapStackToLocal(VariableStack variablestack) {
	if (variablestack == null)
	    throw new AssertError("initial stack is null");
	stackMap = variablestack;
	block.mapStackToLocal(variablestack);
	Iterator iterator = successors.values().iterator();
	while (iterator.hasNext()) {
	    SuccessorInfo successorinfo = (SuccessorInfo) iterator.next();
	    Jump jump = successorinfo.jumps;
	    FlowBlock flowblock_54_ = jump.destination;
	    if (flowblock_54_ != END_OF_METHOD) {
		VariableStack variablestack_55_ = flowblock_54_.stackMap;
		for (/**/; jump != null; jump = jump.next) {
		    if (jump.stackMap == null)
			GlobalOptions.err.println("Dead jump? " + jump.prev
						  + " in " + this);
		    variablestack_55_ = VariableStack.merge(variablestack_55_,
							    jump.stackMap);
		}
		if (flowblock_54_.stackMap == null)
		    flowblock_54_.mapStackToLocal(variablestack_55_);
	    }
	}
    }
    
    public void removePush() {
	if (stackMap != null) {
	    stackMap = null;
	    block.removePush();
	    Iterator iterator = successors.keySet().iterator();
	    while (iterator.hasNext()) {
		FlowBlock flowblock_56_ = (FlowBlock) iterator.next();
		flowblock_56_.removePush();
	    }
	}
    }
    
    public void removeOnetimeLocals() {
	block.removeOnetimeLocals();
	if (nextByAddr != null)
	    nextByAddr.removeOnetimeLocals();
    }
    
    private void promoteInSets() {
	Iterator iterator = predecessors.iterator();
	while (iterator.hasNext()) {
	    FlowBlock flowblock_57_ = (FlowBlock) iterator.next();
	    SuccessorInfo successorinfo
		= (SuccessorInfo) flowblock_57_.successors.get(this);
	    VariableSet variableset = successorinfo.gen;
	    SlotSet slotset = successorinfo.kill;
	    in.merge(variableset);
	    SlotSet slotset_58_ = (SlotSet) in.clone();
	    slotset_58_.removeAll(slotset);
	    if (flowblock_57_.in.addAll(slotset_58_))
		flowblock_57_.promoteInSets();
	}
	if (nextByAddr != null)
	    nextByAddr.promoteInSets();
    }
    
    public void mergeParams(LocalInfo[] localinfos) {
	promoteInSets();
	VariableSet variableset = new VariableSet(localinfos);
	in.merge(variableset);
    }
    
    public void makeDeclaration(Set set) {
	block.propagateUsage();
	block.makeDeclaration(set);
	if (nextByAddr != null)
	    nextByAddr.makeDeclaration(set);
    }
    
    public void simplify() {
	block.simplify();
	if (nextByAddr != null)
	    nextByAddr.simplify();
    }
    
    public void dumpSource(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	if (predecessors.size() != 0) {
	    tabbedprintwriter.untab();
	    tabbedprintwriter.println(getLabel() + ":");
	    tabbedprintwriter.tab();
	}
	if ((GlobalOptions.debuggingFlags & 0x10) != 0)
	    tabbedprintwriter.println("in: " + in);
	block.dumpSource(tabbedprintwriter);
	if ((GlobalOptions.debuggingFlags & 0x10) != 0) {
	    Iterator iterator = successors.entrySet().iterator();
	    while (iterator.hasNext()) {
		Map.Entry entry = (Map.Entry) iterator.next();
		FlowBlock flowblock_59_ = (FlowBlock) entry.getKey();
		SuccessorInfo successorinfo = (SuccessorInfo) entry.getValue();
		tabbedprintwriter.println("successor: "
					  + flowblock_59_.getLabel()
					  + "  gen : " + successorinfo.gen
					  + "  kill: " + successorinfo.kill);
	    }
	}
	if (nextByAddr != null)
	    nextByAddr.dumpSource(tabbedprintwriter);
    }
    
    public int getAddr() {
	return addr;
    }
    
    public String getLabel() {
	if (label == null)
	    label = "flow_" + addr + "_" + serialno++ + "_";
	return label;
    }
    
    public StructuredBlock getBlock() {
	return block;
    }
    
    public String toString() {
	try {
	    StringWriter stringwriter = new StringWriter();
	    TabbedPrintWriter tabbedprintwriter
		= new TabbedPrintWriter(stringwriter);
	    tabbedprintwriter.println(super.toString() + ": " + addr + "-"
				      + (addr + length));
	    if ((GlobalOptions.debuggingFlags & 0x10) != 0)
		tabbedprintwriter.println("in: " + in);
	    tabbedprintwriter.tab();
	    block.dumpSource(tabbedprintwriter);
	    tabbedprintwriter.untab();
	    if ((GlobalOptions.debuggingFlags & 0x10) != 0) {
		Iterator iterator = successors.entrySet().iterator();
		while (iterator.hasNext()) {
		    Map.Entry entry = (Map.Entry) iterator.next();
		    FlowBlock flowblock_60_ = (FlowBlock) entry.getKey();
		    SuccessorInfo successorinfo
			= (SuccessorInfo) entry.getValue();
		    tabbedprintwriter.println("successor: "
					      + flowblock_60_.getLabel()
					      + "  gen : " + successorinfo.gen
					      + "  kill: "
					      + successorinfo.kill);
		}
	    }
	    return stringwriter.toString();
	} catch (RuntimeException runtimeexception) {
	    return super.toString() + ": (RUNTIME EXCEPTION)";
	} catch (IOException ioexception) {
	    return super.toString();
	}
    }
    
    static {
	END_OF_METHOD.appendBlock(new EmptyBlock(), 0);
	END_OF_METHOD.label = "END_OF_METHOD";
	NEXT_BY_ADDR = new FlowBlock(null, -1);
	NEXT_BY_ADDR.appendBlock(new DescriptionBlock("FALL THROUGH"), 0);
	NEXT_BY_ADDR.label = "NEXT_BY_ADDR";
	serialno = 0;
    }
}
