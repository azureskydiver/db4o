/* TryBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;
import jode.expr.InvokeOperator;
import jode.expr.LocalLoadOperator;
import jode.type.Type;

public class TryBlock extends StructuredBlock
{
    VariableSet gen;
    StructuredBlock[] subBlocks = new StructuredBlock[1];
    
    public TryBlock(FlowBlock flowblock) {
	gen = (VariableSet) flowblock.gen.clone();
	flowBlock = flowblock;
	StructuredBlock structuredblock = flowblock.block;
	this.replace(structuredblock);
	subBlocks = new StructuredBlock[] { structuredblock };
	structuredblock.outer = this;
	flowblock.lastModified = this;
	flowblock.checkConsistent();
    }
    
    public void addCatchBlock(StructuredBlock structuredblock) {
	StructuredBlock[] structuredblocks
	    = new StructuredBlock[subBlocks.length + 1];
	System.arraycopy(subBlocks, 0, structuredblocks, 0, subBlocks.length);
	structuredblocks[subBlocks.length] = structuredblock;
	subBlocks = structuredblocks;
	structuredblock.outer = this;
	structuredblock.setFlowBlock(flowBlock);
    }
    
    public boolean replaceSubBlock(StructuredBlock structuredblock,
				   StructuredBlock structuredblock_0_) {
	for (int i = 0; i < subBlocks.length; i++) {
	    if (subBlocks[i] == structuredblock) {
		subBlocks[i] = structuredblock_0_;
		return true;
	    }
	}
	return false;
    }
    
    public StructuredBlock[] getSubBlocks() {
	return subBlocks;
    }
    
    public VariableStack mapStackToLocal(VariableStack variablestack) {
	VariableStack variablestack_1_
	    = subBlocks[0].mapStackToLocal(variablestack);
	for (int i = 1; i < subBlocks.length; i++)
	    variablestack_1_
		= (VariableStack.merge
		   (variablestack_1_,
		    subBlocks[i].mapStackToLocal(VariableStack.EMPTY)));
	if (jump != null) {
	    jump.stackMap = variablestack_1_;
	    return null;
	}
	return variablestack_1_;
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.print("try");
	tabbedprintwriter.openBrace();
	tabbedprintwriter.tab();
	subBlocks[0].dumpSource(tabbedprintwriter);
	tabbedprintwriter.untab();
	for (int i = 1; i < subBlocks.length; i++)
	    subBlocks[i].dumpSource(tabbedprintwriter);
	tabbedprintwriter.closeBrace();
    }
    
    public boolean jumpMayBeChanged() {
	for (int i = 0; i < subBlocks.length; i++) {
	    if (subBlocks[i].jump == null && !subBlocks[i].jumpMayBeChanged())
		return false;
	}
	return true;
    }
    
    public boolean checkJikesArrayClone() {
	if (subBlocks.length != 2
	    || !(subBlocks[0] instanceof InstructionBlock)
	    || !(subBlocks[1] instanceof CatchBlock))
	    return false;
	Expression expression
	    = ((InstructionBlock) subBlocks[0]).getInstruction();
	CatchBlock catchblock = (CatchBlock) subBlocks[1];
	if (expression.isVoid() || expression.getFreeOperandCount() != 0
	    || !(expression instanceof InvokeOperator)
	    || !(catchblock.catchBlock instanceof ThrowBlock)
	    || !(catchblock.exceptionType.equals
		 (Type.tClass("java.lang.CloneNotSupportedException"))))
	    return false;
	InvokeOperator invokeoperator = (InvokeOperator) expression;
	if (!invokeoperator.getMethodName().equals("clone")
	    || invokeoperator.isStatic()
	    || !invokeoperator.getMethodType().getTypeSignature()
		    .equals("()Ljava/lang/Object;")
	    || !invokeoperator.getSubExpressions()[0].getType()
		    .isOfType(Type.tArray(Type.tUnknown)))
	    return false;
	Expression expression_2_
	    = ((ThrowBlock) catchblock.catchBlock).getInstruction();
	if (expression_2_.getFreeOperandCount() != 0
	    || !(expression_2_ instanceof InvokeOperator))
	    return false;
	InvokeOperator invokeoperator_3_ = (InvokeOperator) expression_2_;
	if (!invokeoperator_3_.isConstructor()
	    || !invokeoperator_3_.getClassType()
		    .equals(Type.tClass("java.lang.InternalError"))
	    || (invokeoperator_3_.getMethodType().getParameterTypes().length
		!= 1))
	    return false;
	Expression expression_4_ = invokeoperator_3_.getSubExpressions()[1];
	if (!(expression_4_ instanceof InvokeOperator))
	    return false;
	InvokeOperator invokeoperator_5_ = (InvokeOperator) expression_4_;
	if (!invokeoperator_5_.getMethodName().equals("getMessage")
	    || invokeoperator_5_.isStatic()
	    || (invokeoperator_5_.getMethodType().getParameterTypes().length
		!= 0)
	    || (invokeoperator_5_.getMethodType().getReturnType()
		!= Type.tString))
	    return false;
	Expression expression_6_ = invokeoperator_5_.getSubExpressions()[0];
	if (!(expression_6_ instanceof LocalLoadOperator)
	    || !((LocalLoadOperator) expression_6_).getLocalInfo()
		    .equals(catchblock.exceptionLocal))
	    return false;
	subBlocks[0].replace(this);
	if (flowBlock.lastModified == this)
	    flowBlock.lastModified = subBlocks[0];
	return true;
    }
    
    public boolean doTransformations() {
	return checkJikesArrayClone();
    }
}
