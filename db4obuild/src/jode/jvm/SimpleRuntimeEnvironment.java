/* SimpleRuntimeEnvironment - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.jvm;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;

public class SimpleRuntimeEnvironment implements RuntimeEnvironment
{
    public static Object fromReflectType(String string, Object object) {
	switch (string.charAt(0)) {
	case 'Z':
	    return new Integer(((Boolean) object).booleanValue() ? 1 : 0);
	case 'B':
	case 'S':
	    return new Integer(((Number) object).intValue());
	case 'C':
	    return new Integer(((Character) object).charValue());
	default:
	    return object;
	}
    }
    
    public static Object toReflectType(String string, Object object) {
	switch (string.charAt(0)) {
	case 'Z':
	    return new Boolean(((Integer) object).intValue() != 0);
	case 'B':
	    return new Byte(((Integer) object).byteValue());
	case 'S':
	    return new Short(((Integer) object).shortValue());
	case 'C':
	    return new Character((char) ((Integer) object).intValue());
	default:
	    return object;
	}
    }
    
    public Object getField(Reference reference, Object object)
	throws InterpreterException {
	Field field;
	try {
	    Class var_class = TypeSignature.getClass(reference.getClazz());
	    try {
		field = var_class.getField(reference.getName());
	    } catch (NoSuchFieldException nosuchfieldexception) {
		field = var_class.getDeclaredField(reference.getName());
	    }
	} catch (ClassNotFoundException classnotfoundexception) {
	    throw new InterpreterException(reference + ": Class not found");
	} catch (NoSuchFieldException nosuchfieldexception) {
	    throw new InterpreterException("Constructor " + reference
					   + " not found");
	} catch (SecurityException securityexception) {
	    throw new InterpreterException(reference + ": Security exception");
	}
	try {
	    return fromReflectType(reference.getType(), field.get(object));
	} catch (IllegalAccessException illegalaccessexception) {
	    throw new InterpreterException("Field " + reference
					   + " not accessible");
	}
    }
    
    public void putField
	(Reference reference, Object object, Object object_0_)
	throws InterpreterException {
	Field field;
	try {
	    Class var_class = TypeSignature.getClass(reference.getClazz());
	    try {
		field = var_class.getField(reference.getName());
	    } catch (NoSuchFieldException nosuchfieldexception) {
		field = var_class.getDeclaredField(reference.getName());
	    }
	} catch (ClassNotFoundException classnotfoundexception) {
	    throw new InterpreterException(reference + ": Class not found");
	} catch (NoSuchFieldException nosuchfieldexception) {
	    throw new InterpreterException("Constructor " + reference
					   + " not found");
	} catch (SecurityException securityexception) {
	    throw new InterpreterException(reference + ": Security exception");
	}
	try {
	    field.set(object, toReflectType(reference.getType(), object_0_));
	} catch (IllegalAccessException illegalaccessexception) {
	    throw new InterpreterException("Field " + reference
					   + " not accessible");
	}
    }
    
    public Object invokeConstructor(Reference reference, Object[] objects)
	throws InterpreterException, InvocationTargetException {
	Constructor constructor;
	try {
	    String[] strings
		= TypeSignature.getParameterTypes(reference.getType());
	    Class var_class = TypeSignature.getClass(reference.getClazz());
	    Class[] var_classes = new Class[strings.length];
	    for (int i = 0; i < strings.length; i++) {
		objects[i] = toReflectType(strings[i], objects[i]);
		var_classes[i] = TypeSignature.getClass(strings[i]);
	    }
	    try {
		constructor = var_class.getConstructor(var_classes);
	    } catch (NoSuchMethodException nosuchmethodexception) {
		constructor = var_class.getDeclaredConstructor(var_classes);
	    }
	} catch (ClassNotFoundException classnotfoundexception) {
	    throw new InterpreterException(reference + ": Class not found");
	} catch (NoSuchMethodException nosuchmethodexception) {
	    throw new InterpreterException("Constructor " + reference
					   + " not found");
	} catch (SecurityException securityexception) {
	    throw new InterpreterException(reference + ": Security exception");
	}
	try {
	    return constructor.newInstance(objects);
	} catch (IllegalAccessException illegalaccessexception) {
	    throw new InterpreterException("Constructor " + reference
					   + " not accessible");
	} catch (InstantiationException instantiationexception) {
	    throw new InterpreterException("InstantiationException in "
					   + reference + ".");
	}
    }
    
    public Object invokeMethod
	(Reference reference, boolean bool, Object object, Object[] objects)
	throws InterpreterException, InvocationTargetException {
	if (!bool && object != null)
	    throw new InterpreterException("Can't invoke nonvirtual Method "
					   + reference + ".");
	Method method;
	try {
	    String[] strings
		= TypeSignature.getParameterTypes(reference.getType());
	    Class var_class = TypeSignature.getClass(reference.getClazz());
	    Class[] var_classes = new Class[strings.length];
	    for (int i = 0; i < strings.length; i++) {
		objects[i] = toReflectType(strings[i], objects[i]);
		var_classes[i] = TypeSignature.getClass(strings[i]);
	    }
	    try {
		method = var_class.getMethod(reference.getName(), var_classes);
	    } catch (NoSuchMethodException nosuchmethodexception) {
		method = var_class.getDeclaredMethod(reference.getName(),
						     var_classes);
	    }
	} catch (ClassNotFoundException classnotfoundexception) {
	    throw new InterpreterException(reference + ": Class not found");
	} catch (NoSuchMethodException nosuchmethodexception) {
	    throw new InterpreterException("Method " + reference
					   + " not found");
	} catch (SecurityException securityexception) {
	    throw new InterpreterException(reference + ": Security exception");
	}
	String string = TypeSignature.getReturnType(reference.getType());
	try {
	    return fromReflectType(string, method.invoke(object, objects));
	} catch (IllegalAccessException illegalaccessexception) {
	    throw new InterpreterException("Method " + reference
					   + " not accessible");
	}
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
	return object != null && !var_class.isInstance(object);
    }
    
    public Object newArray(String string, int[] is)
	throws InterpreterException, NegativeArraySizeException {
	Class var_class;
	try {
	    var_class = TypeSignature.getClass(string.substring(is.length));
	} catch (ClassNotFoundException classnotfoundexception) {
	    throw new InterpreterException("Class "
					   + classnotfoundexception
						 .getMessage()
					   + " not found");
	}
	return Array.newInstance(var_class, is);
    }
    
    public void enterMonitor(Object object) throws InterpreterException {
	throw new InterpreterException("monitor not implemented");
    }
    
    public void exitMonitor(Object object) throws InterpreterException {
	throw new InterpreterException("monitor not implemented");
    }
}
