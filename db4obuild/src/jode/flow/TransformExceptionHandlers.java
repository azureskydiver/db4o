/* TransformExceptionHandlers - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jode.AssertError;
import jode.GlobalOptions;
import jode.decompiler.LocalInfo;
import jode.expr.Expression;
import jode.expr.LocalLoadOperator;
import jode.expr.LocalStoreOperator;
import jode.expr.MonitorExitOperator;
import jode.expr.NopOperator;
import jode.expr.Operator;
import jode.expr.StoreInstruction;
import jode.type.Type;

public class TransformExceptionHandlers
{
    SortedSet handlers = new TreeSet();
    
    static class Handler implements Comparable
    {
	FlowBlock start;
	int endAddr;
	FlowBlock handler;
	Type type;
	
	public Handler(FlowBlock flowblock, int i, FlowBlock flowblock_0_,
		       Type type) {
	    start = flowblock;
	    endAddr = i;
	    this.handler = flowblock_0_;
	    this.type = type;
	}
	
	public int compareTo(Object object) {
	    Handler handler_1_ = (Handler) object;
	    if (start.getAddr() != handler_1_.start.getAddr())
		return handler_1_.start.getAddr() - start.getAddr();
	    if (endAddr != handler_1_.endAddr)
		return endAddr - handler_1_.endAddr;
	    if (this.handler.getAddr() != handler_1_.handler.getAddr())
		return this.handler.getAddr() - handler_1_.handler.getAddr();
	    if (type == handler_1_.type)
		return 0;
	    if (type == null)
		return -1;
	    if (handler_1_.type == null)
		return 1;
	    return type.getTypeSignature()
		       .compareTo(handler_1_.type.getTypeSignature());
	}
    }
    
    public void addHandler(FlowBlock flowblock, int i, FlowBlock flowblock_2_,
			   Type type) {
	handlers.add(new Handler(flowblock, i, flowblock_2_, type));
    }
    
    static void mergeTryCatch(FlowBlock flowblock, FlowBlock flowblock_3_) {
	if ((GlobalOptions.debuggingFlags & 0x20) != 0)
	    GlobalOptions.err.println("mergeTryCatch(" + flowblock.getAddr()
				      + ", " + flowblock_3_.getAddr() + ")");
	flowblock.updateInOutCatch(flowblock_3_);
	flowblock.mergeSuccessors(flowblock_3_);
	flowblock.mergeAddr(flowblock_3_);
    }
    
    static void analyzeCatchBlock(Type type, FlowBlock flowblock,
				  FlowBlock flowblock_4_) {
	mergeTryCatch(flowblock, flowblock_4_);
	CatchBlock catchblock = new CatchBlock(type);
	((TryBlock) flowblock.block).addCatchBlock(catchblock);
	catchblock.setCatchBlock(flowblock_4_.block);
    }
    
    boolean transformSubRoutine(StructuredBlock structuredblock) {
	StructuredBlock structuredblock_5_ = structuredblock;
	if (structuredblock_5_ instanceof SequentialBlock)
	    structuredblock_5_ = structuredblock.getSubBlocks()[0];
	LocalInfo localinfo = null;
	if (structuredblock_5_ instanceof SpecialBlock) {
	    SpecialBlock specialblock = (SpecialBlock) structuredblock_5_;
	    if (specialblock.type != SpecialBlock.POP
		|| specialblock.count != 1)
		return false;
	} else if (structuredblock_5_ instanceof InstructionBlock) {
	    Expression expression
		= ((InstructionBlock) structuredblock_5_).getInstruction();
	    if (expression instanceof StoreInstruction
		&& (((StoreInstruction) expression).getLValue()
		    instanceof LocalStoreOperator)) {
		LocalStoreOperator localstoreoperator
		    = ((LocalStoreOperator)
		       ((StoreInstruction) expression).getLValue());
		localinfo = localstoreoperator.getLocalInfo();
		expression
		    = ((StoreInstruction) expression).getSubExpressions()[1];
	    }
	    if (!(expression instanceof NopOperator))
		return false;
	} else
	    return false;
	structuredblock_5_.removeBlock();
	for (/**/; structuredblock instanceof SequentialBlock;
	     structuredblock = structuredblock.getSubBlocks()[1]) {
	    /* empty */
	}
	if (structuredblock instanceof RetBlock
	    && ((RetBlock) structuredblock).local.equals(localinfo))
	    structuredblock.removeBlock();
	return true;
    }
    
    private void removeReturnLocal(ReturnBlock returnblock) {
	StructuredBlock structuredblock = getPredecessor(returnblock);
	if (structuredblock instanceof InstructionBlock) {
	    Expression expression
		= ((InstructionBlock) structuredblock).getInstruction();
	    if (expression instanceof StoreInstruction) {
		Expression expression_6_ = returnblock.getInstruction();
		if (expression_6_ instanceof LocalLoadOperator
		    && ((StoreInstruction) expression)
			   .lvalueMatches((LocalLoadOperator) expression_6_)) {
		    Expression expression_7_
			= (((StoreInstruction) expression).getSubExpressions()
			   [1]);
		    returnblock.setInstruction(expression_7_);
		    returnblock.replace(returnblock.outer);
		}
	    }
	}
    }
    
    private void removeJSR(FlowBlock flowblock, FlowBlock flowblock_8_) {
	for (Jump jump = flowblock.removeJumps(flowblock_8_); jump != null;
	     jump = jump.next) {
	    StructuredBlock structuredblock = jump.prev;
	    structuredblock.removeJump();
	    if (structuredblock instanceof EmptyBlock
		&& structuredblock.outer instanceof JsrBlock
		&& ((JsrBlock) structuredblock.outer).isGood()) {
		StructuredBlock structuredblock_9_
		    = structuredblock.outer.getNextBlock();
		structuredblock.outer.removeBlock();
		if (structuredblock_9_ instanceof ReturnBlock)
		    removeReturnLocal((ReturnBlock) structuredblock_9_);
	    } else {
		DescriptionBlock descriptionblock
		    = (new DescriptionBlock
		       ("ERROR: invalid jump to finally block!"));
		structuredblock.appendBlock(descriptionblock);
	    }
	}
    }
    
    private static StructuredBlock getPredecessor
	(StructuredBlock structuredblock) {
	if (structuredblock.outer instanceof SequentialBlock) {
	    SequentialBlock sequentialblock
		= (SequentialBlock) structuredblock.outer;
	    if (sequentialblock.subBlocks[1] == structuredblock)
		return sequentialblock.subBlocks[0];
	    if (sequentialblock.outer instanceof SequentialBlock)
		return sequentialblock.outer.getSubBlocks()[0];
	}
	return null;
    }
    
    private static int getMonitorExitSlot(StructuredBlock structuredblock) {
	if (structuredblock instanceof InstructionBlock) {
	    Expression expression
		= ((InstructionBlock) structuredblock).getInstruction();
	    if (expression instanceof MonitorExitOperator) {
		MonitorExitOperator monitorexitoperator
		    = (MonitorExitOperator) expression;
		if (monitorexitoperator.getFreeOperandCount() == 0
		    && (monitorexitoperator.getSubExpressions()[0]
			instanceof LocalLoadOperator))
		    return ((LocalLoadOperator)
			    monitorexitoperator.getSubExpressions()[0])
			       .getLocalInfo
			       ().getSlot();
	    }
	}
	return -1;
    }
    
    private boolean isMonitorExitSubRoutine(FlowBlock flowblock,
					    LocalInfo localinfo) {
	if (transformSubRoutine(flowblock.block)
	    && getMonitorExitSlot(flowblock.block) == localinfo.getSlot())
	    return true;
	return false;
    }
    
    private static StructuredBlock skipFinExitChain
	(StructuredBlock structuredblock) {
	StructuredBlock structuredblock_10_;
	if (structuredblock instanceof ReturnBlock)
	    structuredblock_10_ = getPredecessor(structuredblock);
	else
	    structuredblock_10_ = structuredblock;
	StructuredBlock structuredblock_11_ = null;
	for (/**/;
	     (structuredblock_10_ instanceof JsrBlock
	      || getMonitorExitSlot(structuredblock_10_) >= 0);
	     structuredblock_10_ = getPredecessor(structuredblock_10_))
	    structuredblock_11_ = structuredblock_10_;
	return structuredblock_11_;
    }
    
    private void checkAndRemoveJSR(FlowBlock flowblock,
				   FlowBlock flowblock_12_, int i, int i_13_) {
	Iterator iterator = flowblock.getSuccessors().iterator();
	while (iterator.hasNext()) {
	    FlowBlock flowblock_14_ = (FlowBlock) iterator.next();
	    if (flowblock_14_ != flowblock_12_) {
		boolean bool = true;
		Jump jump = flowblock.getJumps(flowblock_14_);
	    while_25_:
		while (jump != null) {
		    StructuredBlock structuredblock = jump.prev;
		    do {
			if (!(structuredblock instanceof ThrowBlock)
			    && (!(structuredblock instanceof EmptyBlock)
				|| !(structuredblock.outer
				     instanceof JsrBlock))) {
			    StructuredBlock structuredblock_15_
				= skipFinExitChain(structuredblock);
			    if (structuredblock_15_ instanceof JsrBlock) {
				JsrBlock jsrblock
				    = (JsrBlock) structuredblock_15_;
				StructuredBlock structuredblock_16_
				    = jsrblock.innerBlock;
				if (structuredblock_16_ instanceof EmptyBlock
				    && structuredblock_16_.jump != null
				    && (structuredblock_16_.jump.destination
					== flowblock_12_)) {
				    jsrblock.setGood(true);
				    break;
				}
			    }
			    if (structuredblock_15_ == null && bool
				&& jump.destination.predecessors.size() == 1
				&& jump.destination.getAddr() >= i
				&& jump.destination.getNextAddr() <= i_13_) {
				jump.destination.analyze(i, i_13_);
				StructuredBlock structuredblock_17_
				    = jump.destination.block;
				if (structuredblock_17_
				    instanceof SequentialBlock)
				    structuredblock_17_
					= (structuredblock_17_.getSubBlocks()
					   [0]);
				if (structuredblock_17_ instanceof JsrBlock
				    && (structuredblock_17_.getSubBlocks()[0]
					instanceof EmptyBlock)
				    && (structuredblock_17_.getSubBlocks()[0]
					.jump.destination) == flowblock_12_) {
				    StructuredBlock structuredblock_18_
					= (structuredblock_17_.getSubBlocks()
					   [0]);
				    jump.destination.removeSuccessor
					(structuredblock_18_.jump);
				    structuredblock_18_.removeJump();
				    structuredblock_17_.removeBlock();
				    break while_25_;
				}
			    }
			    DescriptionBlock descriptionblock
				= (new DescriptionBlock
				   ("ERROR: no jsr to finally"));
			    if (structuredblock_15_ != null)
				structuredblock_15_
				    .prependBlock(descriptionblock);
			    else {
				structuredblock.appendBlock(descriptionblock);
				descriptionblock
				    .moveJump(structuredblock.jump);
			    }
			}
		    } while (false);
		    jump = jump.next;
		    bool = false;
		}
	    }
	}
	if (flowblock.getSuccessors().contains(flowblock_12_))
	    removeJSR(flowblock, flowblock_12_);
    }
    
    private void checkAndRemoveMonitorExit
	(FlowBlock flowblock, LocalInfo localinfo, int i, int i_19_) {
	FlowBlock flowblock_20_ = null;
	Iterator iterator = flowblock.getSuccessors().iterator();
	while (iterator.hasNext()) {
	    boolean bool = true;
	    FlowBlock flowblock_21_ = (FlowBlock) iterator.next();
	    Jump jump = flowblock.getJumps(flowblock_21_);
	while_26_:
	    while (jump != null) {
		StructuredBlock structuredblock = jump.prev;
		do {
		    if (!(structuredblock instanceof ThrowBlock)
			&& (!(structuredblock instanceof EmptyBlock)
			    || !(structuredblock.outer instanceof JsrBlock))) {
			StructuredBlock structuredblock_22_
			    = skipFinExitChain(structuredblock);
			if (structuredblock_22_ instanceof JsrBlock) {
			    JsrBlock jsrblock = (JsrBlock) structuredblock_22_;
			    StructuredBlock structuredblock_23_
				= jsrblock.innerBlock;
			    if (structuredblock_23_ instanceof EmptyBlock
				&& structuredblock_23_.jump != null) {
				FlowBlock flowblock_24_
				    = structuredblock_23_.jump.destination;
				if (flowblock_20_ == null
				    && flowblock_24_.getAddr() >= i
				    && flowblock_24_.getNextAddr() <= i_19_) {
				    flowblock_24_.analyze(i, i_19_);
				    if (isMonitorExitSubRoutine(flowblock_24_,
								localinfo))
					flowblock_20_ = flowblock_24_;
				}
				if (flowblock_24_ == flowblock_20_) {
				    jsrblock.setGood(true);
				    break;
				}
			    }
			} else if (getMonitorExitSlot(structuredblock_22_)
				   == localinfo.getSlot()) {
			    structuredblock_22_.removeBlock();
			    if (structuredblock instanceof ReturnBlock)
				removeReturnLocal((ReturnBlock)
						  structuredblock);
			    break;
			}
			if (structuredblock_22_ == null && bool
			    && flowblock_21_.predecessors.size() == 1
			    && flowblock_21_.getAddr() >= i
			    && flowblock_21_.getNextAddr() <= i_19_) {
			    flowblock_21_.analyze(i, i_19_);
			    StructuredBlock structuredblock_25_
				= flowblock_21_.block;
			    if (structuredblock_25_ instanceof SequentialBlock)
				structuredblock_25_
				    = structuredblock_25_.getSubBlocks()[0];
			    if (structuredblock_25_ instanceof JsrBlock
				&& (structuredblock_25_.getSubBlocks()[0]
				    instanceof EmptyBlock)) {
				StructuredBlock structuredblock_26_
				    = structuredblock_25_.getSubBlocks()[0];
				FlowBlock flowblock_27_
				    = structuredblock_26_.jump.destination;
				if (flowblock_20_ == null
				    && flowblock_27_.getAddr() >= i
				    && flowblock_27_.getNextAddr() <= i_19_) {
				    flowblock_27_.analyze(i, i_19_);
				    if (isMonitorExitSubRoutine(flowblock_27_,
								localinfo))
					flowblock_20_ = flowblock_27_;
				}
				if (flowblock_20_ == flowblock_27_) {
				    flowblock_21_.removeSuccessor
					(structuredblock_26_.jump);
				    structuredblock_26_.removeJump();
				    structuredblock_25_.removeBlock();
				    break while_26_;
				}
			    }
			    if (getMonitorExitSlot(structuredblock_25_)
				== localinfo.getSlot()) {
				structuredblock_25_.removeBlock();
				break while_26_;
			    }
			}
			DescriptionBlock descriptionblock
			    = new DescriptionBlock("ERROR: no monitorexit");
			structuredblock.appendBlock(descriptionblock);
			descriptionblock.moveJump(jump);
		    }
		} while (false);
		jump = jump.next;
		bool = false;
	    }
	}
	if (flowblock_20_ != null) {
	    if (flowblock.getSuccessors().contains(flowblock_20_))
		removeJSR(flowblock, flowblock_20_);
	    if (flowblock_20_.predecessors.size() == 0)
		flowblock.mergeAddr(flowblock_20_);
	}
    }
    
    private StoreInstruction getExceptionStore
	(StructuredBlock structuredblock) {
	if (!(structuredblock instanceof SequentialBlock)
	    || !(structuredblock.getSubBlocks()[0]
		 instanceof InstructionBlock))
	    return null;
	Expression expression
	    = ((InstructionBlock) structuredblock.getSubBlocks()[0])
		  .getInstruction();
	if (!(expression instanceof StoreInstruction))
	    return null;
	StoreInstruction storeinstruction = (StoreInstruction) expression;
	if (!(storeinstruction.getLValue() instanceof LocalStoreOperator)
	    || !(storeinstruction.getSubExpressions()[1]
		 instanceof NopOperator))
	    return null;
	return storeinstruction;
    }
    
    private boolean analyzeSynchronized(FlowBlock flowblock,
					FlowBlock flowblock_28_, int i) {
	StructuredBlock structuredblock = flowblock_28_.block;
	StoreInstruction storeinstruction = getExceptionStore(structuredblock);
	if (storeinstruction != null)
	    structuredblock = structuredblock.getSubBlocks()[1];
	if (!(structuredblock instanceof SequentialBlock)
	    || !(structuredblock.getSubBlocks()[0]
		 instanceof InstructionBlock))
	    return false;
	Expression expression
	    = ((InstructionBlock) structuredblock.getSubBlocks()[0])
		  .getInstruction();
	if (!(expression instanceof MonitorExitOperator)
	    || expression.getFreeOperandCount() != 0
	    || !(((MonitorExitOperator) expression).getSubExpressions()[0]
		 instanceof LocalLoadOperator)
	    || !(structuredblock.getSubBlocks()[1] instanceof ThrowBlock))
	    return false;
	Expression expression_29_
	    = ((ThrowBlock) structuredblock.getSubBlocks()[1])
		  .getInstruction();
	if (storeinstruction != null) {
	    if (!(expression_29_ instanceof Operator)
		|| !storeinstruction.lvalueMatches((Operator) expression_29_))
		return false;
	} else if (!(expression_29_ instanceof NopOperator))
	    return false;
	flowblock_28_.removeSuccessor(structuredblock.getSubBlocks()[1].jump);
	mergeTryCatch(flowblock, flowblock_28_);
	MonitorExitOperator monitorexitoperator
	    = ((MonitorExitOperator)
	       ((InstructionBlock) structuredblock.getSubBlocks()[0]).instr);
	LocalInfo localinfo
	    = ((LocalLoadOperator) monitorexitoperator.getSubExpressions()[0])
		  .getLocalInfo();
	if ((GlobalOptions.debuggingFlags & 0x20) != 0)
	    GlobalOptions.err.println("analyzeSynchronized("
				      + flowblock.getAddr() + ","
				      + flowblock.getNextAddr() + "," + i
				      + ")");
	checkAndRemoveMonitorExit(flowblock, localinfo,
				  flowblock.getNextAddr(), i);
	SynchronizedBlock synchronizedblock = new SynchronizedBlock(localinfo);
	TryBlock tryblock = (TryBlock) flowblock.block;
	synchronizedblock.replace(tryblock);
	synchronizedblock.moveJump(tryblock.jump);
	synchronizedblock.setBodyBlock(tryblock.subBlocks.length == 1
				       ? (StructuredBlock) (tryblock.subBlocks
							    [0])
				       : tryblock);
	flowblock.lastModified = synchronizedblock;
	return true;
    }
    
    private void mergeFinallyBlock(FlowBlock flowblock,
				   FlowBlock flowblock_30_,
				   StructuredBlock structuredblock) {
	TryBlock tryblock = (TryBlock) flowblock.block;
	if (tryblock.getSubBlocks()[0] instanceof TryBlock) {
	    TryBlock tryblock_31_ = (TryBlock) tryblock.getSubBlocks()[0];
	    tryblock_31_.gen = tryblock.gen;
	    tryblock_31_.replace(tryblock);
	    tryblock = tryblock_31_;
	    flowblock.lastModified = tryblock;
	    flowblock.block = tryblock;
	}
	mergeTryCatch(flowblock, flowblock_30_);
	FinallyBlock finallyblock = new FinallyBlock();
	finallyblock.setCatchBlock(structuredblock);
	tryblock.addCatchBlock(finallyblock);
    }
    
    private boolean analyzeFinally(FlowBlock flowblock,
				   FlowBlock flowblock_32_, int i) {
	StructuredBlock structuredblock = flowblock_32_.block;
	StoreInstruction storeinstruction = getExceptionStore(structuredblock);
	if (storeinstruction == null)
	    return false;
	structuredblock = structuredblock.getSubBlocks()[1];
	if (!(structuredblock instanceof SequentialBlock))
	    return false;
	StructuredBlock structuredblock_33_ = null;
	if (structuredblock.getSubBlocks()[0] instanceof LoopBlock) {
	    LoopBlock loopblock
		= (LoopBlock) structuredblock.getSubBlocks()[0];
	    if (loopblock.type == 1 && loopblock.cond == LoopBlock.FALSE
		&& loopblock.bodyBlock instanceof SequentialBlock
		&& transformSubRoutine(structuredblock.getSubBlocks()[1])) {
		structuredblock_33_ = structuredblock.getSubBlocks()[1];
		structuredblock = (SequentialBlock) loopblock.bodyBlock;
	    }
	}
	if (!(structuredblock instanceof SequentialBlock)
	    || !(structuredblock.getSubBlocks()[0] instanceof JsrBlock)
	    || !(structuredblock.getSubBlocks()[1] instanceof ThrowBlock))
	    return false;
	JsrBlock jsrblock = (JsrBlock) structuredblock.getSubBlocks()[0];
	ThrowBlock throwblock = (ThrowBlock) structuredblock.getSubBlocks()[1];
	if (!(throwblock.getInstruction() instanceof Operator)
	    || !storeinstruction
		    .lvalueMatches((Operator) throwblock.getInstruction()))
	    return false;
	if (structuredblock_33_ != null) {
	    if (!(jsrblock.innerBlock instanceof BreakBlock))
		return false;
	    structuredblock = structuredblock_33_;
	    Object object = null;
	    flowblock_32_.removeSuccessor(throwblock.jump);
	} else {
	    if (!(jsrblock.innerBlock instanceof EmptyBlock))
		return false;
	    structuredblock_33_ = jsrblock.innerBlock;
	    FlowBlock flowblock_34_ = structuredblock_33_.jump.destination;
	    flowblock_32_.removeSuccessor(throwblock.jump);
	    checkAndRemoveJSR(flowblock, flowblock_34_,
			      flowblock.getNextAddr(), i);
	    while (flowblock_34_.analyze(flowblock.getNextAddr(), i)) {
		/* empty */
	    }
	    if (flowblock_34_.predecessors.size() == 1) {
		flowblock_32_.removeSuccessor(structuredblock_33_.jump);
		flowblock_34_.mergeAddr(flowblock_32_);
		flowblock_32_ = flowblock_34_;
		if (!transformSubRoutine(flowblock_34_.block)) {
		    structuredblock_33_ = flowblock_34_.block;
		    DescriptionBlock descriptionblock
			= (new DescriptionBlock
			   ("ERROR: Missing return address handling"));
		    StructuredBlock structuredblock_35_ = flowblock_34_.block;
		    descriptionblock.replace(structuredblock_33_);
		    descriptionblock.appendBlock(structuredblock_33_);
		}
		structuredblock_33_ = flowblock_34_.block;
	    }
	}
	mergeFinallyBlock(flowblock, flowblock_32_, structuredblock_33_);
	return true;
    }
    
    private boolean analyzeSpecialFinally(FlowBlock flowblock,
					  FlowBlock flowblock_36_, int i) {
	StructuredBlock structuredblock = flowblock_36_.block;
	StructuredBlock structuredblock_37_
	    = (structuredblock instanceof SequentialBlock
	       ? structuredblock.getSubBlocks()[0] : structuredblock);
	if (!(structuredblock_37_ instanceof SpecialBlock)
	    || ((SpecialBlock) structuredblock_37_).type != SpecialBlock.POP
	    || ((SpecialBlock) structuredblock_37_).count != 1)
	    return false;
	flowblock.lastModified = flowblock.block.getSubBlocks()[0];
	FlowBlock flowblock_38_;
	if (structuredblock instanceof SequentialBlock) {
	    structuredblock = structuredblock.getSubBlocks()[1];
	    flowblock_38_ = null;
	} else {
	    structuredblock = new EmptyBlock();
	    structuredblock.moveJump(structuredblock_37_.jump);
	    flowblock_38_ = structuredblock.jump.destination;
	    if (flowblock.getSuccessors().contains(flowblock_38_)) {
		Jump jump = flowblock.removeJumps(flowblock_38_);
		jump = flowblock.resolveSomeJumps(jump, flowblock_38_);
		flowblock.resolveRemaining(jump);
	    }
	}
	Set set = flowblock.getSuccessors();
	Iterator iterator = set.iterator();
	while (iterator.hasNext()) {
	    FlowBlock flowblock_39_ = (FlowBlock) iterator.next();
	    if (flowblock_39_ != FlowBlock.END_OF_METHOD
		&& flowblock_39_.block instanceof EmptyBlock
		&& flowblock_39_.block.jump.destination == flowblock_38_) {
		Jump jump = flowblock.removeJumps(flowblock_39_);
		jump = flowblock.resolveSomeJumps(jump, flowblock_38_);
		flowblock.resolveRemaining(jump);
		if (flowblock_39_.predecessors.size() == 0) {
		    flowblock_39_.removeJumps(flowblock_38_);
		    flowblock.mergeAddr(flowblock_39_);
		}
	    } else {
		for (Jump jump = flowblock.getJumps(flowblock_39_);
		     jump != null; jump = jump.next) {
		    if (!(jump.prev instanceof ThrowBlock)) {
			DescriptionBlock descriptionblock
			    = (new DescriptionBlock
			       ("ERROR: doesn't go through finally block!"));
			if (jump.prev instanceof ReturnBlock) {
			    descriptionblock.replace(jump.prev);
			    descriptionblock.appendBlock(jump.prev);
			} else {
			    jump.prev.appendBlock(descriptionblock);
			    descriptionblock.moveJump(jump);
			}
		    }
		}
	    }
	}
	mergeFinallyBlock(flowblock, flowblock_36_, structuredblock);
	flowblock.lastModified = structuredblock;
	return true;
    }
    
    void checkTryCatchOrder() {
    	
    	// xxxcr  
    	// Jode doesn't like db4o coding style.
    	
	Handler handler = null;
	Iterator iterator = handlers.iterator();
	while (iterator.hasNext()) {
	    Handler handler_40_ = (Handler) iterator.next();
	    int i = handler_40_.start.getAddr();
	    int i_41_ = handler_40_.endAddr;
	    int i_42_ = handler_40_.handler.getAddr();
//	    if (i >= i_41_ || i_42_ < i_41_)
//		throw new AssertError("ExceptionHandler order failed: not " + i
//				      + " < " + i_41_ + " <= " + i_42_);
	    if (handler != null
		&& (handler.start.getAddr() != i || handler.endAddr != i_41_)
		&& i_41_ > handler.start.getAddr() && i_41_ < handler.endAddr)
		throw new AssertError
			  ("Exception handlers ranges are intersecting: ["
			   + handler.start.getAddr() + ", " + handler.endAddr
			   + "] and [" + i + ", " + i_41_ + "].");
	    handler = handler_40_;
	}
    }
    
    public void analyze() {
	checkTryCatchOrder();
	Iterator iterator = handlers.iterator();
	Handler handler = null;
	Handler handler_43_
	    = iterator.hasNext() ? (Handler) iterator.next() : null;
	while (handler_43_ != null) {
	    Handler handler_44_ = handler;
	    handler = handler_43_;
	    handler_43_
		= iterator.hasNext() ? (Handler) iterator.next() : null;
	    int i = 2147483647;
	    if (handler_43_ != null && handler_43_.endAddr > handler.endAddr)
		i = handler_43_.endAddr;
	    FlowBlock flowblock = handler.start;
	    flowblock.checkConsistent();
	    if (handler_44_ == null || handler.type == null
		|| handler_44_.start.getAddr() != handler.start.getAddr()
		|| handler_44_.endAddr != handler.endAddr) {
		if ((GlobalOptions.debuggingFlags & 0x20) != 0)
		    GlobalOptions.err.println("analyzeTry("
					      + handler.start.getAddr() + ", "
					      + handler.endAddr + ")");
		while (flowblock.analyze(flowblock.getAddr(),
					 handler.endAddr)) {
		    /* empty */
		}
		TryBlock tryblock = new TryBlock(flowblock);
	    } else if (!(flowblock.block instanceof TryBlock))
		throw new AssertError("no TryBlock");
	    FlowBlock flowblock_45_ = handler.handler;
	    boolean bool = flowblock_45_.predecessors.size() != 0;
	    if (!bool && handler_43_ != null) {
		Iterator iterator_46_
		    = handlers.tailSet(handler_43_).iterator();
		while (iterator_46_.hasNext()) {
		    Handler handler_47_ = (Handler) iterator_46_.next();
		    if (handler_47_.handler == flowblock_45_) {
			bool = true;
			break;
		    }
		}
	    }
	    if (bool) {
		EmptyBlock emptyblock
		    = new EmptyBlock(new Jump(flowblock_45_));
		FlowBlock flowblock_48_
		    = new FlowBlock(flowblock_45_.method,
				    flowblock_45_.getAddr());
		flowblock_48_.appendBlock(emptyblock, 0);
		flowblock_45_.prevByAddr.nextByAddr = flowblock_48_;
		flowblock_48_.prevByAddr = flowblock_45_.prevByAddr;
		flowblock_48_.nextByAddr = flowblock_45_;
		flowblock_45_.prevByAddr = flowblock_48_;
		flowblock_45_ = flowblock_48_;
	    } else {
		if ((GlobalOptions.debuggingFlags & 0x20) != 0)
		    GlobalOptions.err.println("analyzeCatch("
					      + flowblock_45_.getAddr() + ", "
					      + i + ")");
		while (flowblock_45_.analyze(flowblock_45_.getAddr(), i)) {
		    /* empty */
		}
	    }
	    if (handler.type != null)
		analyzeCatchBlock(handler.type, flowblock, flowblock_45_);
	    else if (!analyzeSynchronized(flowblock, flowblock_45_, i)
		     && !analyzeFinally(flowblock, flowblock_45_, i)
		     && !analyzeSpecialFinally(flowblock, flowblock_45_, i))
		analyzeCatchBlock(Type.tObject, flowblock, flowblock_45_);
	    flowblock.checkConsistent();
	    if ((GlobalOptions.debuggingFlags & 0x20) != 0)
		GlobalOptions.err.println("analyzeTryCatch("
					  + flowblock.getAddr() + ", "
					  + flowblock.getNextAddr()
					  + ") done.");
	}
    }
}
