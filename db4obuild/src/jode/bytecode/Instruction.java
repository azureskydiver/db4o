/* Instruction - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.bytecode;
import jode.AssertError;

public final class Instruction implements Opcodes
{
    private int opcode;
    private int shortData;
    private int addr;
    private Object objData;
    private Object succs;
    private Instruction[] preds;
    Instruction nextByAddr;
    Instruction prevByAddr;
    private Object tmpInfo;
    private static final String stackDelta
	= "\0\010\010\010\010\010\010\010\010\020\020\010\010\010\020\020\010\010\010\010\020\010\020\010\020\010\010\010\010\010\020\020\020\020\010\010\010\010\020\020\020\020\010\010\010\010\n\022\n\022\n\n\n\n\001\002\001\002\001\001\001\001\001\002\002\002\002\001\001\001\001\002\002\002\002\001\001\001\001\003\004\003\004\003\003\003\003\001\002\021\032#\"+4\022\n\024\n\024\n\024\n\024\n\024\n\024\n\024\n\024\n\024\n\024\t\022\t\022\n\023\n\023\n\023\n\024\n\024\n\024\0\021\t\021\n\n\022\t\021\021\n\022\n\t\t\t\014\n\n\014\014\001\001\001\001\001\001\002\002\002\002\002\002\002\002\0\010\0\001\001\001\002\001\002\001\0@@@@@@@@\u007f\010\t\t\t\001\t\t\001\001\u007f@\001\001\0\010";
    
    public Instruction(int i) {
	opcode = i;
    }
    
    public final int getOpcode() {
	return opcode;
    }
    
    public final int getAddr() {
	return addr;
    }
    
    public final int getNextAddr() {
	return nextByAddr.addr;
    }
    
    public final int getLength() {
	return getNextAddr() - addr;
    }
    
    final void setAddr(int i) {
	addr = i;
    }
    
    public final boolean hasLocalSlot() {
	return (opcode == 132 || opcode == 169 || opcode >= 21 && opcode <= 25
		|| opcode >= 54 && opcode <= 58);
    }
    
    public final int getLocalSlot() {
	return shortData;
    }
    
    public final void setLocalSlot(int i) {
	shortData = i;
    }
    
    public final int getIncrement() {
	return ((Short) objData).shortValue();
    }
    
    public final void setIncrement(int i) {
	objData = new Short((short) i);
    }
    
    public final int getDimensions() {
	return shortData;
    }
    
    public final void setDimensions(int i) {
	shortData = i;
    }
    
    public final Object getConstant() {
	return objData;
    }
    
    public final void setConstant(Object object) {
	objData = object;
    }
    
    public final Reference getReference() {
	return (Reference) objData;
    }
    
    public final void setReference(Reference reference) {
	objData = reference;
    }
    
    public final String getClazzType() {
	return (String) objData;
    }
    
    public final void setClazzType(String string) {
	objData = string;
    }
    
    public final int[] getValues() {
	return (int[]) objData;
    }
    
    public final void setValues(int[] is) {
	objData = is;
    }
    
    public final boolean doesAlwaysJump() {
	switch (opcode) {
	case 167:
	case 168:
	case 169:
	case 170:
	case 171:
	case 172:
	case 173:
	case 174:
	case 175:
	case 176:
	case 177:
	case 191:
	    return true;
	default:
	    return false;
	}
    }
    
    public final Instruction[] getPreds() {
	return preds;
    }
    
    public boolean hasSuccs() {
	return succs != null;
    }
    
    public final Instruction[] getSuccs() {
	if (succs instanceof Instruction)
	    return new Instruction[] { (Instruction) succs };
	return (Instruction[]) succs;
    }
    
    public final Instruction getSingleSucc() {
	return (Instruction) succs;
    }
    
    public final Instruction getPrevByAddr() {
	if (prevByAddr.opcode == 254)
	    return null;
	return prevByAddr;
    }
    
    public final Instruction getNextByAddr() {
	if (nextByAddr.opcode == 254)
	    return null;
	return nextByAddr;
    }
    
    public final Object getTmpInfo() {
	return tmpInfo;
    }
    
    public final void setTmpInfo(Object object) {
	tmpInfo = object;
    }
    
    final void removeSuccs() {
	if (succs != null) {
	    if (succs instanceof Instruction[]) {
		Instruction[] instructions = (Instruction[]) succs;
		for (int i = 0; i < instructions.length; i++) {
		    if (instructions[i] != null)
			instructions[i].removePredecessor(this);
		}
	    } else
		((Instruction) succs).removePredecessor(this);
	    succs = null;
	}
    }
    
    private final void promoteSuccs(Instruction instruction_0_,
				    Instruction instruction_1_) {
	if (succs == instruction_0_)
	    succs = instruction_1_;
	else if (succs instanceof Instruction[]) {
	    Instruction[] instructions = (Instruction[]) succs;
	    for (int i = 0; i < instructions.length; i++) {
		if (instructions[i] == instruction_0_)
		    instructions[i] = instruction_1_;
	    }
	}
    }
    
    public final void setSuccs(Object object) {
	if (succs != object) {
	    removeSuccs();
	    if (object != null) {
		if (object instanceof Instruction[]) {
		    Instruction[] instructions = (Instruction[]) object;
		    switch (instructions.length) {
		    case 0:
			break;
		    case 1:
			succs = instructions[0];
			instructions[0].addPredecessor(this);
			break;
		    default:
			succs = instructions;
			for (int i = 0; i < instructions.length; i++)
			    instructions[i].addPredecessor(this);
		    }
		} else {
		    succs = object;
		    ((Instruction) object).addPredecessor(this);
		}
	    }
	}
    }
    
    void addPredecessor(Instruction instruction_2_) {
	if (preds == null)
	    preds = new Instruction[] { instruction_2_ };
	else {
	    int i = preds.length;
	    Instruction[] instructions = new Instruction[i + 1];
	    System.arraycopy(preds, 0, instructions, 0, i);
	    instructions[i] = instruction_2_;
	    preds = instructions;
	}
    }
    
    void removePredecessor(Instruction instruction_3_) {
	int i = preds.length;
	if (i == 1) {
	    if (preds[0] != instruction_3_)
		throw new AssertError("removing not existing predecessor");
	    preds = null;
	} else {
	    Instruction[] instructions = new Instruction[i - 1];
	    int i_4_;
	    for (i_4_ = 0; preds[i_4_] != instruction_3_; i_4_++)
		instructions[i_4_] = preds[i_4_];
	    System.arraycopy(preds, i_4_ + 1, instructions, i_4_,
			     i - i_4_ - 1);
	    preds = instructions;
	}
    }
    
    public final void replaceInstruction(Instruction instruction_5_,
					 BytecodeInfo bytecodeinfo) {
	removeSuccs();
	instruction_5_.addr = addr;
	nextByAddr.prevByAddr = instruction_5_;
	instruction_5_.nextByAddr = nextByAddr;
	prevByAddr.nextByAddr = instruction_5_;
	instruction_5_.prevByAddr = prevByAddr;
	prevByAddr = null;
	nextByAddr = null;
	if (preds != null) {
	    for (int i = 0; i < preds.length; i++)
		preds[i].promoteSuccs(this, instruction_5_);
	    instruction_5_.preds = preds;
	    preds = null;
	}
	Handler[] handlers = bytecodeinfo.getExceptionHandlers();
	for (int i = 0; i < handlers.length; i++) {
	    if (handlers[i].start == this)
		handlers[i].start = instruction_5_;
	    if (handlers[i].end == this)
		handlers[i].end = instruction_5_;
	    if (handlers[i].catcher == this)
		handlers[i].catcher = instruction_5_;
	}
	LocalVariableInfo[] localvariableinfos
	    = bytecodeinfo.getLocalVariableTable();
	if (localvariableinfos != null) {
	    for (int i = 0; i < localvariableinfos.length; i++) {
		if (localvariableinfos[i].start == this)
		    localvariableinfos[i].start = instruction_5_;
		if (localvariableinfos[i].end == this)
		    localvariableinfos[i].end = instruction_5_;
	    }
	}
	LineNumber[] linenumbers = bytecodeinfo.getLineNumberTable();
	if (linenumbers != null) {
	    for (int i = 0; i < linenumbers.length; i++) {
		if (linenumbers[i].start == this)
		    linenumbers[i].start = instruction_5_;
	    }
	}
    }
    
    void appendInstruction(Instruction instruction_6_,
			   BytecodeInfo bytecodeinfo) {
	instruction_6_.addr = nextByAddr.addr;
	instruction_6_.nextByAddr = nextByAddr;
	nextByAddr.prevByAddr = instruction_6_;
	instruction_6_.prevByAddr = this;
	nextByAddr = instruction_6_;
	Handler[] handlers = bytecodeinfo.getExceptionHandlers();
	if (handlers != null) {
	    for (int i = 0; i < handlers.length; i++) {
		if (handlers[i].end == this)
		    handlers[i].end = instruction_6_;
	    }
	}
    }
    
    void removeInstruction(BytecodeInfo bytecodeinfo) {
	prevByAddr.nextByAddr = nextByAddr;
	nextByAddr.prevByAddr = prevByAddr;
	removeSuccs();
	if (preds != null) {
	    for (int i = 0; i < preds.length; i++)
		preds[i].promoteSuccs(this, nextByAddr);
	    if (nextByAddr.preds == null)
		nextByAddr.preds = preds;
	    else {
		Instruction[] instructions
		    = new Instruction[nextByAddr.preds.length + preds.length];
		System.arraycopy(nextByAddr.preds, 0, instructions, 0,
				 nextByAddr.preds.length);
		System.arraycopy(preds, 0, instructions,
				 nextByAddr.preds.length, preds.length);
		nextByAddr.preds = instructions;
	    }
	    preds = null;
	}
	Handler[] handlers = bytecodeinfo.getExceptionHandlers();
	for (int i = 0; i < handlers.length; i++) {
	    if (handlers[i].start == this && handlers[i].end == this) {
		Handler[] handlers_7_ = new Handler[handlers.length - 1];
		System.arraycopy(handlers, 0, handlers_7_, 0, i);
		System.arraycopy(handlers, i + 1, handlers_7_, i,
				 handlers.length - (i + 1));
		handlers = handlers_7_;
		bytecodeinfo.setExceptionHandlers(handlers_7_);
		i--;
	    } else {
		if (handlers[i].start == this)
		    handlers[i].start = nextByAddr;
		if (handlers[i].end == this)
		    handlers[i].end = prevByAddr;
		if (handlers[i].catcher == this)
		    handlers[i].catcher = nextByAddr;
	    }
	}
	LocalVariableInfo[] localvariableinfos
	    = bytecodeinfo.getLocalVariableTable();
	if (localvariableinfos != null) {
	    for (int i = 0; i < localvariableinfos.length; i++) {
		if (localvariableinfos[i].start == this
		    && localvariableinfos[i].end == this) {
		    LocalVariableInfo[] localvariableinfos_8_
			= new LocalVariableInfo[localvariableinfos.length - 1];
		    System.arraycopy(localvariableinfos, 0,
				     localvariableinfos_8_, 0, i);
		    System.arraycopy(localvariableinfos, i + 1,
				     localvariableinfos_8_, i,
				     localvariableinfos_8_.length - i);
		    localvariableinfos = localvariableinfos_8_;
		    bytecodeinfo.setLocalVariableTable(localvariableinfos_8_);
		    i--;
		} else {
		    if (localvariableinfos[i].start == this)
			localvariableinfos[i].start = nextByAddr;
		    if (localvariableinfos[i].end == this)
			localvariableinfos[i].end = prevByAddr;
		}
	    }
	}
	LineNumber[] linenumbers = bytecodeinfo.getLineNumberTable();
	if (linenumbers != null) {
	    for (int i = 0; i < linenumbers.length; i++) {
		if (linenumbers[i].start == this) {
		    if (nextByAddr.opcode == 254
			|| (i + 1 < linenumbers.length
			    && linenumbers[i + 1].start == nextByAddr)) {
			LineNumber[] linenumbers_9_
			    = new LineNumber[linenumbers.length - 1];
			System.arraycopy(linenumbers, 0, linenumbers_9_, 0, i);
			System.arraycopy(linenumbers, i + 1, linenumbers_9_, i,
					 linenumbers_9_.length - i);
			linenumbers = linenumbers_9_;
			bytecodeinfo.setLineNumberTable(linenumbers_9_);
			i--;
		    } else
			linenumbers[i].start = nextByAddr;
		}
	    }
	}
	prevByAddr = null;
	nextByAddr = null;
    }
    
    public int compareTo(Instruction instruction_10_) {
	if (addr != instruction_10_.addr)
	    return addr - instruction_10_.addr;
	if (this == instruction_10_)
	    return 0;
	do {
	    instruction_10_ = instruction_10_.nextByAddr;
	    if (instruction_10_.addr > addr)
		return -1;
	} while (instruction_10_ != this);
	return 1;
    }
    
    public void getStackPopPush(int[] is) {
	int i
	    = (byte) "\0\010\010\010\010\010\010\010\010\020\020\010\010\010\020\020\010\010\010\010\020\010\020\010\020\010\010\010\010\010\020\020\020\020\010\010\010\010\020\020\020\020\010\010\010\010\n\022\n\022\n\n\n\n\001\002\001\002\001\001\001\001\001\002\002\002\002\001\001\001\001\002\002\002\002\001\001\001\001\003\004\003\004\003\003\003\003\001\002\021\032#\"+4\022\n\024\n\024\n\024\n\024\n\024\n\024\n\024\n\024\n\024\n\024\t\022\t\022\n\023\n\023\n\023\n\024\n\024\n\024\0\021\t\021\n\n\022\t\021\021\n\022\n\t\t\t\014\n\n\014\014\001\001\001\001\001\001\002\002\002\002\002\002\002\002\0\010\0\001\001\001\002\001\002\001\0@@@@@@@@\u007f\010\t\t\t\001\t\t\001\001\u007f@\001\001\0\010"
			 .charAt(opcode);
	if (i < 64) {
	    is[0] = i & 0x7;
	    is[1] = i >> 3;
	} else {
	    switch (opcode) {
	    case 182:
	    case 183:
	    case 184:
	    case 185: {
		Reference reference = getReference();
		String string = reference.getType();
		is[0] = opcode != 184 ? 1 : 0;
		is[0] += TypeSignature.getArgumentSize(string);
		is[1] = TypeSignature.getReturnSize(string);
		break;
	    }
	    case 179:
	    case 181: {
		Reference reference = getReference();
		is[1] = 0;
		is[0] = TypeSignature.getTypeSize(reference.getType());
		if (opcode == 181)
		    is[0]++;
		break;
	    }
	    case 178:
	    case 180: {
		Reference reference = getReference();
		is[1] = TypeSignature.getTypeSize(reference.getType());
		is[0] = opcode == 180 ? 1 : 0;
		break;
	    }
	    case 197:
		is[1] = 1;
		is[0] = getDimensions();
		break;
	    default:
		throw new AssertError("Unknown Opcode: " + opcode);
	    }
	}
    }
    
    public Instruction findMatchingPop() {
	int[] is = new int[2];
	getStackPopPush(is);
	int i = is[1];
	Instruction instruction_11_ = this;
	for (;;) {
	    if (instruction_11_.succs != null
		|| instruction_11_.doesAlwaysJump())
		return null;
	    instruction_11_ = instruction_11_.nextByAddr;
	    if (instruction_11_.preds != null)
		return null;
	    instruction_11_.getStackPopPush(is);
	    if (i == is[0])
		return instruction_11_;
	    i += is[1] - is[0];
	}
    }
    
    public Instruction findMatchingPush() {
	int i = 0;
	Instruction instruction_12_ = this;
	int[] is = new int[2];
	for (;;) {
	    if (instruction_12_.preds != null)
		return null;
	    instruction_12_ = instruction_12_.prevByAddr;
	    if (instruction_12_ == null || instruction_12_.succs != null
		|| instruction_12_.doesAlwaysJump())
		return null;
	    instruction_12_.getStackPopPush(is);
	    if (i < is[1])
		return i == 0 ? instruction_12_ : null;
	    i += is[0] - is[1];
	}
    }
    
    public String getDescription() {
	StringBuffer stringbuffer
	    = new StringBuffer(String.valueOf(addr)).append('_').append
		  (Integer.toHexString(this.hashCode())).append
		  (": ").append(Opcodes.opcodeString[opcode]);
	if (opcode != 171) {
	    if (hasLocalSlot())
		stringbuffer.append(' ').append(getLocalSlot());
	    if (succs != null)
		stringbuffer.append(' ').append(((Instruction) succs).addr);
	    if (objData != null)
		stringbuffer.append(' ').append(objData);
	    if (opcode == 197)
		stringbuffer.append(' ').append(getDimensions());
	} else {
	    int[] is = getValues();
	    Instruction[] instructions = getSuccs();
	    for (int i = 0; i < is.length; i++)
		stringbuffer.append(' ').append(is[i]).append("->")
		    .append(instructions[i].addr);
	    stringbuffer.append(' ').append("default: ")
		.append(instructions[is.length].addr);
	}
	return stringbuffer.toString();
    }
    
    // xxxcr: added opcode
    public String toString() {
		return "" + addr + "_" + Integer.toHexString(this.hashCode()) + " " + opcode;
    }
}
