/* ConstOperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.expr;
import java.io.IOException;

import jode.AssertError;
import jode.decompiler.TabbedPrintWriter;
import jode.type.IntegerType;
import jode.type.Type;

public class ConstOperator extends NoArgOperator
{
    Object value;
    boolean isInitializer = false;
    private static final Type tBoolConstInt = new IntegerType(31);
    
    public ConstOperator(Object object) {
	super(Type.tUnknown);
	if (object instanceof Boolean) {
	    this.updateParentType(Type.tBoolean);
	    object = new Integer(((Boolean) object).booleanValue() ? 1 : 0);
	} else if (object instanceof Integer) {
	    int i = ((Integer) object).intValue();
	    this.updateParentType(i == 0 || i == 1 ? tBoolConstInt
				  : i < -32768 || i > 65535 ? (Type) Type.tInt
				  : new IntegerType(i < -128 ? 10 : i < 0 ? 26
						    : i <= 127 ? 30
						    : i <= 32767 ? 14 : 6));
	} else if (object instanceof Long)
	    this.updateParentType(Type.tLong);
	else if (object instanceof Float)
	    this.updateParentType(Type.tFloat);
	else if (object instanceof Double)
	    this.updateParentType(Type.tDouble);
	else if (object instanceof String)
	    this.updateParentType(Type.tString);
	else if (object == null)
	    this.updateParentType(Type.tUObject);
	else
	    throw new IllegalArgumentException("Illegal constant type: "
					       + object.getClass());
	value = object;
    }
    
    public Object getValue() {
	return value;
    }
    
    public boolean isOne(Type type) {
	if (type instanceof IntegerType)
	    return (value instanceof Integer
		    && ((Integer) value).intValue() == 1);
	if (type == Type.tLong)
	    return value instanceof Long && ((Long) value).longValue() == 1L;
	if (type == Type.tFloat)
	    return (value instanceof Float
		    && ((Float) value).floatValue() == 1.0F);
	if (type == Type.tDouble)
	    return (value instanceof Double
		    && ((Double) value).doubleValue() == 1.0);
	return false;
    }
    
    public int getPriority() {
	return 1000;
    }
    
    public boolean opEquals(Operator operator) {
	if (operator instanceof ConstOperator) {
	    Object object = ((ConstOperator) operator).value;
	    return value == null ? object == null : value.equals(object);
	}
	return false;
    }
    
    public void makeInitializer(Type type) {
	isInitializer = true;
    }
    
    private static String quoted(String string) {
	StringBuffer stringbuffer = new StringBuffer("\"");
	for (int i = 0; i < string.length(); i++) {
	    char c;
	    switch (c = string.charAt(i)) {
	    case '\0':
		stringbuffer.append("\\0");
		break;
	    case '\t':
		stringbuffer.append("\\t");
		break;
	    case '\n':
		stringbuffer.append("\\n");
		break;
	    case '\r':
		stringbuffer.append("\\r");
		break;
	    case '\\':
		stringbuffer.append("\\\\");
		break;
	    case '\"':
		stringbuffer.append("\\\"");
		break;
	    default:
		if (c < ' ') {
		    String string_0_ = Integer.toOctalString(c);
		    stringbuffer.append
			("\\000".substring(0, 4 - string_0_.length()))
			.append(string_0_);
		} else if (c >= ' ' && c < '\u007f')
		    stringbuffer.append(string.charAt(i));
		else {
		    String string_1_ = Integer.toHexString(c);
		    stringbuffer.append
			("\\u0000".substring(0, 6 - string_1_.length()))
			.append(string_1_);
		}
	    }
	}
	return stringbuffer.append("\"").toString();
    }
    
    public String toString() {
	String string = String.valueOf(value);
	if (type.isOfType(Type.tBoolean)) {
	    int i = ((Integer) value).intValue();
	    if (i == 0)
		return "false";
	    if (i == 1)
		return "true";
	    throw new AssertError("boolean is neither false nor true");
	}
	if (type.getHint().equals(Type.tChar)) {
	    char c = (char) ((Integer) value).intValue();
	    switch (c) {
	    case '\0':
		return "'\\0'";
	    case '\t':
		return "'\\t'";
	    case '\n':
		return "'\\n'";
	    case '\r':
		return "'\\r'";
	    case '\\':
		return "'\\\\'";
	    case '\"':
		return "'\\\"'";
	    case '\'':
		return "'\\''";
	    default: {
		if (c < ' ') {
		    String string_2_ = Integer.toOctalString(c);
		    return ("'\\000".substring(0, 5 - string_2_.length())
			    + string_2_ + "'");
		}
		if (c >= ' ' && c < '\u007f')
		    return "'" + c + "'";
		String string_3_ = Integer.toHexString(c);
		return ("'\\u0000".substring(0, 7 - string_3_.length())
			+ string_3_ + "'");
	    }
	    }
	}
	if (type.equals(Type.tString))
	    return quoted(string);
	if (parent != null) {
	    int i = parent.getOperatorIndex();
	    if (i >= 13 && i < 24)
		i -= 12;
	    if (i >= 9 && i < 12) {
		if (type.isOfType(Type.tUInt)) {
		    int i_4_ = ((Integer) value).intValue();
		    if (i_4_ < -1)
			string = "~0x" + Integer.toHexString(-i_4_ - 1);
		    else
			string = "0x" + Integer.toHexString(i_4_);
		} else if (type.equals(Type.tLong)) {
		    long l = ((Long) value).longValue();
		    if (l < -1L)
			string = "~0x" + Long.toHexString(-l - 1L);
		    else
			string = "0x" + Long.toHexString(l);
		}
	    }
	}
	if (type.isOfType(Type.tLong))
	    return string + "L";
	if (type.isOfType(Type.tFloat)) {
	    if (string.equals("NaN"))
		return "Float.NaN";
	    if (string.equals("-Infinity"))
		return "Float.NEGATIVE_INFINITY";
	    if (string.equals("Infinity"))
		return "Float.POSITIVE_INFINITY";
	    return string + "F";
	}
	if (type.isOfType(Type.tDouble)) {
	    if (string.equals("NaN"))
		return "Double.NaN";
	    if (string.equals("-Infinity"))
		return "Double.NEGATIVE_INFINITY";
	    if (string.equals("Infinity"))
		return "Double.POSITIVE_INFINITY";
	    return string;
	}
	if (!type.isOfType(Type.tInt)
	    && (type.getHint().equals(Type.tByte)
		|| type.getHint().equals(Type.tShort))
	    && !isInitializer
	    && (!(parent instanceof StoreInstruction)
		|| parent.getOperatorIndex() == 12
		|| parent.subExpressions[1] != this))
	    return "(" + type.getHint() + ") " + string;
	return string;
    }
    
    public void dumpExpression(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.print(toString());
    }
}
