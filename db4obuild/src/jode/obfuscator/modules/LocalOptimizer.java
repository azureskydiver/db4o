/* LocalOptimizer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator.modules;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;

import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.LocalVariableInfo;
import jode.bytecode.Opcodes;
import jode.bytecode.TypeSignature;
import jode.obfuscator.CodeTransformer;
import jode.obfuscator.Main;

public class LocalOptimizer implements Opcodes, CodeTransformer
{
    BytecodeInfo bc;
    TodoQueue changedInfos;
    InstrInfo firstInfo;
    Hashtable instrInfos;
    boolean produceLVT;
    int maxlocals;
    LocalInfo[] paramLocals;
    private InstrInfo CONFLICT = new InstrInfo();
    
    static class InstrInfo
    {
	InstrInfo nextTodo;
	LocalInfo local;
	InstrInfo[] nextReads;
	BitSet usedBySub;
	LocalInfo[] lifeLocals;
	InstrInfo retInfo;
	InstrInfo jsrTargetInfo;
	Instruction instr;
	InstrInfo nextInfo;
    }
    
    private static class TodoQueue
    {
	public final InstrInfo LAST = new InstrInfo();
	InstrInfo first = LAST;
	
	private TodoQueue() {
	    /* empty */
	}
	
	public void add(InstrInfo instrinfo) {
	    if (instrinfo.nextTodo == null) {
		instrinfo.nextTodo = first;
		first = instrinfo;
	    }
	}
	
	public boolean isEmpty() {
	    return first == LAST;
	}
	
	public InstrInfo remove() {
	    if (first == LAST)
		throw new NoSuchElementException();
	    InstrInfo instrinfo = first;
	    first = instrinfo.nextTodo;
	    instrinfo.nextTodo = null;
	    return instrinfo;
	}
    }
    
    class LocalInfo
    {
	LocalInfo shadow = null;
	String name;
	String type;
	Vector usingInstrs;
	Vector conflictingLocals;
	int size;
	int newSlot;
	
	public LocalInfo getReal() {
	    LocalInfo localinfo_0_;
	    for (localinfo_0_ = this; localinfo_0_.shadow != null;
		 localinfo_0_ = localinfo_0_.shadow) {
		/* empty */
	    }
	    return localinfo_0_;
	}
	
	LocalInfo() {
	    usingInstrs = new Vector();
	    conflictingLocals = new Vector();
	    newSlot = -1;
	}
	
	LocalInfo(InstrInfo instrinfo) {
	    usingInstrs = new Vector();
	    conflictingLocals = new Vector();
	    newSlot = -1;
	    usingInstrs.addElement(instrinfo);
	}
	
	void conflictsWith(LocalInfo localinfo_1_) {
	    if (shadow != null)
		getReal().conflictsWith(localinfo_1_);
	    else {
		localinfo_1_ = localinfo_1_.getReal();
		if (!conflictingLocals.contains(localinfo_1_)) {
		    conflictingLocals.addElement(localinfo_1_);
		    localinfo_1_.conflictingLocals.addElement(this);
		}
	    }
	}
	
	void combineInto(LocalInfo localinfo_2_) {
	    if (shadow != null)
		getReal().combineInto(localinfo_2_);
	    else {
		localinfo_2_ = localinfo_2_.getReal();
		if (this != localinfo_2_) {
		    shadow = localinfo_2_;
		    if (shadow.name == null) {
			shadow.name = name;
			shadow.type = type;
		    }
		    Enumeration enumeration = usingInstrs.elements();
		    while (enumeration.hasMoreElements()) {
			InstrInfo instrinfo
			    = (InstrInfo) enumeration.nextElement();
			instrinfo.local = localinfo_2_;
			localinfo_2_.usingInstrs.addElement(instrinfo);
		    }
		}
	    }
	}
	
	public int getFirstAddr() {
	    int i = 2147483647;
	    Enumeration enumeration = usingInstrs.elements();
	    while (enumeration.hasMoreElements()) {
		InstrInfo instrinfo = (InstrInfo) enumeration.nextElement();
		if (instrinfo.instr.getAddr() < i)
		    i = instrinfo.instr.getAddr();
	    }
	    return i;
	}
    }
    
    Vector merge(Vector vector, Vector vector_3_) {
	if (vector == null || vector.isEmpty())
	    return vector_3_;
	if (vector_3_ == null || vector_3_.isEmpty())
	    return vector;
	Vector vector_4_ = (Vector) vector.clone();
	Enumeration enumeration = vector_3_.elements();
	while (enumeration.hasMoreElements()) {
	    Object object = enumeration.nextElement();
	    if (!vector_4_.contains(object))
		vector_4_.addElement(object);
	}
	return vector_4_;
    }
    
    void promoteReads(InstrInfo instrinfo, Instruction instruction,
		      BitSet bitset, boolean bool) {
	InstrInfo instrinfo_5_ = (InstrInfo) instrInfos.get(instruction);
	int i = -1;
	if (instruction.getOpcode() >= 54 && instruction.getOpcode() <= 58) {
	    i = instruction.getLocalSlot();
	    if (instrinfo.nextReads[i] != null)
		instrinfo_5_.local.combineInto(instrinfo.nextReads[i].local);
	}
	for (int i_6_ = 0; i_6_ < maxlocals; i_6_++) {
	    if (instrinfo.nextReads[i_6_] != null && i_6_ != i
		&& (bitset == null || bitset.get(i_6_) != bool)) {
		if (instrinfo_5_.nextReads[i_6_] == null) {
		    instrinfo_5_.nextReads[i_6_] = instrinfo.nextReads[i_6_];
		    changedInfos.add(instrinfo_5_);
		} else
		    instrinfo_5_.nextReads[i_6_].local
			.combineInto(instrinfo.nextReads[i_6_].local);
	    }
	}
    }
    
    void promoteReads(InstrInfo instrinfo, Instruction instruction) {
	promoteReads(instrinfo, instruction, null, false);
    }
    
    public LocalVariableInfo findLVTEntry
	(LocalVariableInfo[] localvariableinfos, int i, int i_7_) {
	LocalVariableInfo localvariableinfo = null;
	for (int i_8_ = 0; i_8_ < localvariableinfos.length; i_8_++) {
	    if (localvariableinfos[i_8_].slot == i
		&& localvariableinfos[i_8_].start.getAddr() <= i_7_
		&& localvariableinfos[i_8_].end.getAddr() >= i_7_) {
		if (localvariableinfo != null
		    && (!localvariableinfo.name
			     .equals(localvariableinfos[i_8_].name)
			|| !localvariableinfo.type
				.equals(localvariableinfos[i_8_].type)))
		    return null;
		localvariableinfo = localvariableinfos[i_8_];
	    }
	}
	return localvariableinfo;
    }
    
    public LocalVariableInfo findLVTEntry
	(LocalVariableInfo[] localvariableinfos, Instruction instruction) {
	int i;
	if (instruction.getOpcode() >= 54 && instruction.getOpcode() <= 58)
	    i = instruction.getNextAddr();
	else
	    i = instruction.getAddr();
	return findLVTEntry(localvariableinfos, instruction.getLocalSlot(), i);
    }
    
    public void calcLocalInfo() {
	maxlocals = bc.getMaxLocals();
	Handler[] handlers = bc.getExceptionHandlers();
	LocalVariableInfo[] localvariableinfos = bc.getLocalVariableTable();
	if (localvariableinfos != null)
	    produceLVT = true;
	String string = bc.getMethodInfo().getType();
	int i = ((bc.getMethodInfo().isStatic() ? 0 : 1)
		 + TypeSignature.getArgumentSize(string));
	paramLocals = new LocalInfo[i];
	int i_9_ = 0;
	if (!bc.getMethodInfo().isStatic()) {
	    LocalInfo localinfo = new LocalInfo();
	    if (localvariableinfos != null) {
		LocalVariableInfo localvariableinfo
		    = findLVTEntry(localvariableinfos, 0, 0);
		if (localvariableinfo != null) {
		    localinfo.name = localvariableinfo.name;
		    localinfo.type = localvariableinfo.type;
		}
	    }
	    localinfo.size = 1;
	    paramLocals[i_9_++] = localinfo;
	}
	int i_10_ = 1;
	while (i_10_ < string.length() && string.charAt(i_10_) != ')') {
	    LocalInfo localinfo = new LocalInfo();
	    if (localvariableinfos != null) {
		LocalVariableInfo localvariableinfo
		    = findLVTEntry(localvariableinfos, i_9_, 0);
		if (localvariableinfo != null)
		    localinfo.name = localvariableinfo.name;
	    }
	    int i_11_ = i_10_;
	    i_10_ = TypeSignature.skipType(string, i_10_);
	    localinfo.type = string.substring(i_11_, i_10_);
	    localinfo.size = TypeSignature.getTypeSize(localinfo.type);
	    paramLocals[i_9_] = localinfo;
	    i_9_ += localinfo.size;
	}
	changedInfos = new TodoQueue();
	instrInfos = new Hashtable();
	InstrInfo instrinfo = firstInfo = new InstrInfo();
	Iterator iterator = bc.getInstructions().iterator();
	for (;;) {
	    Instruction instruction = (Instruction) iterator.next();
	    instrInfos.put(instruction, instrinfo);
	    instrinfo.instr = instruction;
	    instrinfo.nextReads = new InstrInfo[maxlocals];
	    if (instruction.hasLocalSlot()) {
		instrinfo.local = new LocalInfo(instrinfo);
		if (localvariableinfos != null) {
		    LocalVariableInfo localvariableinfo
			= findLVTEntry(localvariableinfos, instruction);
		    if (localvariableinfo != null) {
			instrinfo.local.name = localvariableinfo.name;
			instrinfo.local.type = localvariableinfo.type;
		    }
		}
		instrinfo.local.size = 1;
		switch (instruction.getOpcode()) {
		case 22:
		case 24:
		    instrinfo.local.size = 2;
		    /* fall through */
		case 21:
		case 23:
		case 25:
		case 132:
		    instrinfo.nextReads[instruction.getLocalSlot()]
			= instrinfo;
		    changedInfos.add(instrinfo);
		    break;
		case 169:
		    instrinfo.usedBySub = new BitSet();
		    instrinfo.nextReads[instruction.getLocalSlot()]
			= instrinfo;
		    changedInfos.add(instrinfo);
		    break;
		case 55:
		case 57:
		    instrinfo.local.size = 2;
		    break;
		}
	    }
	    if (iterator.hasNext())
		instrinfo = instrinfo.nextInfo = new InstrInfo();
	    else {
		while (!changedInfos.isEmpty()) {
		    instrinfo = changedInfos.remove();
		    Instruction instruction_12_ = instrinfo.instr;
		    if (instruction_12_.hasLocalSlot()) {
			i_9_ = instruction_12_.getLocalSlot();
			for (i_10_ = 0; i_10_ < maxlocals; i_10_++) {
			    InstrInfo instrinfo_13_
				= instrinfo.nextReads[i_10_];
			    if (instrinfo_13_ != null
				&& instrinfo_13_.instr.getOpcode() == 169
				&& !instrinfo_13_.usedBySub.get(i_9_)) {
				instrinfo_13_.usedBySub.set(i_9_);
				if (instrinfo_13_.jsrTargetInfo != null)
				    changedInfos
					.add(instrinfo_13_.jsrTargetInfo);
			    }
			}
		    }
		    instruction = instruction_12_.getPrevByAddr();
		    if (instruction != null) {
			if (!instruction.doesAlwaysJump())
			    promoteReads(instrinfo, instruction);
			else if (instruction.getOpcode() == 168) {
			    InstrInfo instrinfo_14_
				= ((InstrInfo)
				   instrInfos
				       .get(instruction.getSingleSucc()));
			    if (instrinfo_14_.retInfo != null) {
				promoteReads(instrinfo,
					     instrinfo_14_.retInfo.instr,
					     instrinfo_14_.retInfo.usedBySub,
					     false);
				promoteReads(instrinfo, instruction,
					     instrinfo_14_.retInfo.usedBySub,
					     true);
			    }
			}
		    }
		    if (instruction_12_.getPreds() != null) {
			for (i_10_ = 0;
			     i_10_ < instruction_12_.getPreds().length;
			     i_10_++) {
			    Instruction instruction_15_
				= instruction_12_.getPreds()[i_10_];
			    if (instruction_12_.getPreds()[i_10_].getOpcode()
				== 168) {
				if (instrinfo.instr.getOpcode() != 58)
				    throw new AssertError("Non standard jsr");
				InstrInfo instrinfo_16_
				    = (instrinfo.nextInfo.nextReads
				       [instrinfo.instr.getLocalSlot()]);
				if (instrinfo_16_ != null) {
				    if (instrinfo_16_.instr.getOpcode() != 169)
					throw new AssertError
						  ("reading return address");
				    instrinfo.retInfo = instrinfo_16_;
				    instrinfo_16_.jsrTargetInfo = instrinfo;
				    Instruction instruction_17_
					= instruction_15_.getNextByAddr();
				    InstrInfo instrinfo_18_
					= ((InstrInfo)
					   instrInfos.get(instruction_17_));
				    promoteReads(instrinfo_18_,
						 instrinfo_16_.instr,
						 instrinfo_16_.usedBySub,
						 false);
				    promoteReads(instrinfo_18_,
						 instruction_15_,
						 instrinfo_16_.usedBySub,
						 true);
				}
			    }
			    promoteReads(instrinfo,
					 instruction_12_.getPreds()[i_10_]);
			}
		    }
		    for (i_10_ = 0; i_10_ < handlers.length; i_10_++) {
			if (handlers[i_10_].catcher == instruction_12_) {
			    for (Instruction instruction_19_
				     = handlers[i_10_].start;
				 (instruction_19_
				  != handlers[i_10_].end.getNextByAddr());
				 instruction_19_
				     = instruction_19_.getNextByAddr())
				promoteReads(instrinfo, instruction_19_);
			}
		    }
		}
		changedInfos = null;
		for (int i_20_ = 0; i_20_ < paramLocals.length; i_20_++) {
		    if (firstInfo.nextReads[i_20_] != null) {
			firstInfo.nextReads[i_20_].local
			    .combineInto(paramLocals[i_20_]);
			paramLocals[i_20_] = paramLocals[i_20_].getReal();
		    }
		}
		break;
	    }
	}
    }
    
    public void stripLocals() {
	ListIterator listiterator = bc.getInstructions().listIterator();
	for (InstrInfo instrinfo = firstInfo; instrinfo != null;
	     instrinfo = instrinfo.nextInfo) {
	    Instruction instruction = (Instruction) listiterator.next();
	    if (instrinfo.local != null
		&& instrinfo.local.usingInstrs.size() == 1) {
		switch (instruction.getOpcode()) {
		case 54:
		case 56:
		case 58:
		    listiterator.set(new Instruction(87));
		    break;
		case 55:
		case 57:
		    listiterator.set(new Instruction(88));
		    break;
		}
	    }
	}
    }
    
    void distributeLocals(Vector vector) {
	if (vector.size() != 0) {
	    int i = 2147483647;
	    LocalInfo localinfo = null;
	    Enumeration enumeration = vector.elements();
	    while (enumeration.hasMoreElements()) {
		LocalInfo localinfo_21_
		    = (LocalInfo) enumeration.nextElement();
		int i_22_ = 0;
		Enumeration enumeration_23_
		    = localinfo_21_.conflictingLocals.elements();
		while (enumeration_23_.hasMoreElements()) {
		    if (((LocalInfo) enumeration_23_.nextElement()).newSlot
			!= -2)
			i_22_++;
		}
		if (i_22_ < i) {
		    i = i_22_;
		    localinfo = localinfo_21_;
		}
	    }
	    vector.removeElement(localinfo);
	    localinfo.newSlot = -2;
	    distributeLocals(vector);
	    int i_24_ = 0;
	while_33_:
	    for (/**/; true; i_24_++) {
		Enumeration enumeration_25_
		    = localinfo.conflictingLocals.elements();
		while (enumeration_25_.hasMoreElements()) {
		    LocalInfo localinfo_26_
			= (LocalInfo) enumeration_25_.nextElement();
		    if (localinfo.size == 2
			&& localinfo_26_.newSlot == i_24_ + 1) {
			i_24_++;
			continue while_33_;
		    }
		    if (localinfo_26_.size == 2
			&& localinfo_26_.newSlot + 1 == i_24_)
			continue while_33_;
		    if (localinfo_26_.newSlot == i_24_) {
			if (localinfo_26_.size == 2)
			    i_24_++;
			continue while_33_;
		    }
		}
		localinfo.newSlot = i_24_;
		break;
	    }
	}
    }
    
    public void distributeLocals() {
	for (int i = 0; i < paramLocals.length; i++) {
	    if (paramLocals[i] != null)
		paramLocals[i].newSlot = i;
	}
	for (InstrInfo instrinfo = firstInfo; instrinfo != null;
	     instrinfo = instrinfo.nextInfo) {
	    if (instrinfo.instr.getOpcode() >= 54
		&& instrinfo.instr.getOpcode() <= 58) {
		for (int i = 0; i < maxlocals; i++) {
		    if (i != instrinfo.instr.getLocalSlot()
			&& instrinfo.nextReads[i] != null)
			instrinfo.local
			    .conflictsWith(instrinfo.nextReads[i].local);
		    if (instrinfo.nextInfo.nextReads[i] != null
			&& (instrinfo.nextInfo.nextReads[i].jsrTargetInfo
			    != null)) {
			Instruction[] instructions
			    = instrinfo.nextInfo.nextReads[i].jsrTargetInfo
				  .instr.getPreds();
			for (int i_27_ = 0; i_27_ < instructions.length;
			     i_27_++) {
			    InstrInfo instrinfo_28_
				= ((InstrInfo)
				   instrInfos.get(instructions[i_27_]));
			    for (int i_29_ = 0; i_29_ < maxlocals; i_29_++) {
				if (!instrinfo.nextInfo.nextReads[i]
					 .usedBySub.get(i_29_)
				    && instrinfo_28_.nextReads[i_29_] != null)
				    instrinfo.local.conflictsWith(instrinfo_28_
								  .nextReads
								  [i_29_]
								  .local);
			    }
			}
		    }
		}
	    }
	}
	Vector vector = new Vector();
	for (InstrInfo instrinfo = firstInfo; instrinfo != null;
	     instrinfo = instrinfo.nextInfo) {
	    if (instrinfo.local != null && instrinfo.local.newSlot == -1
		&& !vector.contains(instrinfo.local))
		vector.addElement(instrinfo.local);
	}
	distributeLocals(vector);
	for (InstrInfo instrinfo = firstInfo; instrinfo != null;
	     instrinfo = instrinfo.nextInfo) {
	    if (instrinfo.local != null)
		instrinfo.instr.setLocalSlot(instrinfo.local.newSlot);
	}
	if (produceLVT)
	    buildNewLVT();
    }
    
    boolean promoteLifeLocals(LocalInfo[] localinfos, InstrInfo instrinfo) {
	if (instrinfo.lifeLocals == null) {
	    instrinfo.lifeLocals = (LocalInfo[]) localinfos.clone();
	    return true;
	}
	boolean bool = false;
	for (int i = 0; i < maxlocals; i++) {
	    LocalInfo localinfo = instrinfo.lifeLocals[i];
	    if (localinfo != null) {
		localinfo = localinfo.getReal();
		LocalInfo localinfo_30_ = localinfos[i];
		if (localinfo_30_ != null)
		    localinfo_30_ = localinfo_30_.getReal();
		if (localinfo != localinfo_30_) {
		    instrinfo.lifeLocals[i] = null;
		    bool = true;
		}
	    }
	}
	return bool;
    }
    
    public void buildNewLVT() {
	for (InstrInfo instrinfo = firstInfo; instrinfo != null;
	     instrinfo = instrinfo.nextInfo) {
	    if (instrinfo.usedBySub != null)
		instrinfo.usedBySub = new BitSet();
	}
	for (InstrInfo instrinfo = firstInfo; instrinfo != null;
	     instrinfo = instrinfo.nextInfo) {
	    if (instrinfo.local != null) {
		for (int i = 0; i < instrinfo.nextReads.length; i++) {
		    if (instrinfo.nextReads[i] != null
			&& instrinfo.nextReads[i].instr.getOpcode() == 169)
			instrinfo.nextReads[i].usedBySub
			    .set(instrinfo.local.newSlot);
		}
	    }
	}
	firstInfo.lifeLocals = new LocalInfo[maxlocals];
	for (int i = 0; i < paramLocals.length; i++)
	    firstInfo.lifeLocals[i] = paramLocals[i];
	Stack stack = new Stack();
	stack.push(firstInfo);
	Handler[] handlers = bc.getExceptionHandlers();
	while (!stack.isEmpty()) {
	    InstrInfo instrinfo = (InstrInfo) stack.pop();
	    Instruction instruction = instrinfo.instr;
	    LocalInfo[] localinfos = instrinfo.lifeLocals;
	    if (instruction.hasLocalSlot()) {
		int i = instruction.getLocalSlot();
		LocalInfo localinfo = instrinfo.local.getReal();
		localinfos = (LocalInfo[]) localinfos.clone();
		localinfos[i] = localinfo;
		if (localinfo.name != null) {
		    for (int i_31_ = 0; i_31_ < localinfos.length; i_31_++) {
			if (i_31_ != i && localinfos[i_31_] != null
			    && localinfo.name.equals(localinfos[i_31_].name))
			    localinfos[i_31_] = null;
		    }
		}
	    }
	    if (!instruction.doesAlwaysJump()) {
		InstrInfo instrinfo_32_ = instrinfo.nextInfo;
		if (promoteLifeLocals(localinfos, instrinfo_32_))
		    stack.push(instrinfo_32_);
	    }
	    if (instruction.hasSuccs()) {
		Instruction[] instructions = instruction.getSuccs();
		for (int i = 0; i < instructions.length; i++) {
		    InstrInfo instrinfo_33_
			= (InstrInfo) instrInfos.get(instructions[i]);
		    if (promoteLifeLocals(localinfos, instrinfo_33_))
			stack.push(instrinfo_33_);
		}
	    }
	    for (int i = 0; i < handlers.length; i++) {
		if (handlers[i].start.compareTo(instruction) <= 0
		    && handlers[i].end.compareTo(instruction) >= 0) {
		    InstrInfo instrinfo_34_
			= (InstrInfo) instrInfos.get(handlers[i].catcher);
		    if (promoteLifeLocals(localinfos, instrinfo_34_))
			stack.push(instrinfo_34_);
		}
	    }
	    if (instrinfo.instr.getOpcode() == 168) {
		Instruction instruction_35_ = instrinfo.instr.getSingleSucc();
		InstrInfo instrinfo_36_
		    = (InstrInfo) instrInfos.get(instruction_35_);
		InstrInfo instrinfo_37_ = instrinfo_36_.retInfo;
		if (instrinfo_37_ != null
		    && instrinfo_37_.lifeLocals != null) {
		    LocalInfo[] localinfos_38_
			= (LocalInfo[]) localinfos.clone();
		    for (int i = 0; i < maxlocals; i++) {
			if (instrinfo_37_.usedBySub.get(i))
			    localinfos_38_[i] = instrinfo_37_.lifeLocals[i];
		    }
		    if (promoteLifeLocals(localinfos_38_, instrinfo.nextInfo))
			stack.push(instrinfo.nextInfo);
		}
	    }
	    if (instrinfo.jsrTargetInfo != null) {
		Instruction instruction_39_ = instrinfo.jsrTargetInfo.instr;
		for (int i = 0; i < instruction_39_.getPreds().length; i++) {
		    InstrInfo instrinfo_40_
			= ((InstrInfo)
			   instrInfos.get(instruction_39_.getPreds()[i]));
		    if (instrinfo_40_.lifeLocals != null) {
			LocalInfo[] localinfos_41_
			    = (LocalInfo[]) localinfos.clone();
			for (int i_42_ = 0; i_42_ < maxlocals; i_42_++) {
			    if (!instrinfo.usedBySub.get(i_42_))
				localinfos_41_[i_42_]
				    = instrinfo_40_.lifeLocals[i_42_];
			}
			if (promoteLifeLocals(localinfos_41_,
					      instrinfo_40_.nextInfo))
			    stack.push(instrinfo_40_.nextInfo);
		    }
		}
	    }
	}
	Vector vector = new Vector();
	LocalVariableInfo[] localvariableinfos
	    = new LocalVariableInfo[maxlocals];
	LocalInfo[] localinfos = new LocalInfo[maxlocals];
	for (int i = 0; i < paramLocals.length; i++) {
	    if (paramLocals[i] != null) {
		localinfos[i] = paramLocals[i];
		if (localinfos[i].name != null) {
		    localvariableinfos[i] = new LocalVariableInfo();
		    vector.addElement(localvariableinfos[i]);
		    localvariableinfos[i].name = localinfos[i].name;
		    localvariableinfos[i].type
			= Main.getClassBundle()
			      .getTypeAlias(localinfos[i].type);
		    localvariableinfos[i].start
			= (Instruction) bc.getInstructions().get(0);
		    localvariableinfos[i].slot = i;
		}
	    }
	}
	Instruction instruction = null;
	for (InstrInfo instrinfo = firstInfo; instrinfo != null;
	     instrinfo = instrinfo.nextInfo) {
	    for (int i = 0; i < maxlocals; i++) {
		LocalInfo localinfo = (instrinfo.lifeLocals != null
				       ? instrinfo.lifeLocals[i] : null);
		if (localinfo != localinfos[i]
		    && (localinfo == null || localinfos[i] == null
			|| localinfo.name == null || localinfo.type == null
			|| !localinfo.name.equals(localinfos[i].name)
			|| !localinfo.type.equals(localinfos[i].type))) {
		    if (localvariableinfos[i] != null)
			localvariableinfos[i].end
			    = instrinfo.instr.getPrevByAddr();
		    localvariableinfos[i] = null;
		    localinfos[i] = localinfo;
		    if (localinfos[i] != null && localinfos[i].name != null
			&& localinfos[i].type != null) {
			localvariableinfos[i] = new LocalVariableInfo();
			vector.addElement(localvariableinfos[i]);
			localvariableinfos[i].name = localinfos[i].name;
			localvariableinfos[i].type
			    = Main.getClassBundle()
				  .getTypeAlias(localinfos[i].type);
			localvariableinfos[i].start = instrinfo.instr;
			localvariableinfos[i].slot = i;
		    }
		}
	    }
	    instruction = instrinfo.instr;
	}
	for (int i = 0; i < maxlocals; i++) {
	    if (localvariableinfos[i] != null)
		localvariableinfos[i].end = instruction;
	}
	LocalVariableInfo[] localvariableinfos_43_
	    = new LocalVariableInfo[vector.size()];
	vector.copyInto(localvariableinfos_43_);
	bc.setLocalVariableTable(localvariableinfos_43_);
    }
    
    public void dumpLocals() {
	Vector vector = new Vector();
	for (InstrInfo instrinfo = firstInfo; instrinfo != null;
	     instrinfo = instrinfo.nextInfo) {
	    GlobalOptions.err.println(instrinfo.instr.getDescription());
	    GlobalOptions.err.print("nextReads: ");
	    for (int i = 0; i < maxlocals; i++) {
		if (instrinfo.nextReads[i] == null)
		    GlobalOptions.err.print("-,");
		else
		    GlobalOptions.err
			.print(instrinfo.nextReads[i].instr.getAddr() + ",");
	    }
	    if (instrinfo.usedBySub != null)
		GlobalOptions.err.print("  usedBySub: " + instrinfo.usedBySub);
	    if (instrinfo.retInfo != null)
		GlobalOptions.err
		    .print("  ret info: " + instrinfo.retInfo.instr.getAddr());
	    if (instrinfo.jsrTargetInfo != null)
		GlobalOptions.err.print("  jsr info: " + instrinfo
							     .jsrTargetInfo
							     .instr.getAddr());
	    GlobalOptions.err.println();
	    if (instrinfo.local != null && !vector.contains(instrinfo.local))
		vector.addElement(instrinfo.local);
	}
	Enumeration enumeration = vector.elements();
	while (enumeration.hasMoreElements()) {
	    LocalInfo localinfo = (LocalInfo) enumeration.nextElement();
	    int i = ((InstrInfo) localinfo.usingInstrs.elementAt(0)).instr
			.getLocalSlot();
	    GlobalOptions.err.print("Slot: " + i + " conflicts:");
	    Enumeration enumeration_44_
		= localinfo.conflictingLocals.elements();
	    while (enumeration_44_.hasMoreElements()) {
		LocalInfo localinfo_45_
		    = (LocalInfo) enumeration_44_.nextElement();
		GlobalOptions.err.print(localinfo_45_.getFirstAddr() + ", ");
	    }
	    GlobalOptions.err.println();
	    GlobalOptions.err.print(localinfo.getFirstAddr());
	    GlobalOptions.err.print("     instrs: ");
	    Enumeration enumeration_46_ = localinfo.usingInstrs.elements();
	    while (enumeration_46_.hasMoreElements())
		GlobalOptions.err.print(((InstrInfo)
					 enumeration_46_.nextElement())
					    .instr.getAddr() + ", ");
	    GlobalOptions.err.println();
	}
	GlobalOptions.err.println("-----------");
    }
    
    public void transformCode(BytecodeInfo bytecodeinfo) {
	bc = bytecodeinfo;
	calcLocalInfo();
	if ((GlobalOptions.debuggingFlags & 0x100) != 0) {
	    GlobalOptions.err.println("Before Local Optimization: ");
	    dumpLocals();
	}
	stripLocals();
	distributeLocals();
	if ((GlobalOptions.debuggingFlags & 0x100) != 0) {
	    GlobalOptions.err.println("After Local Optimization: ");
	    dumpLocals();
	}
	firstInfo = null;
	changedInfos = null;
	instrInfos = null;
	paramLocals = null;
    }
}
