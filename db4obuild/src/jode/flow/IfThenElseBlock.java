/* IfThenElseBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;
import java.util.Set;

import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;
import jode.util.SimpleSet;

public class IfThenElseBlock extends StructuredBlock
{
    Expression cond;
    VariableStack condStack;
    StructuredBlock thenBlock;
    StructuredBlock elseBlock;
    
    public IfThenElseBlock(Expression expression) {
	cond = expression;
    }
    
    public void setThenBlock(StructuredBlock structuredblock) {
	thenBlock = structuredblock;
	structuredblock.outer = this;
	structuredblock.setFlowBlock(flowBlock);
    }
    
    public void setElseBlock(StructuredBlock structuredblock) {
	elseBlock = structuredblock;
	structuredblock.outer = this;
	structuredblock.setFlowBlock(flowBlock);
    }
    
    public boolean replaceSubBlock(StructuredBlock structuredblock,
				   StructuredBlock structuredblock_0_) {
	if (thenBlock == structuredblock)
	    thenBlock = structuredblock_0_;
	else if (elseBlock == structuredblock)
	    elseBlock = structuredblock_0_;
	else
	    return false;
	return true;
    }
    
    public VariableStack mapStackToLocal(VariableStack variablestack) {
	int i = cond.getFreeOperandCount();
	VariableStack variablestack_1_;
	if (i > 0) {
	    condStack = variablestack.peek(i);
	    variablestack_1_ = variablestack.pop(i);
	} else
	    variablestack_1_ = variablestack;
	VariableStack variablestack_2_
	    = VariableStack.merge(thenBlock.mapStackToLocal(variablestack_1_),
				  (elseBlock == null ? variablestack_1_
				   : elseBlock
					 .mapStackToLocal(variablestack_1_)));
	if (jump != null) {
	    jump.stackMap = variablestack_2_;
	    return null;
	}
	return variablestack_2_;
    }
    
    public void removePush() {
	if (condStack != null)
	    cond = condStack.mergeIntoExpression(cond);
	thenBlock.removePush();
	if (elseBlock != null)
	    elseBlock.removePush();
    }
    
    public Set getDeclarables() {
	SimpleSet simpleset = new SimpleSet();
	cond.fillDeclarables(simpleset);
	return simpleset;
    }
    
    public void makeDeclaration(Set set) {
	cond.makeDeclaration(set);
	super.makeDeclaration(set);
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	boolean bool = thenBlock.needsBraces();
	tabbedprintwriter.print("if (");
	cond.dumpExpression(0, tabbedprintwriter);
	tabbedprintwriter.print(")");
	if (bool)
	    tabbedprintwriter.openBrace();
	else
	    tabbedprintwriter.println();
	tabbedprintwriter.tab();
	thenBlock.dumpSource(tabbedprintwriter);
	tabbedprintwriter.untab();
	if (elseBlock != null) {
	    if (bool)
		tabbedprintwriter.closeBraceContinue();
	    if (elseBlock instanceof IfThenElseBlock
		&& (elseBlock.declare == null
		    || elseBlock.declare.isEmpty())) {
		bool = false;
		tabbedprintwriter.print("else ");
		elseBlock.dumpSource(tabbedprintwriter);
	    } else {
		bool = elseBlock.needsBraces();
		tabbedprintwriter.print("else");
		if (bool)
		    tabbedprintwriter.openBrace();
		else
		    tabbedprintwriter.println();
		tabbedprintwriter.tab();
		elseBlock.dumpSource(tabbedprintwriter);
		tabbedprintwriter.untab();
	    }
	}
	if (bool)
	    tabbedprintwriter.closeBrace();
    }
    
    public StructuredBlock[] getSubBlocks() {
	return (elseBlock == null ? new StructuredBlock[] { thenBlock }
		: new StructuredBlock[] { thenBlock, elseBlock });
    }
    
    public boolean jumpMayBeChanged() {
	return ((thenBlock.jump != null || thenBlock.jumpMayBeChanged())
		&& elseBlock != null
		&& (elseBlock.jump != null || elseBlock.jumpMayBeChanged()));
    }
    
    public void simplify() {
	cond = cond.simplify();
	super.simplify();
    }
    
    public boolean doTransformations() {
	StructuredBlock structuredblock = flowBlock.lastModified;
	return (CreateCheckNull.transformJikes(this, structuredblock)
		|| CreateClassField.transform(this, structuredblock));
    }
}
