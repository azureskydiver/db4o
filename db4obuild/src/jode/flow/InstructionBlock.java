/* InstructionBlock - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.io.IOException;
import java.util.Set;

import jode.decompiler.LocalInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.expr.Expression;
import jode.expr.LocalStoreOperator;
import jode.expr.StoreInstruction;
import jode.type.Type;

public class InstructionBlock extends InstructionContainer
{
    VariableStack stack;
    LocalInfo pushedLocal = null;
    boolean isDeclaration = false;
    
    public InstructionBlock(Expression expression) {
	super(expression);
    }
    
    public InstructionBlock(Expression expression, Jump jump) {
	super(expression, jump);
    }
    
    public VariableStack mapStackToLocal(VariableStack variablestack) {
	int i = instr.getFreeOperandCount();
	if (i > 0)
	    stack = variablestack.peek(i);
	VariableStack variablestack_0_;
	if (instr.getType() != Type.tVoid) {
	    pushedLocal = new LocalInfo();
	    pushedLocal.setType(instr.getType());
	    variablestack_0_ = variablestack.poppush(i, pushedLocal);
	} else if (i > 0)
	    variablestack_0_ = variablestack.pop(i);
	else
	    variablestack_0_ = variablestack;
	return super.mapStackToLocal(variablestack_0_);
    }
    
    public void removePush() {
	if (stack != null)
	    instr = stack.mergeIntoExpression(instr);
	if (pushedLocal != null) {
	    Expression expression
		= new StoreInstruction
		      (new LocalStoreOperator(pushedLocal.getType(),
					      pushedLocal))
		      .addOperand(instr);
	    instr = expression;
	}
	super.removePush();
    }
    
    public boolean needsBraces() {
	return isDeclaration || declare != null && !declare.isEmpty();
    }
    
    public void checkDeclaration(Set set) {
	if (instr instanceof StoreInstruction
	    && (((StoreInstruction) instr).getLValue()
		instanceof LocalStoreOperator)) {
	    StoreInstruction storeinstruction = (StoreInstruction) instr;
	    LocalInfo localinfo
		= ((LocalStoreOperator) storeinstruction.getLValue())
		      .getLocalInfo();
	    if (set.contains(localinfo)) {
		isDeclaration = true;
		set.remove(localinfo);
	    }
	}
    }
    
    public void makeDeclaration(Set set) {
	super.makeDeclaration(set);
	checkDeclaration(declare);
    }
    
    public void dumpInstruction(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	if (isDeclaration) {
	    StoreInstruction storeinstruction = (StoreInstruction) instr;
	    LocalInfo localinfo
		= ((LocalStoreOperator) storeinstruction.getLValue())
		      .getLocalInfo();
	    tabbedprintwriter.startOp(1, 0);
	    localinfo.dumpDeclaration(tabbedprintwriter);
	    tabbedprintwriter.breakOp();
	    tabbedprintwriter.print(" = ");
	    storeinstruction.getSubExpressions()[1]
		.makeInitializer(localinfo.getType());
	    storeinstruction.getSubExpressions()[1]
		.dumpExpression(2, tabbedprintwriter);
	    tabbedprintwriter.endOp();
	} else {
	    try {
		if (instr.getType() != Type.tVoid) {
		    tabbedprintwriter.print("PUSH ");
		    instr.dumpExpression(2, tabbedprintwriter);
		} else
		    instr.dumpExpression(1, tabbedprintwriter);
	    } catch (RuntimeException runtimeexception) {
		tabbedprintwriter.print("(RUNTIME ERROR IN EXPRESSION)");
	    }
	}
	tabbedprintwriter.println(";");
    }
}
