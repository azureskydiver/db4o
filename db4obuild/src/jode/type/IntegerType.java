/* IntegerType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.type;
import jode.AssertError;
import jode.GlobalOptions;

public class IntegerType extends Type
{
    public static final int IT_Z = 1;
    public static final int IT_I = 2;
    public static final int IT_C = 4;
    public static final int IT_S = 8;
    public static final int IT_B = 16;
    public static final int IT_cS = 32;
    public static final int IT_cB = 64;
    private static final int NUM_TYPES = 7;
    private static final int[] subTypes = { 1, 30, 4, 24, 16, 96, 64 };
    private static final int[] superTypes = { 1, 2, 6, 10, 26, 46, 126 };
    private static final Type[] simpleTypes
	= { new IntegerType(1), new IntegerType(2), new IntegerType(4),
	    new IntegerType(8), new IntegerType(16), new IntegerType(32),
	    new IntegerType(64) };
    private static final String[] typeNames
	= { "Z", "I", "C", "S", "B", "s", "b" };
    int possTypes;
    int hintTypes;
    
    public IntegerType(int i) {
	this(i, i);
    }
    
    public IntegerType(int i, int i_0_) {
	super(107);
	possTypes = i;
	hintTypes = i_0_;
    }
    
    public Type getHint() {
	int i = possTypes & hintTypes;
	if (i == 0)
	    i = possTypes;
	int i_1_ = 0;
	while ((i & 0x1) == 0) {
	    i >>= 1;
	    i_1_++;
	}
	return simpleTypes[i_1_];
    }
    
    public Type getCanonic() {
	int i = possTypes;
	int i_2_ = 0;
	while ((i >>= 1) != 0)
	    i_2_++;
	return simpleTypes[i_2_];
    }
    
    private static int getSubTypes(int i) {
	int i_3_ = 0;
	for (int i_4_ = 0; i_4_ < 7; i_4_++) {
	    if ((1 << i_4_ & i) != 0)
		i_3_ |= subTypes[i_4_];
	}
	return i_3_;
    }
    
    private static int getSuperTypes(int i) {
	int i_5_ = 0;
	for (int i_6_ = 0; i_6_ < 7; i_6_++) {
	    if ((1 << i_6_ & i) != 0)
		i_5_ |= superTypes[i_6_];
	}
	return i_5_;
    }
    
    public Type getSubType() {
	return new IntegerType(getSubTypes(possTypes), getSubTypes(hintTypes));
    }
    
    public Type getSuperType() {
	return new IntegerType(getSuperTypes(possTypes), hintTypes);
    }
    
    public boolean isValidType() {
	return true;
    }
    
    public boolean isOfType(Type type) {
	return (type.typecode == 107
		&& (((IntegerType) type).possTypes & possTypes) != 0);
    }
    
    public String getDefaultName() {
	switch (((IntegerType) getHint()).possTypes) {
	case 1:
	    return "bool";
	case 4:
	    return "c";
	case 2:
	case 8:
	case 16:
	    return "i";
	default:
	    throw new AssertError("Local can't be of constant type!");
	}
    }
    
    public Object getDefaultValue() {
	return new Integer(0);
    }
    
    public String getTypeSignature() {
	switch (((IntegerType) getHint()).possTypes) {
	case 1:
	    return "Z";
	case 4:
	    return "C";
	case 16:
	    return "B";
	case 8:
	    return "S";
	default:
	    return "I";
	}
    }
    
    public Class getTypeClass() {
	switch (((IntegerType) getHint()).possTypes) {
	case 1:
	    return Boolean.TYPE;
	case 4:
	    return Character.TYPE;
	case 16:
	    return Byte.TYPE;
	case 8:
	    return Short.TYPE;
	default:
	    return Integer.TYPE;
	}
    }
    
    public String toString() {
	if (possTypes == hintTypes) {
	    switch (possTypes) {
	    case 1:
		return "boolean";
	    case 4:
		return "char";
	    case 16:
		return "byte";
	    case 8:
		return "short";
	    case 2:
		return "int";
	    }
	}
	StringBuffer stringbuffer = new StringBuffer("{");
	for (int i = 0; i < 7; i++) {
	    if ((1 << i & possTypes) != 0)
		stringbuffer.append(typeNames[i]);
	}
	if (possTypes != hintTypes) {
	    stringbuffer.append(":");
	    for (int i = 0; i < 7; i++) {
		if ((1 << i & hintTypes) != 0)
		    stringbuffer.append(typeNames[i]);
	    }
	}
	stringbuffer.append("}");
	return stringbuffer.toString();
    }
    
    public Type intersection(Type type) {
	if (type == Type.tError)
	    return type;
	if (type == Type.tUnknown)
	    return this;
	int i = 0;
	int i_7_;
	if (type.typecode != 107)
	    i_7_ = 0;
	else {
	    IntegerType integertype_8_ = (IntegerType) type;
	    i_7_ = possTypes & integertype_8_.possTypes;
	    i = hintTypes & integertype_8_.hintTypes;
	    if (i_7_ == possTypes && i == hintTypes)
		return this;
	    if (i_7_ == integertype_8_.possTypes
		&& i == integertype_8_.hintTypes)
		return integertype_8_;
	}
	Type type_9_
	    = i_7_ == 0 ? (Type) Type.tError : new IntegerType(i_7_, i);
	if ((GlobalOptions.debuggingFlags & 0x4) != 0)
	    GlobalOptions.err.println("intersecting " + this + " and " + type
				      + " to " + type_9_);
	return type_9_;
    }
    
    public boolean equals(Object object) {
	if (object == this)
	    return true;
	if (object instanceof IntegerType) {
	    IntegerType integertype_10_ = (IntegerType) object;
	    return (integertype_10_.possTypes == possTypes
		    && integertype_10_.hintTypes == hintTypes);
	}
	return false;
    }
}
