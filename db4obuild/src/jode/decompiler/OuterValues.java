/* OuterValues - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.util.Enumeration;
import java.util.Vector;

import jode.GlobalOptions;
import jode.expr.Expression;
import jode.expr.LocalLoadOperator;
import jode.expr.OuterLocalOperator;
import jode.expr.ThisOperator;
import jode.type.Type;

public class OuterValues
{
    private ClassAnalyzer clazzAnalyzer;
    private Expression[] head;
    private Vector ovListeners;
    private boolean jikesAnonymousInner;
    private boolean implicitOuterClass;
    private int headCount;
    private int headMinCount;
    
    public OuterValues(ClassAnalyzer classanalyzer, Expression[] expressions) {
	clazzAnalyzer = classanalyzer;
	head = expressions;
	headMinCount = 0;
	headCount = expressions.length;
	if ((GlobalOptions.debuggingFlags & 0x200) != 0)
	    GlobalOptions.err.println("Created OuterValues: " + this);
    }
    
    public Expression getValue(int i) {
	return head[i];
    }
    
    public int getCount() {
	return headCount;
    }
    
    private int getNumberBySlot(int i) {
	i--;
	for (int i_0_ = 0; i >= 0 && i_0_ < headCount; i_0_++) {
	    if (i == 0)
		return i_0_;
	    i -= head[i_0_].getType().stackSize();
	}
	return -1;
    }
    
    public Expression getValueBySlot(int i) {
	i--;
	for (int i_1_ = 0; i_1_ < headCount; i_1_++) {
	    if (i == 0) {
		Expression expression = head[i_1_];
		if (i_1_ >= headMinCount)
		    headMinCount = i_1_;
		return expression;
	    }
	    i -= head[i_1_].getType().stackSize();
	}
	return null;
    }
    
    private Expression liftOuterValue(LocalInfo localinfo, final int nr) {
	MethodAnalyzer methodanalyzer = localinfo.getMethodAnalyzer();
	if (!methodanalyzer.isConstructor() || methodanalyzer.isStatic())
	    return null;
	OuterValues outervalues_2_
	    = methodanalyzer.getClassAnalyzer().getOuterValues();
	if (outervalues_2_ == null)
	    return null;
	int i = outervalues_2_.getNumberBySlot(localinfo.getSlot());
	if ((GlobalOptions.debuggingFlags & 0x200) != 0)
	    GlobalOptions.err.println("  ovNr " + i + "," + outervalues_2_);
	if (i < 0 && outervalues_2_.getCount() >= 1
	    && outervalues_2_.isJikesAnonymousInner()) {
	    Type[] types = methodanalyzer.getType().getParameterTypes();
	    int i_3_ = 1;
	    for (int i_4_ = 0; i_4_ < types.length - 1; i_4_++)
		i_3_ += types[i_4_].stackSize();
	    if (localinfo.getSlot() == i_3_)
		i = 0;
	}
	if (i < 0)
	    return null;
	if (outervalues_2_ != this || i > nr) {
	    final int limit = i;
	    outervalues_2_.addOuterValueListener(new OuterValueListener() {
		public void shrinkingOuterValues(OuterValues outervalues_8_,
						 int i_9_) {
		    if (i_9_ <= limit)
			setCount(nr);
		}
	    });
	}
	return outervalues_2_.head[i];
    }
    
    public boolean unifyOuterValues(int i, Expression expression) {
	if ((GlobalOptions.debuggingFlags & 0x200) != 0)
	    GlobalOptions.err.println("unifyOuterValues: " + this + "," + i
				      + "," + expression);
	Expression expression_10_ = expression;
	Expression expression_11_ = head[i];
	LocalInfo localinfo;
	if (expression_10_ instanceof ThisOperator)
	    localinfo = null;
	else if (expression_10_ instanceof OuterLocalOperator)
	    localinfo = ((OuterLocalOperator) expression_10_).getLocalInfo();
	else if (expression_10_ instanceof LocalLoadOperator)
	    localinfo = ((LocalLoadOperator) expression_10_).getLocalInfo();
	else
	    return false;
	while (localinfo != null) {
	    if (localinfo.getMethodAnalyzer().isMoreOuterThan(clazzAnalyzer))
		break;
	    expression_10_ = liftOuterValue(localinfo, i);
	    if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		GlobalOptions.err.println("  lift1 " + localinfo + " in "
					  + localinfo.getMethodAnalyzer()
					  + "  to " + expression_10_);
	    if (expression_10_ instanceof ThisOperator)
		localinfo = null;
	    else if (expression_10_ instanceof OuterLocalOperator)
		localinfo
		    = ((OuterLocalOperator) expression_10_).getLocalInfo();
	    else
		return false;
	}
	while (!expression_10_.equals(expression_11_)) {
	    if (expression_11_ instanceof OuterLocalOperator) {
		LocalInfo localinfo_12_
		    = ((OuterLocalOperator) expression_11_).getLocalInfo();
		if (localinfo_12_.equals(localinfo))
		    break;
		expression_11_ = liftOuterValue(localinfo_12_, i);
		if ((GlobalOptions.debuggingFlags & 0x200) != 0)
		    GlobalOptions.err.println("  lift2 " + localinfo_12_
					      + " in "
					      + localinfo_12_
						    .getMethodAnalyzer()
					      + "  to " + expression_11_);
	    } else
		return false;
	}
	if ((GlobalOptions.debuggingFlags & 0x200) != 0)
	    GlobalOptions.err.println("unifyOuterValues succeeded.");
	return true;
    }
    
    public boolean isJikesAnonymousInner() {
	return jikesAnonymousInner;
    }
    
    public boolean isImplicitOuterClass() {
	return implicitOuterClass;
    }
    
    public void addOuterValueListener(OuterValueListener outervaluelistener) {
	if (ovListeners == null)
	    ovListeners = new Vector();
	ovListeners.addElement(outervaluelistener);
    }
    
    public void setJikesAnonymousInner(boolean bool) {
	jikesAnonymousInner = bool;
    }
    
    public void setImplicitOuterClass(boolean bool) {
	implicitOuterClass = bool;
    }
    
    private static int countSlots(Expression[] expressions, int i) {
	int i_13_ = 0;
	for (int i_14_ = 0; i_14_ < i; i_14_++)
	    i_13_ += expressions[i_14_].getType().stackSize();
	return i_13_;
    }
    
    public void setMinCount(int i) {
	if (headCount < i) {
	    GlobalOptions.err.println
		("WARNING: something got wrong with scoped class "
		 + clazzAnalyzer.getClazz() + ": " + i + "," + headCount);
	    new Throwable().printStackTrace(GlobalOptions.err);
	    headMinCount = headCount;
	} else if (i > headMinCount)
	    headMinCount = i;
    }
    
    public void setCount(int i) {
	if (i < headCount) {
	    headCount = i;
	    if ((GlobalOptions.debuggingFlags & 0x200) != 0) {
		GlobalOptions.err.println("setCount: " + this + "," + i);
		new Throwable().printStackTrace(GlobalOptions.err);
	    }
	    if (i < headMinCount) {
		GlobalOptions.err.println
		    ("WARNING: something got wrong with scoped class "
		     + clazzAnalyzer.getClazz() + ": " + headMinCount + ","
		     + headCount);
		new Throwable().printStackTrace(GlobalOptions.err);
		headMinCount = i;
	    }
	    if (ovListeners != null) {
		Enumeration enumeration = ovListeners.elements();
		while (enumeration.hasMoreElements())
		    ((OuterValueListener) enumeration.nextElement())
			.shrinkingOuterValues(this, i);
	    }
	}
    }
    
    public String toString() {
	StringBuffer stringbuffer
	    = new StringBuffer().append(clazzAnalyzer.getClazz())
		  .append(".OuterValues[");
	String string = "";
	int i = 1;
	for (int i_15_ = 0; i_15_ < headCount; i_15_++) {
	    if (i_15_ == headMinCount)
		stringbuffer.append("<-");
	    stringbuffer.append(string).append(i).append(":")
		.append(head[i_15_]);
	    i += head[i_15_].getType().stackSize();
	    string = ",";
	}
	if (jikesAnonymousInner)
	    stringbuffer.append("!jikesAnonymousInner");
	if (implicitOuterClass)
	    stringbuffer.append("!implicitOuterClass");
	return stringbuffer.append("]").toString();
    }
}
