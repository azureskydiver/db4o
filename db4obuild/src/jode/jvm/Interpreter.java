/* Interpreter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.jvm;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.Opcodes;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;

public class Interpreter implements Opcodes
{
    private static final int CMP_EQ = 0;
    private static final int CMP_NE = 1;
    private static final int CMP_LT = 2;
    private static final int CMP_GE = 3;
    private static final int CMP_GT = 4;
    private static final int CMP_LE = 5;
    private static final int CMP_GREATER_MASK = 26;
    private static final int CMP_LESS_MASK = 38;
    private static final int CMP_EQUAL_MASK = 41;
    private RuntimeEnvironment env;
    
    public Interpreter(RuntimeEnvironment runtimeenvironment) {
	env = runtimeenvironment;
    }
    
    private Value[] fillParameters(BytecodeInfo bytecodeinfo, Object object,
				   Object[] objects) {
	Value[] values = new Value[bytecodeinfo.getMaxLocals()];
	for (int i = 0; i < values.length; i++)
	    values[i] = new Value();
	String string = bytecodeinfo.getMethodInfo().getType();
	String[] strings = TypeSignature.getParameterTypes(string);
	int i = 0;
	if (!bytecodeinfo.getMethodInfo().isStatic())
	    values[i++].setObject(object);
	for (int i_0_ = 0; i_0_ < strings.length; i_0_++) {
	    values[i].setObject(objects[i_0_]);
	    i += TypeSignature.getTypeSize(strings[i_0_]);
	}
	return values;
    }
    
    public Object interpretMethod
	(BytecodeInfo bytecodeinfo, Object object, Object[] objects)
	throws InterpreterException, InvocationTargetException {
	if ((GlobalOptions.debuggingFlags & 0x400) != 0)
	    GlobalOptions.err.println("Interpreting " + bytecodeinfo);
	Value[] values = fillParameters(bytecodeinfo, object, objects);
	Value[] values_1_ = new Value[bytecodeinfo.getMaxStack()];
	for (int i = 0; i < values_1_.length; i++)
	    values_1_[i] = new Value();
	Instruction instruction
	    = (Instruction) bytecodeinfo.getInstructions().get(0);
	int i = 0;
    while_13_:
	for (;;) {
	    try {
		Instruction instruction_2_ = instruction;
		if ((GlobalOptions.debuggingFlags & 0x400) != 0) {
		    GlobalOptions.err.println(instruction_2_.getDescription());
		    GlobalOptions.err.print("stack: [");
		    for (int i_3_ = 0; i_3_ < i; i_3_++) {
			if (i_3_ > 0)
			    GlobalOptions.err.print(",");
			GlobalOptions.err.print(values_1_[i_3_]);
			if (values_1_[i_3_].objectValue() instanceof char[])
			    GlobalOptions.err.print
				(new String((char[])
					    values_1_[i_3_].objectValue()));
		    }
		    GlobalOptions.err.println("]");
		    GlobalOptions.err.print("local: [");
		    for (int i_4_ = 0; i_4_ < values.length; i_4_++)
			GlobalOptions.err.print(values[i_4_] + ",");
		    GlobalOptions.err.println("]");
		}
		instruction = instruction_2_.getNextByAddr();
		int i_5_ = instruction_2_.getOpcode();
		switch (i_5_) {
		case 0:
		    break;
		case 18:
		    values_1_[i++].setObject(instruction_2_.getConstant());
		    break;
		case 20:
		    values_1_[i].setObject(instruction_2_.getConstant());
		    i += 2;
		    break;
		case 21:
		case 23:
		case 25:
		    values_1_[i++]
			.setValue(values[instruction_2_.getLocalSlot()]);
		    break;
		case 22:
		case 24:
		    values_1_[i]
			.setValue(values[instruction_2_.getLocalSlot()]);
		    i += 2;
		    break;
		case 46:
		case 47:
		case 48:
		case 49:
		case 50:
		case 51:
		case 52:
		case 53: {
		    int i_6_ = values_1_[--i].intValue();
		    Object object_7_ = values_1_[--i].objectValue();
		    Object object_8_;
		    try {
			switch (i_5_) {
			case 51:
			    object_8_
				= new Integer(object_7_ instanceof byte[]
					      ? ((byte[]) object_7_)[i_6_]
					      : ((boolean[]) object_7_)[i_6_]
					      ? 1 : 0);
			    break;
			case 52:
			    object_8_
				= new Integer(((char[]) object_7_)[i_6_]);
			    break;
			case 53:
			    object_8_
				= new Integer(((short[]) object_7_)[i_6_]);
			    break;
			default:
			    object_8_ = Array.get(object_7_, i_6_);
			}
		    } catch (NullPointerException nullpointerexception) {
			throw new InvocationTargetException
				  (nullpointerexception);
		    } catch (ArrayIndexOutOfBoundsException arrayindexoutofboundsexception) {
			throw new InvocationTargetException
				  (arrayindexoutofboundsexception);
		    }
		    values_1_[i++].setObject(object_8_);
		    if (i_5_ == 47 || i_5_ == 49)
			i++;
		    break;
		}
		case 54:
		case 56:
		case 58:
		    values[instruction_2_.getLocalSlot()]
			.setValue(values_1_[--i]);
		    break;
		case 55:
		case 57: {
		    Value value = values[instruction_2_.getLocalSlot()];
		    Value[] values_9_ = values_1_;
		    i -= 2;
		    value.setValue(values_9_[i]);
		    break;
		}
		case 80:
		case 82:
		    i--;
		    /* fall through */
		case 79:
		case 81:
		case 83:
		case 84:
		case 85:
		case 86: {
		    Value value = values_1_[--i];
		    int i_10_ = values_1_[--i].intValue();
		    Object object_11_ = values_1_[--i].objectValue();
		    try {
			switch (i_5_) {
			case 84:
			    if (object_11_ instanceof byte[])
				((byte[]) object_11_)[i_10_]
				    = (byte) value.intValue();
			    else
				((boolean[]) object_11_)[i_10_]
				    = value.intValue() != 0;
			    break;
			case 85:
			    ((char[]) object_11_)[i_10_]
				= (char) value.intValue();
			    break;
			case 86:
			    ((short[]) object_11_)[i_10_]
				= (short) value.intValue();
			    break;
			default:
			    Array.set(object_11_, i_10_, value.objectValue());
			}
		    } catch (NullPointerException nullpointerexception) {
			throw new InvocationTargetException
				  (nullpointerexception);
		    } catch (ArrayIndexOutOfBoundsException arrayindexoutofboundsexception) {
			throw new InvocationTargetException
				  (arrayindexoutofboundsexception);
		    } catch (ArrayStoreException arraystoreexception) {
			throw new InvocationTargetException
				  (arraystoreexception);
		    }
		    break;
		}
		case 87:
		case 88:
		    i -= i_5_ - 86;
		    break;
		case 89:
		case 90:
		case 91: {
		    int i_12_ = i_5_ - 89;
		    for (int i_13_ = 0; i_13_ < i_12_ + 1; i_13_++)
			values_1_[i - i_13_]
			    .setValue(values_1_[i - i_13_ - 1]);
		    values_1_[i - i_12_ - 1].setValue(values_1_[i]);
		    i++;
		    break;
		}
		case 92:
		case 93:
		case 94: {
		    int i_14_ = i_5_ - 92;
		    for (int i_15_ = 0; i_15_ < i_14_ + 2; i_15_++)
			values_1_[i + 1 - i_15_]
			    .setValue(values_1_[i - 1 - i_15_]);
		    values_1_[i - i_14_ - 1].setValue(values_1_[i + 1]);
		    values_1_[i - i_14_ - 2].setValue(values_1_[i]);
		    i += 2;
		    break;
		}
		case 95: {
		    Value value = values_1_[i - 1];
		    values_1_[i - 1] = values_1_[i - 2];
		    values_1_[i - 2] = value;
		    break;
		}
		case 96:
		    values_1_[i - 2].setInt(values_1_[i - 2].intValue()
					    + values_1_[i - 1].intValue());
		    i--;
		    break;
		case 100:
		    values_1_[i - 2].setInt(values_1_[i - 2].intValue()
					    - values_1_[i - 1].intValue());
		    i--;
		    break;
		case 104:
		    values_1_[i - 2].setInt(values_1_[i - 2].intValue()
					    * values_1_[i - 1].intValue());
		    i--;
		    break;
		case 108:
		    try {
			values_1_[i - 2].setInt(values_1_[i - 2].intValue()
						/ values_1_[i - 1].intValue());
		    } catch (ArithmeticException arithmeticexception) {
			throw new InvocationTargetException
				  (arithmeticexception);
		    }
		    i--;
		    break;
		case 112:
		    try {
			values_1_[i - 2].setInt(values_1_[i - 2].intValue()
						% values_1_[i - 1].intValue());
		    } catch (ArithmeticException arithmeticexception) {
			throw new InvocationTargetException
				  (arithmeticexception);
		    }
		    i--;
		    break;
		case 97:
		    i -= 2;
		    values_1_[i - 2].setLong(values_1_[i - 2].longValue()
					     + values_1_[i].longValue());
		    break;
		case 101:
		    i -= 2;
		    values_1_[i - 2].setLong(values_1_[i - 2].longValue()
					     - values_1_[i].longValue());
		    break;
		case 105:
		    i -= 2;
		    values_1_[i - 2].setLong(values_1_[i - 2].longValue()
					     * values_1_[i].longValue());
		    break;
		case 109:
		    i -= 2;
		    try {
			values_1_[i - 2].setLong(values_1_[i - 2].longValue()
						 / values_1_[i].longValue());
		    } catch (ArithmeticException arithmeticexception) {
			throw new InvocationTargetException
				  (arithmeticexception);
		    }
		    break;
		case 113:
		    i -= 2;
		    try {
			values_1_[i - 2].setLong(values_1_[i - 2].longValue()
						 % values_1_[i].longValue());
		    } catch (ArithmeticException arithmeticexception) {
			throw new InvocationTargetException
				  (arithmeticexception);
		    }
		    break;
		case 98:
		    values_1_[i - 2].setFloat(values_1_[i - 2].floatValue()
					      + values_1_[i - 1].floatValue());
		    i--;
		    break;
		case 102:
		    values_1_[i - 2].setFloat(values_1_[i - 2].floatValue()
					      - values_1_[i - 1].floatValue());
		    i--;
		    break;
		case 106:
		    values_1_[i - 2].setFloat(values_1_[i - 2].floatValue()
					      * values_1_[i - 1].floatValue());
		    i--;
		    break;
		case 110:
		    values_1_[i - 2].setFloat(values_1_[i - 2].floatValue()
					      / values_1_[i - 1].floatValue());
		    i--;
		    break;
		case 114:
		    values_1_[i - 2].setFloat(values_1_[i - 2].floatValue()
					      % values_1_[i - 1].floatValue());
		    i--;
		    break;
		case 99:
		    i -= 2;
		    values_1_[i - 2].setDouble(values_1_[i - 2].doubleValue()
					       + values_1_[i].doubleValue());
		    break;
		case 103:
		    i -= 2;
		    values_1_[i - 2].setDouble(values_1_[i - 2].doubleValue()
					       - values_1_[i].doubleValue());
		    break;
		case 107:
		    i -= 2;
		    values_1_[i - 2].setDouble(values_1_[i - 2].doubleValue()
					       * values_1_[i].doubleValue());
		    break;
		case 111:
		    i -= 2;
		    values_1_[i - 2].setDouble(values_1_[i - 2].doubleValue()
					       / values_1_[i].doubleValue());
		    break;
		case 115:
		    i -= 2;
		    values_1_[i - 2].setDouble(values_1_[i - 2].doubleValue()
					       % values_1_[i].doubleValue());
		    break;
		case 116:
		    values_1_[i - 1].setInt(-values_1_[i - 1].intValue());
		    break;
		case 117:
		    values_1_[i - 2].setLong(-values_1_[i - 2].longValue());
		    break;
		case 118:
		    values_1_[i - 1].setFloat(-values_1_[i - 1].floatValue());
		    break;
		case 119:
		    values_1_[i - 2]
			.setDouble(-values_1_[i - 2].doubleValue());
		    break;
		case 120:
		    values_1_[i - 2].setInt(values_1_[i - 2].intValue()
					    << values_1_[i - 1].intValue());
		    i--;
		    break;
		case 122:
		    values_1_[i - 2].setInt(values_1_[i - 2].intValue()
					    >> values_1_[i - 1].intValue());
		    i--;
		    break;
		case 124:
		    values_1_[i - 2].setInt(values_1_[i - 2].intValue()
					    >>> values_1_[i - 1].intValue());
		    i--;
		    break;
		case 126:
		    values_1_[i - 2].setInt(values_1_[i - 2].intValue()
					    & values_1_[i - 1].intValue());
		    i--;
		    break;
		case 128:
		    values_1_[i - 2].setInt(values_1_[i - 2].intValue()
					    | values_1_[i - 1].intValue());
		    i--;
		    break;
		case 130:
		    values_1_[i - 2].setInt(values_1_[i - 2].intValue()
					    ^ values_1_[i - 1].intValue());
		    i--;
		    break;
		case 121:
		    values_1_[i - 3].setLong(values_1_[i - 3].longValue()
					     << values_1_[i - 1].intValue());
		    i--;
		    break;
		case 123:
		    values_1_[i - 3].setLong(values_1_[i - 3].longValue()
					     >> values_1_[i - 1].intValue());
		    i--;
		    break;
		case 125:
		    values_1_[i - 3].setLong(values_1_[i - 3].longValue()
					     >>> values_1_[i - 1].intValue());
		    i--;
		    break;
		case 127:
		    i -= 2;
		    values_1_[i - 2].setLong(values_1_[i - 2].longValue()
					     & values_1_[i].longValue());
		    break;
		case 129:
		    i -= 2;
		    values_1_[i - 2].setLong(values_1_[i - 2].longValue()
					     | values_1_[i].longValue());
		    break;
		case 131:
		    i -= 2;
		    values_1_[i - 2].setLong(values_1_[i - 2].longValue()
					     ^ values_1_[i].longValue());
		    break;
		case 132:
		    values[instruction_2_.getLocalSlot()].setInt
			(values[instruction_2_.getLocalSlot()].intValue()
			 + instruction_2_.getIncrement());
		    break;
		case 133:
		    values_1_[i - 1]
			.setLong((long) values_1_[i - 1].intValue());
		    i++;
		    break;
		case 134:
		    values_1_[i - 1]
			.setFloat((float) values_1_[i - 1].intValue());
		    break;
		case 135:
		    values_1_[i - 1]
			.setDouble((double) values_1_[i - 1].intValue());
		    i++;
		    break;
		case 136:
		    i--;
		    values_1_[i - 1]
			.setInt((int) values_1_[i - 1].longValue());
		    break;
		case 137:
		    i--;
		    values_1_[i - 1]
			.setFloat((float) values_1_[i - 1].longValue());
		    break;
		case 138:
		    values_1_[i - 2]
			.setDouble((double) values_1_[i - 2].longValue());
		    break;
		case 139:
		    values_1_[i - 1]
			.setInt((int) values_1_[i - 1].floatValue());
		    break;
		case 140:
		    values_1_[i - 1]
			.setLong((long) values_1_[i - 1].floatValue());
		    i++;
		    break;
		case 141:
		    values_1_[i - 1]
			.setDouble((double) values_1_[i - 1].floatValue());
		    i++;
		    break;
		case 142:
		    i--;
		    values_1_[i - 1]
			.setInt((int) values_1_[i - 1].doubleValue());
		    break;
		case 143:
		    values_1_[i - 2]
			.setLong((long) values_1_[i - 2].doubleValue());
		    break;
		case 144:
		    i--;
		    values_1_[i - 1]
			.setFloat((float) values_1_[i - 1].doubleValue());
		    break;
		case 145:
		    values_1_[i - 1]
			.setInt((byte) values_1_[i - 1].intValue());
		    break;
		case 146:
		    values_1_[i - 1]
			.setInt((char) values_1_[i - 1].intValue());
		    break;
		case 147:
		    values_1_[i - 1]
			.setInt((short) values_1_[i - 1].intValue());
		    break;
		case 148: {
		    i -= 3;
		    long l = values_1_[i - 1].longValue();
		    long l_16_ = values_1_[i + 1].longValue();
		    values_1_[i - 1]
			.setInt(l == l_16_ ? 0 : l < l_16_ ? -1 : 1);
		    break;
		}
		case 149:
		case 150: {
		    float f = values_1_[i - 2].floatValue();
		    float f_17_ = values_1_[--i].floatValue();
		    values_1_[i - 1].setInt(f == f_17_ ? 0 : i_5_ == 150
					    ? f < f_17_ ? -1 : 1 : f > f_17_
					    ? 1 : -1);
		    break;
		}
		case 151:
		case 152: {
		    i -= 3;
		    double d = values_1_[i - 1].doubleValue();
		    double d_18_ = values_1_[i + 1].doubleValue();
		    values_1_[i - 1].setInt(d == d_18_ ? 0 : i_5_ == 152
					    ? d < d_18_ ? -1 : 1 : d > d_18_
					    ? 1 : -1);
		    break;
		}
		case 153:
		case 154:
		case 155:
		case 156:
		case 157:
		case 158:
		case 159:
		case 160:
		case 161:
		case 162:
		case 163:
		case 164:
		case 165:
		case 166:
		case 198:
		case 199: {
		    int i_19_;
		    if (i_5_ >= 165) {
			Object object_20_ = values_1_[--i].objectValue();
			if (i_5_ >= 198) {
			    i_19_ = object_20_ == null ? 0 : 1;
			    i_5_ -= 198;
			} else {
			    i_19_ = (object_20_ == values_1_[--i].objectValue()
				     ? 0 : 1);
			    i_5_ -= 165;
			}
		    } else {
			i_19_ = values_1_[--i].intValue();
			if (i_5_ >= 159) {
			    int i_21_ = values_1_[--i].intValue();
			    i_19_
				= i_21_ == i_19_ ? 0 : i_21_ < i_19_ ? -1 : 1;
			    i_5_ -= 159;
			} else
			    i_5_ -= 153;
		    }
		    int i_22_ = 1 << i_5_;
		    if (i_19_ > 0 && (i_22_ & 0x1a) != 0
			|| i_19_ < 0 && (i_22_ & 0x26) != 0
			|| i_19_ == 0 && (i_22_ & 0x29) != 0)
			instruction = instruction_2_.getSingleSucc();
		    break;
		}
		case 168:
		case 201:
		    values_1_[i++].setObject(instruction_2_);
		    /* fall through */
		case 167:
		case 200:
		    instruction = instruction_2_.getSingleSucc();
		    break;
		case 169:
		    instruction
			= (Instruction) values
					    [instruction_2_.getLocalSlot()]
					    .objectValue();
		    break;
		case 171: {
		    int i_23_ = values_1_[--i].intValue();
		    int[] is = instruction_2_.getValues();
		    int i_24_ = Arrays.binarySearch(is, i_23_);
		    instruction
			= (i_24_ < 0 ? instruction_2_.getSuccs()[is.length]
			   : instruction_2_.getSuccs()[i_24_]);
		    break;
		}
		case 172:
		case 174:
		case 176:
		    return values_1_[--i].objectValue();
		case 173:
		case 175: {
		    Value[] values_25_ = values_1_;
		    i -= 2;
		    return values_25_[i].objectValue();
		}
		case 177:
		    return Void.TYPE;
		case 178: {
		    Reference reference = instruction_2_.getReference();
		    Object object_26_
			= env.getField(instruction_2_.getReference(), null);
		    values_1_[i].setObject(object_26_);
		    i += TypeSignature.getTypeSize(reference.getType());
		    break;
		}
		case 180: {
		    Reference reference = instruction_2_.getReference();
		    Object object_27_ = values_1_[--i].objectValue();
		    if (object_27_ == null)
			throw new InvocationTargetException
				  (new NullPointerException());
		    Object object_28_
			= env.getField(instruction_2_.getReference(),
				       object_27_);
		    values_1_[i].setObject(object_28_);
		    i += TypeSignature.getTypeSize(reference.getType());
		    break;
		}
		case 179: {
		    Reference reference = instruction_2_.getReference();
		    i -= TypeSignature.getTypeSize(reference.getType());
		    Object object_29_ = values_1_[i].objectValue();
		    env.putField(instruction_2_.getReference(), null,
				 object_29_);
		    break;
		}
		case 181: {
		    Reference reference = instruction_2_.getReference();
		    i -= TypeSignature.getTypeSize(reference.getType());
		    Object object_30_ = values_1_[i].objectValue();
		    Object object_31_ = values_1_[--i].objectValue();
		    if (object_31_ == null)
			throw new InvocationTargetException
				  (new NullPointerException());
		    env.putField(instruction_2_.getReference(), object_31_,
				 object_30_);
		    break;
		}
		case 182:
		case 183:
		case 184:
		case 185: {
		    Reference reference = instruction_2_.getReference();
		    String[] strings
			= TypeSignature.getParameterTypes(reference.getType());
		    Object[] objects_32_ = new Object[strings.length];
		    for (int i_33_ = strings.length - 1; i_33_ >= 0; i_33_--) {
			i -= TypeSignature.getTypeSize(strings[i_33_]);
			objects_32_[i_33_] = values_1_[i].objectValue();
		    }
		    Object object_34_ = null;
		    if (i_5_ == 183 && reference.getName().equals("<init>")
			&& values_1_[--i].getNewObject() != null) {
			NewObject newobject = values_1_[i].getNewObject();
			if (!newobject.getType().equals(reference.getClazz()))
			    throw new InterpreterException
				      ("constructor doesn't match new");
			newobject.setObject
			    (env.invokeConstructor(reference, objects_32_));
		    } else if (i_5_ == 184)
			object_34_ = env.invokeMethod(reference, false, null,
						      objects_32_);
		    else {
			Object object_35_ = values_1_[--i].objectValue();
			if (object_35_ == null)
			    throw new InvocationTargetException
				      (new NullPointerException());
			object_34_ = env.invokeMethod(reference, i_5_ != 183,
						      object_35_, objects_32_);
		    }
		    String string
			= TypeSignature.getReturnType(reference.getType());
		    if (!string.equals("V")) {
			values_1_[i].setObject(object_34_);
			i += TypeSignature.getTypeSize(string);
		    }
		    break;
		}
		case 187: {
		    String string = instruction_2_.getClazzType();
		    values_1_[i++].setNewObject(new NewObject(string));
		    break;
		}
		case 190: {
		    Object object_36_ = values_1_[--i].objectValue();
		    if (object_36_ == null)
			throw new InvocationTargetException
				  (new NullPointerException());
		    values_1_[i++].setInt(Array.getLength(object_36_));
		    break;
		}
		case 191: {
		    Throwable throwable
			= (Throwable) values_1_[--i].objectValue();
		    throw new InvocationTargetException
			      (throwable == null
			       ? (Throwable) new NullPointerException()
			       : throwable);
		}
		case 192: {
		    Object object_37_ = values_1_[i - 1].objectValue();
		    if (object_37_ != null
			&& !env.instanceOf(object_37_,
					   instruction_2_.getClazzType()))
			throw new InvocationTargetException
				  (new ClassCastException(object_37_.getClass
							      ().getName()));
		    break;
		}
		case 193: {
		    Object object_38_ = values_1_[--i].objectValue();
		    values_1_[i++].setInt(env.instanceOf(object_38_,
							 instruction_2_
							     .getClazzType())
					  ? 1 : 0);
		    break;
		}
		case 194:
		    env.enterMonitor(values_1_[--i].objectValue());
		    break;
		case 195:
		    env.exitMonitor(values_1_[--i].objectValue());
		    break;
		case 197: {
		    int i_39_ = instruction_2_.getDimensions();
		    int[] is = new int[i_39_];
		    for (int i_40_ = i_39_ - 1; i_40_ >= 0; i_40_--)
			is[i_40_] = values_1_[--i].intValue();
		    try {
			values_1_[i++].setObject
			    (env.newArray(instruction_2_.getClazzType(), is));
		    } catch (NegativeArraySizeException negativearraysizeexception) {
			throw new InvocationTargetException
				  (negativearraysizeexception);
		    }
		    break;
		}
		default:
		    throw new AssertError("Invalid opcode " + i_5_);
		}
	    } catch (InvocationTargetException invocationtargetexception) {
		Handler[] handlers = bytecodeinfo.getExceptionHandlers();
		Throwable throwable
		    = invocationtargetexception.getTargetException();
		for (int i_41_ = 0; i_41_ < handlers.length; i_41_++) {
		    if (handlers[i_41_].start.compareTo(instruction) <= 0
			&& handlers[i_41_].end.compareTo(instruction) >= 0
			&& (handlers[i_41_].type == null
			    || env.instanceOf(throwable,
					      handlers[i_41_].type))) {
			i = 0;
			values_1_[i++].setObject(throwable);
			instruction = handlers[i_41_].catcher;
			continue while_13_;
		    }
		}
		throw invocationtargetexception;
	    }
	}
    }
}
