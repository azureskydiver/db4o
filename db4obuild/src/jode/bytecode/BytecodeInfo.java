/* BytecodeInfo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.bytecode;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractSequentialList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import jode.GlobalOptions;

public class BytecodeInfo extends BinaryInfo implements Opcodes
{
    private MethodInfo methodInfo;
    private int maxStack;
    private int maxLocals;
    private Handler[] exceptionHandlers;
    private LocalVariableInfo[] lvt;
    private LineNumber[] lnt;
    private Instruction[] instrs;
    private InstructionList instructions;
    private static final Object[] constants
	= { null, new Integer(-1), new Integer(0), new Integer(1),
	    new Integer(2), new Integer(3), new Integer(4), new Integer(5),
	    new Long(0L), new Long(1L), new Float(0.0F), new Float(1.0F),
	    new Float(2.0F), new Double(0.0), new Double(1.0) };
    
    private class InstructionList extends AbstractSequentialList
    {
	Instruction borderInstr;
	int instructionCount = 0;
	
	InstructionList() {
	    borderInstr = new Instruction(254);
	    borderInstr.nextByAddr = borderInstr.prevByAddr = borderInstr;
	}
	
	public int size() {
	    return instructionCount;
	}
	
	Instruction get0(int i) {
	    Instruction instruction = borderInstr;
	    if (i < instructionCount / 2) {
		for (int i_0_ = 0; i_0_ <= i; i_0_++)
		    instruction = instruction.nextByAddr;
	    } else {
		for (int i_1_ = instructionCount; i_1_ > i; i_1_--)
		    instruction = instruction.prevByAddr;
	    }
	    return instruction;
	}
	
	public Object get(int i) {
	    if (i < 0 || i >= instructionCount)
		throw new IllegalArgumentException();
	    return get0(i);
	}
	
	public boolean add(Object object) {
	    instructionCount++;
	    borderInstr.prevByAddr.appendInstruction((Instruction) object,
						     BytecodeInfo.this);
	    return true;
	}
	
	public ListIterator listIterator(final int startIndex) {
	    if (startIndex < 0 || startIndex > instructionCount)
		throw new IllegalArgumentException();
	    return new ListIterator() {
		Instruction instr = get0(startIndex);
		Instruction toRemove = null;
		int index = startIndex;
		
		public boolean hasNext() {
		    return index < instructionCount;
		}
		
		public boolean hasPrevious() {
		    return index > 0;
		}
		
		public Object next() {
		    if (index >= instructionCount)
			throw new NoSuchElementException();
		    index++;
		    toRemove = instr;
		    instr = instr.nextByAddr;
		    return toRemove;
		}
		
		public Object previous() {
		    if (index == 0)
			throw new NoSuchElementException();
		    index--;
		    instr = instr.prevByAddr;
		    toRemove = instr;
		    return toRemove;
		}
		
		public int nextIndex() {
		    return index;
		}
		
		public int previousIndex() {
		    return index - 1;
		}
		
		public void remove() {
		    if (toRemove == null)
			throw new IllegalStateException();
		    instructionCount--;
		    if (instr == toRemove)
			instr = instr.nextByAddr;
		    else
			index--;
		    toRemove.removeInstruction(BytecodeInfo.this);
		    toRemove = null;
		}
		
		public void add(Object object) {
		    instructionCount++;
		    index++;
		    instr.prevByAddr.appendInstruction((Instruction) object,
						       BytecodeInfo.this);
		    toRemove = null;
		}
		
		public void set(Object object) {
		    if (toRemove == null)
			throw new IllegalStateException();
		    toRemove.replaceInstruction((Instruction) object,
						BytecodeInfo.this);
		    if (instr == toRemove)
			instr = (Instruction) object;
		    toRemove = (Instruction) object;
		}
	    };
	}
	
	void setLastAddr(int i) {
	    borderInstr.setAddr(i);
	}
	
	int getCodeLength() {
	    return borderInstr.getAddr();
	}
    }
    
    public BytecodeInfo(MethodInfo methodinfo) {
	methodInfo = methodinfo;
    }
    
    protected void readAttribute
	(String string, int i, ConstantPool constantpool,
	 DataInputStream datainputstream, int i_3_)
	throws IOException {
	if ((i_3_ & 0x10) != 0 && string.equals("LocalVariableTable")) {
	    if ((GlobalOptions.debuggingFlags & 0x40) != 0)
		GlobalOptions.err
		    .println("LocalVariableTable of " + methodInfo);
	    int i_4_ = datainputstream.readUnsignedShort();
	    if (i != 2 + i_4_ * 10) {
		if ((GlobalOptions.debuggingFlags & 0x40) != 0)
		    GlobalOptions.err
			.println("Illegal LVT length, ignoring it");
	    } else {
		lvt = new LocalVariableInfo[i_4_];
		for (int i_5_ = 0; i_5_ < i_4_; i_5_++) {
		    lvt[i_5_] = new LocalVariableInfo();
		    int i_6_ = datainputstream.readUnsignedShort();
		    int i_7_ = i_6_ + datainputstream.readUnsignedShort();
		    int i_8_ = datainputstream.readUnsignedShort();
		    int i_9_ = datainputstream.readUnsignedShort();
		    int i_10_ = datainputstream.readUnsignedShort();
		    Instruction instruction
			= (i_6_ >= 0 && i_6_ < instrs.length ? instrs[i_6_]
			   : null);
		    Instruction instruction_11_;
		    if (i_7_ >= 0 && i_7_ < instrs.length)
			instruction_11_ = (instrs[i_7_] == null ? null
					   : instrs[i_7_].getPrevByAddr());
		    else {
			instruction_11_ = null;
			for (int i_12_ = instrs.length - 1; i_12_ >= 0;
			     i_12_--) {
			    if (instrs[i_12_] != null) {
				if (instrs[i_12_].getNextAddr() == i_7_)
				    instruction_11_ = instrs[i_12_];
				break;
			    }
			}
		    }
		    if (instruction == null || instruction_11_ == null
			|| i_8_ == 0 || i_9_ == 0 || i_10_ >= maxLocals
			|| constantpool.getTag(i_8_) != 1
			|| constantpool.getTag(i_9_) != 1) {
			if ((GlobalOptions.debuggingFlags & 0x40) != 0)
			    GlobalOptions.err
				.println("Illegal entry, ignoring LVT");
			lvt = null;
			break;
		    }
		    lvt[i_5_].start = instruction;
		    lvt[i_5_].end = instruction_11_;
		    lvt[i_5_].name = constantpool.getUTF8(i_8_);
		    lvt[i_5_].type = constantpool.getUTF8(i_9_);
		    lvt[i_5_].slot = i_10_;
		    if ((GlobalOptions.debuggingFlags & 0x40) != 0)
			GlobalOptions.err.println("\t" + lvt[i_5_].name + ": "
						  + lvt[i_5_].type + " range "
						  + i_6_ + " - " + i_7_
						  + " slot " + i_10_);
		}
	    }
	} else if ((i_3_ & 0x10) != 0 && string.equals("LineNumberTable")) {
	    int i_13_ = datainputstream.readUnsignedShort();
	    if (i != 2 + i_13_ * 4)
		GlobalOptions.err
		    .println("Illegal LineNumberTable, ignoring it");
	    else {
		lnt = new LineNumber[i_13_];
		for (int i_14_ = 0; i_14_ < i_13_; i_14_++) {
		    lnt[i_14_] = new LineNumber();
		    int i_15_ = datainputstream.readUnsignedShort();
		    Instruction instruction = instrs[i_15_];
		    if (instruction == null) {
			GlobalOptions.err.println
			    ("Illegal entry, ignoring LineNumberTable table");
			lnt = null;
			break;
		    }
		    lnt[i_14_].start = instruction;
		    lnt[i_14_].linenr = datainputstream.readUnsignedShort();
		}
	    }
	} else
	    super.readAttribute(string, i, constantpool, datainputstream,
				i_3_);
    }
    
    public void read(ConstantPool constantpool,
		     DataInputStream datainputstream) throws IOException {
	maxStack = datainputstream.readUnsignedShort();
	maxLocals = datainputstream.readUnsignedShort();
	this.instructions = new InstructionList();
	int i = datainputstream.readInt();
	instrs = new Instruction[i];
	int[][] is = new int[i][];
	int i_16_ = 0;
	while (i_16_ < i) {
	    int i_17_ = datainputstream.readUnsignedByte();
	    if ((GlobalOptions.debuggingFlags & 0x1) != 0
		&& (GlobalOptions.debuggingFlags & 0x1) != 0)
		GlobalOptions.err
		    .print(i_16_ + ": " + Opcodes.opcodeString[i_17_]);
	    Instruction instruction;
	    int i_18_;
	    switch (i_17_) {
	    case 196: {
		int i_19_ = datainputstream.readUnsignedByte();
		switch (i_19_) {
		case 21:
		case 23:
		case 25:
		case 54:
		case 56:
		case 58: {
		    int i_20_ = datainputstream.readUnsignedShort();
		    if (i_20_ >= maxLocals)
			throw new ClassFormatError("Invalid local slot "
						   + i_20_);
		    instruction = new Instruction(i_19_);
		    instruction.setLocalSlot(i_20_);
		    i_18_ = 4;
		    if ((GlobalOptions.debuggingFlags & 0x1) != 0)
			GlobalOptions.err.print(" "
						+ Opcodes.opcodeString[i_19_]
						+ " " + i_20_);
		    break;
		}
		case 22:
		case 24:
		case 55:
		case 57: {
		    int i_21_ = datainputstream.readUnsignedShort();
		    if (i_21_ >= maxLocals - 1)
			throw new ClassFormatError("Invalid local slot "
						   + i_21_);
		    instruction = new Instruction(i_19_);
		    instruction.setLocalSlot(i_21_);
		    i_18_ = 4;
		    if ((GlobalOptions.debuggingFlags & 0x1) != 0)
			GlobalOptions.err.print(" "
						+ Opcodes.opcodeString[i_19_]
						+ " " + i_21_);
		    break;
		}
		case 169: {
		    int i_22_ = datainputstream.readUnsignedShort();
		    if (i_22_ >= maxLocals)
			throw new ClassFormatError("Invalid local slot "
						   + i_22_);
		    instruction = new Instruction(i_19_);
		    instruction.setLocalSlot(i_22_);
		    i_18_ = 4;
		    if ((GlobalOptions.debuggingFlags & 0x1) != 0)
			GlobalOptions.err.print(" ret " + i_22_);
		    break;
		}
		case 132: {
		    int i_23_ = datainputstream.readUnsignedShort();
		    if (i_23_ >= maxLocals)
			throw new ClassFormatError("Invalid local slot "
						   + i_23_);
		    instruction = new Instruction(i_19_);
		    instruction.setLocalSlot(i_23_);
		    instruction.setIncrement(datainputstream.readShort());
		    i_18_ = 6;
		    if ((GlobalOptions.debuggingFlags & 0x1) != 0)
			GlobalOptions.err.print(" iinc " + i_23_ + " "
						+ instruction.getIncrement());
		    break;
		}
		default:
		    throw new ClassFormatError("Invalid wide opcode " + i_19_);
		}
		break;
	    }
	    case 26:
	    case 27:
	    case 28:
	    case 29:
	    case 30:
	    case 31:
	    case 32:
	    case 33:
	    case 34:
	    case 35:
	    case 36:
	    case 37:
	    case 38:
	    case 39:
	    case 40:
	    case 41:
	    case 42:
	    case 43:
	    case 44:
	    case 45: {
		int i_24_ = i_17_ - 26 & 0x3;
		if (i_24_ >= maxLocals)
		    throw new ClassFormatError("Invalid local slot " + i_24_);
		instruction = new Instruction(21 + (i_17_ - 26) / 4);
		instruction.setLocalSlot(i_24_);
		i_18_ = 1;
		break;
	    }
	    case 59:
	    case 60:
	    case 61:
	    case 62:
	    case 67:
	    case 68:
	    case 69:
	    case 70:
	    case 75:
	    case 76:
	    case 77:
	    case 78: {
		int i_25_ = i_17_ - 59 & 0x3;
		if (i_25_ >= maxLocals)
		    throw new ClassFormatError("Invalid local slot " + i_25_);
		instruction = new Instruction(54 + (i_17_ - 59) / 4);
		instruction.setLocalSlot(i_25_);
		i_18_ = 1;
		break;
	    }
	    case 63:
	    case 64:
	    case 65:
	    case 66:
	    case 71:
	    case 72:
	    case 73:
	    case 74: {
		int i_26_ = i_17_ - 59 & 0x3;
		if (i_26_ >= maxLocals - 1)
		    throw new ClassFormatError("Invalid local slot " + i_26_);
		instruction = new Instruction(54 + (i_17_ - 59) / 4);
		instruction.setLocalSlot(i_26_);
		i_18_ = 1;
		break;
	    }
	    case 21:
	    case 23:
	    case 25:
	    case 54:
	    case 56:
	    case 58: {
		int i_27_ = datainputstream.readUnsignedByte();
		if (i_27_ >= maxLocals)
		    throw new ClassFormatError("Invalid local slot " + i_27_);
		instruction = new Instruction(i_17_);
		instruction.setLocalSlot(i_27_);
		i_18_ = 2;
		if ((GlobalOptions.debuggingFlags & 0x1) != 0)
		    GlobalOptions.err.print(" " + i_27_);
		break;
	    }
	    case 22:
	    case 24:
	    case 55:
	    case 57: {
		int i_28_ = datainputstream.readUnsignedByte();
		if (i_28_ >= maxLocals - 1)
		    throw new ClassFormatError("Invalid local slot " + i_28_);
		instruction = new Instruction(i_17_);
		instruction.setLocalSlot(i_28_);
		i_18_ = 2;
		if ((GlobalOptions.debuggingFlags & 0x1) != 0)
		    GlobalOptions.err.print(" " + i_28_);
		break;
	    }
	    case 169: {
		int i_29_ = datainputstream.readUnsignedByte();
		if (i_29_ >= maxLocals)
		    throw new ClassFormatError("Invalid local slot " + i_29_);
		instruction = new Instruction(i_17_);
		instruction.setLocalSlot(i_29_);
		i_18_ = 2;
		if ((GlobalOptions.debuggingFlags & 0x1) != 0)
		    GlobalOptions.err.print(" " + i_29_);
		break;
	    }
	    case 1:
	    case 2:
	    case 3:
	    case 4:
	    case 5:
	    case 6:
	    case 7:
	    case 8:
	    case 11:
	    case 12:
	    case 13:
		instruction = new Instruction(18);
		instruction.setConstant(constants[i_17_ - 1]);
		i_18_ = 1;
		break;
	    case 9:
	    case 10:
	    case 14:
	    case 15:
		instruction = new Instruction(20);
		instruction.setConstant(constants[i_17_ - 1]);
		i_18_ = 1;
		break;
	    case 16:
		instruction = new Instruction(18);
		instruction
		    .setConstant(new Integer(datainputstream.readByte()));
		i_18_ = 2;
		break;
	    case 17:
		instruction = new Instruction(18);
		instruction
		    .setConstant(new Integer(datainputstream.readShort()));
		i_18_ = 3;
		break;
	    case 18: {
		int i_30_ = datainputstream.readUnsignedByte();
		int i_31_ = constantpool.getTag(i_30_);
		if (i_31_ != 8 && i_31_ != 3 && i_31_ != 4)
		    throw new ClassFormatException("wrong constant tag: "
						   + i_31_);
		instruction = new Instruction(i_17_);
		instruction.setConstant(constantpool.getConstant(i_30_));
		i_18_ = 2;
		break;
	    }
	    case 19: {
		int i_32_ = datainputstream.readUnsignedShort();
		int i_33_ = constantpool.getTag(i_32_);
		if (i_33_ != 8 && i_33_ != 3 && i_33_ != 4)
		    throw new ClassFormatException("wrong constant tag: "
						   + i_33_);
		instruction = new Instruction(18);
		instruction.setConstant(constantpool.getConstant(i_32_));
		i_18_ = 3;
		break;
	    }
	    case 20: {
		int i_34_ = datainputstream.readUnsignedShort();
		int i_35_ = constantpool.getTag(i_34_);
		if (i_35_ != 5 && i_35_ != 6)
		    throw new ClassFormatException("wrong constant tag: "
						   + i_35_);
		instruction = new Instruction(i_17_);
		instruction.setConstant(constantpool.getConstant(i_34_));
		i_18_ = 3;
		break;
	    }
	    case 132: {
		int i_36_ = datainputstream.readUnsignedByte();
		if (i_36_ >= maxLocals)
		    throw new ClassFormatError("Invalid local slot " + i_36_);
		instruction = new Instruction(i_17_);
		instruction.setLocalSlot(i_36_);
		instruction.setIncrement(datainputstream.readByte());
		i_18_ = 3;
		if ((GlobalOptions.debuggingFlags & 0x1) != 0)
		    GlobalOptions.err
			.print(" " + i_36_ + " " + instruction.getIncrement());
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
	    case 167:
	    case 168:
	    case 198:
	    case 199:
		instruction = new Instruction(i_17_);
		i_18_ = 3;
		is[i_16_] = new int[] { i_16_ + datainputstream.readShort() };
		if ((GlobalOptions.debuggingFlags & 0x1) != 0)
		    GlobalOptions.err.print(" " + is[i_16_][0]);
		break;
	    case 200:
	    case 201:
		instruction = new Instruction(i_17_ - 33);
		i_18_ = 5;
		is[i_16_] = new int[] { i_16_ + datainputstream.readInt() };
		if ((GlobalOptions.debuggingFlags & 0x1) != 0)
		    GlobalOptions.err.print(" " + is[i_16_][0]);
		break;
	    case 170: {
		i_18_ = 3 - i_16_ % 4;
		datainputstream.readFully(new byte[i_18_]);
		int i_37_ = datainputstream.readInt();
		int i_38_ = datainputstream.readInt();
		int i_39_ = datainputstream.readInt();
		int[] is_40_ = new int[i_39_ - i_38_ + 1];
		int i_41_ = 0;
		for (int i_42_ = 0; i_42_ < is_40_.length; i_42_++) {
		    is_40_[i_42_] = datainputstream.readInt();
		    if (is_40_[i_42_] != i_37_)
			i_41_++;
		}
		instruction = new Instruction(171);
		is[i_16_] = new int[i_41_ + 1];
		int[] is_43_ = new int[i_41_];
		int i_44_ = 0;
		for (int i_45_ = 0; i_45_ < is_40_.length; i_45_++) {
		    if (is_40_[i_45_] != i_37_) {
			is_43_[i_44_] = i_45_ + i_38_;
			is[i_16_][i_44_] = i_16_ + is_40_[i_45_];
			i_44_++;
		    }
		}
		is[i_16_][i_41_] = i_16_ + i_37_;
		instruction.setValues(is_43_);
		i_18_ += 13 + 4 * (i_39_ - i_38_ + 1);
		break;
	    }
	    case 171: {
		i_18_ = 3 - i_16_ % 4;
		datainputstream.readFully(new byte[i_18_]);
		int i_46_ = datainputstream.readInt();
		int i_47_ = datainputstream.readInt();
		instruction = new Instruction(i_17_);
		is[i_16_] = new int[i_47_ + 1];
		int[] is_48_ = new int[i_47_];
		for (int i_49_ = 0; i_49_ < i_47_; i_49_++) {
		    is_48_[i_49_] = datainputstream.readInt();
		    if (i_49_ > 0 && is_48_[i_49_ - 1] >= is_48_[i_49_])
			throw new ClassFormatException
				  ("lookupswitch not sorted");
		    is[i_16_][i_49_] = i_16_ + datainputstream.readInt();
		}
		is[i_16_][i_47_] = i_16_ + i_46_;
		instruction.setValues(is_48_);
		i_18_ += 9 + 8 * i_47_;
		break;
	    }
	    case 178:
	    case 179:
	    case 180:
	    case 181:
	    case 182:
	    case 183:
	    case 184: {
		int i_50_ = datainputstream.readUnsignedShort();
		int i_51_ = constantpool.getTag(i_50_);
		if (i_17_ < 182) {
		    if (i_51_ != 9)
			throw new ClassFormatException("field tag mismatch: "
						       + i_51_);
		} else if (i_51_ != 10)
		    throw new ClassFormatException("method tag mismatch: "
						   + i_51_);
		Reference reference = constantpool.getRef(i_50_);
		if (reference.getName().charAt(0) == '<'
		    && (!reference.getName().equals("<init>") || i_17_ != 183))
		    throw new ClassFormatException
			      ("Illegal call of special method/field "
			       + reference);
		instruction = new Instruction(i_17_);
		instruction.setReference(reference);
		i_18_ = 3;
		if ((GlobalOptions.debuggingFlags & 0x1) != 0)
		    GlobalOptions.err.print(" " + reference);
		break;
	    }
	    case 185: {
		int i_52_ = datainputstream.readUnsignedShort();
		int i_53_ = constantpool.getTag(i_52_);
		if (i_53_ != 11)
		    throw new ClassFormatException("interface tag mismatch: "
						   + i_53_);
		Reference reference = constantpool.getRef(i_52_);
		if (reference.getName().charAt(0) == '<')
		    throw new ClassFormatException
			      ("Illegal call of special method " + reference);
		int i_54_ = datainputstream.readUnsignedByte();
		if (TypeSignature.getArgumentSize(reference.getType())
		    != i_54_ - 1)
		    throw new ClassFormatException("Interface nargs mismatch: "
						   + reference + " vs. "
						   + i_54_);
		if (datainputstream.readUnsignedByte() != 0)
		    throw new ClassFormatException
			      ("Interface reserved param not zero");
		instruction = new Instruction(i_17_);
		instruction.setReference(reference);
		i_18_ = 5;
		if ((GlobalOptions.debuggingFlags & 0x1) != 0)
		    GlobalOptions.err.print(" " + reference);
		break;
	    }
	    case 187:
	    case 192:
	    case 193: {
		String string
		    = constantpool
			  .getClassType(datainputstream.readUnsignedShort());
		if (i_17_ == 187 && string.charAt(0) == '[')
		    throw new ClassFormatException
			      ("Can't create array with opc_new");
		instruction = new Instruction(i_17_);
		instruction.setClazzType(string);
		i_18_ = 3;
		if ((GlobalOptions.debuggingFlags & 0x1) != 0)
		    GlobalOptions.err.print(" " + string);
		break;
	    }
	    case 197: {
		String string
		    = constantpool
			  .getClassType(datainputstream.readUnsignedShort());
		int i_55_ = datainputstream.readUnsignedByte();
		if (i_55_ == 0)
		    throw new ClassFormatException
			      ("multianewarray dimension is 0.");
		for (int i_56_ = 0; i_56_ < i_55_; i_56_++) {
		    if (string.charAt(i_56_) != '[')
			throw new ClassFormatException
				  ("multianewarray called for non array:"
				   + string);
		}
		instruction = new Instruction(i_17_);
		instruction.setClazzType(string);
		instruction.setDimensions(i_55_);
		i_18_ = 4;
		if ((GlobalOptions.debuggingFlags & 0x1) != 0)
		    GlobalOptions.err.print(" " + string + " " + i_55_);
		break;
	    }
	    case 189: {
		String string
		    = constantpool
			  .getClassType(datainputstream.readUnsignedShort());
		instruction = new Instruction(197);
		instruction.setClazzType(("[" + string).intern());
		instruction.setDimensions(1);
		i_18_ = 3;
		if ((GlobalOptions.debuggingFlags & 0x1) != 0)
		    GlobalOptions.err.print(" " + string);
		break;
	    }
	    case 188: {
		char c = "ZCFDBSIJ"
			     .charAt(datainputstream.readUnsignedByte() - 4);
		String string = new String(new char[] { '[', c });
		if ((GlobalOptions.debuggingFlags & 0x1) != 0)
		    GlobalOptions.err.print(" " + string);
		instruction = new Instruction(197);
		instruction.setClazzType(string);
		instruction.setDimensions(1);
		i_18_ = 2;
		break;
	    }
	    case 0:
	    case 46:
	    case 47:
	    case 48:
	    case 49:
	    case 50:
	    case 51:
	    case 52:
	    case 53:
	    case 79:
	    case 80:
	    case 81:
	    case 82:
	    case 83:
	    case 84:
	    case 85:
	    case 86:
	    case 87:
	    case 88:
	    case 89:
	    case 90:
	    case 91:
	    case 92:
	    case 93:
	    case 94:
	    case 95:
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
	    case 116:
	    case 117:
	    case 118:
	    case 119:
	    case 120:
	    case 121:
	    case 122:
	    case 123:
	    case 124:
	    case 125:
	    case 126:
	    case 127:
	    case 128:
	    case 129:
	    case 130:
	    case 131:
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
	    case 144:
	    case 145:
	    case 146:
	    case 147:
	    case 148:
	    case 149:
	    case 150:
	    case 151:
	    case 152:
	    case 172:
	    case 173:
	    case 174:
	    case 175:
	    case 176:
	    case 177:
	    case 190:
	    case 191:
	    case 194:
	    case 195:
		instruction = new Instruction(i_17_);
		i_18_ = 1;
		break;
	    default:
		throw new ClassFormatError("Invalid opcode " + i_17_);
	    }
	    if ((GlobalOptions.debuggingFlags & 0x1) != 0)
		GlobalOptions.err.println();
	    instrs[i_16_] = instruction;
	    this.instructions.add(instruction);
	    i_16_ += i_18_;
	    this.instructions.setLastAddr(i_16_);
	}
	if (i_16_ != i)
	    throw new ClassFormatError("last instruction too long");
	Iterator iterator = this.instructions.iterator();
	while (iterator.hasNext()) {
	    Instruction instruction = (Instruction) iterator.next();
	    int i_57_ = instruction.getAddr();
	    if (is[i_57_] != null) {
		int i_58_ = is[i_57_].length;
		Instruction[] instructions = new Instruction[i_58_];
		for (int i_59_ = 0; i_59_ < i_58_; i_59_++) {
		    int i_60_ = is[i_57_][i_59_];
		    if (i_60_ < 0 || i_60_ > i || instrs[i_60_] == null)
			throw new ClassFormatException
				  ("Illegal jump target at " + this + "@"
				   + i_57_);
		    instructions[i_59_] = instrs[i_60_];
		}
		instruction.setSuccs(instructions);
	    }
	}
	Object object = null;
	int i_61_ = datainputstream.readUnsignedShort();
	exceptionHandlers = new Handler[i_61_];
	for (int i_62_ = 0; i_62_ < i_61_; i_62_++) {
	    exceptionHandlers[i_62_] = new Handler();
	    exceptionHandlers[i_62_].start
		= instrs[datainputstream.readUnsignedShort()];
	    exceptionHandlers[i_62_].end
		= instrs[datainputstream.readUnsignedShort()].getPrevByAddr();
	    exceptionHandlers[i_62_].catcher
		= instrs[datainputstream.readUnsignedShort()];
	    int i_63_ = datainputstream.readUnsignedShort();
	    exceptionHandlers[i_62_].type
		= i_63_ == 0 ? null : constantpool.getClassName(i_63_);
	    if (exceptionHandlers[i_62_].catcher.getOpcode() == 191) {
		i_61_--;
		i_62_--;
	    }
	}
	if (i_61_ < exceptionHandlers.length) {
	    Handler[] handlers = new Handler[i_61_];
	    System.arraycopy(exceptionHandlers, 0, handlers, 0, i_61_);
	    exceptionHandlers = handlers;
	}
	this.readAttributes(constantpool, datainputstream, 255);
	instrs = null;
    }
    
    public void dumpCode(PrintWriter printwriter) {
	Iterator iterator = this.instructions.iterator();
	while (iterator.hasNext()) {
	    Instruction instruction = (Instruction) iterator.next();
	    printwriter.println(instruction.getDescription() + " "
				+ Integer.toHexString(this.hashCode()));
	    Instruction[] instructions = instruction.getSuccs();
	    if (instructions != null) {
		printwriter.print("\tsuccs: " + instructions[0]);
		for (int i = 1; i < instructions.length; i++)
		    printwriter.print(", " + instructions[i]);
		printwriter.println();
	    }
	    if (instruction.getPreds() != null) {
		printwriter.print("\tpreds: " + instruction.getPreds()[0]);
		for (int i = 1; i < instruction.getPreds().length; i++)
		    printwriter.print(", " + instruction.getPreds()[i]);
		printwriter.println();
	    }
	}
	for (int i = 0; i < exceptionHandlers.length; i++)
	    printwriter.println("catch " + exceptionHandlers[i].type + " from "
				+ exceptionHandlers[i].start + " to "
				+ exceptionHandlers[i].end + " catcher "
				+ exceptionHandlers[i].catcher);
    }
    
    public void reserveSmallConstants
	(GrowableConstantPool growableconstantpool) {
	Iterator iterator = instructions.iterator();
    while_1_:
	while (iterator.hasNext()) {
	    Instruction instruction = (Instruction) iterator.next();
	    if (instruction.getOpcode() == 18) {
		Object object = instruction.getConstant();
		if (object != null) {
		    for (int i = 1; i < constants.length; i++) {
			if (object.equals(constants[i]))
			    continue while_1_;
		    }
		    if (object instanceof Integer) {
			int i = ((Integer) object).intValue();
			if (i >= -32768 && i <= 32767)
			    continue;
		    }
		    growableconstantpool.reserveConstant(object);
		}
	    }
	}
    }
    
    private void calculateMaxStack() {
	maxStack = 0;
	int[] is = new int[this.instructions.getCodeLength()];
	int[] is_64_ = new int[2];
	Stack stack = new Stack();
	for (int i = 0; i < is.length; i++)
	    is[i] = -1;
	is[0] = 0;
	stack.push(this.instructions.get(0));
	while (!stack.isEmpty()) {
	    Instruction instruction = (Instruction) stack.pop();
	    Instruction instruction_65_ = instruction.getNextByAddr();
	    Instruction[] instructions = instruction.getSuccs();
	    int i = instruction.getAddr();
	    instruction.getStackPopPush(is_64_);
	    int i_66_ = is[i] - is_64_[0] + is_64_[1];
	    if (maxStack < i_66_)
		maxStack = i_66_;
	    if (instruction.getOpcode() == 168) {
		if (is[instruction_65_.getAddr()] == -1) {
		    is[instruction_65_.getAddr()] = i_66_ - 1;
		    stack.push(instruction_65_);
		}
		if (is[instructions[0].getAddr()] == -1) {
		    is[instructions[0].getAddr()] = i_66_;
		    stack.push(instructions[0]);
		}
	    } else {
		if (instructions != null) {
		    for (int i_67_ = 0; i_67_ < instructions.length; i_67_++) {
			if (is[instructions[i_67_].getAddr()] == -1) {
			    is[instructions[i_67_].getAddr()] = i_66_;
			    stack.push(instructions[i_67_]);
			}
		    }
		}
		if (!instruction.doesAlwaysJump()
		    && is[instruction_65_.getAddr()] == -1) {
		    is[instruction_65_.getAddr()] = i_66_;
		    stack.push(instruction_65_);
		}
	    }
	    for (int i_68_ = 0; i_68_ < exceptionHandlers.length; i_68_++) {
		if (exceptionHandlers[i_68_].start.compareTo(instruction) <= 0
		    && (exceptionHandlers[i_68_].end.compareTo(instruction)
			>= 0)) {
		    int i_69_ = exceptionHandlers[i_68_].catcher.getAddr();
		    if (is[i_69_] == -1) {
			is[i_69_] = 1;
			stack.push(exceptionHandlers[i_68_].catcher);
		    }
		}
	    }
	}
    }
    
    public void prepareWriting(GrowableConstantPool growableconstantpool) {
	int i = 0;
	maxLocals = ((methodInfo.isStatic() ? 0 : 1)
		     + TypeSignature.getArgumentSize(methodInfo.getType()));
	Iterator iterator = instructions.iterator();
	while (iterator.hasNext()) {
	    Instruction instruction = (Instruction) iterator.next();
	    int i_70_ = instruction.getOpcode();
	    instruction.setAddr(i);
	    int i_71_;
	switch_0_:
	    switch (i_70_) {
	    case 18:
	    case 20: {
		Object object = instruction.getConstant();
		if (object == null)
		    i_71_ = 1;
		else {
		    for (int i_72_ = 1; i_72_ < constants.length; i_72_++) {
			if (object.equals(constants[i_72_])) {
			    i_71_ = 1;
			    break switch_0_;
			}
		    }
		    if (i_70_ == 20) {
			growableconstantpool.putLongConstant(object);
			i_71_ = 3;
		    } else {
			if (object instanceof Integer) {
			    int i_73_ = ((Integer) object).intValue();
			    if (i_73_ >= -128 && i_73_ <= 127) {
				i_71_ = 2;
				break;
			    }
			    if (i_73_ >= -32768 && i_73_ <= 32767) {
				i_71_ = 3;
				break;
			    }
			}
			if (growableconstantpool.putConstant(object) < 256)
			    i_71_ = 2;
			else
			    i_71_ = 3;
		    }
		}
		break;
	    }
	    case 132: {
		int i_74_ = instruction.getLocalSlot();
		int i_75_ = instruction.getIncrement();
		if (i_74_ < 256 && i_75_ >= -128 && i_75_ <= 127)
		    i_71_ = 3;
		else
		    i_71_ = 6;
		if (i_74_ >= maxLocals)
		    maxLocals = i_74_ + 1;
		break;
	    }
	    case 21:
	    case 23:
	    case 25:
	    case 54:
	    case 56:
	    case 58: {
		int i_76_ = instruction.getLocalSlot();
		if (i_76_ < 4)
		    i_71_ = 1;
		else if (i_76_ < 256)
		    i_71_ = 2;
		else
		    i_71_ = 4;
		if (i_76_ >= maxLocals)
		    maxLocals = i_76_ + 1;
		break;
	    }
	    case 22:
	    case 24:
	    case 55:
	    case 57: {
		int i_77_ = instruction.getLocalSlot();
		if (i_77_ < 4)
		    i_71_ = 1;
		else if (i_77_ < 256)
		    i_71_ = 2;
		else
		    i_71_ = 4;
		if (i_77_ + 1 >= maxLocals)
		    maxLocals = i_77_ + 2;
		break;
	    }
	    case 169: {
		int i_78_ = instruction.getLocalSlot();
		if (i_78_ < 256)
		    i_71_ = 2;
		else
		    i_71_ = 4;
		if (i_78_ >= maxLocals)
		    maxLocals = i_78_ + 1;
		break;
	    }
	    case 171: {
		i_71_ = 3 - i % 4;
		int[] is = instruction.getValues();
		int i_79_ = is.length;
		if (i_79_ > 0) {
		    int i_80_ = is[i_79_ - 1] - is[0] + 1;
		    if (4 + i_80_ * 4 < 8 * i_79_) {
			i_71_ += 13 + 4 * i_80_;
			break;
		    }
		}
		i_71_ += 9 + 8 * i_79_;
		break;
	    }
	    case 167:
	    case 168: {
		int i_81_ = (instruction.getSingleSucc().getAddr()
			     - instruction.getAddr());
		if (i_81_ < -32768 || i_81_ > 32767) {
		    i_71_ = 5;
		    break;
		}
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
	    case 198:
	    case 199:
		i_71_ = 3;
		break;
	    case 197:
		if (instruction.getDimensions() == 1) {
		    String string = instruction.getClazzType().substring(1);
		    if ("ZCFDBSIJ".indexOf(string.charAt(0)) != -1)
			i_71_ = 2;
		    else {
			growableconstantpool.putClassType(string);
			i_71_ = 3;
		    }
		} else {
		    growableconstantpool
			.putClassType(instruction.getClazzType());
		    i_71_ = 4;
		}
		break;
	    case 178:
	    case 179:
	    case 180:
	    case 181:
		growableconstantpool.putRef(9, instruction.getReference());
		i_71_ = 3;
		break;
	    case 182:
	    case 183:
	    case 184:
		growableconstantpool.putRef(10, instruction.getReference());
		i_71_ = 3;
		break;
	    case 185:
		growableconstantpool.putRef(11, instruction.getReference());
		i_71_ = 5;
		break;
	    case 187:
	    case 192:
	    case 193:
		growableconstantpool.putClassType(instruction.getClazzType());
		i_71_ = 3;
		break;
	    case 0:
	    case 46:
	    case 47:
	    case 48:
	    case 49:
	    case 50:
	    case 51:
	    case 52:
	    case 53:
	    case 79:
	    case 80:
	    case 81:
	    case 82:
	    case 83:
	    case 84:
	    case 85:
	    case 86:
	    case 87:
	    case 88:
	    case 89:
	    case 90:
	    case 91:
	    case 92:
	    case 93:
	    case 94:
	    case 95:
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
	    case 116:
	    case 117:
	    case 118:
	    case 119:
	    case 120:
	    case 121:
	    case 122:
	    case 123:
	    case 124:
	    case 125:
	    case 126:
	    case 127:
	    case 128:
	    case 129:
	    case 130:
	    case 131:
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
	    case 144:
	    case 145:
	    case 146:
	    case 147:
	    case 148:
	    case 149:
	    case 150:
	    case 151:
	    case 152:
	    case 172:
	    case 173:
	    case 174:
	    case 175:
	    case 176:
	    case 177:
	    case 190:
	    case 191:
	    case 194:
	    case 195:
		i_71_ = 1;
		break;
	    default:
		throw new ClassFormatError("Invalid opcode " + i_70_);
	    }
	    i += i_71_;
	}
	instructions.setLastAddr(i);
	try {
	    calculateMaxStack();
	} catch (RuntimeException runtimeexception) {
	    runtimeexception.printStackTrace();
	    dumpCode(GlobalOptions.err);
	}
	for (int i_82_ = 0; i_82_ < exceptionHandlers.length; i_82_++) {
	    if (exceptionHandlers[i_82_].type != null)
		growableconstantpool
		    .putClassName(exceptionHandlers[i_82_].type);
	}
	if (lvt != null) {
	    growableconstantpool.putUTF8("LocalVariableTable");
	    for (int i_83_ = 0; i_83_ < lvt.length; i_83_++) {
		growableconstantpool.putUTF8(lvt[i_83_].name);
		growableconstantpool.putUTF8(lvt[i_83_].type);
	    }
	}
	if (lnt != null)
	    growableconstantpool.putUTF8("LineNumberTable");
	this.prepareAttributes(growableconstantpool);
    }
    
    protected int getKnownAttributeCount() {
	int i = 0;
	if (lvt != null)
	    i++;
	if (lnt != null)
	    i++;
	return i;
    }
    
    public void writeKnownAttributes
	(GrowableConstantPool growableconstantpool,
	 DataOutputStream dataoutputstream)
	throws IOException {
	if (lvt != null) {
	    dataoutputstream.writeShort(growableconstantpool
					    .putUTF8("LocalVariableTable"));
	    int i = lvt.length;
	    int i_84_ = 2 + 10 * i;
	    dataoutputstream.writeInt(i_84_);
	    dataoutputstream.writeShort(i);
	    for (int i_85_ = 0; i_85_ < i; i_85_++) {
		dataoutputstream.writeShort(lvt[i_85_].start.getAddr());
		dataoutputstream.writeShort(lvt[i_85_].end.getAddr()
					    + lvt[i_85_].end.getLength()
					    - lvt[i_85_].start.getAddr());
		dataoutputstream
		    .writeShort(growableconstantpool.putUTF8(lvt[i_85_].name));
		dataoutputstream
		    .writeShort(growableconstantpool.putUTF8(lvt[i_85_].type));
		dataoutputstream.writeShort(lvt[i_85_].slot);
	    }
	}
	if (lnt != null) {
	    dataoutputstream
		.writeShort(growableconstantpool.putUTF8("LineNumberTable"));
	    int i = lnt.length;
	    int i_86_ = 2 + 4 * i;
	    dataoutputstream.writeInt(i_86_);
	    dataoutputstream.writeShort(i);
	    for (int i_87_ = 0; i_87_ < i; i_87_++) {
		dataoutputstream.writeShort(lnt[i_87_].start.getAddr());
		dataoutputstream.writeShort(lnt[i_87_].linenr);
	    }
	}
    }
    
    public void write(GrowableConstantPool growableconstantpool,
		      DataOutputStream dataoutputstream) throws IOException {
	dataoutputstream.writeShort(maxStack);
	dataoutputstream.writeShort(maxLocals);
	dataoutputstream.writeInt(instructions.getCodeLength());
	Iterator iterator = instructions.iterator();
	while (iterator.hasNext()) {
	    Instruction instruction = (Instruction) iterator.next();
	    int i = instruction.getOpcode();
	switch_1_:
	    switch (i) {
	    case 21:
	    case 22:
	    case 23:
	    case 24:
	    case 25:
	    case 54:
	    case 55:
	    case 56:
	    case 57:
	    case 58: {
		int i_88_ = instruction.getLocalSlot();
		if (i_88_ < 4) {
		    if (i < 54)
			dataoutputstream.writeByte(26 + 4 * (i - 21) + i_88_);
		    else
			dataoutputstream.writeByte(59 + 4 * (i - 54) + i_88_);
		} else if (i_88_ < 256) {
		    dataoutputstream.writeByte(i);
		    dataoutputstream.writeByte(i_88_);
		} else {
		    dataoutputstream.writeByte(196);
		    dataoutputstream.writeByte(i);
		    dataoutputstream.writeShort(i_88_);
		}
		break;
	    }
	    case 169: {
		int i_89_ = instruction.getLocalSlot();
		if (i_89_ < 256) {
		    dataoutputstream.writeByte(i);
		    dataoutputstream.writeByte(i_89_);
		} else {
		    dataoutputstream.writeByte(196);
		    dataoutputstream.writeByte(i);
		    dataoutputstream.writeShort(i_89_);
		}
		break;
	    }
	    case 18:
	    case 20: {
		Object object = instruction.getConstant();
		if (object == null)
		    dataoutputstream.writeByte(1);
		else {
		    for (int i_90_ = 1; i_90_ < constants.length; i_90_++) {
			if (object.equals(constants[i_90_])) {
			    dataoutputstream.writeByte(1 + i_90_);
			    break switch_1_;
			}
		    }
		    if (i == 20) {
			dataoutputstream.writeByte(i);
			dataoutputstream.writeShort
			    (growableconstantpool.putLongConstant(object));
		    } else {
			if (object instanceof Integer) {
			    int i_91_ = ((Integer) object).intValue();
			    if (i_91_ >= -128 && i_91_ <= 127) {
				dataoutputstream.writeByte(16);
				dataoutputstream
				    .writeByte(((Integer) object).intValue());
				break;
			    }
			    if (i_91_ >= -32768 && i_91_ <= 32767) {
				dataoutputstream.writeByte(17);
				dataoutputstream
				    .writeShort(((Integer) object).intValue());
				break;
			    }
			}
			if (instruction.getLength() == 2) {
			    dataoutputstream.writeByte(18);
			    dataoutputstream.writeByte
				(growableconstantpool.putConstant(object));
			} else {
			    dataoutputstream.writeByte(19);
			    dataoutputstream.writeShort
				(growableconstantpool.putConstant(object));
			}
		    }
		}
		break;
	    }
	    case 132: {
		int i_92_ = instruction.getLocalSlot();
		int i_93_ = instruction.getIncrement();
		if (instruction.getLength() == 3) {
		    dataoutputstream.writeByte(i);
		    dataoutputstream.writeByte(i_92_);
		    dataoutputstream.writeByte(i_93_);
		} else {
		    dataoutputstream.writeByte(196);
		    dataoutputstream.writeByte(i);
		    dataoutputstream.writeShort(i_92_);
		    dataoutputstream.writeShort(i_93_);
		}
		break;
	    }
	    case 167:
	    case 168:
		if (instruction.getLength() == 5) {
		    dataoutputstream.writeByte(i + 33);
		    dataoutputstream.writeInt(instruction.getSingleSucc()
						  .getAddr()
					      - instruction.getAddr());
		    break;
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
	    case 198:
	    case 199:
		dataoutputstream.writeByte(i);
		dataoutputstream.writeShort(instruction.getSingleSucc()
						.getAddr()
					    - instruction.getAddr());
		break;
	    case 171: {
		int i_94_ = 3 - instruction.getAddr() % 4;
		int[] is = instruction.getValues();
		int i_95_ = is.length;
		int i_96_ = (instruction.getSuccs()[i_95_].getAddr()
			     - instruction.getAddr());
		if (i_95_ > 0) {
		    int i_97_ = is[i_95_ - 1] - is[0] + 1;
		    if (4 + i_97_ * 4 < 8 * i_95_) {
			dataoutputstream.writeByte(170);
			dataoutputstream.write(new byte[i_94_]);
			dataoutputstream.writeInt(i_96_);
			dataoutputstream.writeInt(is[0]);
			dataoutputstream.writeInt(is[i_95_ - 1]);
			int i_98_ = is[0];
			for (int i_99_ = 0; i_99_ < i_95_; i_99_++) {
			    while (i_98_++ < is[i_99_])
				dataoutputstream.writeInt(i_96_);
			    dataoutputstream.writeInt(instruction.getSuccs
							  ()[i_99_].getAddr()
						      - instruction.getAddr());
			}
			break;
		    }
		}
		dataoutputstream.writeByte(171);
		dataoutputstream.write(new byte[i_94_]);
		dataoutputstream.writeInt(i_96_);
		dataoutputstream.writeInt(i_95_);
		for (int i_100_ = 0; i_100_ < i_95_; i_100_++) {
		    dataoutputstream.writeInt(is[i_100_]);
		    dataoutputstream.writeInt(instruction.getSuccs()
						  [i_100_].getAddr()
					      - instruction.getAddr());
		}
		break;
	    }
	    case 178:
	    case 179:
	    case 180:
	    case 181:
		dataoutputstream.writeByte(i);
		dataoutputstream.writeShort
		    (growableconstantpool.putRef(9,
						 instruction.getReference()));
		break;
	    case 182:
	    case 183:
	    case 184:
	    case 185: {
		Reference reference = instruction.getReference();
		dataoutputstream.writeByte(i);
		if (i == 185) {
		    dataoutputstream.writeShort(growableconstantpool
						    .putRef(11, reference));
		    dataoutputstream.writeByte
			(TypeSignature.getArgumentSize(reference.getType())
			 + 1);
		    dataoutputstream.writeByte(0);
		} else
		    dataoutputstream.writeShort(growableconstantpool
						    .putRef(10, reference));
		break;
	    }
	    case 187:
	    case 192:
	    case 193:
		dataoutputstream.writeByte(i);
		dataoutputstream.writeShort(growableconstantpool.putClassType
					    (instruction.getClazzType()));
		break;
	    case 197:
		if (instruction.getDimensions() == 1) {
		    String string = instruction.getClazzType().substring(1);
		    int i_101_ = "ZCFDBSIJ".indexOf(string.charAt(0));
		    if (i_101_ != -1) {
			dataoutputstream.writeByte(188);
			dataoutputstream.writeByte(i_101_ + 4);
		    } else {
			dataoutputstream.writeByte(189);
			dataoutputstream.writeShort(growableconstantpool
							.putClassType(string));
		    }
		} else {
		    dataoutputstream.writeByte(i);
		    dataoutputstream.writeShort
			(growableconstantpool
			     .putClassType(instruction.getClazzType()));
		    dataoutputstream.writeByte(instruction.getDimensions());
		}
		break;
	    case 0:
	    case 46:
	    case 47:
	    case 48:
	    case 49:
	    case 50:
	    case 51:
	    case 52:
	    case 53:
	    case 79:
	    case 80:
	    case 81:
	    case 82:
	    case 83:
	    case 84:
	    case 85:
	    case 86:
	    case 87:
	    case 88:
	    case 89:
	    case 90:
	    case 91:
	    case 92:
	    case 93:
	    case 94:
	    case 95:
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
	    case 116:
	    case 117:
	    case 118:
	    case 119:
	    case 120:
	    case 121:
	    case 122:
	    case 123:
	    case 124:
	    case 125:
	    case 126:
	    case 127:
	    case 128:
	    case 129:
	    case 130:
	    case 131:
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
	    case 144:
	    case 145:
	    case 146:
	    case 147:
	    case 148:
	    case 149:
	    case 150:
	    case 151:
	    case 152:
	    case 172:
	    case 173:
	    case 174:
	    case 175:
	    case 176:
	    case 177:
	    case 190:
	    case 191:
	    case 194:
	    case 195:
		dataoutputstream.writeByte(i);
		break;
	    default:
		throw new ClassFormatError("Invalid opcode " + i);
	    }
	}
	dataoutputstream.writeShort(exceptionHandlers.length);
	for (int i = 0; i < exceptionHandlers.length; i++) {
	    dataoutputstream.writeShort(exceptionHandlers[i].start.getAddr());
	    dataoutputstream.writeShort(exceptionHandlers[i].end.getNextByAddr
					    ().getAddr());
	    dataoutputstream
		.writeShort(exceptionHandlers[i].catcher.getAddr());
	    dataoutputstream.writeShort(exceptionHandlers[i].type == null ? 0
					: (growableconstantpool.putClassName
					   (exceptionHandlers[i].type)));
	}
	this.writeAttributes(growableconstantpool, dataoutputstream);
    }
    
    public void dropInfo(int i) {
	if ((i & 0x10) != 0) {
	    lvt = null;
	    lnt = null;
	}
	super.dropInfo(i);
    }
    
    public int getSize() {
	int i = 0;
	if (lvt != null)
	    i += 8 + lvt.length * 10;
	if (lnt != null)
	    i += 8 + lnt.length * 4;
	return (10 + instructions.getCodeLength()
		+ exceptionHandlers.length * 8 + this.getAttributeSize() + i);
    }
    
    public int getMaxStack() {
	return maxStack;
    }
    
    public int getMaxLocals() {
	return maxLocals;
    }
    
    public MethodInfo getMethodInfo() {
	return methodInfo;
    }
    
    public List getInstructions() {
	return instructions;
    }
    
    public Handler[] getExceptionHandlers() {
	return exceptionHandlers;
    }
    
    public LocalVariableInfo[] getLocalVariableTable() {
	return lvt;
    }
    
    public LineNumber[] getLineNumberTable() {
	return lnt;
    }
    
    public void setExceptionHandlers(Handler[] handlers) {
	exceptionHandlers = handlers;
    }
    
    public void setLocalVariableTable(LocalVariableInfo[] localvariableinfos) {
	lvt = localvariableinfos;
    }
    
    public void setLineNumberTable(LineNumber[] linenumbers) {
	lnt = linenumbers;
    }
    
    public String toString() {
	return "Bytecode " + methodInfo;
    }
}
