/* FieldOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;

import jode.AssertError;
import jode.bytecode.ClassInfo;
import jode.bytecode.FieldInfo;
import jode.bytecode.InnerClassInfo;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.decompiler.ClassAnalyzer;
import jode.decompiler.FieldAnalyzer;
import jode.decompiler.MethodAnalyzer;
import jode.decompiler.Options;
import jode.decompiler.Scope;
import jode.decompiler.TabbedPrintWriter;
import jode.type.ClassInterfacesType;
import jode.type.NullType;
import jode.type.Type;

public abstract class FieldOperator extends Operator
{
    MethodAnalyzer methodAnalyzer;
    boolean staticFlag;
    Reference ref;
    Type classType;
    
    public FieldOperator(MethodAnalyzer methodanalyzer, boolean bool,
			 Reference reference) {
	super(Type.tType(reference.getType()));
	methodAnalyzer = methodanalyzer;
	staticFlag = bool;
	classType = Type.tType(reference.getClazz());
	ref = reference;
	if (bool)
	    methodanalyzer.useType(classType);
	this.initOperands(bool ? 0 : 1);
    }
    
    public int getPriority() {
	return 950;
    }
    
    public void updateSubTypes() {
	if (!staticFlag)
	    subExpressions[0].setType(Type.tSubType(classType));
    }
    
    public void updateType() {
	this.updateParentType(getFieldType());
    }
    
    public boolean isStatic() {
	return staticFlag;
    }
    
    public ClassInfo getClassInfo() {
	if (classType instanceof ClassInterfacesType)
	    return ((ClassInterfacesType) classType).getClassInfo();
	return null;
    }
    
    public FieldAnalyzer getField() {
	ClassInfo classinfo = getClassInfo();
	if (classinfo != null) {
	    ClassAnalyzer classanalyzer = methodAnalyzer.getClassAnalyzer();
	    for (;;) {
		if (classinfo == classanalyzer.getClazz()) {
		    int i = (classanalyzer.getFieldIndex
			     (ref.getName(), Type.tType(ref.getType())));
		    if (i >= 0)
			return classanalyzer.getField(i);
		    return null;
		}
		if (classanalyzer.getParent() == null)
		    return null;
		if (classanalyzer.getParent() instanceof MethodAnalyzer)
		    classanalyzer
			= ((MethodAnalyzer) classanalyzer.getParent())
			      .getClassAnalyzer();
		else {
		    if (!(classanalyzer.getParent() instanceof ClassAnalyzer))
			break;
		    classanalyzer = (ClassAnalyzer) classanalyzer.getParent();
		}
	    }
	    throw new AssertError("Unknown parent");
	}
	return null;
    }
    
    public String getFieldName() {
	return ref.getName();
    }
    
    public Type getFieldType() {
	return Type.tType(ref.getType());
    }
    
    private static FieldInfo getFieldInfo(ClassInfo classinfo, String string,
					  String string_0_) {
	for (/**/; classinfo != null; classinfo = classinfo.getSuperclass()) {
	    FieldInfo fieldinfo = classinfo.findField(string, string_0_);
	    if (fieldinfo != null)
		return fieldinfo;
	    ClassInfo[] classinfos = classinfo.getInterfaces();
	    for (int i = 0; i < classinfos.length; i++) {
		fieldinfo = getFieldInfo(classinfos[i], string, string_0_);
		if (fieldinfo != null)
		    return fieldinfo;
	    }
	}
	return null;
    }
    
    public FieldInfo getFieldInfo() {
	ClassInfo classinfo;
	if (ref.getClazz().charAt(0) == '[')
	    classinfo = ClassInfo.javaLangObject;
	else
	    classinfo = TypeSignature.getClassInfo(ref.getClazz());
	return getFieldInfo(classinfo, ref.getName(), ref.getType());
    }
    
    public boolean needsCast(Type type) {
	if (type instanceof NullType)
	    return true;
	if (!(type instanceof ClassInterfacesType)
	    || !(classType instanceof ClassInterfacesType))
	    return false;
	ClassInfo classinfo = ((ClassInterfacesType) classType).getClassInfo();
	ClassInfo classinfo_1_ = ((ClassInterfacesType) type).getClassInfo();
	FieldInfo fieldinfo;
    while_17_:
	for (fieldinfo = classinfo.findField(ref.getName(), ref.getType());
	     fieldinfo == null;
	     fieldinfo = classinfo.findField(ref.getName(), ref.getType())) {
	    ClassInfo[] classinfos = classinfo.getInterfaces();
	    for (int i = 0; i < classinfos.length; i++) {
		fieldinfo
		    = classinfos[i].findField(ref.getName(), ref.getType());
		if (fieldinfo != null)
		    break while_17_;
	    }
	    classinfo = classinfo.getSuperclass();
	    if (classinfo == null)
		return false;
	}
	if (Modifier.isPrivate(fieldinfo.getModifiers()))
	    return classinfo_1_ != classinfo;
	if ((fieldinfo.getModifiers() & 0x5) == 0) {
	    int i = classinfo.getName().lastIndexOf('.');
	    if (i == -1 || i != classinfo_1_.getName().lastIndexOf('.')
		|| !classinfo_1_.getName()
			.startsWith(classinfo.getName().substring(0, i)))
		return true;
	}
	for (/**/; classinfo != classinfo_1_ && classinfo != null;
	     classinfo_1_ = classinfo_1_.getSuperclass()) {
	    FieldInfo[] fieldinfos = classinfo_1_.getFields();
	    for (int i = 0; i < fieldinfos.length; i++) {
		if (fieldinfos[i].getName().equals(ref.getName()))
		    return true;
	    }
	}
	return false;
    }
    
    public InnerClassInfo getOuterClassInfo(ClassInfo classinfo) {
	if (classinfo != null) {
	    InnerClassInfo[] innerclassinfos = classinfo.getOuterClasses();
	    if (innerclassinfos != null)
		return innerclassinfos[0];
	}
	return null;
    }
    
    public void fillDeclarables(Collection collection) {
	ClassInfo classinfo = getClassInfo();
	InnerClassInfo innerclassinfo = getOuterClassInfo(classinfo);
	ClassAnalyzer classanalyzer
	    = methodAnalyzer.getClassAnalyzer(classinfo);
	if ((Options.options & 0x4) != 0 && innerclassinfo != null
	    && innerclassinfo.outer == null && innerclassinfo.name != null
	    && classanalyzer != null
	    && classanalyzer.getParent() == methodAnalyzer) {
	    classanalyzer.fillDeclarables(collection);
	    collection.add(classanalyzer);
	}
	super.fillDeclarables(collection);
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	boolean bool
	    = !staticFlag && subExpressions[0] instanceof ThisOperator;
	String string = ref.getName();
	if (staticFlag) {
	    if (!classType.equals(Type.tClass(methodAnalyzer.getClazz()))
		|| methodAnalyzer.findLocal(string) != null) {
		tabbedprintwriter.printType(classType);
		tabbedprintwriter.breakOp();
		tabbedprintwriter.print(".");
	    }
	    tabbedprintwriter.print(string);
	} else if (needsCast(subExpressions[0].getType().getCanonic())) {
	    tabbedprintwriter.print("(");
	    tabbedprintwriter.startOp(0, 1);
	    tabbedprintwriter.print("(");
	    tabbedprintwriter.printType(classType);
	    tabbedprintwriter.print(") ");
	    tabbedprintwriter.breakOp();
	    subExpressions[0].dumpExpression(tabbedprintwriter, 700);
	    tabbedprintwriter.endOp();
	    tabbedprintwriter.print(")");
	    tabbedprintwriter.breakOp();
	    tabbedprintwriter.print(".");
	    tabbedprintwriter.print(string);
	} else {
	    if (bool) {
		ThisOperator thisoperator = (ThisOperator) subExpressions[0];
		Scope scope
		    = tabbedprintwriter.getScope(thisoperator.getClassInfo(),
						 1);
		if (scope == null
		    || tabbedprintwriter.conflicts(string, scope, 3)) {
		    thisoperator.dumpExpression(tabbedprintwriter, 950);
		    tabbedprintwriter.breakOp();
		    tabbedprintwriter.print(".");
		} else if (tabbedprintwriter.conflicts(string, scope, 4)
			   || (getField() == null
			       && tabbedprintwriter.conflicts(string, null,
							      13))) {
		    thisoperator.dumpExpression(tabbedprintwriter, 950);
		    tabbedprintwriter.breakOp();
		    tabbedprintwriter.print(".");
		}
	    } else {
		subExpressions[0].dumpExpression(tabbedprintwriter, 950);
		tabbedprintwriter.breakOp();
		tabbedprintwriter.print(".");
	    }
	    tabbedprintwriter.print(string);
	}
    }
}
