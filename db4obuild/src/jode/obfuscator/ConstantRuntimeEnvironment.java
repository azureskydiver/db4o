/* ConstantRuntimeEnvironment - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import jode.bytecode.BytecodeInfo;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.jvm.Interpreter;
import jode.jvm.InterpreterException;
import jode.jvm.SimpleRuntimeEnvironment;

public class ConstantRuntimeEnvironment extends SimpleRuntimeEnvironment
{
    static Set whiteList = new HashSet();
    private Interpreter interpreter = new Interpreter(this);
    private Identifier currentFieldListener;
    
    static void addWhite(Reference reference) {
	whiteList.add(reference);
    }
    
    public static boolean isWhite(Reference reference) {
	return whiteList.contains(reference);
    }
    
    public static boolean isWhite(String string) {
	return string.length() == 1 || whiteList.contains(string);
    }
    
    public void setFieldListener(Identifier identifier) {
	currentFieldListener = identifier;
    }
    
    public static Object getDefaultValue(String string) {
	switch (string.charAt(0)) {
	case 'B':
	case 'C':
	case 'I':
	case 'S':
	case 'Z':
	    return new Integer(0);
	case 'J':
	    return new Long(0L);
	case 'D':
	    return new Double(0.0);
	case 'F':
	    return new Float(0.0F);
	default:
	    return null;
	}
    }
    
    public Object getField(Reference reference, Object object)
	throws InterpreterException {
	if (isWhite(reference))
	    return super.getField(reference, object);
	FieldIdentifier fieldidentifier
	    = (FieldIdentifier) Main.getClassBundle().getIdentifier(reference);
	if (fieldidentifier != null && !fieldidentifier.isNotConstant()) {
	    Object object_0_ = fieldidentifier.getConstant();
	    if (currentFieldListener != null)
		fieldidentifier.addFieldListener(currentFieldListener);
	    if (object_0_ == null)
		object_0_ = getDefaultValue(reference.getType());
	    return object_0_;
	}
	throw new InterpreterException("Field " + reference + " not constant");
    }
    
    public void putField
	(Reference reference, Object object, Object object_1_)
	throws InterpreterException {
	throw new InterpreterException("Modifying Field " + reference + ".");
    }
    
    public Object invokeConstructor(Reference reference, Object[] objects)
	throws InterpreterException, InvocationTargetException {
	if (isWhite(reference))
	    return super.invokeConstructor(reference, objects);
	throw new InterpreterException("Creating new Object " + reference
				       + ".");
    }
    
    public Object invokeMethod
	(Reference reference, boolean bool, Object object, Object[] objects)
	throws InterpreterException, InvocationTargetException {
	if (isWhite(reference))
	    return super.invokeMethod(reference, bool, object, objects);
	MethodIdentifier methodidentifier
	    = ((MethodIdentifier)
	       Main.getClassBundle().getIdentifier(reference));
	if (methodidentifier != null) {
	    BytecodeInfo bytecodeinfo = methodidentifier.info.getBytecode();
	    if (bytecodeinfo != null)
		return interpreter.interpretMethod(bytecodeinfo, object,
						   objects);
	}
	throw new InterpreterException("Invoking library method " + reference
				       + ".");
    }
    
    public boolean instanceOf(Object object, String string)
	throws InterpreterException {
	Class var_class;
	try {
	    var_class = Class.forName(string);
	} catch (ClassNotFoundException classnotfoundexception) {
	    throw new InterpreterException("Class "
					   + classnotfoundexception
						 .getMessage()
					   + " not found");
	}
	return object != null && var_class.isInstance(object);
    }
    
    public Object newArray(String string, int[] is)
	throws InterpreterException, NegativeArraySizeException {
	if (string.length() == is.length + 1) {
	    Class var_class;
	    try {
		var_class
		    = TypeSignature.getClass(string.substring(is.length));
	    } catch (ClassNotFoundException classnotfoundexception) {
		throw new InterpreterException("Class "
					       + classnotfoundexception
						     .getMessage()
					       + " not found");
	    }
	    return Array.newInstance(var_class, is);
	}
	throw new InterpreterException("Creating object array.");
    }
    
    static {
	addWhite(Reference.getReference("Ljava/lang/String;", "toCharArray",
					"()[C"));
	addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "<init>",
					"(Ljava/lang/String;)V"));
	addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "<init>",
					"()V"));
	addWhite(Reference.getReference
		 ("Ljava/lang/StringBuffer;", "append",
		  "(Ljava/lang/String;)Ljava/lang/StringBuffer;"));
	addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append",
					"(C)Ljava/lang/StringBuffer;"));
	addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append",
					"(B)Ljava/lang/StringBuffer;"));
	addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append",
					"(S)Ljava/lang/StringBuffer;"));
	addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append",
					"(Z)Ljava/lang/StringBuffer;"));
	addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append",
					"(F)Ljava/lang/StringBuffer;"));
	addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append",
					"(I)Ljava/lang/StringBuffer;"));
	addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append",
					"(J)Ljava/lang/StringBuffer;"));
	addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "append",
					"(D)Ljava/lang/StringBuffer;"));
	addWhite(Reference.getReference("Ljava/lang/StringBuffer;", "toString",
					"()Ljava/lang/String;"));
	addWhite(Reference.getReference("Ljava/lang/String;", "<init>",
					"()V"));
	addWhite(Reference.getReference("Ljava/lang/String;", "<init>",
					"([C)V"));
	addWhite(Reference.getReference("Ljava/lang/String;", "<init>",
					"([CII)V"));
	addWhite(Reference.getReference("Ljava/lang/String;", "<init>",
					"(Ljava/lang/String;)V"));
	addWhite(Reference.getReference("Ljava/lang/String;", "<init>",
					"(Ljava/lang/StringBuffer;)V"));
	addWhite(Reference.getReference("Ljava/lang/String;", "length",
					"()I"));
	addWhite(Reference.getReference("Ljava/lang/String;", "replace",
					"(CC)Ljava/lang/String;"));
	addWhite(Reference.getReference("Ljava/lang/String;", "valueOf",
					"(Z)Ljava/lang/String;"));
	addWhite(Reference.getReference("Ljava/lang/String;", "valueOf",
					"(B)Ljava/lang/String;"));
	addWhite(Reference.getReference("Ljava/lang/String;", "valueOf",
					"(S)Ljava/lang/String;"));
	addWhite(Reference.getReference("Ljava/lang/String;", "valueOf",
					"(C)Ljava/lang/String;"));
	addWhite(Reference.getReference("Ljava/lang/String;", "valueOf",
					"(D)Ljava/lang/String;"));
	addWhite(Reference.getReference("Ljava/lang/String;", "valueOf",
					"(F)Ljava/lang/String;"));
	addWhite(Reference.getReference("Ljava/lang/String;", "valueOf",
					"(I)Ljava/lang/String;"));
	addWhite(Reference.getReference("Ljava/lang/String;", "valueOf",
					"(J)Ljava/lang/String;"));
	addWhite
	    (Reference.getReference("Ljava/lang/String;", "valueOf",
				    "(Ljava/lang/Object;)Ljava/lang/String;"));
	addWhite(Reference.getReference("Ljava/lang/String;", "substring",
					"(I)Ljava/lang/String;"));
	addWhite(Reference.getReference("Ljava/lang/String;", "substring",
					"(II)Ljava/lang/String;"));
	addWhite(Reference.getReference("Ljava.lang/reflect/Modifier;",
					"toString", "(I)Ljava/lang/String;"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "abs", "(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "abs", "(F)F"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "abs", "(I)I"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "abs", "(J)J"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "acos", "(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "asin", "(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "atan", "(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "atan2", "(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "ceil", "(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "cos", "(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "exp", "(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "floor", "(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "IEEEremainder",
					"(DD)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "log", "(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "max", "(DD)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "max", "(FF)F"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "max", "(II)I"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "max", "(JJ)J"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "min", "(DD)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "min", "(FF)F"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "min", "(II)I"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "min", "(JJ)J"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "pow", "(DD)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "rint", "(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "round", "(D)J"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "round", "(F)I"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "sin", "(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "sqrt", "(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "tan", "(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "toDegrees",
					"(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "toRadians",
					"(D)D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "E", "D"));
	addWhite(Reference.getReference("Ljava/lang/Math;", "PI", "D"));
	whiteList.add("Ljava/lang/String;");
    }
}
