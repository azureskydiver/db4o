/* InstructionContainer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.util.Set;

import jode.expr.Expression;
import jode.expr.InvokeOperator;
import jode.util.SimpleSet;

public abstract class InstructionContainer extends StructuredBlock
{
    Expression instr;
    
    public InstructionContainer(Expression expression) {
	instr = expression;
    }
    
    public InstructionContainer(Expression expression, Jump jump) {
	this(expression);
	this.setJump(jump);
    }
    
    public void makeDeclaration(Set set) {
	if (instr != null)
	    instr.makeDeclaration(set);
	super.makeDeclaration(set);
    }
    
    public void removeOnetimeLocals() {
	if (instr != null)
	    instr = instr.removeOnetimeLocals();
	super.removeOnetimeLocals();
    }
    
    public void fillInGenSet(Set set, Set set_0_) {
	if (instr != null)
	    instr.fillInGenSet(set, set_0_);
    }
    
    public Set getDeclarables() {
	SimpleSet simpleset = new SimpleSet();
	if (instr != null)
	    instr.fillDeclarables(simpleset);
	return simpleset;
    }
    
    public boolean doTransformations() {
	if (instr == null)
	    return false;
	if (instr instanceof InvokeOperator) {
	    Expression expression = ((InvokeOperator) instr).simplifyAccess();
	    if (expression != null)
		instr = expression;
	}
	StructuredBlock structuredblock = flowBlock.lastModified;
	return (CreateNewConstructor.transform(this, structuredblock)
		|| CreateAssignExpression.transform(this, structuredblock)
		|| CreateExpression.transform(this, structuredblock)
		|| CreatePrePostIncExpression.transform(this, structuredblock)
		|| CreateIfThenElseOperator.create(this, structuredblock)
		|| CreateConstantArray.transform(this, structuredblock)
		|| CreateCheckNull.transformJavac(this, structuredblock));
    }
    
    public final Expression getInstruction() {
	return instr;
    }
    
    public void simplify() {
	if (instr != null)
	    instr = instr.simplify();
	super.simplify();
    }
    
    public final void setInstruction(Expression expression) {
	instr = expression;
    }
}
