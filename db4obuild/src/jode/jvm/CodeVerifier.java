/* CodeVerifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.jvm;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;

import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.ClassInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.MethodInfo;
import jode.bytecode.Opcodes;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;

public class CodeVerifier implements Opcodes
{
    ClassInfo ci;
    MethodInfo mi;
    BytecodeInfo bi;
    String methodType;
    String returnType;
    static Type tNull = Type.tType("0");
    static Type tInt = Type.tType("I");
    static Type tLong = Type.tType("J");
    static Type tFloat = Type.tType("F");
    static Type tDouble = Type.tType("D");
    static Type tString = Type.tType("Ljava/lang/String;");
    static Type tNone = Type.tType("?");
    static Type tSecondPart = new Type("2");
    static Type tObject = new Type("Ljava/lang/Object;");
    String[] types = { "I", "J", "F", "D", "+", "B", "C", "S" };
    String[] arrayTypes
	= { "[I", "[J", "[F", "[D", "[Ljava/lang/Object;", "[B", "[C", "[S" };
    
    class VerifyInfo implements Cloneable
    {
	Type[] stack = new Type[bi.getMaxStack()];
	Type[] locals = new Type[bi.getMaxLocals()];
	Instruction[] jsrTargets = null;
	BitSet[] jsrLocals = null;
	int stackHeight = 0;
	int maxHeight = 0;
	Instruction retInstr = null;
	
	public Object clone() {
	    try {
		VerifyInfo verifyinfo_0_ = (VerifyInfo) super.clone();
		verifyinfo_0_.stack = (Type[]) stack.clone();
		verifyinfo_0_.locals = (Type[]) locals.clone();
		return verifyinfo_0_;
	    } catch (CloneNotSupportedException clonenotsupportedexception) {
		throw new AssertError("Clone not supported?");
	    }
	}
	
	public final void reserve(int i) throws VerifyException {
	    if (stackHeight + i > maxHeight) {
		maxHeight = stackHeight + i;
		if (maxHeight > stack.length)
		    throw new VerifyException("stack overflow");
	    }
	}
	
	public final void need(int i) throws VerifyException {
	    if (stackHeight < i)
		throw new VerifyException("stack underflow");
	}
	
	public final void push(Type type) throws VerifyException {
	    reserve(1);
	    stack[stackHeight++] = type;
	}
	
	public final Type pop() throws VerifyException {
	    need(1);
	    return stack[--stackHeight];
	}
	
	public String toString() {
	    StringBuffer stringbuffer = new StringBuffer("locals:[");
	    String string = "";
	    for (int i = 0; i < locals.length; i++) {
		stringbuffer.append(string).append(i).append(':');
		stringbuffer.append(locals[i]);
		string = ",";
	    }
	    stringbuffer.append("], stack:[");
	    string = "";
	    for (int i = 0; i < stackHeight; i++) {
		stringbuffer.append(string).append(stack[i]);
		string = ",";
	    }
	    if (jsrTargets != null) {
		stringbuffer.append("], jsrs:[");
		string = "";
		for (int i = 0; i < jsrTargets.length; i++) {
		    stringbuffer.append(string).append(jsrTargets[i])
			.append(jsrLocals[i]);
		    string = ",";
		}
	    }
	    return stringbuffer.append("]").toString();
	}
    }
    
    private static class Type
    {
	private String typeSig;
	private Instruction instr;
	
	public Type(String string) {
	    typeSig = string;
	}
	
	public Type(String string, Instruction instruction) {
	    typeSig = string;
	    instr = instruction;
	}
	
	public static Type tType(String string) {
	    return new Type(string);
	}
	
	public static Type tType(String string, Instruction instruction) {
	    return new Type(string, instruction);
	}
	
	public String getTypeSig() {
	    return typeSig;
	}
	
	public Instruction getInstruction() {
	    return instr;
	}
	
	public boolean isOfType(String string) {
	    String string_1_ = typeSig;
	    if ((GlobalOptions.debuggingFlags & 0x2) != 0)
		GlobalOptions.err
		    .println("isOfType(" + string_1_ + "," + string + ")");
	    if (string_1_.equals(string))
		return true;
	    char c = string_1_.charAt(0);
	    char c_2_ = string.charAt(0);
	    switch (c_2_) {
	    case 'B':
	    case 'C':
	    case 'I':
	    case 'S':
	    case 'Z':
		return "ZBCSI".indexOf(c) >= 0;
	    case '+':
		return "L[nNR0".indexOf(c) >= 0;
	    case '[':
		if (c == '0')
		    return true;
		for (/**/; c == '[' && c_2_ == '['; c_2_ = string.charAt(0)) {
		    string_1_ = string_1_.substring(1);
		    string = string.substring(1);
		    c = string_1_.charAt(0);
		}
		if (c_2_ == '*')
		    return true;
		if (c_2_ != 'L')
		    return false;
		/* fall through */
	    case 'L': {
		if (c == '0')
		    return true;
		if ("L[".indexOf(c) < 0)
		    return false;
		ClassInfo classinfo = TypeSignature.getClassInfo(string);
		if (classinfo.isInterface()
		    || classinfo == ClassInfo.javaLangObject)
		    return true;
		if (c == 'L')
		    return (classinfo.superClassOf
			    (TypeSignature.getClassInfo(string_1_)));
	    }
		/* fall through */
	    default:
		return false;
	    }
	}
	
	public Type mergeType(Type type_3_) {
	    String string = typeSig;
	    String string_4_ = type_3_.typeSig;
	    if (equals(type_3_))
		return this;
	    char c = string.charAt(0);
	    char c_5_ = string_4_.charAt(0);
	    if (c == '*')
		return type_3_;
	    if (c_5_ == '*')
		return this;
	    if ("ZBCSI".indexOf(c) >= 0 && "ZBCSI".indexOf(c_5_) >= 0)
		return this;
	    if (c == '0')
		return "L[0".indexOf(c_5_) >= 0 ? type_3_ : CodeVerifier.tNone;
	    if (c_5_ == '0')
		return "L[".indexOf(c) >= 0 ? this : CodeVerifier.tNone;
	    int i = 0;
	    while (c == '[' && c_5_ == '[') {
		string = string.substring(1);
		string_4_ = string_4_.substring(1);
		c = string.charAt(0);
		c_5_ = string_4_.charAt(0);
		i++;
	    }
	    if (c == '[' && c_5_ == 'L' || c == 'L' && c_5_ == '[') {
		if (i == 0)
		    return CodeVerifier.tObject;
		StringBuffer stringbuffer = new StringBuffer(i + 18);
		for (int i_6_ = 0; i_6_ < i; i_6_++)
		    stringbuffer.append("[");
		stringbuffer.append("Ljava/lang/Object;");
		return tType(stringbuffer.toString());
	    }
	    if (c == 'L' && c_5_ == 'L') {
		ClassInfo classinfo = TypeSignature.getClassInfo(string);
		ClassInfo classinfo_7_ = TypeSignature.getClassInfo(string_4_);
		if (classinfo.superClassOf(classinfo_7_))
		    return this;
		if (classinfo_7_.superClassOf(classinfo))
		    return type_3_;
		do
		    classinfo = classinfo.getSuperclass();
		while (!classinfo.superClassOf(classinfo_7_));
		StringBuffer stringbuffer
		    = new StringBuffer(i + classinfo.getName().length() + 2);
		for (int i_8_ = 0; i_8_ < i; i_8_++)
		    stringbuffer.append("[");
		stringbuffer.append("L").append
		    (classinfo.getName().replace('.', '/')).append(";");
		return tType(stringbuffer.toString());
	    }
	    if (i > 0) {
		if (i == 1)
		    return CodeVerifier.tObject;
		StringBuffer stringbuffer = new StringBuffer(i + 17);
		for (int i_9_ = 0; i_9_ < i - 1; i_9_++)
		    stringbuffer.append("[");
		stringbuffer.append("Ljava/lang/Object;");
		return tType(stringbuffer.toString());
	    }
	    return CodeVerifier.tNone;
	}
	
	public boolean equals(Object object) {
	    if (object instanceof Type) {
		Type type_10_ = (Type) object;
		return (typeSig.equals(type_10_.typeSig)
			&& instr == type_10_.instr);
	    }
	    return false;
	}
	
	public String toString() {
	    if (instr != null)
		return typeSig + "@" + instr.getAddr();
	    return typeSig;
	}
    }
    
    public CodeVerifier(ClassInfo classinfo, MethodInfo methodinfo,
			BytecodeInfo bytecodeinfo) {
	ci = classinfo;
	mi = methodinfo;
	bi = bytecodeinfo;
	methodType = methodinfo.getType();
	returnType = TypeSignature.getReturnType(methodType);
    }
    
    public VerifyInfo initInfo() {
	VerifyInfo verifyinfo = new VerifyInfo();
	int i = 1;
	int i_11_ = 0;
	if (!mi.isStatic()) {
	    String string = ci.getName().replace('.', '/');
	    if (mi.getName().equals("<init>"))
		verifyinfo.locals[i_11_++]
		    = Type.tType("N" + string + ";", null);
	    else
		verifyinfo.locals[i_11_++] = Type.tType("L" + string + ";");
	}
	while (methodType.charAt(i) != ')') {
	    int i_12_ = i;
	    i = TypeSignature.skipType(methodType, i);
	    String string = methodType.substring(i_12_, i);
	    verifyinfo.locals[i_11_++] = Type.tType(string);
	    if (TypeSignature.getTypeSize(string) == 2)
		verifyinfo.locals[i_11_++] = tSecondPart;
	}
	while (i_11_ < bi.getMaxLocals())
	    verifyinfo.locals[i_11_++] = tNone;
	return verifyinfo;
    }
    
    public boolean mergeInfo
	(Instruction instruction, VerifyInfo verifyinfo)
	throws VerifyException {
	if (instruction.getTmpInfo() == null) {
	    instruction.setTmpInfo(verifyinfo);
	    return true;
	}
	boolean bool = false;
	VerifyInfo verifyinfo_13_ = (VerifyInfo) instruction.getTmpInfo();
	if (verifyinfo_13_.stackHeight != verifyinfo.stackHeight)
	    throw new VerifyException("Stack height differ at: "
				      + instruction.getDescription());
	for (int i = 0; i < verifyinfo_13_.stackHeight; i++) {
	    Type type = verifyinfo_13_.stack[i].mergeType(verifyinfo.stack[i]);
	    if (!type.equals(verifyinfo_13_.stack[i])) {
		if (type == tNone)
		    throw new VerifyException("Type error while merging: "
					      + verifyinfo_13_.stack[i]
					      + " and " + verifyinfo.stack[i]);
		bool = true;
		verifyinfo_13_.stack[i] = type;
	    }
	}
	for (int i = 0; i < bi.getMaxLocals(); i++) {
	    Type type
		= verifyinfo_13_.locals[i].mergeType(verifyinfo.locals[i]);
	    if (!type.equals(verifyinfo_13_.locals[i])) {
		bool = true;
		verifyinfo_13_.locals[i] = type;
	    }
	}
	if (verifyinfo_13_.jsrTargets != null) {
	    int i;
	    if (verifyinfo.jsrTargets == null)
		i = 0;
	    else {
		i = verifyinfo.jsrTargets.length;
		int i_14_ = 0;
		for (int i_15_ = 0; i_15_ < verifyinfo_13_.jsrTargets.length;
		     i_15_++) {
		    for (int i_16_ = i_14_; i_16_ < i; i_16_++) {
			if (verifyinfo_13_.jsrTargets[i_15_]
			    == verifyinfo.jsrTargets[i_16_]) {
			    System.arraycopy(verifyinfo.jsrTargets, i_16_,
					     verifyinfo.jsrTargets, i_14_,
					     i - i_16_);
			    i -= i_16_ - i_14_;
			    i_14_++;
			    break;
			}
		    }
		}
		i = i_14_;
	    }
	    if (i != verifyinfo_13_.jsrTargets.length) {
		if (i == 0)
		    verifyinfo_13_.jsrTargets = null;
		else {
		    verifyinfo_13_.jsrTargets = new Instruction[i];
		    System.arraycopy(verifyinfo.jsrTargets, 0,
				     verifyinfo_13_.jsrTargets, 0, i);
		}
		bool = true;
	    }
	}
	return bool;
    }
    
    public VerifyInfo modelEffect
	(Instruction instruction, VerifyInfo verifyinfo)
	throws VerifyException {
	int i
	    = verifyinfo.jsrTargets != null ? verifyinfo.jsrTargets.length : 0;
	VerifyInfo verifyinfo_17_ = (VerifyInfo) verifyinfo.clone();
	int i_18_ = instruction.getOpcode();
	switch (i_18_) {
	case 0:
	case 167:
	    break;
	case 18: {
	    Object object = instruction.getConstant();
	    Type type;
	    if (object == null)
		type = tNull;
	    else if (object instanceof Integer)
		type = tInt;
	    else if (object instanceof Float)
		type = tFloat;
	    else
		type = tString;
	    verifyinfo_17_.push(type);
	    break;
	}
	case 20: {
	    Object object = instruction.getConstant();
	    Type type;
	    if (object instanceof Long)
		type = tLong;
	    else
		type = tDouble;
	    verifyinfo_17_.push(type);
	    verifyinfo_17_.push(tSecondPart);
	    break;
	}
	case 21:
	case 22:
	case 23:
	case 24:
	case 25: {
	    if (i > 0 && (!verifyinfo_17_.jsrLocals[i - 1]
			       .get(instruction.getLocalSlot())
			  || ((i_18_ & 0x1) == 0
			      && !verifyinfo_17_.jsrLocals[i - 1]
				      .get(instruction.getLocalSlot() + 1)))) {
		verifyinfo_17_.jsrLocals
		    = (BitSet[]) verifyinfo_17_.jsrLocals.clone();
		verifyinfo_17_.jsrLocals[i - 1]
		    = (BitSet) verifyinfo_17_.jsrLocals[i - 1].clone();
		verifyinfo_17_.jsrLocals[i - 1]
		    .set(instruction.getLocalSlot());
		if ((i_18_ & 0x1) == 0)
		    verifyinfo_17_.jsrLocals[i - 1]
			.set(instruction.getLocalSlot() + 1);
	    }
	    if ((i_18_ & 0x1) == 0
		&& (verifyinfo_17_.locals[instruction.getLocalSlot() + 1]
		    != tSecondPart))
		throw new VerifyException(instruction.getDescription());
	    Type type = verifyinfo_17_.locals[instruction.getLocalSlot()];
	    if (!type.isOfType(types[i_18_ - 21]))
		throw new VerifyException(instruction.getDescription());
	    verifyinfo_17_.push(type);
	    if ((i_18_ & 0x1) == 0)
		verifyinfo_17_.push(tSecondPart);
	    break;
	}
	case 46:
	case 47:
	case 48:
	case 49:
	case 50:
	case 51:
	case 52:
	case 53: {
	    if (!verifyinfo_17_.pop().isOfType("I"))
		throw new VerifyException(instruction.getDescription());
	    Type type = verifyinfo_17_.pop();
	    if (!type.isOfType(arrayTypes[i_18_ - 46])
		&& (i_18_ != 51 || !type.isOfType("[Z")))
		throw new VerifyException(instruction.getDescription());
	    String string = type.getTypeSig();
	    Type type_19_
		= (string.charAt(0) == '[' ? Type.tType(string.substring(1))
		   : i_18_ == 50 ? tNull : Type.tType(types[i_18_ - 46]));
	    verifyinfo_17_.push(type_19_);
	    if ((1 << i_18_ - 46 & 0xa) != 0)
		verifyinfo_17_.push(tSecondPart);
	    break;
	}
	case 54:
	case 55:
	case 56:
	case 57:
	case 58: {
	    if (i > 0 && (!verifyinfo_17_.jsrLocals[i - 1]
			       .get(instruction.getLocalSlot())
			  || ((i_18_ & 0x1) != 0
			      && !verifyinfo_17_.jsrLocals[i - 1]
				      .get(instruction.getLocalSlot() + 1)))) {
		verifyinfo_17_.jsrLocals
		    = (BitSet[]) verifyinfo_17_.jsrLocals.clone();
		verifyinfo_17_.jsrLocals[i - 1]
		    = (BitSet) verifyinfo_17_.jsrLocals[i - 1].clone();
		verifyinfo_17_.jsrLocals[i - 1]
		    .set(instruction.getLocalSlot());
		if ((i_18_ & 0x1) != 0)
		    verifyinfo_17_.jsrLocals[i - 1]
			.set(instruction.getLocalSlot() + 1);
	    }
	    if ((i_18_ & 0x1) != 0 && verifyinfo_17_.pop() != tSecondPart)
		throw new VerifyException(instruction.getDescription());
	    Type type = verifyinfo_17_.pop();
	    if (!type.isOfType(types[i_18_ - 54])
		&& (i_18_ != 58 || !type.isOfType("R")))
		throw new VerifyException(instruction.getDescription());
	    verifyinfo_17_.locals[instruction.getLocalSlot()] = type;
	    if ((i_18_ & 0x1) != 0)
		verifyinfo_17_.locals[instruction.getLocalSlot() + 1]
		    = tSecondPart;
	    break;
	}
	case 79:
	case 80:
	case 81:
	case 82:
	case 83:
	case 84:
	case 85:
	case 86: {
	    if ((1 << i_18_ - 79 & 0xa) != 0
		&& verifyinfo_17_.pop() != tSecondPart)
		throw new VerifyException(instruction.getDescription());
	    Type type = verifyinfo_17_.pop();
	    if (!verifyinfo_17_.pop().isOfType("I"))
		throw new VerifyException(instruction.getDescription());
	    Type type_20_ = verifyinfo_17_.pop();
	    if (!type_20_.isOfType(arrayTypes[i_18_ - 79])
		&& (i_18_ != 84 || !type_20_.isOfType("[Z")))
		throw new VerifyException(instruction.getDescription());
	    String string = i_18_ >= 84 ? "I" : types[i_18_ - 79];
	    if (!type.isOfType(string))
		throw new VerifyException(instruction.getDescription());
	    break;
	}
	case 87:
	case 88: {
	    int i_21_ = i_18_ - 86;
	    verifyinfo_17_.need(i_21_);
	    verifyinfo_17_.stackHeight -= i_21_;
	    break;
	}
	case 89:
	case 90:
	case 91: {
	    int i_22_ = i_18_ - 89;
	    verifyinfo_17_.reserve(1);
	    verifyinfo_17_.need(i_22_ + 1);
	    if (verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 1]
		== tSecondPart)
		throw new VerifyException(instruction.getDescription());
	    int i_23_ = verifyinfo_17_.stackHeight - (i_22_ + 1);
	    if (verifyinfo_17_.stack[i_23_] == tSecondPart)
		throw new VerifyException(instruction.getDescription()
					  + " on long or double");
	    for (int i_24_ = verifyinfo_17_.stackHeight; i_24_ > i_23_;
		 i_24_--)
		verifyinfo_17_.stack[i_24_] = verifyinfo_17_.stack[i_24_ - 1];
	    verifyinfo_17_.stack[i_23_]
		= verifyinfo_17_.stack[verifyinfo_17_.stackHeight++];
	    break;
	}
	case 92:
	case 93:
	case 94: {
	    int i_25_ = i_18_ - 92;
	    verifyinfo_17_.reserve(2);
	    verifyinfo_17_.need(i_25_ + 2);
	    if (verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 2]
		== tSecondPart)
		throw new VerifyException(instruction.getDescription()
					  + " on misaligned long or double");
	    int i_26_ = verifyinfo_17_.stackHeight;
	    int i_27_ = i_26_ - (i_25_ + 2);
	    if (verifyinfo_17_.stack[i_27_] == tSecondPart)
		throw new VerifyException(instruction.getDescription()
					  + " on long or double");
	    for (int i_28_ = i_26_; i_28_ > i_27_; i_28_--)
		verifyinfo_17_.stack[i_28_ + 1]
		    = verifyinfo_17_.stack[i_28_ - 1];
	    verifyinfo_17_.stack[i_27_ + 1] = verifyinfo_17_.stack[i_26_ + 1];
	    verifyinfo_17_.stack[i_27_] = verifyinfo_17_.stack[i_26_];
	    verifyinfo_17_.stackHeight += 2;
	    break;
	}
	case 95: {
	    verifyinfo_17_.need(2);
	    if ((verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 2]
		 == tSecondPart)
		|| (verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 1]
		    == tSecondPart))
		throw new VerifyException(instruction.getDescription()
					  + " on misaligned long or double");
	    Type type = verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 1];
	    verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 1]
		= verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 2];
	    verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 2] = type;
	    break;
	}
	case 96:
	case 97:
	case 98:
	case 99:
	case 100:
	case 101:
	case 102:
	case 103:
	case 104:
	case 105:
	case 106:
	case 107:
	case 108:
	case 109:
	case 110:
	case 111:
	case 112:
	case 113:
	case 114:
	case 115: {
	    String string = types[i_18_ - 96 & 0x3];
	    if ((i_18_ & 0x1) != 0 && verifyinfo_17_.pop() != tSecondPart)
		throw new VerifyException(instruction.getDescription());
	    if (!verifyinfo_17_.pop().isOfType(string))
		throw new VerifyException(instruction.getDescription());
	    if ((i_18_ & 0x1) != 0) {
		verifyinfo_17_.need(2);
		if ((verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 1]
		     != tSecondPart)
		    || !verifyinfo_17_.stack
			    [verifyinfo_17_.stackHeight - 2].isOfType(string))
		    throw new VerifyException(instruction.getDescription());
	    } else {
		verifyinfo_17_.need(1);
		if (!verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 1]
			 .isOfType(string))
		    throw new VerifyException(instruction.getDescription());
	    }
	    break;
	}
	case 116:
	case 117:
	case 118:
	case 119: {
	    String string = types[i_18_ - 116 & 0x3];
	    if ((i_18_ & 0x1) != 0) {
		verifyinfo_17_.need(2);
		if ((verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 1]
		     != tSecondPart)
		    || !verifyinfo_17_.stack
			    [verifyinfo_17_.stackHeight - 2].isOfType(string))
		    throw new VerifyException(instruction.getDescription());
	    } else {
		verifyinfo_17_.need(1);
		if (!verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 1]
			 .isOfType(string))
		    throw new VerifyException(instruction.getDescription());
	    }
	    break;
	}
	case 120:
	case 121:
	case 122:
	case 123:
	case 124:
	case 125:
	    if (!verifyinfo_17_.pop().isOfType("I"))
		throw new VerifyException(instruction.getDescription());
	    if ((i_18_ & 0x1) != 0) {
		verifyinfo_17_.need(2);
		if ((verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 1]
		     != tSecondPart)
		    || !verifyinfo_17_.stack
			    [verifyinfo_17_.stackHeight - 2].isOfType("J"))
		    throw new VerifyException(instruction.getDescription());
	    } else {
		verifyinfo_17_.need(1);
		if (!verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 1]
			 .isOfType("I"))
		    throw new VerifyException(instruction.getDescription());
	    }
	    break;
	case 126:
	case 127:
	case 128:
	case 129:
	case 130:
	case 131:
	    if ((i_18_ & 0x1) != 0 && verifyinfo_17_.pop() != tSecondPart)
		throw new VerifyException(instruction.getDescription());
	    if (!verifyinfo_17_.pop().isOfType(types[i_18_ & 0x1]))
		throw new VerifyException(instruction.getDescription());
	    if ((i_18_ & 0x1) != 0) {
		verifyinfo_17_.need(2);
		if ((verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 1]
		     != tSecondPart)
		    || !verifyinfo_17_.stack
			    [verifyinfo_17_.stackHeight - 2].isOfType("J"))
		    throw new VerifyException(instruction.getDescription());
	    } else {
		verifyinfo_17_.need(1);
		if (!verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 1]
			 .isOfType("I"))
		    throw new VerifyException(instruction.getDescription());
	    }
	    break;
	case 132:
	    if (!verifyinfo_17_.locals[instruction.getLocalSlot()]
		     .isOfType("I"))
		throw new VerifyException(instruction.getDescription());
	    break;
	case 133:
	case 134:
	case 135:
	case 136:
	case 137:
	case 138:
	case 139:
	case 140:
	case 141:
	case 142:
	case 143:
	case 144: {
	    int i_29_ = (i_18_ - 133) / 3;
	    int i_30_ = (i_18_ - 133) % 3;
	    if (i_30_ >= i_29_)
		i_30_++;
	    if ((i_29_ & 0x1) != 0 && verifyinfo_17_.pop() != tSecondPart)
		throw new VerifyException(instruction.getDescription());
	    if (!verifyinfo_17_.pop().isOfType(types[i_29_]))
		throw new VerifyException(instruction.getDescription());
	    verifyinfo_17_.push(Type.tType(types[i_30_]));
	    if ((i_30_ & 0x1) != 0)
		verifyinfo_17_.push(tSecondPart);
	    break;
	}
	case 145:
	case 146:
	case 147:
	    verifyinfo_17_.need(1);
	    if (!verifyinfo_17_.stack[verifyinfo_17_.stackHeight - 1]
		     .isOfType("I"))
		throw new VerifyException(instruction.getDescription());
	    break;
	case 148:
	    if (verifyinfo_17_.pop() != tSecondPart)
		throw new VerifyException(instruction.getDescription());
	    if (!verifyinfo_17_.pop().isOfType("J"))
		throw new VerifyException(instruction.getDescription());
	    if (verifyinfo_17_.pop() != tSecondPart)
		throw new VerifyException(instruction.getDescription());
	    if (!verifyinfo_17_.pop().isOfType("J"))
		throw new VerifyException(instruction.getDescription());
	    verifyinfo_17_.push(tInt);
	    break;
	case 151:
	case 152:
	    if (verifyinfo_17_.pop() != tSecondPart)
		throw new VerifyException(instruction.getDescription());
	    if (!verifyinfo_17_.pop().isOfType("D"))
		throw new VerifyException(instruction.getDescription());
	    if (verifyinfo_17_.pop() != tSecondPart)
		throw new VerifyException(instruction.getDescription());
	    if (!verifyinfo_17_.pop().isOfType("D"))
		throw new VerifyException(instruction.getDescription());
	    verifyinfo_17_.push(tInt);
	    break;
	case 149:
	case 150:
	    if (!verifyinfo_17_.pop().isOfType("F"))
		throw new VerifyException(instruction.getDescription());
	    if (!verifyinfo_17_.pop().isOfType("F"))
		throw new VerifyException(instruction.getDescription());
	    verifyinfo_17_.push(tInt);
	    break;
	case 153:
	case 154:
	case 155:
	case 156:
	case 157:
	case 158:
	case 170:
	case 171:
	    if (!verifyinfo_17_.pop().isOfType("I"))
		throw new VerifyException(instruction.getDescription());
	    break;
	case 159:
	case 160:
	case 161:
	case 162:
	case 163:
	case 164:
	    if (!verifyinfo_17_.pop().isOfType("I"))
		throw new VerifyException(instruction.getDescription());
	    if (!verifyinfo_17_.pop().isOfType("I"))
		throw new VerifyException(instruction.getDescription());
	    break;
	case 165:
	case 166:
	    if (!verifyinfo_17_.pop().isOfType("+"))
		throw new VerifyException(instruction.getDescription());
	    if (!verifyinfo_17_.pop().isOfType("+"))
		throw new VerifyException(instruction.getDescription());
	    break;
	case 198:
	case 199:
	    if (!verifyinfo_17_.pop().isOfType("+"))
		throw new VerifyException(instruction.getDescription());
	    break;
	case 172:
	case 173:
	case 174:
	case 175:
	case 176: {
	    if ((1 << i_18_ - 172 & 0xa) != 0
		&& verifyinfo_17_.pop() != tSecondPart)
		throw new VerifyException(instruction.getDescription());
	    Type type = verifyinfo_17_.pop();
	    if (!type.isOfType(types[i_18_ - 172])
		|| !type.isOfType(TypeSignature.getReturnType(methodType)))
		throw new VerifyException(instruction.getDescription());
	    break;
	}
	case 168: {
	    Instruction instruction_31_ = instruction.getSingleSucc();
	    verifyinfo_17_.stack[verifyinfo_17_.stackHeight++]
		= Type.tType("R", instruction_31_);
	    verifyinfo_17_.jsrTargets = new Instruction[i + 1];
	    verifyinfo_17_.jsrLocals = new BitSet[i + 1];
	    if (i > 0) {
		for (int i_32_ = 0; i_32_ < verifyinfo.jsrTargets.length;
		     i_32_++) {
		    if (verifyinfo.jsrTargets[i_32_]
			== instruction.getSingleSucc())
			throw new VerifyException(instruction.getDescription()
						  + " is recursive");
		}
		System.arraycopy(verifyinfo.jsrTargets, 0,
				 verifyinfo_17_.jsrTargets, 0, i);
		System.arraycopy(verifyinfo.jsrLocals, 0,
				 verifyinfo_17_.jsrLocals, 0, i);
	    }
	    verifyinfo_17_.jsrTargets[i] = instruction.getSingleSucc();
	    verifyinfo_17_.jsrLocals[i] = new BitSet();
	    break;
	}
	case 177:
	    if (!returnType.equals("V"))
		throw new VerifyException(instruction.getDescription());
	    break;
	case 178: {
	    Reference reference = instruction.getReference();
	    String string = reference.getType();
	    verifyinfo_17_.push(Type.tType(string));
	    if (TypeSignature.getTypeSize(string) == 2)
		verifyinfo_17_.push(tSecondPart);
	    break;
	}
	case 180: {
	    Reference reference = instruction.getReference();
	    String string = reference.getClazz();
	    if (!verifyinfo_17_.pop().isOfType(string))
		throw new VerifyException(instruction.getDescription());
	    String string_33_ = reference.getType();
	    verifyinfo_17_.push(Type.tType(string_33_));
	    if (TypeSignature.getTypeSize(string_33_) == 2)
		verifyinfo_17_.push(tSecondPart);
	    break;
	}
	case 179: {
	    Reference reference = instruction.getReference();
	    String string = reference.getType();
	    if (TypeSignature.getTypeSize(string) == 2
		&& verifyinfo_17_.pop() != tSecondPart)
		throw new VerifyException(instruction.getDescription());
	    if (!verifyinfo_17_.pop().isOfType(string))
		throw new VerifyException(instruction.getDescription());
	    break;
	}
	case 181: {
	    Reference reference = instruction.getReference();
	    String string = reference.getType();
	    if (TypeSignature.getTypeSize(string) == 2
		&& verifyinfo_17_.pop() != tSecondPart)
		throw new VerifyException(instruction.getDescription());
	    if (!verifyinfo_17_.pop().isOfType(string))
		throw new VerifyException(instruction.getDescription());
	    String string_34_ = reference.getClazz();
	    if (!verifyinfo_17_.pop().isOfType(string_34_))
		throw new VerifyException(instruction.getDescription());
	    break;
	}
	case 182:
	case 183:
	case 184:
	case 185: {
	    Reference reference = instruction.getReference();
	    String string = reference.getType();
	    String[] strings = TypeSignature.getParameterTypes(string);
	    for (int i_35_ = strings.length - 1; i_35_ >= 0; i_35_--) {
		if (TypeSignature.getTypeSize(strings[i_35_]) == 2
		    && verifyinfo_17_.pop() != tSecondPart)
		    throw new VerifyException(instruction.getDescription());
		if (!verifyinfo_17_.pop().isOfType(strings[i_35_]))
		    throw new VerifyException(instruction.getDescription());
	    }
	    if (reference.getName().equals("<init>")) {
		Type type = verifyinfo_17_.pop();
		String string_36_ = type.getTypeSig();
		String string_37_ = reference.getClazz();
		if (i_18_ != 183 || string_36_.charAt(0) != 'N'
		    || string_37_.charAt(0) != 'L')
		    throw new VerifyException(instruction.getDescription());
		if (!string_36_.substring(1).equals(string_37_.substring(1))) {
		    ClassInfo classinfo
			= ClassInfo.forName(string_36_.substring
						(1, string_36_.length() - 1)
						.replace('/', '.'));
		    if ((classinfo.getSuperclass()
			 != TypeSignature.getClassInfo(string_37_))
			|| type.getInstruction() != null)
			throw new VerifyException(instruction
						      .getDescription());
		}
		Type type_38_ = Type.tType("L" + string_36_.substring(1));
		for (int i_39_ = 0; i_39_ < verifyinfo_17_.stackHeight;
		     i_39_++) {
		    if (verifyinfo_17_.stack[i_39_] == type)
			verifyinfo_17_.stack[i_39_] = type_38_;
		}
		for (int i_40_ = 0; i_40_ < verifyinfo_17_.locals.length;
		     i_40_++) {
		    if (verifyinfo_17_.locals[i_40_] == type)
			verifyinfo_17_.locals[i_40_] = type_38_;
		}
	    } else if (i_18_ != 184) {
		String string_41_ = reference.getClazz();
		if (!verifyinfo_17_.pop().isOfType(string_41_))
		    throw new VerifyException(instruction.getDescription());
	    }
	    String string_42_ = TypeSignature.getReturnType(string);
	    if (!string_42_.equals("V")) {
		verifyinfo_17_.push(Type.tType(string_42_));
		if (TypeSignature.getTypeSize(string_42_) == 2)
		    verifyinfo_17_.push(tSecondPart);
	    }
	    break;
	}
	case 187: {
	    String string = instruction.getClazzType();
	    verifyinfo_17_.stack[verifyinfo_17_.stackHeight++]
		= Type.tType("N" + string.substring(1), instruction);
	    break;
	}
	case 190:
	    if (!verifyinfo_17_.pop().isOfType("[*"))
		throw new VerifyException(instruction.getDescription());
	    verifyinfo_17_.push(tInt);
	    break;
	case 191:
	    if (!verifyinfo_17_.pop().isOfType("Ljava/lang/Throwable;"))
		throw new VerifyException(instruction.getDescription());
	    break;
	case 192: {
	    String string = instruction.getClazzType();
	    if (!verifyinfo_17_.pop().isOfType("+"))
		throw new VerifyException(instruction.getDescription());
	    verifyinfo_17_.push(Type.tType(string));
	    break;
	}
	case 193:
	    if (!verifyinfo_17_.pop().isOfType("Ljava/lang/Object;"))
		throw new VerifyException(instruction.getDescription());
	    verifyinfo_17_.push(tInt);
	    break;
	case 194:
	case 195:
	    if (!verifyinfo_17_.pop().isOfType("Ljava/lang/Object;"))
		throw new VerifyException(instruction.getDescription());
	    break;
	case 197: {
	    int i_43_ = instruction.getDimensions();
	    for (int i_44_ = i_43_ - 1; i_44_ >= 0; i_44_--) {
		if (!verifyinfo_17_.pop().isOfType("I"))
		    throw new VerifyException(instruction.getDescription());
	    }
	    String string = instruction.getClazzType();
	    verifyinfo_17_.push(Type.tType(string));
	    break;
	}
	default:
	    throw new AssertError("Invalid opcode " + i_18_);
	}
	return verifyinfo_17_;
    }
    
    public void doVerify() throws VerifyException {
	HashSet hashset = new HashSet();
	Instruction instruction = (Instruction) bi.getInstructions().get(0);
	instruction.setTmpInfo(initInfo());
	hashset.add(instruction);
	Handler[] handlers = bi.getExceptionHandlers();
	while (!hashset.isEmpty()) {
	    Iterator iterator = hashset.iterator();
	    Instruction instruction_45_ = (Instruction) iterator.next();
	    iterator.remove();
	    if (!instruction_45_.doesAlwaysJump()
		&& instruction_45_.getNextByAddr() == null)
		throw new VerifyException("Flow can fall off end of method");
	    VerifyInfo verifyinfo = (VerifyInfo) instruction_45_.getTmpInfo();
	    int i = instruction_45_.getOpcode();
	    if (i == 169) {
		Type type = verifyinfo.locals[instruction_45_.getLocalSlot()];
		if (verifyinfo.jsrTargets == null || !type.isOfType("R"))
		    throw new VerifyException(instruction_45_
						  .getDescription());
		int i_46_ = verifyinfo.jsrTargets.length - 1;
		Instruction instruction_47_ = type.getInstruction();
		while (instruction_47_ != verifyinfo.jsrTargets[i_46_]) {
		    if (--i_46_ < 0)
			throw new VerifyException(instruction_45_
						      .getDescription());
		}
		VerifyInfo verifyinfo_48_
		    = (VerifyInfo) instruction_47_.getTmpInfo();
		if (verifyinfo_48_.retInstr == null)
		    verifyinfo_48_.retInstr = instruction_45_;
		else if (verifyinfo_48_.retInstr != instruction_45_)
		    throw new VerifyException
			      ("JsrTarget has more than one ret: "
			       + instruction_47_.getDescription());
		if (i_46_ > 0) {
		    Instruction[] instructions = new Instruction[i_46_];
		    BitSet[] bitsets = new BitSet[i_46_];
		    System.arraycopy(verifyinfo.jsrTargets, 0, instructions, 0,
				     i_46_);
		    System.arraycopy(verifyinfo.jsrLocals, 0, bitsets, 0,
				     i_46_);
		} else {
		    Object object = null;
		    Object object_49_ = null;
		}
		for (int i_50_ = 0; i_50_ < instruction_47_.getPreds().length;
		     i_50_++) {
		    Instruction instruction_51_
			= instruction_47_.getPreds()[i_50_];
		    if (instruction_51_.getTmpInfo() != null)
			hashset.add(instruction_51_);
		}
	    } else {
		VerifyInfo verifyinfo_52_
		    = modelEffect(instruction_45_, verifyinfo);
		if (!instruction_45_.doesAlwaysJump()
		    && mergeInfo(instruction_45_.getNextByAddr(),
				 verifyinfo_52_))
		    hashset.add(instruction_45_.getNextByAddr());
		if (i == 168) {
		    VerifyInfo verifyinfo_53_
			= ((VerifyInfo)
			   instruction_45_.getSingleSucc().getTmpInfo());
		    if (verifyinfo_53_ != null
			&& verifyinfo_53_.retInstr != null) {
			VerifyInfo verifyinfo_54_
			    = (VerifyInfo) verifyinfo.clone();
			VerifyInfo verifyinfo_55_
			    = ((VerifyInfo)
			       verifyinfo_53_.retInstr.getTmpInfo());
			BitSet bitset
			    = (verifyinfo_55_.jsrLocals
			       [verifyinfo_55_.jsrLocals.length - 1]);
			for (int i_56_ = 0; i_56_ < bi.getMaxLocals();
			     i_56_++) {
			    if (bitset.get(i_56_))
				verifyinfo_54_.locals[i_56_]
				    = verifyinfo_55_.locals[i_56_];
			}
			if (mergeInfo(instruction_45_.getNextByAddr(),
				      verifyinfo_54_))
			    hashset.add(instruction_45_.getNextByAddr());
		    }
		}
		if (instruction_45_.getSuccs() != null) {
		    for (int i_57_ = 0;
			 i_57_ < instruction_45_.getSuccs().length; i_57_++) {
			if (mergeInfo(instruction_45_.getSuccs()[i_57_],
				      (VerifyInfo) verifyinfo_52_.clone()))
			    hashset.add(instruction_45_.getSuccs()[i_57_]);
		    }
		}
		for (int i_58_ = 0; i_58_ < handlers.length; i_58_++) {
		    if (handlers[i_58_].start.compareTo(instruction_45_) <= 0
			&& (handlers[i_58_].end.compareTo(instruction_45_)
			    >= 0)) {
			VerifyInfo verifyinfo_59_
			    = (VerifyInfo) verifyinfo.clone();
			verifyinfo_59_.stackHeight = 1;
			if (handlers[i_58_].type != null)
			    verifyinfo_59_.stack[0]
				= Type.tType("L"
					     + handlers[i_58_].type
						   .replace('.', '/')
					     + ";");
			else
			    verifyinfo_59_.stack[0]
				= Type.tType("Ljava/lang/Throwable;");
			if (mergeInfo(handlers[i_58_].catcher, verifyinfo_59_))
			    hashset.add(handlers[i_58_].catcher);
		    }
		}
	    }
	}
	if ((GlobalOptions.debuggingFlags & 0x2) != 0) {
	    Iterator iterator = bi.getInstructions().iterator();
	    while (iterator.hasNext()) {
		Instruction instruction_60_ = (Instruction) iterator.next();
		VerifyInfo verifyinfo
		    = (VerifyInfo) instruction_60_.getTmpInfo();
		if (verifyinfo != null)
		    GlobalOptions.err.println(verifyinfo.toString());
		GlobalOptions.err.println(instruction_60_.getDescription());
	    }
	}
	Iterator iterator = bi.getInstructions().iterator();
	while (iterator.hasNext()) {
	    Instruction instruction_61_ = (Instruction) iterator.next();
	    instruction_61_.setTmpInfo(null);
	}
    }
    
    public void verify() throws VerifyException {
	try {
	    doVerify();
	} catch (VerifyException verifyexception) {
	    Iterator iterator = bi.getInstructions().iterator();
	    while (iterator.hasNext()) {
		Instruction instruction = (Instruction) iterator.next();
		VerifyInfo verifyinfo = (VerifyInfo) instruction.getTmpInfo();
		if (verifyinfo != null)
		    GlobalOptions.err.println(verifyinfo.toString());
		GlobalOptions.err.println(instruction.getDescription());
		instruction.setTmpInfo(null);
	    }
	    throw verifyexception;
	}
    }
}
