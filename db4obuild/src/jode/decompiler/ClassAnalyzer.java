/* ClassAnalyzer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Set;
import java.util.Vector;

import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.ClassInfo;
import jode.bytecode.FieldInfo;
import jode.bytecode.InnerClassInfo;
import jode.bytecode.MethodInfo;
import jode.expr.Expression;
import jode.expr.ThisOperator;
import jode.flow.StructuredBlock;
import jode.flow.TransformConstructors;
import jode.type.MethodType;
import jode.type.Type;
import jode.util.SimpleSet;

public class ClassAnalyzer implements Scope, Declarable, ClassDeclarer
{
    ImportHandler imports;
    ClassInfo clazz;
    ClassDeclarer parent;
    ProgressListener progressListener;
    private static double INITIALIZE_COMPLEXITY = 0.03;
    private static double STEP_COMPLEXITY = 0.03;
    private static int STRICTFP = 2048;
    double methodComplexity = 0.0;
    double innerComplexity = 0.0;
    String name;
    StructuredBlock[] blockInitializers;
    FieldAnalyzer[] fields;
    MethodAnalyzer[] methods;
    ClassAnalyzer[] inners;
    int modifiers;
    TransformConstructors constrAna;
    MethodAnalyzer staticConstructor;
    MethodAnalyzer[] constructors;
    OuterValues outerValues;
    static int serialnr = 0;
    
    public ClassAnalyzer(ClassDeclarer classdeclarer, ClassInfo classinfo,
			 ImportHandler importhandler,
			 Expression[] expressions) {
	classinfo.loadInfo(127);
	parent = classdeclarer;
	clazz = classinfo;
	imports = importhandler;
	if (expressions != null)
	    outerValues = new OuterValues(this, expressions);
	modifiers = classinfo.getModifiers();
	if (classdeclarer != null) {
	    InnerClassInfo[] innerclassinfos = classinfo.getOuterClasses();
	    if (innerclassinfos[0].outer == null
		|| innerclassinfos[0].name == null) {
		if (classdeclarer instanceof ClassAnalyzer)
		    throw new AssertError
			      ("ClassInfo Attributes are inconsistent: "
			       + classinfo.getName());
	    } else if (!(classdeclarer instanceof ClassAnalyzer)
		       || !((ClassAnalyzer) classdeclarer).clazz.getName()
			       .equals(innerclassinfos[0].outer)
		       || innerclassinfos[0].name == null)
		throw new AssertError("ClassInfo Attributes are inconsistent: "
				      + classinfo.getName());
	    name = innerclassinfos[0].name;
	    modifiers = innerclassinfos[0].modifiers;
	} else {
	    name = classinfo.getName();
	    int i = name.lastIndexOf('.');
	    if (i >= 0)
		name = name.substring(i + 1);
	}
    }
    
    public ClassAnalyzer(ClassDeclarer classdeclarer, ClassInfo classinfo,
			 ImportHandler importhandler) {
	this(classdeclarer, classinfo, importhandler, null);
    }
    
    public ClassAnalyzer(ClassInfo classinfo, ImportHandler importhandler) {
	this(null, classinfo, importhandler);
    }
    
    public final boolean isStatic() {
	return Modifier.isStatic(modifiers);
    }
    
    public final boolean isStrictFP() {
	return (modifiers & STRICTFP) != 0;
    }
    
    public FieldAnalyzer getField(int i) {
	return fields[i];
    }
    
    public int getFieldIndex(String string, Type type) {
	for (int i = 0; i < fields.length; i++) {
	    if (fields[i].getName().equals(string)
		&& fields[i].getType().equals(type))
		return i;
	}
	return -1;
    }
    
    public MethodAnalyzer getMethod(String string, MethodType methodtype) {
	for (int i = 0; i < methods.length; i++) {
	    if (methods[i].getName().equals(string)
		&& methods[i].getType().equals(methodtype))
		return methods[i];
	}
	return null;
    }
    
    public int getModifiers() {
	return modifiers;
    }
    
    public ClassDeclarer getParent() {
	return parent;
    }
    
    public void setParent(ClassDeclarer classdeclarer) {
	parent = classdeclarer;
    }
    
    public ClassInfo getClazz() {
	return clazz;
    }
    
    public String getName() {
	return name;
    }
    
    public void setName(String string) {
	name = string;
    }
    
    public OuterValues getOuterValues() {
	return outerValues;
    }
    
    public void addBlockInitializer(int i, StructuredBlock structuredblock) {
	if (blockInitializers[i] == null)
	    blockInitializers[i] = structuredblock;
	else
	    blockInitializers[i].appendBlock(structuredblock);
    }
    
    public void initialize() {
	FieldInfo[] fieldinfos = clazz.getFields();
	MethodInfo[] methodinfos = clazz.getMethods();
	InnerClassInfo[] innerclassinfos = clazz.getInnerClasses();
	if (fieldinfos != null) {
	    if ((Options.options & 0x2) != 0 && innerclassinfos != null) {
		Expression[] expressions = { new ThisOperator(clazz) };
		int i = innerclassinfos.length;
		inners = new ClassAnalyzer[i];
		for (int i_0_ = 0; i_0_ < i; i_0_++) {
		    ClassInfo classinfo
			= ClassInfo.forName(innerclassinfos[i_0_].inner);
		    inners[i_0_]
			= new ClassAnalyzer(this, classinfo, imports,
					    (Modifier.isStatic(innerclassinfos
							       [i_0_]
							       .modifiers)
					     ? null : expressions));
		}
	    } else
		inners = new ClassAnalyzer[0];
	    fields = new FieldAnalyzer[fieldinfos.length];
	    methods = new MethodAnalyzer[methodinfos.length];
	    blockInitializers = new StructuredBlock[fieldinfos.length + 1];
	    for (int i = 0; i < fieldinfos.length; i++)
		fields[i] = new FieldAnalyzer(this, fieldinfos[i], imports);
	    staticConstructor = null;
	    Vector vector = new Vector();
	    for (int i = 0; i < methods.length; i++) {
		methods[i] = new MethodAnalyzer(this, methodinfos[i], imports);
		if (methods[i].isConstructor()) {
		    if (methods[i].isStatic())
			staticConstructor = methods[i];
		    else
			vector.addElement(methods[i]);
		    if (methods[i].isStrictFP())
			modifiers |= STRICTFP;
		}
		methodComplexity += methods[i].getComplexity();
	    }
	    constructors = new MethodAnalyzer[vector.size()];
	    vector.copyInto(constructors);
	    for (int i = 0; i < inners.length; i++) {
		inners[i].initialize();
		innerComplexity += inners[i].getComplexity();
	    }
	}
    }
    
    public double getComplexity() {
	return methodComplexity + innerComplexity;
    }
    
    public void analyze(ProgressListener progresslistener, double d,
			double d_1_) {
	if (GlobalOptions.verboseLevel > 0)
	    GlobalOptions.err.println("Class " + name);
	double d_2_ = d_1_ / methodComplexity;
	if (progresslistener != null)
	    progresslistener.updateProgress(d, name);
	imports.useClass(clazz);
	if (clazz.getSuperclass() != null)
	    imports.useClass(clazz.getSuperclass());
	ClassInfo[] classinfos = clazz.getInterfaces();
	for (int i = 0; i < classinfos.length; i++)
	    imports.useClass(classinfos[i]);
	if (fields != null) {
	    constrAna = null;
	    if (constructors.length > 0) {
		for (int i = 0; i < constructors.length; i++) {
		    if (progresslistener != null) {
			double d_3_ = constructors[i].getComplexity() * d_2_;
			if (d_3_ > STEP_COMPLEXITY)
			    constructors[i].analyze(progresslistener, d, d_3_);
			else {
			    progresslistener.updateProgress(d, name);
			    constructors[i].analyze(null, 0.0, 0.0);
			}
			d += d_3_;
		    } else
			constructors[i].analyze(null, 0.0, 0.0);
		}
		constrAna
		    = new TransformConstructors(this, false, constructors);
		constrAna.removeSynthInitializers();
	    }
	    if (staticConstructor != null) {
		if (progresslistener != null) {
		    double d_4_ = staticConstructor.getComplexity() * d_2_;
		    if (d_4_ > STEP_COMPLEXITY)
			staticConstructor.analyze(progresslistener, d, d_4_);
		    else {
			progresslistener.updateProgress(d, name);
			staticConstructor.analyze(null, 0.0, 0.0);
		    }
		    d += d_4_;
		} else
		    staticConstructor.analyze(null, 0.0, 0.0);
	    }
	    if ((Options.options & 0x80) == 0) {
		for (int i = 0; i < fields.length; i++)
		    fields[i].analyze();
		for (int i = 0; i < methods.length; i++) {
		    if (!methods[i].isConstructor()) {
			if (progresslistener != null) {
			    double d_5_ = methods[i].getComplexity() * d_2_;
			    if (d_5_ > STEP_COMPLEXITY)
				methods[i].analyze(progresslistener, d, d_5_);
			    else {
				progresslistener
				    .updateProgress(d, methods[i].getName());
				methods[i].analyze(null, 0.0, 0.0);
			    }
			    d += d_5_;
			} else
			    methods[i].analyze(null, 0.0, 0.0);
		    }
		}
	    }
	}
    }
    
    public void analyzeInnerClasses(ProgressListener progresslistener,
				    double d, double d_6_) {
	double d_7_ = d_6_ / innerComplexity;
	if ((Options.options & 0x80) == 0) {
	    for (int i = 0; i < inners.length; i++) {
		if (progresslistener != null) {
		    double d_8_ = inners[i].getComplexity() * d_7_;
		    if (d_8_ > STEP_COMPLEXITY) {
			double d_9_ = d_7_ * inners[i].methodComplexity;
			inners[i].analyze(progresslistener, d, d_9_);
			inners[i].analyzeInnerClasses(null, d + d_9_,
						      d_8_ - d_9_);
		    } else {
			progresslistener.updateProgress(d, inners[i].name);
			inners[i].analyze(null, 0.0, 0.0);
			inners[i].analyzeInnerClasses(null, 0.0, 0.0);
		    }
		    d += d_8_;
		} else {
		    inners[i].analyze(null, 0.0, 0.0);
		    inners[i].analyzeInnerClasses(null, 0.0, 0.0);
		}
	    }
	    for (int i = 0; i < methods.length; i++)
		methods[i].analyzeInnerClasses();
	}
    }
    
    public void makeDeclaration(Set set) {
	if (constrAna != null)
	    constrAna.transform();
	if (staticConstructor != null)
	    new TransformConstructors
		(this, true, new MethodAnalyzer[] { staticConstructor })
		.transform();
	if ((Options.options & 0x80) == 0) {
	    for (int i = 0; i < fields.length; i++)
		fields[i].makeDeclaration(set);
	    for (int i = 0; i < inners.length; i++)
		inners[i].makeDeclaration(set);
	    for (int i = 0; i < methods.length; i++)
		methods[i].makeDeclaration(set);
	}
    }
    
    public void dumpDeclaration(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	dumpDeclaration(tabbedprintwriter, null, 0.0, 0.0);
    }
    
    public void dumpDeclaration(TabbedPrintWriter tabbedprintwriter,
				ProgressListener progresslistener, double d,
				double d_10_) throws IOException {
	if (fields != null) {
	    tabbedprintwriter.startOp(1, 0);
	    int i = modifiers & ((0x20 | STRICTFP) ^ 0xffffffff);
	    if (clazz.isInterface())
		i &= ~0x400;
	    if (parent instanceof MethodAnalyzer) {
		i &= ~0x2;
		if (name == null)
		    i &= ~0x10;
	    }
	    String string = Modifier.toString(i);
	    if (string.length() > 0)
		tabbedprintwriter.print(string + " ");
	    if (isStrictFP())
		tabbedprintwriter.print("strictfp ");
	    if (!clazz.isInterface())
		tabbedprintwriter.print("class ");
	    tabbedprintwriter.print(name);
	    ClassInfo classinfo = clazz.getSuperclass();
	    if (classinfo != null && classinfo != ClassInfo.javaLangObject) {
		tabbedprintwriter.breakOp();
		tabbedprintwriter.print(" extends "
					+ tabbedprintwriter
					      .getClassString(classinfo, 1));
	    }
	    ClassInfo[] classinfos = clazz.getInterfaces();
	    if (classinfos.length > 0) {
		tabbedprintwriter.breakOp();
		tabbedprintwriter
		    .print(clazz.isInterface() ? " extends " : " implements ");
		tabbedprintwriter.startOp(0, 1);
		for (int i_11_ = 0; i_11_ < classinfos.length; i_11_++) {
		    if (i_11_ > 0) {
			tabbedprintwriter.print(", ");
			tabbedprintwriter.breakOp();
		    }
		    tabbedprintwriter.print
			(tabbedprintwriter.getClassString(classinfos[i_11_],
							  1));
		}
		tabbedprintwriter.endOp();
	    }
	    tabbedprintwriter.println();
	    tabbedprintwriter.openBraceClass();
	    tabbedprintwriter.tab();
	    dumpBlock(tabbedprintwriter, progresslistener, d, d_10_);
	    tabbedprintwriter.untab();
	    tabbedprintwriter.closeBraceClass();
	}
    }
    
    public void dumpBlock(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	dumpBlock(tabbedprintwriter, null, 0.0, 0.0);
    }
    
    public void dumpBlock(TabbedPrintWriter tabbedprintwriter,
			  ProgressListener progresslistener, double d,
			  double d_12_) throws IOException {
	double d_13_ = d_12_ / getComplexity();
	tabbedprintwriter.pushScope(this);
	boolean bool = false;
	boolean bool_14_ = false;
	SimpleSet simpleset = null;
	if ((Options.options & 0x80) != 0)
	    simpleset = new SimpleSet();
	for (int i = 0; i < fields.length; i++) {
	    if (blockInitializers[i] != null) {
		if (bool_14_)
		    tabbedprintwriter.println();
		tabbedprintwriter.openBrace();
		tabbedprintwriter.tab();
		blockInitializers[i].dumpSource(tabbedprintwriter);
		tabbedprintwriter.untab();
		tabbedprintwriter.closeBrace();
		bool = bool_14_ = true;
	    }
	    if ((Options.options & 0x80) != 0) {
		fields[i].analyze();
		fields[i].makeDeclaration(simpleset);
	    }
	    if (!fields[i].skipWriting()) {
		if (bool)
		    tabbedprintwriter.println();
		fields[i].dumpSource(tabbedprintwriter);
		bool_14_ = true;
	    }
	}
	if (blockInitializers[fields.length] != null) {
	    if (bool_14_)
		tabbedprintwriter.println();
	    tabbedprintwriter.openBrace();
	    tabbedprintwriter.tab();
	    blockInitializers[fields.length].dumpSource(tabbedprintwriter);
	    tabbedprintwriter.untab();
	    tabbedprintwriter.closeBrace();
	    bool_14_ = true;
	}
	for (int i = 0; i < inners.length; i++) {
	    if (bool_14_)
		tabbedprintwriter.println();
	    if ((Options.options & 0x80) != 0) {
		inners[i].analyze(null, 0.0, 0.0);
		inners[i].analyzeInnerClasses(null, 0.0, 0.0);
		inners[i].makeDeclaration(simpleset);
	    }
	    if (progresslistener != null) {
		double d_15_ = inners[i].getComplexity() * d_13_;
		if (d_15_ > STEP_COMPLEXITY)
		    inners[i].dumpSource(tabbedprintwriter, progresslistener,
					 d, d_15_);
		else {
		    progresslistener.updateProgress(d, name);
		    inners[i].dumpSource(tabbedprintwriter);
		}
		d += d_15_;
	    } else
		inners[i].dumpSource(tabbedprintwriter);
	    bool_14_ = true;
	}
	for (int i = 0; i < methods.length; i++) {
	    if ((Options.options & 0x80) != 0) {
		if (!methods[i].isConstructor())
		    methods[i].analyze(null, 0.0, 0.0);
		methods[i].analyzeInnerClasses();
		methods[i].makeDeclaration(simpleset);
	    }
	    if (!methods[i].skipWriting()) {
		if (bool_14_)
		    tabbedprintwriter.println();
		if (progresslistener != null) {
		    double d_16_ = methods[i].getComplexity() * d_13_;
		    progresslistener.updateProgress(d, methods[i].getName());
		    methods[i].dumpSource(tabbedprintwriter);
		    d += d_16_;
		} else
		    methods[i].dumpSource(tabbedprintwriter);
		bool_14_ = true;
	    }
	}
	tabbedprintwriter.popScope();
	ClassInfo classinfo = clazz;
	if (this != null) {
	    /* empty */
	}
	int i = 16;
	if (this != null) {
	    /* empty */
	}
	classinfo.dropInfo(i | 0x80);
    }
    
    public void dumpSource(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	dumpSource(tabbedprintwriter, null, 0.0, 0.0);
    }
    
    public void dumpSource(TabbedPrintWriter tabbedprintwriter,
			   ProgressListener progresslistener, double d,
			   double d_17_) throws IOException {
	dumpDeclaration(tabbedprintwriter, progresslistener, d, d_17_);
	tabbedprintwriter.println();
    }
    
    public void dumpJavaFile(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	dumpJavaFile(tabbedprintwriter, null);
    }
    
    public void dumpJavaFile
	(TabbedPrintWriter tabbedprintwriter,
	 ProgressListener progresslistener)
	throws IOException {
	imports.init(clazz.getName());
	LocalInfo.init();
	initialize();
	double d = 0.05;
	double d_18_
	    = 0.75 * methodComplexity / (methodComplexity + innerComplexity);
	analyze(progresslistener, INITIALIZE_COMPLEXITY, d_18_);
	d += d_18_;
	analyzeInnerClasses(progresslistener, d, 0.8 - d);
	makeDeclaration(new SimpleSet());
	imports.dumpHeader(tabbedprintwriter);
	dumpSource(tabbedprintwriter, progresslistener, 0.8, 0.2);
	if (progresslistener != null)
	    progresslistener.updateProgress(1.0, name);
    }
    
    public boolean isScopeOf(Object object, int i) {
	if (clazz.equals(object) && i == 1)
	    return true;
	return false;
    }
    
    public void makeNameUnique() {
	name = name + "_" + serialnr++ + "_";
    }
    
    public boolean conflicts(String string, int i) {
	return conflicts(clazz, string, i);
    }
    
    private static boolean conflicts(ClassInfo classinfo, String string,
				     int i) {
	for (/**/; classinfo != null; classinfo = classinfo.getSuperclass()) {
	    if (i == 12 || i == 2) {
		MethodInfo[] methodinfos = classinfo.getMethods();
		for (int i_19_ = 0; i_19_ < methodinfos.length; i_19_++) {
		    if (methodinfos[i_19_].getName().equals(string))
			return true;
		}
	    }
	    if (i == 13 || i == 3 || i == 4) {
		FieldInfo[] fieldinfos = classinfo.getFields();
		for (int i_20_ = 0; i_20_ < fieldinfos.length; i_20_++) {
		    if (fieldinfos[i_20_].getName().equals(string))
			return true;
		}
	    }
	    if (i == 1 || i == 4) {
		InnerClassInfo[] innerclassinfos = classinfo.getInnerClasses();
		if (innerclassinfos != null) {
		    for (int i_21_ = 0; i_21_ < innerclassinfos.length;
			 i_21_++) {
			if (innerclassinfos[i_21_].name.equals(string))
			    return true;
		    }
		}
	    }
	    if (i == 13 || i == 12)
		return false;
	    ClassInfo[] classinfos = classinfo.getInterfaces();
	    for (int i_22_ = 0; i_22_ < classinfos.length; i_22_++) {
		if (conflicts(classinfos[i_22_], string, i))
		    return true;
	    }
	}
	return false;
    }
    
    public ClassAnalyzer getClassAnalyzer(ClassInfo classinfo) {
	if (classinfo == getClazz())
	    return this;
	if (parent == null)
	    return null;
	return getParent().getClassAnalyzer(classinfo);
    }
    
    public ClassAnalyzer getInnerClassAnalyzer(String string) {
	int i = inners.length;
	for (int i_23_ = 0; i_23_ < i; i_23_++) {
	    if (inners[i_23_].name.equals(string))
		return inners[i_23_];
	}
	return null;
    }
    
    public void fillDeclarables(Collection collection) {
	for (int i = 0; i < methods.length; i++)
	    methods[i].fillDeclarables(collection);
    }
    
    public void addClassAnalyzer(ClassAnalyzer classanalyzer_24_) {
	if (parent != null)
	    parent.addClassAnalyzer(classanalyzer_24_);
    }
    
    public String toString() {
	return this.getClass().getName() + "[" + getClazz() + "]";
    }
}
