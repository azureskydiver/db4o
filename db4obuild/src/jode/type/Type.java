/* Type - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.type;
import java.util.Iterator;

import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.ClassInfo;
import jode.util.UnifyHash;

public class Type
{
    public static final int TC_BOOLEAN = 0;
    public static final int TC_BYTE = 1;
    public static final int TC_CHAR = 2;
    public static final int TC_SHORT = 3;
    public static final int TC_INT = 4;
    public static final int TC_LONG = 5;
    public static final int TC_FLOAT = 6;
    public static final int TC_DOUBLE = 7;
    public static final int TC_NULL = 8;
    public static final int TC_ARRAY = 9;
    public static final int TC_CLASS = 10;
    public static final int TC_VOID = 11;
    public static final int TC_METHOD = 12;
    public static final int TC_ERROR = 13;
    public static final int TC_UNKNOWN = 101;
    public static final int TC_RANGE = 103;
    public static final int TC_INTEGER = 107;
    private static final UnifyHash classHash = new UnifyHash();
    private static final UnifyHash arrayHash = new UnifyHash();
    private static final UnifyHash methodHash = new UnifyHash();
    public static final Type tBoolean = new IntegerType(1);
    public static final Type tByte = new IntegerType(16);
    public static final Type tChar = new IntegerType(4);
    public static final Type tShort = new IntegerType(8);
    public static final Type tInt = new IntegerType(2);
    public static final Type tLong = new Type(5);
    public static final Type tFloat = new Type(6);
    public static final Type tDouble = new Type(7);
    public static final Type tVoid = new Type(11);
    public static final Type tError = new Type(13);
    public static final Type tUnknown = new Type(101);
    public static final Type tUInt = new IntegerType(30);
    public static final Type tBoolInt = new IntegerType(3);
    public static final Type tBoolUInt = new IntegerType(31);
    public static final Type tBoolByte = new IntegerType(17);
    public static final ClassInterfacesType tObject
	= tClass("java.lang.Object");
    public static final ReferenceType tNull = new NullType();
    public static final Type tUObject = tRange(tObject, tNull);
    public static final Type tString = tClass("java.lang.String");
    public static final Type tStringBuffer = tClass("java.lang.StringBuffer");
    public static final Type tJavaLangClass = tClass("java.lang.Class");
    final int typecode;
    
    public static final Type tType(String string) {
	if (string == null || string.length() == 0)
	    return tError;
	switch (string.charAt(0)) {
	case 'Z':
	    return tBoolean;
	case 'B':
	    return tByte;
	case 'C':
	    return tChar;
	case 'S':
	    return tShort;
	case 'I':
	    return tInt;
	case 'F':
	    return tFloat;
	case 'J':
	    return tLong;
	case 'D':
	    return tDouble;
	case 'V':
	    return tVoid;
	case '[':
	    return tArray(tType(string.substring(1)));
	case 'L': {
	    int i = string.indexOf(';');
	    if (i != string.length() - 1)
		return tError;
	    return tClass(string.substring(1, i));
	}
	case '(':
	    return tMethod(string);
	default:
	    throw new AssertError("Unknown type signature: " + string);
	}
    }
    
    public static final ClassInterfacesType tClass(String string) {
	return tClass(ClassInfo.forName(string.replace('/', '.')));
    }
    
    public static final ClassInterfacesType tClass(ClassInfo classinfo) {
	int i = classinfo.hashCode();
	Iterator iterator = classHash.iterateHashCode(i);
	while (iterator.hasNext()) {
	    ClassInterfacesType classinterfacestype
		= (ClassInterfacesType) iterator.next();
	    if (classinterfacestype.getClassInfo() == classinfo)
		return classinterfacestype;
	}
	ClassInterfacesType classinterfacestype
	    = new ClassInterfacesType(classinfo);
	classHash.put(i, classinterfacestype);
	return classinterfacestype;
    }
    
    public static final Type tArray(Type type) {
	if (type == tError)
	    return type;
	int i = type.hashCode();
	Iterator iterator = arrayHash.iterateHashCode(i);
	while (iterator.hasNext()) {
	    ArrayType arraytype = (ArrayType) iterator.next();
	    if (arraytype.getElementType().equals(type))
		return arraytype;
	}
	ArrayType arraytype = new ArrayType(type);
	arrayHash.put(i, arraytype);
	return arraytype;
    }
    
    public static MethodType tMethod(String string) {
	int i = string.hashCode();
	Iterator iterator = methodHash.iterateHashCode(i);
	while (iterator.hasNext()) {
	    MethodType methodtype = (MethodType) iterator.next();
	    if (methodtype.getTypeSignature().equals(string))
		return methodtype;
	}
	MethodType methodtype = new MethodType(string);
	methodHash.put(i, methodtype);
	return methodtype;
    }
    
    public static final Type tRange(ReferenceType referencetype,
				    ReferenceType referencetype_0_) {
	return new RangeType(referencetype, referencetype_0_);
    }
    
    public static Type tSuperType(Type type) {
	return type.getSuperType();
    }
    
    public static Type tSubType(Type type) {
	return type.getSubType();
    }
    
    protected Type(int i) {
	typecode = i;
    }
    
    public Type getSubType() {
	return this;
    }
    
    public Type getSuperType() {
	return this;
    }
    
    public Type getHint() {
	return getCanonic();
    }
    
    public Type getCanonic() {
	return this;
    }
    
    public final int getTypeCode() {
	return typecode;
    }
    
    public int stackSize() {
	switch (typecode) {
	case 11:
	    return 0;
	default:
	    return 1;
	case 5:
	case 7:
	    return 2;
	}
    }
    
    public Type intersection(Type type_1_) {
	if (this == tError || type_1_ == tError)
	    return tError;
	if (this == tUnknown)
	    return type_1_;
	if (type_1_ == tUnknown || this == type_1_)
	    return this;
	if ((GlobalOptions.debuggingFlags & 0x4) != 0)
	    GlobalOptions.err.println("intersecting " + this + " and "
				      + type_1_ + " to <error>");
	return tError;
    }
    
    public Type getCastHelper(Type type_2_) {
	return null;
    }
    
    public boolean isValidType() {
	return typecode <= 7;
    }
    
    public boolean isClassType() {
	return false;
    }
    
    public boolean isOfType(Type type_3_) {
	return intersection(type_3_) != tError;
    }
    
    public String getDefaultName() {
	switch (typecode) {
	case 5:
	    return "l";
	case 6:
	    return "f";
	case 7:
	    return "d";
	default:
	    return "local";
	}
    }
    
    public Object getDefaultValue() {
	switch (typecode) {
	case 5:
	    return new Long(0L);
	case 6:
	    return new Float(0.0F);
	case 7:
	    return new Double(0.0);
	default:
	    return null;
	}
    }
    
    public String getTypeSignature() {
	switch (typecode) {
	case 5:
	    return "J";
	case 6:
	    return "F";
	case 7:
	    return "D";
	default:
	    return "?";
	}
    }
    
    public Class getTypeClass() throws ClassNotFoundException {
	switch (typecode) {
	case 5:
	    return Long.TYPE;
	case 6:
	    return Float.TYPE;
	case 7:
	    return Double.TYPE;
	default:
	    throw new AssertError("getTypeClass() called on illegal type");
	}
    }
    
    public String toString() {
	switch (typecode) {
	case 5:
	    return "long";
	case 6:
	    return "float";
	case 7:
	    return "double";
	case 8:
	    return "null";
	case 11:
	    return "void";
	case 101:
	    return "<unknown>";
	default:
	    return "<error>";
	}
    }
}
