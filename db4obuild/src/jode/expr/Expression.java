/* Expression - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Set;

import jode.GlobalOptions;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public abstract class Expression
{
    protected Type type;
    Operator parent = null;
    public static Expression EMPTYSTRING = new ConstOperator("");
    
    public Expression(Type type) {
	this.type = type;
    }
    
    public void setType(Type type) {
	Type type_0_ = type.intersection(this.type);
	if (!this.type.equals(type_0_)) {
	    if (type_0_ == Type.tError && type != Type.tError) {
		GlobalOptions.err.println("setType: Type error in " + this
					  + ": merging " + this.type + " and "
					  + type);
		if (parent != null)
		    GlobalOptions.err.println("\tparent is " + parent);
		if ((GlobalOptions.debuggingFlags & 0x4) != 0)
		    Thread.dumpStack();
	    }
	    this.type = type_0_;
	    if (this.type != Type.tError)
		updateSubTypes();
	}
    }
    
    public void updateParentType(Type type) {
	setType(type);
	if (parent != null)
	    parent.updateType();
    }
    
    public abstract void updateType();
    
    public abstract void updateSubTypes();
    
    public Type getType() {
	return type;
    }
    
    public Operator getParent() {
	return parent;
    }
    
    public abstract int getPriority();
    
    public int getBreakPenalty() {
	return 0;
    }
    
    public abstract int getFreeOperandCount();
    
    public abstract Expression addOperand(Expression expression_1_);
    
    public Expression negate() {
	UnaryOperator unaryoperator = new UnaryOperator(Type.tBoolean, 34);
	unaryoperator.addOperand(this);
	return unaryoperator;
    }
    
    public boolean hasSideEffects(Expression expression_2_) {
	return false;
    }
    
    public int canCombine(CombineableOperator combineableoperator) {
	return 0;
    }
    
    public boolean containsMatchingLoad
	(CombineableOperator combineableoperator) {
	return false;
    }
    
    public boolean containsConflictingLoad
	(MatchableOperator matchableoperator) {
	return false;
    }
    
    public Expression combine(CombineableOperator combineableoperator) {
	return null;
    }
    
    public Expression removeOnetimeLocals() {
	return this;
    }
    
    public Expression simplify() {
	return this;
    }
    
    public Expression simplifyString() {
	return this;
    }
    
    public Expression simplifyStringBuffer() {
	return null;
    }
    
    public void makeInitializer(Type type) {
	/* empty */
    }
    
    public boolean isConstant() {
	return true;
    }
    
    public void fillInGenSet(Collection collection, Collection collection_3_) {
	/* empty */
    }
    
    public void fillDeclarables(Collection collection) {
	/* empty */
    }
    
    public void makeDeclaration(Set set) {
	/* empty */
    }
    
    public abstract void dumpExpression
	(TabbedPrintWriter tabbedprintwriter) throws IOException;
    
    public void dumpExpression(int i, TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.startOp(i, getBreakPenalty());
	dumpExpression(tabbedprintwriter);
	tabbedprintwriter.endOp();
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter, int i)
	throws IOException {
	boolean bool = false;
	boolean bool_4_ = false;
	boolean bool_5_ = false;
	boolean bool_6_ = false;
	String string = "";
	if (type == Type.tError)
	    string = "/*TYPE_ERROR*/";
	else if ((GlobalOptions.debuggingFlags & 0x4) != 0)
	    string = "(TYPE " + type + ")";
	if (string != "") {
	    if (i > 700) {
		bool = true;
		bool_5_ = true;
		tabbedprintwriter.print("(");
		tabbedprintwriter.startOp(0, 0);
	    } else if (i < 700) {
		bool_5_ = true;
		tabbedprintwriter.startOp(2, 1);
	    }
	    tabbedprintwriter.print(string);
	    tabbedprintwriter.breakOp();
	    tabbedprintwriter.print(" ");
	    i = 700;
	}
	int i_7_ = getPriority();
	if (i_7_ < i) {
	    bool_4_ = true;
	    bool_6_ = true;
	    tabbedprintwriter.print("(");
	    tabbedprintwriter.startOp(0, getBreakPenalty());
	} else if (i_7_ != i) {
	    bool_6_ = true;
	    if (getType() == Type.tVoid)
		tabbedprintwriter.startOp(1, getBreakPenalty());
	    else
		tabbedprintwriter.startOp(2, 1 + getBreakPenalty());
	}
	try {
	    dumpExpression(tabbedprintwriter);
	} catch (RuntimeException runtimeexception) {
	    tabbedprintwriter.print("(RUNTIME ERROR IN EXPRESSION)");
	    runtimeexception.printStackTrace(GlobalOptions.err);
	}
	if (bool_6_) {
	    tabbedprintwriter.endOp();
	    if (bool_4_)
		tabbedprintwriter.print(")");
	}
	if (bool_5_) {
	    tabbedprintwriter.endOp();
	    if (bool)
		tabbedprintwriter.print(")");
	}
    }
    
    public String toString() {
	try {
	    StringWriter stringwriter = new StringWriter();
	    TabbedPrintWriter tabbedprintwriter
		= new TabbedPrintWriter(stringwriter);
	    dumpExpression(tabbedprintwriter);
	    return stringwriter.toString();
	} catch (IOException ioexception) {
	    return "/*IOException*/" + super.toString();
	} catch (RuntimeException runtimeexception) {
	    return "/*RuntimeException*/" + super.toString();
	}
    }
    
    public boolean isVoid() {
	return getType() == Type.tVoid;
    }
}
