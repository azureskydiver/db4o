/* Operator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import jode.AssertError;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public abstract class Operator extends Expression
{
    public static final int ADD_OP = 1;
    public static final int SUB_OP = 2;
    public static final int SHIFT_OP = 6;
    public static final int AND_OP = 9;
    public static final int ASSIGN_OP = 12;
    public static final int OPASSIGN_OP = 12;
    public static final int INC_OP = 24;
    public static final int DEC_OP = 25;
    public static final int COMPARE_OP = 26;
    public static final int EQUALS_OP = 26;
    public static final int NOTEQUALS_OP = 27;
    public static final int LESS_OP = 28;
    public static final int GREATEREQ_OP = 29;
    public static final int GREATER_OP = 30;
    public static final int LESSEQ_OP = 31;
    public static final int LOG_AND_OP = 32;
    public static final int LOG_OR_OP = 33;
    public static final int LOG_NOT_OP = 34;
    public static final int NEG_OP = 36;
    static String[] opString
	= { "", " + ", " - ", " * ", " / ", " % ", " << ", " >> ", " >>> ",
	    " & ", " | ", " ^ ", " = ", " += ", " -= ", " *= ", " /= ", " %= ",
	    " <<= ", " >>= ", " >>>= ", " &= ", " |= ", " ^= ", "++", "--",
	    " == ", " != ", " < ", " >= ", " > ", " <= ", " && ", " || ", "!",
	    "~", "-" };
    protected int operatorIndex;
    private int operandcount;
    Expression[] subExpressions;
    
    public Operator(Type type) {
	this(type, 0);
    }
    
    public Operator(Type type, int i) {
	super(type);
	operatorIndex = i;
	if (type == null)
	    throw new AssertError("type == null");
    }
    
    public void initOperands(int i) {
	operandcount = i;
	subExpressions = new Expression[i];
	for (int i_0_ = 0; i_0_ < i; i_0_++) {
	    subExpressions[i_0_] = new NopOperator(Type.tUnknown);
	    subExpressions[i_0_].parent = this;
	}
	this.updateSubTypes();
    }
    
    public int getFreeOperandCount() {
	return operandcount;
    }
    
    public boolean isFreeOperator() {
	return (subExpressions.length == 0
		|| (subExpressions[subExpressions.length - 1]
		    instanceof NopOperator));
    }
    
    public boolean isFreeOperator(int i) {
	return (subExpressions.length == i
		&& (i == 0 || subExpressions[i - 1] instanceof NopOperator));
    }
    
    public Expression addOperand(Expression expression) {
	int i = subExpressions.length;
	while (i-- > 0) {
	    int i_1_ = subExpressions[i].getFreeOperandCount();
	    if (i_1_ > 0) {
		subExpressions[i] = subExpressions[i].addOperand(expression);
		operandcount += subExpressions[i].getFreeOperandCount() - i_1_;
		this.updateType();
		return this;
	    }
	}
	throw new AssertError("addOperand called, but no operand needed");
    }
    
    public Operator getOperator() {
	return this;
    }
    
    public Expression[] getSubExpressions() {
	return subExpressions;
    }
    
    public void setSubExpressions(int i, Expression expression) {
	int i_2_ = (expression.getFreeOperandCount()
		    - subExpressions[i].getFreeOperandCount());
	subExpressions[i] = expression;
	expression.parent = this;
	for (Operator operator_3_ = this; operator_3_ != null;
	     operator_3_ = operator_3_.parent)
	    operator_3_.operandcount += i_2_;
	this.updateType();
    }
    
    public int getOperatorIndex() {
	return operatorIndex;
    }
    
    public void setOperatorIndex(int i) {
	operatorIndex = i;
    }
    
    public String getOperatorString() {
	return opString[operatorIndex];
    }
    
    public boolean opEquals(Operator operator_4_) {
	return this == operator_4_;
    }
    
    public Expression simplify() {
	for (int i = 0; i < subExpressions.length; i++) {
	    subExpressions[i] = subExpressions[i].simplify();
	    subExpressions[i].parent = this;
	}
	return this;
    }
    
    public void fillInGenSet(Collection collection, Collection collection_5_) {
	if (this instanceof LocalVarOperator) {
	    LocalVarOperator localvaroperator = (LocalVarOperator) this;
	    if (localvaroperator.isRead() && collection != null)
		collection.add(localvaroperator.getLocalInfo());
	    if (collection_5_ != null)
		collection_5_.add(localvaroperator.getLocalInfo());
	}
	for (int i = 0; i < subExpressions.length; i++)
	    subExpressions[i].fillInGenSet(collection, collection_5_);
    }
    
    public void fillDeclarables(Collection collection) {
	for (int i = 0; i < subExpressions.length; i++)
	    subExpressions[i].fillDeclarables(collection);
    }
    
    public void makeDeclaration(Set set) {
	for (int i = 0; i < subExpressions.length; i++)
	    subExpressions[i].makeDeclaration(set);
    }
    
    public boolean hasSideEffects(Expression expression) {
	if (expression instanceof MatchableOperator
	    && expression
		   .containsConflictingLoad((MatchableOperator) expression))
	    return true;
	for (int i = 0; i < subExpressions.length; i++) {
	    if (subExpressions[i].hasSideEffects(expression))
		return true;
	}
	return false;
    }
    
    public boolean containsConflictingLoad
	(MatchableOperator matchableoperator) {
	if (matchableoperator.matches(this))
	    return true;
	for (int i = 0; i < subExpressions.length; i++) {
	    if (subExpressions[i].containsConflictingLoad(matchableoperator))
		return true;
	}
	return false;
    }
    
    public boolean containsMatchingLoad
	(CombineableOperator combineableoperator) {
	Operator operator_6_ = (Operator) combineableoperator;
	if (combineableoperator.getLValue().matches(this)
	    && subsEquals((Operator) combineableoperator.getLValue()))
	    return true;
	for (int i = 0; i < subExpressions.length; i++) {
	    if (subExpressions[i].containsMatchingLoad(combineableoperator))
		return true;
	}
	return false;
    }
    
    public int canCombine(CombineableOperator combineableoperator) {
	if (combineableoperator.getLValue() instanceof LocalStoreOperator
	    && ((Operator) combineableoperator).getFreeOperandCount() == 0) {
	    for (int i = 0; i < subExpressions.length; i++) {
		int i_7_ = subExpressions[i].canCombine(combineableoperator);
		if (i_7_ != 0)
		    return i_7_;
		if (subExpressions[i]
			.hasSideEffects((Expression) combineableoperator))
		    return -1;
	    }
	}
	if (combineableoperator.lvalueMatches(this))
	    return subsEquals((Operator) combineableoperator) ? 1 : -1;
	if (subExpressions.length > 0)
	    return subExpressions[0].canCombine(combineableoperator);
	return 0;
    }
    
    public Expression combine(CombineableOperator combineableoperator) {
	Operator operator_8_ = (Operator) combineableoperator;
	if (combineableoperator.lvalueMatches(this)) {
	    combineableoperator.makeNonVoid();
	    operator_8_.parent = parent;
	    return operator_8_;
	}
	for (int i = 0; i < subExpressions.length; i++) {
	    Expression expression
		= subExpressions[i].combine(combineableoperator);
	    if (expression != null) {
		subExpressions[i] = expression;
		this.updateType();
		return this;
	    }
	}
	return null;
    }
    
    public boolean subsEquals(Operator operator_9_) {
	if (this == operator_9_)
	    return true;
	if (operator_9_.subExpressions == null)
	    return subExpressions == null;
	if (subExpressions.length != operator_9_.subExpressions.length)
	    return false;
	for (int i = 0; i < subExpressions.length; i++) {
	    if (!subExpressions[i].equals(operator_9_.subExpressions[i]))
		return false;
	}
	return true;
    }
    
    public boolean equals(Object object) {
	if (this == object)
	    return true;
	if (!(object instanceof Operator))
	    return false;
	Operator operator_10_ = (Operator) object;
	return opEquals(operator_10_) && subsEquals(operator_10_);
    }
    
    public boolean isConstant() {
	for (int i = 0; i < subExpressions.length; i++) {
	    if (!subExpressions[i].isConstant())
		return false;
	}
	return true;
    }
    
    public abstract void dumpExpression
	(TabbedPrintWriter tabbedprintwriter) throws IOException;
}
