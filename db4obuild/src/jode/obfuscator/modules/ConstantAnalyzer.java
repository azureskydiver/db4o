/* ConstantAnalyzer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator.modules;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.MethodInfo;
import jode.bytecode.Opcodes;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.jvm.InterpreterException;
import jode.obfuscator.CodeAnalyzer;
import jode.obfuscator.ConstantRuntimeEnvironment;
import jode.obfuscator.FieldIdentifier;
import jode.obfuscator.Identifier;
import jode.obfuscator.Main;
import jode.obfuscator.MethodIdentifier;

public class ConstantAnalyzer extends SimpleAnalyzer
    implements Opcodes, CodeAnalyzer
{
    BytecodeInfo bytecode;
    private static ConstantRuntimeEnvironment runtime
	= new ConstantRuntimeEnvironment();
    private static final int CMP_EQ = 0;
    private static final int CMP_NE = 1;
    private static final int CMP_LT = 2;
    private static final int CMP_GE = 3;
    private static final int CMP_GT = 4;
    private static final int CMP_LE = 5;
    private static final int CMP_GREATER_MASK = 26;
    private static final int CMP_LESS_MASK = 38;
    private static final int CMP_EQUAL_MASK = 41;
    static final int CONSTANT = 2;
    static final int CONSTANTFLOW = 4;
    static final int RETASTORE = 8;
    static final int RETURNINGJSR = 16;
    private static ConstValue[] unknownValue
	= { new ConstValue(1), new ConstValue(2) };
    private static ConstantInfo unknownConstInfo = new ConstantInfo();
    
    private static class ConstantInfo implements ConstantListener
    {
	int flags;
	Object constant;
	
	ConstantInfo() {
	    this(0, null);
	}
	
	ConstantInfo(int i) {
	    this(i, null);
	}
	
	ConstantInfo(int i, Object object) {
	    flags = i;
	    constant = object;
	}
	
	public void constantChanged() {
	    constant = null;
	    flags &= ~0x6;
	}
    }
    
    private static class StackLocalInfo implements ConstantListener
    {
	ConstValue[] stack;
	ConstValue[] locals;
	Instruction instr;
	ConstantInfo constInfo;
	StackLocalInfo retInfo;
	StackLocalInfo nextOnQueue;
	TodoQueue notifyQueue;
	
	public ConstValue copy(ConstValue constvalue) {
	    return constvalue == null ? null : constvalue.copy();
	}
	
	private StackLocalInfo(ConstValue[] constvalues,
			       ConstValue[] constvalues_0_,
			       TodoQueue todoqueue) {
	    stack = constvalues;
	    locals = new ConstValue[constvalues_0_.length];
	    for (int i = 0; i < constvalues_0_.length; i++)
		locals[i] = copy(constvalues_0_[i]);
	    notifyQueue = todoqueue;
	}
	
	public StackLocalInfo(int i, boolean bool, String string,
			      TodoQueue todoqueue) {
	    String[] strings = TypeSignature.getParameterTypes(string);
	    locals = new ConstValue[i];
	    stack = new ConstValue[0];
	    notifyQueue = todoqueue;
	    int i_1_ = 0;
	    if (!bool)
		locals[i_1_++] = new ConstValue(1);
	    for (int i_2_ = 0; i_2_ < strings.length; i_2_++) {
		int i_3_ = TypeSignature.getTypeSize(strings[i_2_]);
		locals[i_1_] = ConstantAnalyzer.unknownValue[i_3_ - 1];
		i_1_ += i_3_;
	    }
	}
	
	public final void enqueue() {
	    if (nextOnQueue == null) {
		nextOnQueue = notifyQueue.first;
		notifyQueue.first = this;
	    }
	}
	
	public void constantChanged() {
	    enqueue();
	}
	
	public StackLocalInfo poppush(int i, ConstValue constvalue) {
	    ConstValue[] constvalues
		= new ConstValue[stack.length - i + constvalue.stackSize];
	    ConstValue[] constvalues_4_ = (ConstValue[]) locals.clone();
	    System.arraycopy(stack, 0, constvalues, 0, stack.length - i);
	    constvalues[stack.length - i] = constvalue.copy();
	    return new StackLocalInfo(constvalues, constvalues_4_,
				      notifyQueue);
	}
	
	public StackLocalInfo pop(int i) {
	    ConstValue[] constvalues = new ConstValue[stack.length - i];
	    ConstValue[] constvalues_5_ = (ConstValue[]) locals.clone();
	    System.arraycopy(stack, 0, constvalues, 0, stack.length - i);
	    return new StackLocalInfo(constvalues, constvalues_5_,
				      notifyQueue);
	}
	
	public StackLocalInfo dup(int i, int i_6_) {
	    ConstValue[] constvalues = new ConstValue[stack.length + i];
	    ConstValue[] constvalues_7_ = (ConstValue[]) locals.clone();
	    if (i_6_ == 0)
		System.arraycopy(stack, 0, constvalues, 0, stack.length);
	    else {
		int i_8_ = stack.length - i - i_6_;
		System.arraycopy(stack, 0, constvalues, 0, i_8_);
		for (int i_9_ = 0; i_9_ < i; i_9_++)
		    constvalues[i_8_++] = copy(stack[stack.length - i + i_9_]);
		for (int i_10_ = 0; i_10_ < i_6_; i_10_++)
		    constvalues[i_8_++]
			= copy(stack[stack.length - i - i_6_ + i_10_]);
	    }
	    for (int i_11_ = 0; i_11_ < i; i_11_++)
		constvalues[stack.length + i_11_]
		    = copy(stack[stack.length - i + i_11_]);
	    return new StackLocalInfo(constvalues, constvalues_7_,
				      notifyQueue);
	}
	
	public StackLocalInfo swap() {
	    ConstValue[] constvalues = new ConstValue[stack.length];
	    ConstValue[] constvalues_12_ = (ConstValue[]) locals.clone();
	    System.arraycopy(stack, 0, constvalues, 0, stack.length - 2);
	    constvalues[stack.length - 2] = stack[stack.length - 1].copy();
	    constvalues[stack.length - 1] = stack[stack.length - 2].copy();
	    return new StackLocalInfo(constvalues, constvalues_12_,
				      notifyQueue);
	}
	
	public StackLocalInfo copy() {
	    ConstValue[] constvalues = (ConstValue[]) stack.clone();
	    ConstValue[] constvalues_13_ = (ConstValue[]) locals.clone();
	    return new StackLocalInfo(constvalues, constvalues_13_,
				      notifyQueue);
	}
	
	public ConstValue getLocal(int i) {
	    return locals[i];
	}
	
	public ConstValue getStack(int i) {
	    return stack[stack.length - i];
	}
	
	public StackLocalInfo setLocal(int i, ConstValue constvalue) {
	    locals[i] = constvalue;
	    if (constvalue != null && constvalue.stackSize == 2)
		locals[i + 1] = null;
	    for (int i_14_ = 0; i_14_ < locals.length; i_14_++) {
		if (locals[i_14_] != null
		    && locals[i_14_].value instanceof JSRTargetInfo) {
		    JSRTargetInfo jsrtargetinfo
			= (JSRTargetInfo) locals[i_14_].value;
		    if (!jsrtargetinfo.uses(i)) {
			jsrtargetinfo = jsrtargetinfo.copy();
			locals[i_14_] = locals[i_14_].copy();
			locals[i_14_].value = jsrtargetinfo;
			jsrtargetinfo.addUsed(i);
		    }
		}
	    }
	    for (int i_15_ = 0; i_15_ < stack.length; i_15_++) {
		if (stack[i_15_] != null
		    && stack[i_15_].value instanceof JSRTargetInfo) {
		    JSRTargetInfo jsrtargetinfo
			= (JSRTargetInfo) stack[i_15_].value;
		    if (!jsrtargetinfo.uses(i)) {
			jsrtargetinfo = jsrtargetinfo.copy();
			stack[i_15_] = stack[i_15_].copy();
			stack[i_15_].value = jsrtargetinfo;
			jsrtargetinfo.addUsed(i);
		    }
		}
	    }
	    return this;
	}
	
	public StackLocalInfo mergeRetLocals
	    (JSRTargetInfo jsrtargetinfo, StackLocalInfo stacklocalinfo_16_) {
	    for (int i = 0; i < locals.length; i++) {
		if (jsrtargetinfo.uses(i))
		    locals[i] = stacklocalinfo_16_.locals[i];
	    }
	    locals[stacklocalinfo_16_.instr.getLocalSlot()] = null;
	    for (int i = 0; i < locals.length; i++) {
		if (locals[i] != null
		    && locals[i].value instanceof JSRTargetInfo) {
		    JSRTargetInfo jsrtargetinfo_17_
			= (JSRTargetInfo) locals[i].value;
		    jsrtargetinfo_17_ = jsrtargetinfo_17_.copy();
		    locals[i] = locals[i].copy();
		    locals[i].value = jsrtargetinfo_17_;
		    for (int i_18_ = 0; i_18_ < locals.length; i_18_++) {
			if (jsrtargetinfo.uses(i_18_))
			    jsrtargetinfo_17_.addUsed(i_18_);
		    }
		}
	    }
	    for (int i = 0; i < stack.length; i++) {
		if (stack[i] != null
		    && stack[i].value instanceof JSRTargetInfo) {
		    JSRTargetInfo jsrtargetinfo_19_
			= (JSRTargetInfo) stack[i].value;
		    jsrtargetinfo_19_ = jsrtargetinfo_19_.copy();
		    stack[i] = stack[i].copy();
		    stack[i].value = jsrtargetinfo_19_;
		    for (int i_20_ = 0; i_20_ < locals.length; i_20_++) {
			if (jsrtargetinfo.uses(i_20_))
			    jsrtargetinfo_19_.addUsed(i_20_);
		    }
		}
	    }
	    return this;
	}
	
	public void merge(StackLocalInfo stacklocalinfo_21_) {
	    for (int i = 0; i < locals.length; i++) {
		if (locals[i] != null) {
		    if (stacklocalinfo_21_.locals[i] == null) {
			locals[i].constantChanged();
			locals[i] = null;
			enqueue();
		    } else
			locals[i].merge(stacklocalinfo_21_.locals[i]);
		}
	    }
	    if (stack.length != stacklocalinfo_21_.stack.length)
		throw new AssertError("stack length differs");
	    for (int i = 0; i < stack.length; i++) {
		if ((stacklocalinfo_21_.stack[i] == null)
		    != (stack[i] == null))
		    throw new AssertError("stack types differ");
		if (stack[i] != null)
		    stack[i].merge(stacklocalinfo_21_.stack[i]);
	    }
	}
	
	public String toString() {
	    return ("Locals: " + Arrays.asList(locals) + "Stack: "
		    + Arrays.asList(stack) + "Instr: " + instr);
	}
    }
    
    private static class TodoQueue
    {
	StackLocalInfo first;
	
	private TodoQueue() {
	    /* empty */
	}
    }
    
    private static class ConstValue implements ConstantListener
    {
	public static final Object VOLATILE = new Object();
	Object value;
	int stackSize;
	Set listeners;
	
	public ConstValue(Object object) {
	    value = object;
	    stackSize
		= object instanceof Double || object instanceof Long ? 2 : 1;
	    listeners = new HashSet();
	}
	
	public ConstValue(ConstValue constvalue_22_) {
	    value = constvalue_22_.value;
	    stackSize = constvalue_22_.stackSize;
	    listeners = new HashSet();
	    constvalue_22_.addConstantListener(this);
	}
	
	public ConstValue(int i) {
	    value = VOLATILE;
	    stackSize = i;
	}
	
	public ConstValue copy() {
	    return value == VOLATILE ? this : new ConstValue(this);
	}
	
	public void addConstantListener(ConstantListener constantlistener) {
	    listeners.add(constantlistener);
	}
	
	public void removeConstantListener(ConstantListener constantlistener) {
	    listeners.remove(constantlistener);
	}
	
	public void fireChanged() {
	    value = VOLATILE;
	    Iterator iterator = listeners.iterator();
	    while (iterator.hasNext())
		((ConstantListener) iterator.next()).constantChanged();
	    listeners = null;
	}
	
	public void constantChanged() {
	    if (value != VOLATILE)
		fireChanged();
	}
	
	public void merge(ConstValue constvalue_23_) {
	    if (this != constvalue_23_) {
		if (value == null ? constvalue_23_.value == null
		    : value.equals(constvalue_23_.value)) {
		    if (value != VOLATILE) {
			constvalue_23_.addConstantListener(this);
			addConstantListener(constvalue_23_);
		    }
		} else if (value instanceof JSRTargetInfo
			   && constvalue_23_.value instanceof JSRTargetInfo
			   && (((JSRTargetInfo) value).jsrTarget
			       == (((JSRTargetInfo) constvalue_23_.value)
				   .jsrTarget)))
		    ((JSRTargetInfo) value)
			.merge((JSRTargetInfo) constvalue_23_.value);
		else if (value != VOLATILE)
		    fireChanged();
	    }
	}
	
	public String toString() {
	    return value == VOLATILE ? "vol(" + stackSize + ")" : "" + value;
	}
    }
    
    private static final class JSRTargetInfo implements Cloneable
    {
	Instruction jsrTarget;
	BitSet usedLocals;
	Object dependent;
	
	public JSRTargetInfo(Instruction instruction) {
	    jsrTarget = instruction;
	    usedLocals = new BitSet();
	}
	
	public JSRTargetInfo copy() {
	    try {
		JSRTargetInfo jsrtargetinfo_24_ = (JSRTargetInfo) this.clone();
		jsrtargetinfo_24_.usedLocals = (BitSet) usedLocals.clone();
		addDependent(jsrtargetinfo_24_);
		return jsrtargetinfo_24_;
	    } catch (CloneNotSupportedException clonenotsupportedexception) {
		throw new IncompatibleClassChangeError
			  (clonenotsupportedexception.getMessage());
	    }
	}
	
	private void addDependent(JSRTargetInfo jsrtargetinfo_25_) {
	    if (dependent == null || dependent == jsrtargetinfo_25_)
		dependent = jsrtargetinfo_25_;
	    else if (dependent instanceof JSRTargetInfo) {
		HashSet hashset = new HashSet();
		hashset.add(dependent);
		hashset.add(jsrtargetinfo_25_);
	    } else if (dependent instanceof Collection)
		((Collection) dependent).add(jsrtargetinfo_25_);
	}
	
	public void setRetInfo(StackLocalInfo stacklocalinfo) {
	    dependent = stacklocalinfo;
	}
	
	public boolean uses(int i) {
	    return usedLocals.get(i);
	}
	
	public void addUsed(int i) {
	    if (!usedLocals.get(i)) {
		usedLocals.set(i);
		if (dependent instanceof StackLocalInfo)
		    ((StackLocalInfo) dependent).enqueue();
		else if (dependent instanceof JSRTargetInfo)
		    ((JSRTargetInfo) dependent).addUsed(i);
		else if (dependent instanceof Collection) {
		    Iterator iterator = ((Collection) dependent).iterator();
		    while (iterator.hasNext()) {
			JSRTargetInfo jsrtargetinfo_26_
			    = (JSRTargetInfo) iterator.next();
			jsrtargetinfo_26_.addUsed(i);
		    }
		}
	    }
	}
	
	public void merge(JSRTargetInfo jsrtargetinfo_27_) {
	    jsrtargetinfo_27_.addDependent(this);
	    for (int i = 0; i < jsrtargetinfo_27_.usedLocals.size(); i++) {
		if (jsrtargetinfo_27_.usedLocals.get(i))
		    addUsed(i);
	    }
	}
	
	public String toString() {
	    StringBuffer stringbuffer
		= new StringBuffer(String.valueOf(jsrTarget));
	    if (dependent instanceof StackLocalInfo)
		stringbuffer.append("->")
		    .append(((StackLocalInfo) dependent).instr);
	    return stringbuffer.append(usedLocals).append('_').append
		       (this.hashCode()).toString();
	}
    }
    
    private static interface ConstantListener
    {
	public void constantChanged();
    }
    
    public void mergeInfo(Instruction instruction,
			  StackLocalInfo stacklocalinfo) {
	if (instruction.getTmpInfo() == null) {
	    instruction.setTmpInfo(stacklocalinfo);
	    stacklocalinfo.instr = instruction;
	    stacklocalinfo.enqueue();
	} else
	    ((StackLocalInfo) instruction.getTmpInfo()).merge(stacklocalinfo);
    }
    
    public void handleReference(Reference reference, boolean bool) {
	Main.getClassBundle().reachableReference(reference, bool);
    }
    
    public void handleClass(String string) {
	int i;
	for (i = 0; i < string.length() && string.charAt(i) == '['; i++) {
	    /* empty */
	}
	if (i < string.length() && string.charAt(i) == 'L') {
	    string = string.substring(i + 1, string.length() - 1);
	    Main.getClassBundle().reachableClass(string);
	}
    }
    
    public void handleOpcode(StackLocalInfo stacklocalinfo,
			     Identifier identifier) {
	Instruction instruction = stacklocalinfo.instr;
	stacklocalinfo.constInfo = unknownConstInfo;
	int i = instruction.getOpcode();
	Handler[] handlers = bytecode.getExceptionHandlers();
	for (int i_28_ = 0; i_28_ < handlers.length; i_28_++) {
	    if (handlers[i_28_].start.getAddr() <= instruction.getAddr()
		&& handlers[i_28_].end.getAddr() >= instruction.getAddr())
		mergeInfo(handlers[i_28_].catcher,
			  stacklocalinfo.poppush(stacklocalinfo.stack.length,
						 unknownValue[0]));
	}
	switch (i) {
	case 0:
	    mergeInfo(instruction.getNextByAddr(), stacklocalinfo.pop(0));
	    break;
	case 18:
	case 20: {
	    ConstValue constvalue = new ConstValue(instruction.getConstant());
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.poppush(0, constvalue));
	    break;
	}
	case 21:
	case 22:
	case 23:
	case 24:
	case 25: {
	    ConstValue constvalue
		= stacklocalinfo.getLocal(instruction.getLocalSlot());
	    if (constvalue == null) {
		dumpStackLocalInfo();
		System.err.println(stacklocalinfo);
		System.err.println(instruction);
	    }
	    if (constvalue.value != ConstValue.VOLATILE) {
		stacklocalinfo.constInfo
		    = new ConstantInfo(2, constvalue.value);
		constvalue.addConstantListener(stacklocalinfo.constInfo);
	    }
	    mergeInfo(instruction.getNextByAddr(),
		      (stacklocalinfo.poppush(0, constvalue).setLocal
		       (instruction.getLocalSlot(), constvalue.copy())));
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
	    ConstValue constvalue = unknownValue[i == 47 || i == 49 ? 1 : 0];
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.poppush(2, constvalue));
	    break;
	}
	case 54:
	case 56:
	case 58: {
	    ConstValue constvalue = stacklocalinfo.getStack(1);
	    if (constvalue.value instanceof JSRTargetInfo)
		stacklocalinfo.constInfo.flags |= 0x8;
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.pop(1)
			  .setLocal(instruction.getLocalSlot(), constvalue));
	    break;
	}
	case 55:
	case 57:
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.pop(2).setLocal(instruction
							 .getLocalSlot(),
						     stacklocalinfo
							 .getStack(2)));
	    break;
	case 79:
	case 80:
	case 81:
	case 82:
	case 83:
	case 84:
	case 85:
	case 86: {
	    int i_29_ = i == 80 || i == 82 ? 2 : 1;
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.pop(2 + i_29_));
	    break;
	}
	case 87:
	    mergeInfo(instruction.getNextByAddr(), stacklocalinfo.pop(1));
	    break;
	case 88:
	    mergeInfo(instruction.getNextByAddr(), stacklocalinfo.pop(2));
	    break;
	case 89:
	case 90:
	case 91:
	case 92:
	case 93:
	case 94:
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.dup((i - 86) / 3, (i - 86) % 3));
	    break;
	case 95:
	    mergeInfo(instruction.getNextByAddr(), stacklocalinfo.swap());
	    break;
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
	case 115:
	case 126:
	case 127:
	case 128:
	case 129:
	case 130:
	case 131: {
	    int i_30_ = 1 + (i - 96 & 0x1);
	    ConstValue constvalue = stacklocalinfo.getStack(2 * i_30_);
	    ConstValue constvalue_31_ = stacklocalinfo.getStack(1 * i_30_);
	    boolean bool = (constvalue.value != ConstValue.VOLATILE
			    && constvalue_31_.value != ConstValue.VOLATILE);
	    if (bool
		&& (((i == 108 || i == 112)
		     && ((Integer) constvalue_31_.value).intValue() == 0)
		    || ((i == 109 || i == 113)
			&& ((Long) constvalue_31_.value).longValue() == 0L)))
		bool = false;
	    ConstValue constvalue_32_;
	    if (bool) {
		Number number;
		switch (i) {
		case 96:
		    number
			= new Integer(((Integer) constvalue.value).intValue()
				      + ((Integer) constvalue_31_.value)
					    .intValue());
		    break;
		case 100:
		    number
			= new Integer(((Integer) constvalue.value).intValue()
				      - ((Integer) constvalue_31_.value)
					    .intValue());
		    break;
		case 104:
		    number
			= new Integer(((Integer) constvalue.value).intValue()
				      * ((Integer) constvalue_31_.value)
					    .intValue());
		    break;
		case 108:
		    number
			= new Integer(((Integer) constvalue.value).intValue()
				      / ((Integer) constvalue_31_.value)
					    .intValue());
		    break;
		case 112:
		    number
			= new Integer(((Integer) constvalue.value).intValue()
				      % ((Integer) constvalue_31_.value)
					    .intValue());
		    break;
		case 126:
		    number
			= new Integer(((Integer) constvalue.value).intValue()
				      & ((Integer) constvalue_31_.value)
					    .intValue());
		    break;
		case 128:
		    number
			= new Integer(((Integer) constvalue.value).intValue()
				      | ((Integer) constvalue_31_.value)
					    .intValue());
		    break;
		case 130:
		    number
			= new Integer(((Integer) constvalue.value).intValue()
				      ^ ((Integer) constvalue_31_.value)
					    .intValue());
		    break;
		case 97:
		    number = new Long(((Long) constvalue.value).longValue()
				      + ((Long) constvalue_31_.value)
					    .longValue());
		    break;
		case 101:
		    number = new Long(((Long) constvalue.value).longValue()
				      - ((Long) constvalue_31_.value)
					    .longValue());
		    break;
		case 105:
		    number = new Long(((Long) constvalue.value).longValue()
				      * ((Long) constvalue_31_.value)
					    .longValue());
		    break;
		case 109:
		    number = new Long(((Long) constvalue.value).longValue()
				      / ((Long) constvalue_31_.value)
					    .longValue());
		    break;
		case 113:
		    number = new Long(((Long) constvalue.value).longValue()
				      % ((Long) constvalue_31_.value)
					    .longValue());
		    break;
		case 127:
		    number = new Long(((Long) constvalue.value).longValue()
				      & ((Long) constvalue_31_.value)
					    .longValue());
		    break;
		case 129:
		    number = new Long(((Long) constvalue.value).longValue()
				      | ((Long) constvalue_31_.value)
					    .longValue());
		    break;
		case 131:
		    number = new Long(((Long) constvalue.value).longValue()
				      ^ ((Long) constvalue_31_.value)
					    .longValue());
		    break;
		case 98:
		    number = new Float(((Float) constvalue.value).floatValue()
				       + ((Float) constvalue_31_.value)
					     .floatValue());
		    break;
		case 102:
		    number = new Float(((Float) constvalue.value).floatValue()
				       - ((Float) constvalue_31_.value)
					     .floatValue());
		    break;
		case 106:
		    number = new Float(((Float) constvalue.value).floatValue()
				       * ((Float) constvalue_31_.value)
					     .floatValue());
		    break;
		case 110:
		    number = new Float(((Float) constvalue.value).floatValue()
				       / ((Float) constvalue_31_.value)
					     .floatValue());
		    break;
		case 114:
		    number = new Float(((Float) constvalue.value).floatValue()
				       % ((Float) constvalue_31_.value)
					     .floatValue());
		    break;
		case 99:
		    number
			= new Double(((Double) constvalue.value).doubleValue()
				     + ((Double) constvalue_31_.value)
					   .doubleValue());
		    break;
		case 103:
		    number
			= new Double(((Double) constvalue.value).doubleValue()
				     - ((Double) constvalue_31_.value)
					   .doubleValue());
		    break;
		case 107:
		    number
			= new Double(((Double) constvalue.value).doubleValue()
				     * ((Double) constvalue_31_.value)
					   .doubleValue());
		    break;
		case 111:
		    number
			= new Double(((Double) constvalue.value).doubleValue()
				     / ((Double) constvalue_31_.value)
					   .doubleValue());
		    break;
		case 115:
		    number
			= new Double(((Double) constvalue.value).doubleValue()
				     % ((Double) constvalue_31_.value)
					   .doubleValue());
		    break;
		default:
		    throw new AssertError("Can't happen.");
		}
		stacklocalinfo.constInfo = new ConstantInfo(2, number);
		constvalue_32_ = new ConstValue(number);
		constvalue_32_.addConstantListener(stacklocalinfo.constInfo);
		constvalue.addConstantListener(constvalue_32_);
		constvalue_31_.addConstantListener(constvalue_32_);
	    } else
		constvalue_32_ = unknownValue[i_30_ - 1];
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.poppush(2 * i_30_, constvalue_32_));
	    break;
	}
	case 116:
	case 117:
	case 118:
	case 119: {
	    int i_33_ = 1 + (i - 116 & 0x1);
	    ConstValue constvalue = stacklocalinfo.getStack(i_33_);
	    ConstValue constvalue_34_;
	    if (constvalue.value != ConstValue.VOLATILE) {
		Number number;
		switch (i) {
		case 116:
		    number = new Integer(-((Integer) constvalue.value)
					      .intValue());
		    break;
		case 117:
		    number = new Long(-((Long) constvalue.value).longValue());
		    break;
		case 118:
		    number
			= new Float(-((Float) constvalue.value).floatValue());
		    break;
		case 119:
		    number = new Double(-((Double) constvalue.value)
					     .doubleValue());
		    break;
		default:
		    throw new AssertError("Can't happen.");
		}
		stacklocalinfo.constInfo = new ConstantInfo(2, number);
		constvalue_34_ = new ConstValue(number);
		constvalue_34_.addConstantListener(stacklocalinfo.constInfo);
		constvalue.addConstantListener(constvalue_34_);
	    } else
		constvalue_34_ = unknownValue[i_33_ - 1];
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.poppush(i_33_, constvalue_34_));
	    break;
	}
	case 120:
	case 121:
	case 122:
	case 123:
	case 124:
	case 125: {
	    int i_35_ = 1 + (i - 96 & 0x1);
	    ConstValue constvalue = stacklocalinfo.getStack(i_35_ + 1);
	    ConstValue constvalue_36_ = stacklocalinfo.getStack(1);
	    ConstValue constvalue_37_;
	    if (constvalue.value != ConstValue.VOLATILE
		&& constvalue_36_.value != ConstValue.VOLATILE) {
		Number number;
		switch (i) {
		case 120:
		    number
			= new Integer(((Integer) constvalue.value).intValue()
				      << ((Integer) constvalue_36_.value)
					     .intValue());
		    break;
		case 122:
		    number
			= new Integer(((Integer) constvalue.value).intValue()
				      >> ((Integer) constvalue_36_.value)
					     .intValue());
		    break;
		case 124:
		    number
			= new Integer(((Integer) constvalue.value).intValue()
				      >>> ((Integer) constvalue_36_.value)
					      .intValue());
		    break;
		case 121:
		    number = new Long(((Long) constvalue.value).longValue()
				      << ((Integer) constvalue_36_.value)
					     .intValue());
		    break;
		case 123:
		    number = new Long(((Long) constvalue.value).longValue()
				      >> ((Integer) constvalue_36_.value)
					     .intValue());
		    break;
		case 125:
		    number = new Long(((Long) constvalue.value).longValue()
				      >>> ((Integer) constvalue_36_.value)
					      .intValue());
		    break;
		default:
		    throw new AssertError("Can't happen.");
		}
		stacklocalinfo.constInfo = new ConstantInfo(2, number);
		constvalue_37_ = new ConstValue(number);
		constvalue_37_.addConstantListener(stacklocalinfo.constInfo);
		constvalue.addConstantListener(constvalue_37_);
		constvalue_36_.addConstantListener(constvalue_37_);
	    } else
		constvalue_37_ = unknownValue[i_35_ - 1];
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.poppush(i_35_ + 1, constvalue_37_));
	    break;
	}
	case 132: {
	    ConstValue constvalue
		= stacklocalinfo.getLocal(instruction.getLocalSlot());
	    ConstValue constvalue_38_;
	    if (constvalue.value != ConstValue.VOLATILE) {
		constvalue_38_
		    = new ConstValue(new Integer(((Integer) constvalue.value)
						     .intValue()
						 + instruction
						       .getIncrement()));
		constvalue.addConstantListener(constvalue_38_);
	    } else
		constvalue_38_ = unknownValue[0];
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.copy().setLocal(instruction
							 .getLocalSlot(),
						     constvalue_38_));
	    break;
	}
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
	    int i_39_ = 1 + ((i - 133) / 3 & 0x1);
	    ConstValue constvalue = stacklocalinfo.getStack(i_39_);
	    ConstValue constvalue_40_;
	    if (constvalue.value != ConstValue.VOLATILE) {
		Number number;
		switch (i) {
		case 136:
		case 139:
		case 142:
		    number
			= new Integer(((Number) constvalue.value).intValue());
		    break;
		case 133:
		case 140:
		case 143:
		    number = new Long(((Number) constvalue.value).longValue());
		    break;
		case 134:
		case 137:
		case 144:
		    number
			= new Float(((Number) constvalue.value).floatValue());
		    break;
		case 135:
		case 138:
		case 141:
		    number = new Double(((Number) constvalue.value)
					    .doubleValue());
		    break;
		default:
		    throw new AssertError("Can't happen.");
		}
		stacklocalinfo.constInfo = new ConstantInfo(2, number);
		constvalue_40_ = new ConstValue(number);
		constvalue_40_.addConstantListener(stacklocalinfo.constInfo);
		constvalue.addConstantListener(constvalue_40_);
	    } else {
		switch (i) {
		case 133:
		case 135:
		case 138:
		case 140:
		case 141:
		case 143:
		    constvalue_40_ = unknownValue[1];
		    break;
		default:
		    constvalue_40_ = unknownValue[0];
		}
	    }
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.poppush(i_39_, constvalue_40_));
	    break;
	}
	case 145:
	case 146:
	case 147: {
	    ConstValue constvalue = stacklocalinfo.getStack(1);
	    ConstValue constvalue_41_;
	    if (constvalue.value != ConstValue.VOLATILE) {
		int i_42_ = ((Integer) constvalue.value).intValue();
		switch (i) {
		case 145:
		    i_42_ = (byte) i_42_;
		    break;
		case 146:
		    i_42_ = (char) i_42_;
		    break;
		case 147:
		    i_42_ = (short) i_42_;
		    break;
		}
		Integer integer = new Integer(i_42_);
		stacklocalinfo.constInfo = new ConstantInfo(2, integer);
		constvalue_41_ = new ConstValue(integer);
		constvalue.addConstantListener(stacklocalinfo.constInfo);
		constvalue.addConstantListener(constvalue_41_);
	    } else
		constvalue_41_ = unknownValue[0];
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.poppush(1, constvalue_41_));
	    break;
	}
	case 148: {
	    ConstValue constvalue = stacklocalinfo.getStack(4);
	    ConstValue constvalue_43_ = stacklocalinfo.getStack(2);
	    ConstValue constvalue_44_;
	    if (constvalue.value != ConstValue.VOLATILE
		&& constvalue_43_.value != ConstValue.VOLATILE) {
		long l = ((Long) constvalue.value).longValue();
		long l_45_ = ((Long) constvalue.value).longValue();
		Integer integer
		    = new Integer(l == l_45_ ? 0 : l < l_45_ ? -1 : 1);
		stacklocalinfo.constInfo = new ConstantInfo(2, integer);
		constvalue_44_ = new ConstValue(integer);
		constvalue_44_.addConstantListener(stacklocalinfo.constInfo);
		constvalue.addConstantListener(constvalue_44_);
		constvalue_43_.addConstantListener(constvalue_44_);
	    } else
		constvalue_44_ = unknownValue[0];
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.poppush(4, constvalue_44_));
	    break;
	}
	case 149:
	case 150: {
	    ConstValue constvalue = stacklocalinfo.getStack(2);
	    ConstValue constvalue_46_ = stacklocalinfo.getStack(1);
	    ConstValue constvalue_47_;
	    if (constvalue.value != ConstValue.VOLATILE
		&& constvalue_46_.value != ConstValue.VOLATILE) {
		float f = ((Float) constvalue.value).floatValue();
		float f_48_ = ((Float) constvalue.value).floatValue();
		Integer integer
		    = new Integer(f == f_48_ ? 0 : i == 150
				  ? f < f_48_ ? -1 : 1 : f > f_48_ ? 1 : -1);
		stacklocalinfo.constInfo = new ConstantInfo(2, integer);
		constvalue_47_ = new ConstValue(integer);
		constvalue_47_.addConstantListener(stacklocalinfo.constInfo);
		constvalue.addConstantListener(constvalue_47_);
		constvalue_46_.addConstantListener(constvalue_47_);
	    } else
		constvalue_47_ = unknownValue[0];
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.poppush(2, constvalue_47_));
	    break;
	}
	case 151:
	case 152: {
	    ConstValue constvalue = stacklocalinfo.getStack(4);
	    ConstValue constvalue_49_ = stacklocalinfo.getStack(2);
	    ConstValue constvalue_50_;
	    if (constvalue.value != ConstValue.VOLATILE
		&& constvalue_49_.value != ConstValue.VOLATILE) {
		double d = ((Double) constvalue.value).doubleValue();
		double d_51_ = ((Double) constvalue.value).doubleValue();
		Integer integer
		    = new Integer(d == d_51_ ? 0 : i == 152
				  ? d < d_51_ ? -1 : 1 : d > d_51_ ? 1 : -1);
		stacklocalinfo.constInfo = new ConstantInfo(2, integer);
		constvalue_50_ = new ConstValue(integer);
		constvalue_50_.addConstantListener(stacklocalinfo.constInfo);
		constvalue.addConstantListener(constvalue_50_);
		constvalue_49_.addConstantListener(constvalue_50_);
	    } else
		constvalue_50_ = unknownValue[0];
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.poppush(4, constvalue_50_));
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
	    int i_52_ = 1;
	    ConstValue constvalue = stacklocalinfo.getStack(1);
	    ConstValue constvalue_53_ = null;
	    boolean bool = constvalue.value != ConstValue.VOLATILE;
	    if (i >= 159 && i <= 166) {
		constvalue_53_ = stacklocalinfo.getStack(2);
		i_52_ = 2;
		bool = bool & constvalue_53_.value != ConstValue.VOLATILE;
	    }
	    if (bool) {
		constvalue.addConstantListener(stacklocalinfo);
		if (constvalue_53_ != null)
		    constvalue_53_.addConstantListener(stacklocalinfo);
		Instruction instruction_54_ = instruction.getNextByAddr();
		int i_55_;
		if (i >= 165) {
		    if (i >= 198) {
			i_55_ = constvalue.value == null ? 41 : 26;
			i -= 198;
		    } else {
			i_55_ = (constvalue.value == constvalue_53_.value ? 41
				 : 26);
			i -= 165;
		    }
		} else {
		    int i_56_ = ((Integer) constvalue.value).intValue();
		    if (i >= 159) {
			int i_57_
			    = ((Integer) constvalue_53_.value).intValue();
			i_55_ = i_57_ == i_56_ ? 41 : i_57_ < i_56_ ? 38 : 26;
			i -= 159;
		    } else {
			i_55_ = i_56_ == 0 ? 41 : i_56_ < 0 ? 38 : 26;
			i -= 153;
		    }
		}
		if ((i_55_ & 1 << i) != 0)
		    instruction_54_ = instruction.getSingleSucc();
		stacklocalinfo.constInfo
		    = new ConstantInfo(4, instruction_54_);
		mergeInfo(instruction_54_, stacklocalinfo.pop(i_52_));
	    } else {
		mergeInfo(instruction.getNextByAddr(),
			  stacklocalinfo.pop(i_52_));
		mergeInfo(instruction.getSingleSucc(),
			  stacklocalinfo.pop(i_52_));
	    }
	    break;
	}
	case 167:
	    mergeInfo(instruction.getSingleSucc(), stacklocalinfo.copy());
	    break;
	case 171: {
	    ConstValue constvalue = stacklocalinfo.getStack(1);
	    if (constvalue.value != ConstValue.VOLATILE) {
		constvalue.addConstantListener(stacklocalinfo);
		int i_58_ = ((Integer) constvalue.value).intValue();
		int[] is = instruction.getValues();
		Instruction instruction_59_
		    = instruction.getSuccs()[is.length];
		for (int i_60_ = 0; i_60_ < is.length; i_60_++) {
		    if (is[i_60_] == i_58_) {
			instruction_59_ = instruction.getSuccs()[i_60_];
			break;
		    }
		}
		stacklocalinfo.constInfo
		    = new ConstantInfo(4, instruction_59_);
		mergeInfo(instruction_59_, stacklocalinfo.pop(1));
	    } else {
		for (int i_61_ = 0; i_61_ < instruction.getSuccs().length;
		     i_61_++)
		    mergeInfo(instruction.getSuccs()[i_61_],
			      stacklocalinfo.pop(1));
	    }
	    break;
	}
	case 168: {
	    if (instruction.getSingleSucc().getOpcode() != 58)
		throw new RuntimeException("Can't handle jsr to non astores");
	    StackLocalInfo stacklocalinfo_62_
		= (StackLocalInfo) instruction.getSingleSucc().getTmpInfo();
	    ConstValue constvalue;
	    if (stacklocalinfo_62_ != null) {
		constvalue = stacklocalinfo_62_.getStack(1);
		if (stacklocalinfo_62_.retInfo != null
		    && constvalue.value instanceof JSRTargetInfo)
		    mergeInfo(instruction.getNextByAddr(),
			      (stacklocalinfo.copy().mergeRetLocals
			       ((JSRTargetInfo) constvalue.value,
				stacklocalinfo_62_.retInfo)));
	    } else
		constvalue
		    = new ConstValue(new JSRTargetInfo(instruction
							   .getSingleSucc()));
	    mergeInfo(instruction.getSingleSucc(),
		      stacklocalinfo.poppush(0, constvalue));
	    break;
	}
	case 169: {
	    ConstValue constvalue
		= stacklocalinfo.getLocal(instruction.getLocalSlot());
	    JSRTargetInfo jsrtargetinfo = (JSRTargetInfo) constvalue.value;
	    jsrtargetinfo.setRetInfo(stacklocalinfo);
	    constvalue.addConstantListener(stacklocalinfo);
	    Instruction instruction_63_ = jsrtargetinfo.jsrTarget;
	    StackLocalInfo stacklocalinfo_64_
		= (StackLocalInfo) instruction_63_.getTmpInfo();
	    stacklocalinfo_64_.retInfo = stacklocalinfo;
	    stacklocalinfo_64_.constInfo.flags |= 0x10;
	    Instruction[] instructions = instruction_63_.getPreds();
	    for (int i_65_ = 0; i_65_ < instructions.length; i_65_++) {
		Instruction instruction_66_ = instructions[i_65_];
		if (instruction_66_.getTmpInfo() != null)
		    mergeInfo
			(instruction_66_.getNextByAddr(),
			 ((StackLocalInfo) instruction_66_.getTmpInfo()).copy
			     ().mergeRetLocals(jsrtargetinfo, stacklocalinfo));
	    }
	    break;
	}
	case 172:
	case 173:
	case 174:
	case 175:
	case 176:
	case 177:
	case 191:
	    break;
	case 179:
	case 181: {
	    FieldIdentifier fieldidentifier
		= (FieldIdentifier) this.canonizeReference(instruction);
	    Reference reference = instruction.getReference();
	    int i_67_ = TypeSignature.getTypeSize(reference.getType());
	    if (fieldidentifier != null && !fieldidentifier.isNotConstant()) {
		ConstValue constvalue = stacklocalinfo.getStack(i_67_);
		Object object = fieldidentifier.getConstant();
		if (object == null)
		    object = ConstantRuntimeEnvironment
				 .getDefaultValue(reference.getType());
		if (constvalue.value == null ? object == null
		    : constvalue.value.equals(object))
		    constvalue.addConstantListener(stacklocalinfo);
		else {
		    fieldidentifier.setNotConstant();
		    fieldNotConstant(fieldidentifier);
		}
	    }
	    i_67_ = i_67_ + (i == 179 ? 0 : 1);
	    mergeInfo(instruction.getNextByAddr(), stacklocalinfo.pop(i_67_));
	    break;
	}
	case 178:
	case 180: {
	    int i_68_ = i == 178 ? 0 : 1;
	    FieldIdentifier fieldidentifier
		= (FieldIdentifier) this.canonizeReference(instruction);
	    Reference reference = instruction.getReference();
	    int i_69_ = TypeSignature.getTypeSize(reference.getType());
	    ConstValue constvalue;
	    if (fieldidentifier != null) {
		if (fieldidentifier.isNotConstant()) {
		    fieldidentifier.setReachable();
		    constvalue = unknownValue[i_69_ - 1];
		} else {
		    Object object = fieldidentifier.getConstant();
		    if (object == null)
			object = ConstantRuntimeEnvironment
				     .getDefaultValue(reference.getType());
		    stacklocalinfo.constInfo = new ConstantInfo(2, object);
		    constvalue = new ConstValue(object);
		    constvalue.addConstantListener(stacklocalinfo.constInfo);
		    fieldidentifier.addFieldListener(identifier);
		}
	    } else
		constvalue = unknownValue[i_69_ - 1];
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.poppush(i_68_, constvalue));
	    break;
	}
	case 182:
	case 183:
	case 184:
	case 185: {
	    this.canonizeReference(instruction);
	    Reference reference = instruction.getReference();
	    boolean bool = true;
	    int i_70_ = 0;
	    Object object = null;
	    String[] strings
		= TypeSignature.getParameterTypes(reference.getType());
	    Object[] objects = new Object[strings.length];
	    ConstValue constvalue = null;
	    ConstValue[] constvalues = new ConstValue[strings.length];
	    for (int i_71_ = strings.length - 1; i_71_ >= 0; i_71_--) {
		i_70_ += TypeSignature.getTypeSize(strings[i_71_]);
		Object object_72_
		    = ((constvalues[i_71_] = stacklocalinfo.getStack(i_70_))
		       .value);
		if (object_72_ != ConstValue.VOLATILE)
		    objects[i_71_] = object_72_;
		else
		    bool = false;
	    }
	    if (i != 184) {
		i_70_++;
		constvalue = stacklocalinfo.getStack(i_70_);
		object = constvalue.value;
		if (object == ConstValue.VOLATILE || object == null)
		    bool = false;
	    }
	    String string = TypeSignature.getReturnType(reference.getType());
	    if (string.equals("V")) {
		handleReference(reference, i == 182 || i == 185);
		mergeInfo(instruction.getNextByAddr(),
			  stacklocalinfo.pop(i_70_));
	    } else {
		if (bool && !ConstantRuntimeEnvironment.isWhite(string))
		    bool = false;
		Object object_73_ = null;
		if (bool) {
		    try {
			object_73_ = runtime.invokeMethod(reference, i != 183,
							  object, objects);
		    } catch (InterpreterException interpreterexception) {
			bool = false;
			if (GlobalOptions.verboseLevel > 3)
			    GlobalOptions.err.println("Can't interpret "
						      + reference + ": "
						      + interpreterexception
							    .getMessage());
		    } catch (InvocationTargetException invocationtargetexception) {
			bool = false;
			if (GlobalOptions.verboseLevel > 3)
			    GlobalOptions.err.println
				("Method " + reference + " throwed exception: "
				 + invocationtargetexception
				       .getTargetException());
		    }
		}
		ConstValue constvalue_74_;
		if (!bool) {
		    handleReference(reference, i == 182 || i == 185);
		    int i_75_ = TypeSignature.getTypeSize(string);
		    constvalue_74_ = unknownValue[i_75_ - 1];
		} else {
		    stacklocalinfo.constInfo = new ConstantInfo(2, object_73_);
		    constvalue_74_ = new ConstValue(object_73_);
		    constvalue_74_
			.addConstantListener(stacklocalinfo.constInfo);
		    if (constvalue != null)
			constvalue.addConstantListener(constvalue_74_);
		    for (int i_76_ = 0; i_76_ < constvalues.length; i_76_++)
			constvalues[i_76_].addConstantListener(constvalue_74_);
		}
		mergeInfo(instruction.getNextByAddr(),
			  stacklocalinfo.poppush(i_70_, constvalue_74_));
	    }
	    break;
	}
	case 187:
	    handleClass(instruction.getClazzType());
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.poppush(0, unknownValue[0]));
	    break;
	case 190: {
	    ConstValue constvalue = unknownValue[0];
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.poppush(1, constvalue));
	    break;
	}
	case 192:
	    handleClass(instruction.getClazzType());
	    mergeInfo(instruction.getNextByAddr(), stacklocalinfo.pop(0));
	    break;
	case 193:
	    handleClass(instruction.getClazzType());
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.poppush(1, unknownValue[0]));
	    break;
	case 194:
	case 195:
	    mergeInfo(instruction.getNextByAddr(), stacklocalinfo.pop(1));
	    break;
	case 197:
	    handleClass(instruction.getClazzType());
	    mergeInfo(instruction.getNextByAddr(),
		      stacklocalinfo.poppush(instruction.getDimensions(),
					     unknownValue[0]));
	    break;
	default:
	    throw new IllegalArgumentException("Invalid opcode " + i);
	}
    }
    
    public void fieldNotConstant(FieldIdentifier fieldidentifier) {
	Iterator iterator = bytecode.getInstructions().iterator();
	while (iterator.hasNext()) {
	    Instruction instruction = (Instruction) iterator.next();
	    if (instruction.getOpcode() == 180
		|| instruction.getOpcode() == 178) {
		Reference reference = instruction.getReference();
		if (reference.getName().equals(fieldidentifier.getName())
		    && reference.getType().equals(fieldidentifier.getType())
		    && instruction.getTmpInfo() != null)
		    ((StackLocalInfo) instruction.getTmpInfo()).enqueue();
	    }
	}
    }
    
    public void dumpStackLocalInfo() {
	Iterator iterator = bytecode.getInstructions().iterator();
	while (iterator.hasNext()) {
	    Instruction instruction = (Instruction) iterator.next();
	    System.err.println("" + instruction.getTmpInfo());
	    System.err.println(instruction.getDescription());
	}
    }
    
    public void analyzeCode(MethodIdentifier methodidentifier,
			    BytecodeInfo bytecodeinfo) {
	bytecode = bytecodeinfo;
	TodoQueue todoqueue = new TodoQueue();
	MethodInfo methodinfo = bytecodeinfo.getMethodInfo();
	Iterator iterator = bytecodeinfo.getInstructions().iterator();
	while (iterator.hasNext()) {
	    Instruction instruction = (Instruction) iterator.next();
	    instruction.setTmpInfo(null);
	}
	StackLocalInfo stacklocalinfo
	    = new StackLocalInfo(bytecodeinfo.getMaxLocals(),
				 methodinfo.isStatic(), methodinfo.getType(),
				 todoqueue);
	stacklocalinfo.instr
	    = (Instruction) bytecodeinfo.getInstructions().get(0);
	stacklocalinfo.instr.setTmpInfo(stacklocalinfo);
	stacklocalinfo.enqueue();
	runtime.setFieldListener(methodidentifier);
	while (todoqueue.first != null) {
	    StackLocalInfo stacklocalinfo_77_ = todoqueue.first;
	    todoqueue.first = stacklocalinfo_77_.nextOnQueue;
	    stacklocalinfo_77_.nextOnQueue = null;
	    handleOpcode(stacklocalinfo_77_, methodidentifier);
	}
	runtime.setFieldListener(null);
	Handler[] handlers = bytecodeinfo.getExceptionHandlers();
	for (int i = 0; i < handlers.length; i++) {
	    if (handlers[i].catcher.getTmpInfo() != null
		&& handlers[i].type != null)
		Main.getClassBundle().reachableClass(handlers[i].type);
	}
	Iterator iterator_78_ = bytecodeinfo.getInstructions().iterator();
	while (iterator_78_.hasNext()) {
	    Instruction instruction = (Instruction) iterator_78_.next();
	    StackLocalInfo stacklocalinfo_79_
		= (StackLocalInfo) instruction.getTmpInfo();
	    if (stacklocalinfo_79_ != null) {
		if (stacklocalinfo_79_.constInfo.flags == 0)
		    instruction.setTmpInfo(unknownConstInfo);
		else
		    instruction.setTmpInfo(stacklocalinfo_79_.constInfo);
	    }
	}
    }
    
    public static void replaceWith(ListIterator listiterator,
				   Instruction instruction,
				   Instruction instruction_80_) {
	switch (instruction.getOpcode()) {
	case 18:
	case 20:
	case 21:
	case 22:
	case 23:
	case 24:
	case 25:
	case 167:
	case 178:
	    if (instruction_80_ == null)
		listiterator.remove();
	    else
		listiterator.set(instruction_80_);
	    return;
	case 116:
	case 118:
	case 133:
	case 134:
	case 135:
	case 139:
	case 140:
	case 141:
	case 145:
	case 146:
	case 147:
	case 153:
	case 154:
	case 155:
	case 156:
	case 157:
	case 158:
	case 180:
	case 190:
	case 198:
	case 199:
	    listiterator.set(new Instruction(87));
	    break;
	case 97:
	case 99:
	case 101:
	case 103:
	case 105:
	case 107:
	case 109:
	case 111:
	case 113:
	case 115:
	case 127:
	case 129:
	case 131:
	case 148:
	case 151:
	case 152:
	    listiterator.set(new Instruction(88));
	    listiterator.add(new Instruction(88));
	    break;
	case 46:
	case 47:
	case 48:
	case 49:
	case 50:
	case 51:
	case 52:
	case 53:
	case 96:
	case 98:
	case 100:
	case 102:
	case 104:
	case 106:
	case 108:
	case 110:
	case 112:
	case 114:
	case 117:
	case 119:
	case 120:
	case 122:
	case 124:
	case 126:
	case 128:
	case 130:
	case 136:
	case 137:
	case 138:
	case 142:
	case 143:
	case 144:
	case 149:
	case 150:
	case 159:
	case 160:
	case 161:
	case 162:
	case 163:
	case 164:
	case 165:
	case 166:
	    listiterator.set(new Instruction(88));
	    break;
	case 121:
	case 123:
	case 125:
	    listiterator.set(new Instruction(87));
	    listiterator.add(new Instruction(88));
	    break;
	case 179:
	case 181:
	    if (TypeSignature.getTypeSize(instruction.getReference().getType())
		== 2) {
		listiterator.set(new Instruction(88));
		if (instruction.getOpcode() == 181)
		    listiterator.add(new Instruction(87));
	    } else
		listiterator.set(new Instruction(instruction.getOpcode() == 181
						 ? 88 : 87));
	    break;
	case 182:
	case 183:
	case 184:
	case 185: {
	    Reference reference = instruction.getReference();
	    String[] strings
		= TypeSignature.getParameterTypes(reference.getType());
	    int i = strings.length;
	    if (i > 0) {
		listiterator.set(new Instruction(TypeSignature
						     .getTypeSize(strings[--i])
						 + 87 - 1));
		for (int i_81_ = i - 1; i_81_ >= 0; i_81_--)
		    listiterator.add(new Instruction
				     (TypeSignature.getTypeSize(strings[i_81_])
				      + 87 - 1));
		if (instruction.getOpcode() != 184)
		    listiterator.add(new Instruction(87));
	    } else if (instruction.getOpcode() != 184)
		listiterator.set(new Instruction(87));
	    else {
		if (instruction_80_ == null)
		    listiterator.remove();
		else
		    listiterator.set(instruction_80_);
		return;
	    }
	    break;
	}
	}
	if (instruction_80_ != null)
	    listiterator.add(instruction_80_);
    }
    
    public void appendJump(ListIterator listiterator,
			   Instruction instruction) {
	Instruction instruction_82_ = new Instruction(167);
	instruction_82_.setSuccs(instruction);
	listiterator.add(instruction_82_);
    }
    
    public void transformCode(BytecodeInfo bytecodeinfo) {
	ListIterator listiterator
	    = bytecodeinfo.getInstructions().listIterator();
	while (listiterator.hasNext()) {
	    Instruction instruction = (Instruction) listiterator.next();
	    ConstantInfo constantinfo
		= (ConstantInfo) instruction.getTmpInfo();
	    instruction.setTmpInfo(null);
	    if (constantinfo == null || (constantinfo.flags & 0x18) == 8)
		listiterator.remove();
	    else if ((constantinfo.flags & 0x2) != 0) {
		if (instruction.getOpcode() > 20) {
		    Instruction instruction_83_
			= new Instruction(((constantinfo.constant
					    instanceof Long)
					   || (constantinfo.constant
					       instanceof Double)) ? 20 : 18);
		    instruction_83_.setConstant(constantinfo.constant);
		    replaceWith(listiterator, instruction, instruction_83_);
		    if (GlobalOptions.verboseLevel > 2)
			GlobalOptions.err.println(bytecodeinfo + ": Replacing "
						  + instruction
						  + " with constant "
						  + constantinfo.constant);
		}
	    } else if ((constantinfo.flags & 0x4) != 0) {
		Instruction instruction_84_
		    = (Instruction) constantinfo.constant;
		if (instruction.getOpcode() >= 159
		    && instruction.getOpcode() <= 166)
		    listiterator.set(new Instruction(88));
		else
		    listiterator.set(new Instruction(87));
		if (GlobalOptions.verboseLevel > 2)
		    GlobalOptions.err.println(bytecodeinfo + ": Replacing "
					      + instruction + " with goto "
					      + instruction_84_.getAddr());
		while (listiterator.hasNext()) {
		    ConstantInfo constantinfo_85_
			= ((ConstantInfo)
			   ((Instruction) listiterator.next()).getTmpInfo());
		    if (constantinfo_85_ != null) {
			Instruction instruction_86_
			    = (Instruction) listiterator.previous();
			if (instruction_84_ != instruction_86_)
			    appendJump(listiterator, instruction_84_);
			break;
		    }
		    listiterator.remove();
		}
	    } else {
		int i = instruction.getOpcode();
		switch (i) {
		case 0:
		    listiterator.remove();
		    break;
		case 168: {
		    ConstantInfo constantinfo_87_
			= ((ConstantInfo)
			   instruction.getSingleSucc().getTmpInfo());
		    if ((constantinfo_87_.flags & 0x10) != 0)
			break;
		    Instruction instruction_88_ = new Instruction(167);
		    instruction_88_.setSuccs(instruction.getSingleSucc());
		    listiterator.set(instruction_88_);
		}
		    /* fall through */
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
		case 167:
		case 198:
		case 199:
		    while (listiterator.hasNext()) {
			ConstantInfo constantinfo_89_
			    = ((ConstantInfo)
			       ((Instruction) listiterator.next())
				   .getTmpInfo());
			if (constantinfo_89_ != null
			    && (constantinfo_89_.flags & 0x18) != 8) {
			    Instruction instruction_90_
				= (Instruction) listiterator.previous();
			    if (instruction.getSingleSucc()
				== instruction_90_) {
				listiterator.previous();
				listiterator.next();
				replaceWith(listiterator, instruction, null);
			    }
			    break;
			}
			listiterator.remove();
		    }
		    break;
		case 179:
		case 181: {
		    Reference reference = instruction.getReference();
		    FieldIdentifier fieldidentifier
			= ((FieldIdentifier)
			   Main.getClassBundle().getIdentifier(reference));
		    if (fieldidentifier != null && (Main.stripping & 0x1) != 0
			&& !fieldidentifier.isReachable())
			replaceWith(listiterator, instruction, null);
		    break;
		}
		}
	    }
	}
    }
}
