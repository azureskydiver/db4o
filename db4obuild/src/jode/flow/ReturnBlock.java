/* ReturnBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;

import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;

public class ReturnBlock extends InstructionContainer
{
    VariableStack stack;
    
    public ReturnBlock() {
	super(null);
    }
    
    public ReturnBlock(Expression expression) {
	super(expression, new Jump(FlowBlock.END_OF_METHOD));
    }
    
    public VariableStack mapStackToLocal(VariableStack variablestack) {
	VariableStack variablestack_0_ = variablestack;
	if (instr != null) {
	    int i = instr.getFreeOperandCount();
	    if (i > 0) {
		stack = variablestack.peek(i);
		variablestack_0_ = variablestack.pop(i);
	    }
	}
	if (jump != null)
	    jump.stackMap = variablestack_0_;
	return null;
    }
    
    public void removePush() {
	if (stack != null)
	    instr = stack.mergeIntoExpression(instr);
    }
    
    public boolean needsBraces() {
	return declare != null && !declare.isEmpty();
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.print("return");
	if (instr != null) {
	    tabbedprintwriter.print(" ");
	    instr.dumpExpression(2, tabbedprintwriter);
	}
	tabbedprintwriter.println(";");
    }
}
