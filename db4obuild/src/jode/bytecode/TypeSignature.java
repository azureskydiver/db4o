/* TypeSignature - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.bytecode;
import jode.AssertError;

public class TypeSignature
{
    private static final StringBuffer appendSignature
	(StringBuffer stringbuffer, Class var_class) {
	if (var_class.isPrimitive()) {
	    if (var_class == Boolean.TYPE)
		return stringbuffer.append('Z');
	    if (var_class == Byte.TYPE)
		return stringbuffer.append('B');
	    if (var_class == Character.TYPE)
		return stringbuffer.append('C');
	    if (var_class == Short.TYPE)
		return stringbuffer.append('S');
	    if (var_class == Integer.TYPE)
		return stringbuffer.append('I');
	    if (var_class == Long.TYPE)
		return stringbuffer.append('J');
	    if (var_class == Float.TYPE)
		return stringbuffer.append('F');
	    if (var_class == Double.TYPE)
		return stringbuffer.append('D');
	    if (var_class == Void.TYPE)
		return stringbuffer.append('V');
	    throw new AssertError("Unknown primitive type: " + var_class);
	}
	if (var_class.isArray())
	    return appendSignature(stringbuffer.append('['),
				   var_class.getComponentType());
	return stringbuffer.append('L').append
		   (var_class.getName().replace('.', '/')).append(';');
    }
    
    public static String getSignature(Class var_class) {
	return appendSignature(new StringBuffer(), var_class).toString();
    }
    
    public static String getSignature(Class[] var_classes, Class var_class) {
	StringBuffer stringbuffer = new StringBuffer("(");
	for (int i = 0; i < var_classes.length; i++)
	    appendSignature(stringbuffer, var_classes[i]);
	return appendSignature(stringbuffer.append(')'), var_class).toString();
    }
    
    public static Class getClass(String string) throws ClassNotFoundException {
	switch (string.charAt(0)) {
	case 'Z':
	    return Boolean.TYPE;
	case 'B':
	    return Byte.TYPE;
	case 'C':
	    return Character.TYPE;
	case 'S':
	    return Short.TYPE;
	case 'I':
	    return Integer.TYPE;
	case 'F':
	    return Float.TYPE;
	case 'J':
	    return Long.TYPE;
	case 'D':
	    return Double.TYPE;
	case 'V':
	    return Void.TYPE;
	case 'L':
	    string
		= string.substring(1, string.length() - 1).replace('/', '.');
	    /* fall through */
	case '[':
	    return Class.forName(string);
	default:
	    throw new IllegalArgumentException(string);
	}
    }
    
    private static boolean usingTwoSlots(char c) {
	return "JD".indexOf(c) >= 0;
    }
    
    public static int getTypeSize(String string) {
	return usingTwoSlots(string.charAt(0)) ? 2 : 1;
    }
    
    public static String getElementType(String string) {
	if (string.charAt(0) != '[')
	    throw new IllegalArgumentException();
	return string.substring(1);
    }
    
    public static ClassInfo getClassInfo(String string) {
	if (string.charAt(0) != 'L')
	    throw new IllegalArgumentException();
	return ClassInfo.forName(string.substring(1, string.length() - 1)
				     .replace('/', '.'));
    }
    
    public static int skipType(String string, int i) {
	char c;
	for (c = string.charAt(i++); c == '['; c = string.charAt(i++)) {
	    /* empty */
	}
	if (c == 'L')
	    return string.indexOf(';', i) + 1;
	return i;
    }
    
    public static int getArgumentSize(String string) {
	int i = 0;
	int i_0_ = 1;
	for (;;) {
	    char c = string.charAt(i_0_);
	    if (c == ')')
		return i;
	    i_0_ = skipType(string, i_0_);
	    if (usingTwoSlots(c))
		i += 2;
	    else
		i++;
	}
    }
    
    public static int getReturnSize(String string) {
	int i = string.length();
	if (string.charAt(i - 2) == ')') {
	    char c = string.charAt(i - 1);
	    return c == 'V' ? 0 : usingTwoSlots(c) ? 2 : 1;
	}
	return 1;
    }
    
    public static String[] getParameterTypes(String string) {
	int i = 1;
	int i_1_ = 0;
	while (string.charAt(i) != ')') {
	    i = skipType(string, i);
	    i_1_++;
	}
	String[] strings = new String[i_1_];
	i = 1;
	for (int i_2_ = 0; i_2_ < i_1_; i_2_++) {
	    int i_3_ = i;
	    i = skipType(string, i);
	    strings[i_2_] = string.substring(i_3_, i);
	}
	return strings;
    }
    
    public static String getReturnType(String string) {
	return string.substring(string.lastIndexOf(')') + 1);
    }
    
    private static int checkClassName(String string, int i)
	throws IllegalArgumentException, StringIndexOutOfBoundsException {
	char c;
	do {
	    c = string.charAt(i++);
	    if (c == ';')
		return i;
	} while (c == '/' || Character.isJavaIdentifierPart(c));
	throw new IllegalArgumentException("Illegal java class name: "
					   + string);
    }
    
    private static int checkTypeSig(String string, int i) {
	char c;
	for (c = string.charAt(i++); c == '['; c = string.charAt(i++)) {
	    /* empty */
	}
	if (c == 'L')
	    i = checkClassName(string, i);
	else if ("ZBSCIJFD".indexOf(c) == -1)
	    throw new IllegalArgumentException("Type sig error: " + string);
	return i;
    }
    
    public static void checkTypeSig(String string)
	throws IllegalArgumentException {
	try {
	    if (checkTypeSig(string, 0) != string.length())
		throw new IllegalArgumentException("Type sig too long: "
						   + string);
	} catch (StringIndexOutOfBoundsException stringindexoutofboundsexception) {
	    throw new IllegalArgumentException("Incomplete type sig: "
					       + string);
	}
    }
    
    public static void checkMethodTypeSig(String string)
	throws IllegalArgumentException {
	try {
	    if (string.charAt(0) != '(')
		throw new IllegalArgumentException("No method signature: "
						   + string);
	    int i;
	    for (i = 1; string.charAt(i) != ')'; i = checkTypeSig(string, i)) {
		/* empty */
	    }
	    i++;
	    if (string.charAt(i) == 'V')
		i++;
	    else
		i = checkTypeSig(string, i);
	    if (i != string.length())
		throw new IllegalArgumentException("Type sig too long: "
						   + string);
	} catch (StringIndexOutOfBoundsException stringindexoutofboundsexception) {
	    throw new IllegalArgumentException("Incomplete type sig: "
					       + string);
	}
    }
}
