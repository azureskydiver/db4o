/* InvokeOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.ClassInfo;
import jode.bytecode.InnerClassInfo;
import jode.bytecode.MethodInfo;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.decompiler.ClassAnalyzer;
import jode.decompiler.MethodAnalyzer;
import jode.decompiler.Options;
import jode.decompiler.OuterValues;
import jode.decompiler.Scope;
import jode.decompiler.TabbedPrintWriter;
import jode.jvm.Interpreter;
import jode.jvm.InterpreterException;
import jode.jvm.SimpleRuntimeEnvironment;
import jode.jvm.SyntheticAnalyzer;
import jode.type.ClassInterfacesType;
import jode.type.IntegerType;
import jode.type.MethodType;
import jode.type.NullType;
import jode.type.Type;
import jode.util.SimpleMap;

public final class InvokeOperator extends Operator implements MatchableOperator
{
    public static final int VIRTUAL = 0;
    public static final int SPECIAL = 1;
    public static final int STATIC = 2;
    public static final int CONSTRUCTOR = 3;
    public static final int ACCESSSPECIAL = 4;
    MethodAnalyzer methodAnalyzer;
    int methodFlag;
    MethodType methodType;
    String methodName;
    Reference ref;
    int skippedArgs;
    Type classType;
    Type[] hints;
    private static final Hashtable hintTypes = new Hashtable();
    
    class Environment extends SimpleRuntimeEnvironment
    {
	Interpreter interpreter;
	String classSig;
	
	public Environment(String string) {
	    classSig = string.intern();
	}
	
	public Object invokeMethod
	    (Reference reference, boolean bool, Object object,
	     Object[] objects)
	    throws InterpreterException, InvocationTargetException {
	    if (object == null && reference.getClazz().equals(classSig)) {
		String string = reference.getClazz();
		string = string.substring
			     (1, reference.getClazz().length() - 1)
			     .replace('/', '.');
		BytecodeInfo bytecodeinfo
		    = ClassInfo.forName(string).findMethod
			  (reference.getName(), reference.getType())
			  .getBytecode();
		if (bytecodeinfo != null)
		    return interpreter.interpretMethod(bytecodeinfo, null,
						       objects);
		throw new InterpreterException
			  ("Can't interpret static native method: "
			   + reference);
	    }
	    return super.invokeMethod(reference, bool, object, objects);
	}
    }
    
    public InvokeOperator(MethodAnalyzer methodanalyzer, int i,
			  Reference reference) {
	super(Type.tUnknown, 0);
	ref = reference;
	methodType = Type.tMethod(reference.getType());
	methodName = reference.getName();
	classType = Type.tType(reference.getClazz());
	hints = null;
	Map map = (Map) hintTypes.get(methodName + "." + methodType);
	if (map != null) {
	    Iterator iterator = map.entrySet().iterator();
	    while (iterator.hasNext()) {
		Map.Entry entry = (Map.Entry) iterator.next();
		if (classType.isOfType(((Type) entry.getKey()).getSubType())) {
		    hints = (Type[]) entry.getValue();
		    break;
		}
	    }
	}
	if (hints != null && hints[0] != null)
	    type = hints[0];
	else
	    type = methodType.getReturnType();
	methodAnalyzer = methodanalyzer;
	methodFlag = i;
	if (i == 2)
	    methodanalyzer.useType(classType);
	skippedArgs = i == 2 ? 0 : 1;
	this.initOperands(skippedArgs + methodType.getParameterTypes().length);
	checkAnonymousClasses();
    }
    
    public final boolean isStatic() {
	return methodFlag == 2;
    }
    
    public MethodType getMethodType() {
	return methodType;
    }
    
    public String getMethodName() {
	return methodName;
    }
    
    private static MethodInfo getMethodInfo(ClassInfo classinfo, String string,
					    String string_0_) {
	for (/**/; classinfo != null; classinfo = classinfo.getSuperclass()) {
	    MethodInfo methodinfo = classinfo.findMethod(string, string_0_);
	    if (methodinfo != null)
		return methodinfo;
	}
	return null;
    }
    
    public MethodInfo getMethodInfo() {
	ClassInfo classinfo;
	if (ref.getClazz().charAt(0) == '[')
	    classinfo = ClassInfo.javaLangObject;
	else
	    classinfo = TypeSignature.getClassInfo(ref.getClazz());
	return getMethodInfo(classinfo, ref.getName(), ref.getType());
    }
    
    public Type getClassType() {
	return classType;
    }
    
    public int getPriority() {
	return 950;
    }
    
    public void checkAnonymousClasses() {
	if (methodFlag == 3 && (Options.options & 0x4) != 0) {
	    InnerClassInfo innerclassinfo = getOuterClassInfo(getClassInfo());
	    if (innerclassinfo != null && (innerclassinfo.outer == null
					   || innerclassinfo.name == null))
		methodAnalyzer.addAnonymousConstructor(this);
	}
    }
    
    public void updateSubTypes() {
	int i = 0;
	if (!isStatic())
	    subExpressions[i++].setType(Type.tSubType(getClassType()));
	Type[] types = methodType.getParameterTypes();
	for (int i_1_ = 0; i_1_ < types.length; i_1_++) {
	    Type type = (hints != null && hints[i_1_ + 1] != null
			 ? hints[i_1_ + 1] : types[i_1_]);
	    subExpressions[i++].setType(Type.tSubType(type));
	}
    }
    
    public void updateType() {
	/* empty */
    }
    
    public void makeNonVoid() {
	if (type != Type.tVoid)
	    throw new AssertError("already non void");
	ClassInfo classinfo = getClassInfo();
	InnerClassInfo innerclassinfo = getOuterClassInfo(classinfo);
	if (innerclassinfo != null && innerclassinfo.name == null) {
	    if (classinfo.getInterfaces().length > 0)
		type = Type.tClass(classinfo.getInterfaces()[0]);
	    else
		type = Type.tClass(classinfo.getSuperclass());
	} else
	    type = subExpressions[0].getType();
    }
    
    public boolean isConstructor() {
	return methodFlag == 3;
    }
    
    public ClassInfo getClassInfo() {
	if (classType instanceof ClassInterfacesType)
	    return ((ClassInterfacesType) classType).getClassInfo();
	return null;
    }
    
    public boolean isThis() {
	return getClassInfo() == methodAnalyzer.getClazz();
    }
    
    public InnerClassInfo getOuterClassInfo(ClassInfo classinfo) {
	if (classinfo != null) {
	    InnerClassInfo[] innerclassinfos = classinfo.getOuterClasses();
	    if (innerclassinfos != null)
		return innerclassinfos[0];
	}
	return null;
    }
    
    public ClassAnalyzer getClassAnalyzer() {
	if ((Options.options & 0x6) == 0)
	    return null;
	ClassInfo classinfo = getClassInfo();
	if (classinfo == null)
	    return null;
	int i = 0;
	InnerClassInfo[] innerclassinfos = classinfo.getOuterClasses();
	if ((Options.options & 0x2) != 0 && innerclassinfos != null) {
	    i = innerclassinfos.length;
	    if (innerclassinfos[i - 1].outer == null
		|| innerclassinfos[i - 1].name == null)
		i--;
	    if (i > 0)
		classinfo = ClassInfo.forName(innerclassinfos[i - 1].outer);
	}
	ClassAnalyzer classanalyzer
	    = methodAnalyzer.getClassAnalyzer(classinfo);
	if (classanalyzer == null) {
	    classanalyzer = methodAnalyzer.getClassAnalyzer();
	    while (classinfo != classanalyzer.getClazz()) {
		if (classanalyzer.getParent() == null)
		    return null;
		if (classanalyzer.getParent() instanceof MethodAnalyzer
		    && (Options.options & 0x4) != 0)
		    classanalyzer
			= ((MethodAnalyzer) classanalyzer.getParent())
			      .getClassAnalyzer();
		else if (classanalyzer.getParent() instanceof ClassAnalyzer
			 && (Options.options & 0x2) != 0)
		    classanalyzer = (ClassAnalyzer) classanalyzer.getParent();
		else
		    throw new AssertError("Unknown parent: " + classanalyzer
					  + ": " + classanalyzer.getParent());
	    }
	}
	while (i > 0) {
	    i--;
	    classanalyzer
		= classanalyzer.getInnerClassAnalyzer(innerclassinfos[i].name);
	    if (classanalyzer == null)
		return null;
	}
	return classanalyzer;
    }
    
    public boolean isOuter() {
    while_14_:
	do {
	    if (classType instanceof ClassInterfacesType) {
		ClassInfo classinfo
		    = ((ClassInterfacesType) classType).getClassInfo();
		ClassAnalyzer classanalyzer
		    = methodAnalyzer.getClassAnalyzer();
		for (;;) {
		    if (classinfo == classanalyzer.getClazz())
			return true;
		    if (classanalyzer.getParent() == null)
			break while_14_;
		    if (classanalyzer.getParent() instanceof MethodAnalyzer
			&& (Options.options & 0x4) != 0)
			classanalyzer
			    = ((MethodAnalyzer) classanalyzer.getParent())
				  .getClassAnalyzer();
		    else {
			if (!(classanalyzer.getParent()
			      instanceof ClassAnalyzer)
			    || (Options.options & 0x2) == 0)
			    break;
			classanalyzer
			    = (ClassAnalyzer) classanalyzer.getParent();
		    }
		}
		throw new AssertError("Unknown parent: " + classanalyzer + ": "
				      + classanalyzer.getParent());
	    }
	} while (false);
	return false;
    }
    
    public MethodAnalyzer getMethodAnalyzer() {
	ClassAnalyzer classanalyzer = getClassAnalyzer();
	if (classanalyzer == null)
	    return null;
	return classanalyzer.getMethod(methodName, methodType);
    }
    
    public boolean isSuperOrThis() {
	ClassInfo classinfo = getClassInfo();
	if (classinfo != null)
	    return classinfo.superClassOf(methodAnalyzer.getClazz());
	return false;
    }
    
    public boolean isConstant() {
	if ((Options.options & 0x4) == 0)
	    return super.isConstant();
	ClassInfo classinfo = getClassInfo();
	InnerClassInfo innerclassinfo = getOuterClassInfo(classinfo);
	ClassAnalyzer classanalyzer
	    = methodAnalyzer.getClassAnalyzer(classinfo);
	if (classanalyzer != null && innerclassinfo != null
	    && innerclassinfo.outer == null && innerclassinfo.name != null
	    && classanalyzer.getParent() == methodAnalyzer)
	    return false;
	return super.isConstant();
    }
    
    public boolean matches(Operator operator) {
	return (operator instanceof InvokeOperator
		|| operator instanceof GetFieldOperator);
    }
    
    public boolean isGetClass() {
	MethodAnalyzer methodanalyzer = getMethodAnalyzer();
	if (methodanalyzer == null)
	    return false;
	SyntheticAnalyzer syntheticanalyzer
	    = getMethodAnalyzer().getSynthetic();
	return syntheticanalyzer != null && syntheticanalyzer.getKind() == 1;
    }
    
    public ConstOperator deobfuscateString(ConstOperator constoperator) {
	ClassAnalyzer classanalyzer = methodAnalyzer.getClassAnalyzer();
	MethodAnalyzer methodanalyzer
	    = classanalyzer.getMethod(methodName, methodType);
	if (methodanalyzer == null)
	    return null;
	Environment environment
	    = new Environment("L" + methodAnalyzer.getClazz().getName()
					.replace('.', '/') + ";");
	Interpreter interpreter = new Interpreter(environment);
	environment.interpreter = interpreter;
	String string;
	try {
	    string = (String) (interpreter.interpretMethod
			       (methodanalyzer.getBytecodeInfo(), null,
				new Object[] { constoperator.getValue() }));
	} catch (InterpreterException interpreterexception) {
	    if ((GlobalOptions.debuggingFlags & 0x400) != 0) {
		GlobalOptions.err
		    .println("Warning: Can't interpret method " + methodName);
		interpreterexception.printStackTrace(GlobalOptions.err);
	    }
	    return null;
	} catch (InvocationTargetException invocationtargetexception) {
	    if ((GlobalOptions.debuggingFlags & 0x400) != 0) {
		GlobalOptions.err.println
		    ("Warning: Interpreted method throws an uncaught exception: ");
		invocationtargetexception.getTargetException()
		    .printStackTrace(GlobalOptions.err);
	    }
	    return null;
	}
	return new ConstOperator(string);
    }
    
    public Expression simplifyStringBuffer() {
	if (getClassType().equals(Type.tStringBuffer)) {
	    if (isConstructor() && subExpressions[0] instanceof NewOperator) {
		if (methodType.getParameterTypes().length == 0)
		    return Expression.EMPTYSTRING;
		if (methodType.getParameterTypes().length == 1
		    && methodType.getParameterTypes()[0].equals(Type.tString))
		    return subExpressions[1].simplifyString();
	    }
	    if (!isStatic() && getMethodName().equals("append")
		&& getMethodType().getParameterTypes().length == 1) {
		Expression expression
		    = subExpressions[0].simplifyStringBuffer();
		if (expression == null)
		    return null;
		subExpressions[1] = subExpressions[1].simplifyString();
		if (expression == Expression.EMPTYSTRING
		    && subExpressions[1].getType().isOfType(Type.tString))
		    return subExpressions[1];
		if (expression instanceof StringAddOperator
		    && (((Operator) expression).getSubExpressions()[0]
			== Expression.EMPTYSTRING))
		    expression
			= ((Operator) expression).getSubExpressions()[1];
		Expression expression_2_ = subExpressions[1];
		Type[] types = { Type.tStringBuffer,
				 expression_2_.getType().getCanonic() };
		if (needsCast(1, types)) {
		    Type type = methodType.getParameterTypes()[0];
		    ConvertOperator convertoperator
			= new ConvertOperator(type, type);
		    convertoperator.addOperand(expression_2_);
		    expression_2_ = convertoperator;
		}
		StringAddOperator stringaddoperator = new StringAddOperator();
		stringaddoperator.addOperand(expression_2_);
		stringaddoperator.addOperand(expression);
		return stringaddoperator;
	    }
	}
	return null;
    }
    
    public Expression simplifyString() {
	if (getMethodName().equals("toString") && !isStatic()
	    && getClassType().equals(Type.tStringBuffer)
	    && subExpressions.length == 1) {
	    Expression expression = subExpressions[0].simplifyStringBuffer();
	    if (expression != null)
		return expression;
	} else if (getMethodName().equals("valueOf") && isStatic()
		   && getClassType().equals(Type.tString)
		   && subExpressions.length == 1) {
	    if (subExpressions[0].getType().isOfType(Type.tString))
		return subExpressions[0];
	    StringAddOperator stringaddoperator = new StringAddOperator();
	    stringaddoperator.addOperand(subExpressions[0]);
	    stringaddoperator.addOperand(Expression.EMPTYSTRING);
	} else if (getMethodName().equals("concat") && !isStatic()
		   && getClassType().equals(Type.tString)) {
	    StringAddOperator stringaddoperator = new StringAddOperator();
	    Expression expression = subExpressions[1].simplify();
	    if (expression instanceof StringAddOperator) {
		Operator operator = (Operator) expression;
		if (operator.subExpressions != null
		    && operator.subExpressions[0] == Expression.EMPTYSTRING)
		    expression = operator.subExpressions[1];
	    }
	    stringaddoperator.addOperand(expression);
	    stringaddoperator.addOperand(subExpressions[0].simplify());
	} else if ((Options.options & 0x20) != 0 && isThis() && isStatic()
		   && methodType.getParameterTypes().length == 1
		   && methodType.getParameterTypes()[0].equals(Type.tString)
		   && methodType.getReturnType().equals(Type.tString)) {
	    Expression expression = subExpressions[0].simplifyString();
	    if (expression instanceof ConstOperator) {
		ConstOperator constoperator
		    = deobfuscateString((ConstOperator) expression);
		if (constoperator != null)
		    return constoperator;
	    }
	}
	return this;
    }
    
    public Expression simplifyAccess() {
	if (getMethodAnalyzer() != null) {
	    SyntheticAnalyzer syntheticanalyzer
		= getMethodAnalyzer().getSynthetic();
	    if (syntheticanalyzer != null) {
		int i = syntheticanalyzer.getUnifyParam();
		Expression expression = null;
		switch (syntheticanalyzer.getKind()) {
		case 2:
		    expression = new GetFieldOperator(methodAnalyzer, false,
						      syntheticanalyzer
							  .getReference());
		    break;
		case 5:
		    expression = new GetFieldOperator(methodAnalyzer, true,
						      syntheticanalyzer
							  .getReference());
		    break;
		case 3:
		case 9:
		    expression = (new StoreInstruction
				  (new PutFieldOperator(methodAnalyzer, false,
							syntheticanalyzer
							    .getReference())));
		    if (syntheticanalyzer.getKind() == 9)
			((StoreInstruction) expression).makeNonVoid();
		    break;
		case 6:
		case 10:
		    expression = (new StoreInstruction
				  (new PutFieldOperator(methodAnalyzer, true,
							syntheticanalyzer
							    .getReference())));
		    if (syntheticanalyzer.getKind() == 10)
			((StoreInstruction) expression).makeNonVoid();
		    break;
		case 4:
		    expression
			= new InvokeOperator(methodAnalyzer, 4,
					     syntheticanalyzer.getReference());
		    break;
		case 7:
		    expression
			= new InvokeOperator(methodAnalyzer, 2,
					     syntheticanalyzer.getReference());
		    break;
		case 8:
		    if (subExpressions[i] instanceof ConstOperator
			&& (((ConstOperator) subExpressions[i]).getValue()
			    == null))
			expression = new InvokeOperator(methodAnalyzer, 3,
							syntheticanalyzer
							    .getReference());
		    break;
		}
		if (expression != null) {
		    if (subExpressions != null) {
			int i_3_ = subExpressions.length;
			while (i_3_-- > 0) {
			    if (i_3_ != i
				|| syntheticanalyzer.getKind() != 8) {
				expression
				    = expression
					  .addOperand(subExpressions[i_3_]);
				if (subExpressions[i_3_].getFreeOperandCount()
				    > 0)
				    break;
			    }
			}
		    }
		    return expression;
		}
	    }
	}
	return null;
    }
    
    public boolean needsCast(int i, Type[] types) {
	Type type;
	if (methodFlag == 2)
	    type = classType;
	else {
	    if (i == 0) {
		if (types[0] instanceof NullType)
		    return true;
		if (!(types[0] instanceof ClassInterfacesType)
		    || !(classType instanceof ClassInterfacesType))
		    return false;
		ClassInfo classinfo
		    = ((ClassInterfacesType) classType).getClassInfo();
		ClassInfo classinfo_4_
		    = ((ClassInterfacesType) types[0]).getClassInfo();
		MethodInfo methodinfo = getMethodInfo();
		if (methodinfo == null)
		    return false;
		if (Modifier.isPrivate(methodinfo.getModifiers()))
		    return classinfo_4_ != classinfo;
		if ((methodinfo.getModifiers() & 0x5) == 0) {
		    int i_5_ = classinfo.getName().lastIndexOf('.');
		    if (i_5_ != classinfo_4_.getName().lastIndexOf('.')
			|| !(classinfo_4_.getName().startsWith
			     (classinfo.getName().substring(0, i_5_ + 1))))
			return true;
		}
		return false;
	    }
	    type = types[0];
	}
	if (!(type instanceof ClassInterfacesType))
	    return false;
	ClassInfo classinfo = ((ClassInterfacesType) type).getClassInfo();
	int i_6_ = skippedArgs;
	Type[] types_7_ = methodType.getParameterTypes();
	if (types_7_[i - i_6_].equals(types[i]))
	    return false;
	for (/**/; classinfo != null; classinfo = classinfo.getSuperclass()) {
	    MethodInfo[] methodinfos = classinfo.getMethods();
	while_16_:
	    for (int i_8_ = 0; i_8_ < methodinfos.length; i_8_++) {
		if (methodinfos[i_8_].getName().equals(methodName)) {
		    Type[] types_9_ = Type.tMethod
					  (methodinfos[i_8_].getType())
					  .getParameterTypes();
		    if (types_9_.length == types_7_.length
			&& !types_7_[i - i_6_]
				.isOfType(Type.tSubType(types_9_[i - i_6_]))) {
			for (int i_10_ = i_6_; i_10_ < types.length; i_10_++) {
			    if (!types[i_10_].isOfType
				 (Type.tSubType(types_9_[i_10_ - i_6_])))
				continue while_16_;
			}
			return true;
		    }
		}
	    }
	}
	return false;
    }
    
    public Expression simplify() {
	Expression expression = simplifyAccess();
	if (expression != null)
	    return expression.simplify();
	expression = simplifyString();
	if (expression != this)
	    return expression.simplify();
	return super.simplify();
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
	if (!isConstructor() || isStatic())
	    super.fillDeclarables(collection);
	else {
	    int i = 1;
	    int i_11_ = subExpressions.length;
	    boolean bool = false;
	    boolean bool_12_ = false;
	    if ((Options.options & 0x4) != 0 && classanalyzer != null
		&& innerclassinfo != null
		&& (innerclassinfo.outer == null
		    || innerclassinfo.name == null)) {
		OuterValues outervalues = classanalyzer.getOuterValues();
		i += outervalues.getCount();
		bool = outervalues.isJikesAnonymousInner();
		bool_12_ = outervalues.isImplicitOuterClass();
		for (int i_13_ = 1; i_13_ < i; i_13_++) {
		    Expression expression = subExpressions[i_13_];
		    if (expression instanceof CheckNullOperator) {
			CheckNullOperator checknulloperator
			    = (CheckNullOperator) expression;
			expression = checknulloperator.subExpressions[0];
		    }
		    expression.fillDeclarables(collection);
		}
		if (innerclassinfo.name == null) {
		    ClassInfo classinfo_14_ = classinfo.getSuperclass();
		    ClassInfo[] classinfos = classinfo.getInterfaces();
		    if (classinfos.length == 1
			&& (classinfo_14_ == null
			    || classinfo_14_ == ClassInfo.javaLangObject))
			classinfo = classinfos[0];
		    else
			classinfo = (classinfo_14_ != null ? classinfo_14_
				     : ClassInfo.javaLangObject);
		    innerclassinfo = getOuterClassInfo(classinfo);
		}
	    }
	    if ((Options.options & 0x2) != 0 && innerclassinfo != null
		&& innerclassinfo.outer != null && innerclassinfo.name != null
		&& !Modifier.isStatic(innerclassinfo.modifiers) && !bool_12_
		&& i < i_11_) {
		Expression expression
		    = bool ? subExpressions[--i_11_] : subExpressions[i++];
		if (expression instanceof CheckNullOperator) {
		    CheckNullOperator checknulloperator
			= (CheckNullOperator) expression;
		    expression = checknulloperator.subExpressions[0];
		}
		expression.fillDeclarables(collection);
	    }
	    for (int i_15_ = i; i_15_ < i_11_; i_15_++)
		subExpressions[i_15_].fillDeclarables(collection);
	}
    }
    
    public void makeDeclaration(Set set) {
	super.makeDeclaration(set);
	if (isConstructor() && !isStatic() && (Options.options & 0x4) != 0) {
	    ClassInfo classinfo = getClassInfo();
	    InnerClassInfo innerclassinfo = getOuterClassInfo(classinfo);
	    ClassAnalyzer classanalyzer
		= methodAnalyzer.getClassAnalyzer(classinfo);
	    if (classanalyzer != null && innerclassinfo != null
		&& innerclassinfo.name == null)
		classanalyzer.makeDeclaration(set);
	}
    }
    
    public int getBreakPenalty() {
	return 5;
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	int i = 1;
	int i_16_ = subExpressions.length;
	boolean bool = false;
	ClassInfo classinfo = getClassInfo();
	ClassAnalyzer classanalyzer = null;
	Type[] types = new Type[subExpressions.length];
	for (int i_17_ = 0; i_17_ < subExpressions.length; i_17_++)
	    types[i_17_] = subExpressions[i_17_].getType().getCanonic();
	tabbedprintwriter.startOp(1, 0);
	switch (methodFlag) {
	case 3: {
	    boolean bool_18_ = false;
	    boolean bool_19_ = false;
	    boolean bool_20_ = false;
	    InnerClassInfo innerclassinfo = getOuterClassInfo(classinfo);
	    if (innerclassinfo != null && innerclassinfo.name == null
		&& (Options.options & 0x4) != 0)
		bool = true;
	    classanalyzer = methodAnalyzer.getClassAnalyzer(classinfo);
	    if (((Options.options ^ 0xffffffff) & 0x204) == 0
		&& classanalyzer != null && innerclassinfo != null
		&& (innerclassinfo.outer == null
		    || innerclassinfo.name == null)) {
		OuterValues outervalues = classanalyzer.getOuterValues();
		i += outervalues.getCount();
		bool_19_ = outervalues.isJikesAnonymousInner();
		bool_20_ = outervalues.isImplicitOuterClass();
		if (innerclassinfo.name == null) {
		    ClassInfo classinfo_21_ = classinfo.getSuperclass();
		    ClassInfo[] classinfos = classinfo.getInterfaces();
		    if (classinfos.length == 1
			&& (classinfo_21_ == null
			    || classinfo_21_ == ClassInfo.javaLangObject))
			classinfo = classinfos[0];
		    else {
			if (classinfos.length > 0)
			    tabbedprintwriter
				.print("too many supers in ANONYMOUS ");
			classinfo = (classinfo_21_ != null ? classinfo_21_
				     : ClassInfo.javaLangObject);
		    }
		    innerclassinfo = getOuterClassInfo(classinfo);
		    if (bool_19_ && innerclassinfo != null
			&& innerclassinfo.outer == null
			&& innerclassinfo.name != null) {
			Expression expression = subExpressions[--i_16_];
			if (expression instanceof CheckNullOperator) {
			    CheckNullOperator checknulloperator
				= (CheckNullOperator) expression;
			    expression = checknulloperator.subExpressions[0];
			}
			if (!(expression instanceof ThisOperator)
			    || (((ThisOperator) expression).getClassInfo()
				!= methodAnalyzer.getClazz()))
			    tabbedprintwriter.print("ILLEGAL ANON CONSTR");
		    }
		}
	    }
	    if (innerclassinfo != null && innerclassinfo.outer != null
		&& innerclassinfo.name != null
		&& !Modifier.isStatic(innerclassinfo.modifiers)
		&& ((Options.options ^ 0xffffffff) & 0x202) == 0
		&& !bool_20_) {
		if (i < i_16_) {
		    Expression expression = (bool_19_ ? subExpressions[--i_16_]
					     : subExpressions[i++]);
		    if (expression instanceof CheckNullOperator) {
			CheckNullOperator checknulloperator
			    = (CheckNullOperator) expression;
			expression = checknulloperator.subExpressions[0];
		    }
		    if (expression instanceof ThisOperator) {
			Scope scope
			    = tabbedprintwriter.getScope(((ThisOperator)
							  expression)
							     .getClassInfo(),
							 1);
			if (tabbedprintwriter.conflicts(innerclassinfo.name,
							scope, 1)) {
			    bool_18_ = true;
			    expression.dumpExpression(tabbedprintwriter, 950);
			    tabbedprintwriter.breakOp();
			    tabbedprintwriter.print(".");
			}
		    } else {
			bool_18_ = true;
			if (expression.getType().getCanonic()
			    instanceof NullType) {
			    tabbedprintwriter.print("(");
			    tabbedprintwriter.startOp(0, 1);
			    tabbedprintwriter.print("(");
			    tabbedprintwriter.printType
				(Type.tClass(ClassInfo.forName(innerclassinfo
							       .outer)));
			    tabbedprintwriter.print(") ");
			    tabbedprintwriter.breakOp();
			    expression.dumpExpression(tabbedprintwriter, 700);
			    tabbedprintwriter.endOp();
			    tabbedprintwriter.print(")");
			} else
			    expression.dumpExpression(tabbedprintwriter, 950);
			tabbedprintwriter.breakOp();
			tabbedprintwriter.print(".");
		    }
		} else
		    tabbedprintwriter.print("MISSING OUTEREXPR ");
	    }
	    if (subExpressions[0] instanceof NewOperator
		&& types[0].equals(classType)) {
		tabbedprintwriter.print("new ");
		if (bool_18_)
		    tabbedprintwriter.print(innerclassinfo.name);
		else
		    tabbedprintwriter.printType(Type.tClass(classinfo));
	    } else if (subExpressions[0] instanceof ThisOperator
		       && (((ThisOperator) subExpressions[0]).getClassInfo()
			   == methodAnalyzer.getClazz())) {
		if (isThis())
		    tabbedprintwriter.print("this");
		else
		    tabbedprintwriter.print("super");
	    } else {
		tabbedprintwriter.print("(");
		tabbedprintwriter.startOp(0, 0);
		tabbedprintwriter.print("(UNCONSTRUCTED)");
		tabbedprintwriter.breakOp();
		subExpressions[0].dumpExpression(tabbedprintwriter, 700);
		tabbedprintwriter.endOp();
		tabbedprintwriter.print(")");
		tabbedprintwriter.breakOp();
		tabbedprintwriter.print(".");
		tabbedprintwriter.printType(Type.tClass(classinfo));
	    }
	    break;
	}
	case 1:
	    if (isSuperOrThis() && subExpressions[0] instanceof ThisOperator
		&& (((ThisOperator) subExpressions[0]).getClassInfo()
		    == methodAnalyzer.getClazz())) {
		if (!isThis()) {
		    tabbedprintwriter.print("super");
		    ClassInfo classinfo_22_ = getClassInfo().getSuperclass();
		    types[0] = (classinfo_22_ == null ? Type.tObject
				: Type.tClass(classinfo_22_));
		    tabbedprintwriter.breakOp();
		    tabbedprintwriter.print(".");
		}
	    } else if (isThis()) {
		if (needsCast(0, types)) {
		    tabbedprintwriter.print("(");
		    tabbedprintwriter.startOp(0, 1);
		    tabbedprintwriter.print("(");
		    tabbedprintwriter.printType(classType);
		    tabbedprintwriter.print(") ");
		    tabbedprintwriter.breakOp();
		    subExpressions[0].dumpExpression(tabbedprintwriter, 700);
		    tabbedprintwriter.endOp();
		    tabbedprintwriter.print(")");
		    types[0] = classType;
		} else
		    subExpressions[0].dumpExpression(tabbedprintwriter, 950);
		tabbedprintwriter.breakOp();
		tabbedprintwriter.print(".");
	    } else {
		tabbedprintwriter.print("(");
		tabbedprintwriter.startOp(0, 0);
		tabbedprintwriter.print("(NON VIRTUAL ");
		tabbedprintwriter.printType(classType);
		tabbedprintwriter.print(") ");
		tabbedprintwriter.breakOp();
		subExpressions[0].dumpExpression(tabbedprintwriter, 700);
		tabbedprintwriter.endOp();
		tabbedprintwriter.print(")");
		tabbedprintwriter.breakOp();
		tabbedprintwriter.print(".");
	    }
	    tabbedprintwriter.print(methodName);
	    break;
	case 4:
	    if (types[0].equals(classType))
		subExpressions[0].dumpExpression(tabbedprintwriter, 950);
	    else {
		tabbedprintwriter.print("(");
		tabbedprintwriter.startOp(0, 0);
		tabbedprintwriter.print("(");
		tabbedprintwriter.printType(classType);
		tabbedprintwriter.print(") ");
		tabbedprintwriter.breakOp();
		types[0] = classType;
		subExpressions[0].dumpExpression(tabbedprintwriter, 700);
		tabbedprintwriter.endOp();
		tabbedprintwriter.print(")");
	    }
	    tabbedprintwriter.breakOp();
	    tabbedprintwriter.print(".");
	    tabbedprintwriter.print(methodName);
	    break;
	case 2: {
	    i = 0;
	    Scope scope = tabbedprintwriter.getScope(getClassInfo(), 1);
	    if (scope == null
		|| tabbedprintwriter.conflicts(methodName, scope, 2)) {
		tabbedprintwriter.printType(classType);
		tabbedprintwriter.breakOp();
		tabbedprintwriter.print(".");
	    }
	    tabbedprintwriter.print(methodName);
	    break;
	}
	case 0:
	    if (subExpressions[0] instanceof ThisOperator) {
		ThisOperator thisoperator = (ThisOperator) subExpressions[0];
		Scope scope
		    = tabbedprintwriter.getScope(thisoperator.getClassInfo(),
						 1);
		if (tabbedprintwriter.conflicts(methodName, scope, 2)
		    || (getMethodAnalyzer() == null
			&& (!isThis()
			    || tabbedprintwriter.conflicts(methodName, null,
							   12)))) {
		    thisoperator.dumpExpression(tabbedprintwriter, 950);
		    tabbedprintwriter.breakOp();
		    tabbedprintwriter.print(".");
		}
	    } else {
		if (needsCast(0, types)) {
		    tabbedprintwriter.print("(");
		    tabbedprintwriter.startOp(0, 1);
		    tabbedprintwriter.print("(");
		    tabbedprintwriter.printType(classType);
		    tabbedprintwriter.print(") ");
		    tabbedprintwriter.breakOp();
		    subExpressions[0].dumpExpression(tabbedprintwriter, 700);
		    tabbedprintwriter.endOp();
		    tabbedprintwriter.print(")");
		    types[0] = classType;
		} else
		    subExpressions[0].dumpExpression(tabbedprintwriter, 950);
		tabbedprintwriter.breakOp();
		tabbedprintwriter.print(".");
	    }
	    tabbedprintwriter.print(methodName);
	    break;
	}
	tabbedprintwriter.endOp();
	tabbedprintwriter.breakOp();
	if ((Options.outputStyle & 0x40) != 0)
	    tabbedprintwriter.print(" ");
	tabbedprintwriter.print("(");
	tabbedprintwriter.startOp(0, 0);
	boolean bool_23_ = true;
	int i_24_ = skippedArgs;
	while (i < i_16_) {
	    if (!bool_23_) {
		tabbedprintwriter.print(", ");
		tabbedprintwriter.breakOp();
	    } else
		bool_23_ = false;
	    int i_25_ = 0;
	    if (needsCast(i, types)) {
		Type type = methodType.getParameterTypes()[i - i_24_];
		tabbedprintwriter.startOp(2, 1);
		tabbedprintwriter.print("(");
		tabbedprintwriter.printType(type);
		tabbedprintwriter.print(") ");
		tabbedprintwriter.breakOp();
		types[i] = type;
		i_25_ = 700;
	    }
	    subExpressions[i++].dumpExpression(tabbedprintwriter, i_25_);
	    if (i_25_ == 700)
		tabbedprintwriter.endOp();
	}
	tabbedprintwriter.endOp();
	tabbedprintwriter.print(")");
	if (bool) {
	    Object object = tabbedprintwriter.saveOps();
	    tabbedprintwriter.openBraceClass();
	    tabbedprintwriter.tab();
	    classanalyzer.dumpBlock(tabbedprintwriter);
	    tabbedprintwriter.untab();
	    tabbedprintwriter.closeBraceClass();
	    tabbedprintwriter.restoreOps(object);
	}
    }
    
    public boolean opEquals(Operator operator) {
	if (operator instanceof InvokeOperator) {
	    InvokeOperator invokeoperator_26_ = (InvokeOperator) operator;
	    return (classType.equals(invokeoperator_26_.classType)
		    && methodName.equals(invokeoperator_26_.methodName)
		    && methodType.equals(invokeoperator_26_.methodType)
		    && methodFlag == invokeoperator_26_.methodFlag);
	}
	return false;
    }
    
    static {
	IntegerType integertype = new IntegerType(2, 4);
	Type[] types = { integertype };
	Type[] types_27_ = { null, integertype };
	Type[] types_28_ = { null, integertype, null };
	SimpleMap simplemap
	    = (new SimpleMap
	       (Collections.singleton(new SimpleMap.SimpleEntry(Type.tString,
								types_27_))));
	SimpleMap simplemap_29_
	    = (new SimpleMap
	       (Collections.singleton(new SimpleMap.SimpleEntry(Type.tString,
								types_28_))));
	hintTypes.put("indexOf.(I)I", simplemap);
	hintTypes.put("lastIndexOf.(I)I", simplemap);
	hintTypes.put("indexOf.(II)I", simplemap_29_);
	hintTypes.put("lastIndexOf.(II)I", simplemap_29_);
	hintTypes.put("write.(I)V",
		      (new SimpleMap
		       (Collections.singleton(new SimpleMap.SimpleEntry
					      (Type.tClass("java.io.Writer"),
					       types_27_)))));
	hintTypes.put("read.()I",
		      new SimpleMap(Collections.singleton
				    (new SimpleMap.SimpleEntry
				     (Type.tClass("java.io.Reader"), types))));
	hintTypes.put("unread.(I)V",
		      new SimpleMap(Collections.singleton
				    (new SimpleMap.SimpleEntry
				     (Type.tClass("java.io.PushbackReader"),
				      types_27_))));
    }
}
