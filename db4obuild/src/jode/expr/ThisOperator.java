/* ThisOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.bytecode.ClassInfo;
import jode.decompiler.TabbedPrintWriter;
import jode.type.Type;

public class ThisOperator extends NoArgOperator
{
    boolean isInnerMost;
    ClassInfo classInfo;
    
    public ThisOperator(ClassInfo classinfo, boolean bool) {
	super(Type.tClass(classinfo));
	classInfo = classinfo;
	isInnerMost = bool;
    }
    
    public ThisOperator(ClassInfo classinfo) {
	this(classinfo, false);
    }
    
    public ClassInfo getClassInfo() {
	return classInfo;
    }
    
    public int getPriority() {
	return 1000;
    }
    
    public String toString() {
	return classInfo + ".this";
    }
    
    public boolean opEquals(Operator operator) {
	return (operator instanceof ThisOperator
		&& ((ThisOperator) operator).classInfo.equals(classInfo));
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	if (!isInnerMost) {
	    tabbedprintwriter.print(tabbedprintwriter.getClassString(classInfo,
								     4));
	    tabbedprintwriter.print(".");
	}
	tabbedprintwriter.print("this");
    }
}
