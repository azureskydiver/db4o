/* SyntheticAnalyzer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.jvm;
import java.util.Iterator;

import jode.bytecode.BytecodeInfo;
import jode.bytecode.ClassInfo;
import jode.bytecode.FieldInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.MethodInfo;
import jode.bytecode.Opcodes;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.type.MethodType;
import jode.type.Type;

public class SyntheticAnalyzer implements Opcodes
{
    public static final int UNKNOWN = 0;
    public static final int GETCLASS = 1;
    public static final int ACCESSGETFIELD = 2;
    public static final int ACCESSPUTFIELD = 3;
    public static final int ACCESSMETHOD = 4;
    public static final int ACCESSGETSTATIC = 5;
    public static final int ACCESSPUTSTATIC = 6;
    public static final int ACCESSSTATICMETHOD = 7;
    public static final int ACCESSCONSTRUCTOR = 8;
    public static final int ACCESSDUPPUTFIELD = 9;
    public static final int ACCESSDUPPUTSTATIC = 10;
    int kind = 0;
    Reference reference;
    MethodInfo method;
    int unifyParam = -1;
    private static final int[] getClassOpcodes
	= { 25, 184, 176, 58, 187, 89, 25, 182, 183, 191 };
    private static final Reference[] getClassRefs
	= { null,
	    Reference.getReference("Ljava/lang/Class;", "forName",
				   "(Ljava/lang/String;)Ljava/lang/Class;"),
	    null, null, null, null, null,
	    Reference.getReference("Ljava/lang/Throwable;", "getMessage",
				   "()Ljava/lang/String;"),
	    Reference.getReference("Ljava/lang/NoClassDefFoundError;",
				   "<init>", "(Ljava/lang/String;)V"),
	    null };
    private final int modifierMask = 9;
    
    public SyntheticAnalyzer(MethodInfo methodinfo, boolean bool) {
	method = methodinfo;
	if (methodinfo.getBytecode() == null
	    || ((!bool || methodinfo.getName().equals("class$"))
		&& checkGetClass())
	    || ((!bool || methodinfo.getName().startsWith("access$"))
		&& checkAccess())
	    || (methodinfo.getName().equals("<init>")
		&& checkConstructorAccess())) {
	    /* empty */
	}
    }
    
    public int getKind() {
	return kind;
    }
    
    public Reference getReference() {
	return reference;
    }
    
    public int getUnifyParam() {
	return unifyParam;
    }
    
    boolean checkGetClass() {
	if (!method.isStatic()
	    || !method.getType()
		    .equals("(Ljava/lang/String;)Ljava/lang/Class;"))
	    return false;
	BytecodeInfo bytecodeinfo = method.getBytecode();
	Handler[] handlers = bytecodeinfo.getExceptionHandlers();
	if (handlers.length != 1
	    || !"java.lang.ClassNotFoundException".equals(handlers[0].type))
	    return false;
	int i = -1;
	int i_0_ = 0;
	Iterator iterator = bytecodeinfo.getInstructions().iterator();
	while (iterator.hasNext()) {
	    Instruction instruction;
	    for (instruction = (Instruction) iterator.next();
		 instruction.getOpcode() == 0 && iterator.hasNext();
		 instruction = (Instruction) iterator.next()) {
		/* empty */
	    }
	    if (i_0_ == getClassOpcodes.length
		|| instruction.getOpcode() != getClassOpcodes[i_0_])
		return false;
	    if (i_0_ == 0 && (instruction.getLocalSlot() != 0
			      || handlers[0].start != instruction))
		return false;
	    if (i_0_ == 2 && handlers[0].end != instruction)
		return false;
	    if (i_0_ == 3) {
		if (handlers[0].catcher != instruction)
		    return false;
		i = instruction.getLocalSlot();
	    }
	    if (i_0_ == 4 && !instruction.getClazzType()
				  .equals("Ljava/lang/NoClassDefFoundError;"))
		return false;
	    if (i_0_ == 6 && instruction.getLocalSlot() != i)
		return false;
	    if (getClassRefs[i_0_] != null
		&& !getClassRefs[i_0_].equals(instruction.getReference()))
		return false;
	    i_0_++;
	}
	kind = 1;
	return true;
    }
    
    public boolean checkStaticAccess() {
	ClassInfo classinfo = method.getClazzInfo();
	BytecodeInfo bytecodeinfo = method.getBytecode();
	Iterator iterator = bytecodeinfo.getInstructions().iterator();
	boolean bool = false;
	Instruction instruction;
	for (instruction = (Instruction) iterator.next();
	     instruction.getOpcode() == 0 && iterator.hasNext();
	     instruction = (Instruction) iterator.next()) {
	    /* empty */
	}
	if (instruction.getOpcode() == 178) {
	    Reference reference = instruction.getReference();
	    ClassInfo classinfo_1_
		= TypeSignature.getClassInfo(reference.getClazz());
	    if (!classinfo_1_.superClassOf(classinfo))
		return false;
	    FieldInfo fieldinfo = classinfo_1_.findField(reference.getName(),
							 reference.getType());
	    if ((fieldinfo.getModifiers() & 0x9) != 8)
		return false;
	    for (instruction = (Instruction) iterator.next();
		 instruction.getOpcode() == 0 && iterator.hasNext();
		 instruction = (Instruction) iterator.next()) {
		/* empty */
	    }
	    if (instruction.getOpcode() < 172 || instruction.getOpcode() > 176)
		return false;
	    this.reference = reference;
	    kind = 5;
	    return true;
	}
	int i = 0;
	int i_2_ = 0;
	while (instruction.getOpcode() >= 21 && instruction.getOpcode() <= 25
	       && instruction.getLocalSlot() == i_2_) {
	    i++;
	    i_2_ = i_2_ + ((instruction.getOpcode() == 22
			    || instruction.getOpcode() == 24)
			   ? 2 : 1);
	    for (instruction = (Instruction) iterator.next();
		 instruction.getOpcode() == 0 && iterator.hasNext();
		 instruction = (Instruction) iterator.next()) {
		/* empty */
	    }
	}
	if (instruction.getOpcode() == 86 + 3 * i_2_) {
	    for (instruction = (Instruction) iterator.next();
		 instruction.getOpcode() == 0 && iterator.hasNext();
		 instruction = (Instruction) iterator.next()) {
		/* empty */
	    }
	    if (instruction.getOpcode() != 179)
		return false;
	    bool = true;
	}
	if (instruction.getOpcode() == 179) {
	    if (i != 1)
		return false;
	    Reference reference = instruction.getReference();
	    ClassInfo classinfo_3_
		= TypeSignature.getClassInfo(reference.getClazz());
	    if (!classinfo_3_.superClassOf(classinfo))
		return false;
	    FieldInfo fieldinfo = classinfo_3_.findField(reference.getName(),
							 reference.getType());
	    if ((fieldinfo.getModifiers() & 0x9) != 8)
		return false;
	    for (instruction = (Instruction) iterator.next();
		 instruction.getOpcode() == 0 && iterator.hasNext();
		 instruction = (Instruction) iterator.next()) {
		/* empty */
	    }
	    if (bool) {
		if (instruction.getOpcode() < 172
		    || instruction.getOpcode() > 176)
		    return false;
		kind = 10;
	    } else {
		if (instruction.getOpcode() != 177)
		    return false;
		kind = 6;
	    }
	    this.reference = reference;
	    return true;
	}
	if (instruction.getOpcode() == 184) {
	    Reference reference = instruction.getReference();
	    ClassInfo classinfo_4_
		= TypeSignature.getClassInfo(reference.getClazz());
	    if (!classinfo_4_.superClassOf(classinfo))
		return false;
	    MethodInfo methodinfo
		= classinfo_4_.findMethod(reference.getName(),
					  reference.getType());
	    MethodType methodtype = Type.tMethod(reference.getType());
	    if ((methodinfo.getModifiers() & 0x9) != 8
		|| methodtype.getParameterTypes().length != i)
		return false;
	    for (instruction = (Instruction) iterator.next();
		 instruction.getOpcode() == 0 && iterator.hasNext();
		 instruction = (Instruction) iterator.next()) {
		/* empty */
	    }
	    if (methodtype.getReturnType() == Type.tVoid) {
		if (instruction.getOpcode() != 177)
		    return false;
	    } else if (instruction.getOpcode() < 172
		       || instruction.getOpcode() > 176)
		return false;
	    this.reference = reference;
	    kind = 7;
	    return true;
	}
	return false;
    }
    
    public boolean checkAccess() {
	ClassInfo classinfo = method.getClazzInfo();
	BytecodeInfo bytecodeinfo = method.getBytecode();
	Handler[] handlers = bytecodeinfo.getExceptionHandlers();
	boolean bool = false;
	if (handlers != null && handlers.length != 0)
	    return false;
	if (method.isStatic() && checkStaticAccess())
	    return true;
	Iterator iterator = bytecodeinfo.getInstructions().iterator();
	Instruction instruction;
	for (instruction = (Instruction) iterator.next();
	     instruction.getOpcode() == 0 && iterator.hasNext();
	     instruction = (Instruction) iterator.next()) {
	    /* empty */
	}
	if (instruction.getOpcode() != 25 || instruction.getLocalSlot() != 0)
	    return false;
	for (instruction = (Instruction) iterator.next();
	     instruction.getOpcode() == 0 && iterator.hasNext();
	     instruction = (Instruction) iterator.next()) {
	    /* empty */
	}
	if (instruction.getOpcode() == 180) {
	    Reference reference = instruction.getReference();
	    ClassInfo classinfo_5_
		= TypeSignature.getClassInfo(reference.getClazz());
	    if (!classinfo_5_.superClassOf(classinfo))
		return false;
	    FieldInfo fieldinfo = classinfo_5_.findField(reference.getName(),
							 reference.getType());
	    if ((fieldinfo.getModifiers() & 0x9) != 0)
		return false;
	    for (instruction = (Instruction) iterator.next();
		 instruction.getOpcode() == 0 && iterator.hasNext();
		 instruction = (Instruction) iterator.next()) {
		/* empty */
	    }
	    if (instruction.getOpcode() < 172 || instruction.getOpcode() > 176)
		return false;
	    this.reference = reference;
	    kind = 2;
	    return true;
	}
	int i = 0;
	int i_6_ = 1;
	while (instruction.getOpcode() >= 21 && instruction.getOpcode() <= 25
	       && instruction.getLocalSlot() == i_6_) {
	    i++;
	    i_6_ = i_6_ + ((instruction.getOpcode() == 22
			    || instruction.getOpcode() == 24)
			   ? 2 : 1);
	    for (instruction = (Instruction) iterator.next();
		 instruction.getOpcode() == 0 && iterator.hasNext();
		 instruction = (Instruction) iterator.next()) {
		/* empty */
	    }
	}
	if (instruction.getOpcode() == 84 + 3 * i_6_) {
	    for (instruction = (Instruction) iterator.next();
		 instruction.getOpcode() == 0 && iterator.hasNext();
		 instruction = (Instruction) iterator.next()) {
		/* empty */
	    }
	    if (instruction.getOpcode() != 181)
		return false;
	    bool = true;
	}
	if (instruction.getOpcode() == 181) {
	    if (i != 1)
		return false;
	    Reference reference = instruction.getReference();
	    ClassInfo classinfo_7_
		= TypeSignature.getClassInfo(reference.getClazz());
	    if (!classinfo_7_.superClassOf(classinfo))
		return false;
	    FieldInfo fieldinfo = classinfo_7_.findField(reference.getName(),
							 reference.getType());
	    if ((fieldinfo.getModifiers() & 0x9) != 0)
		return false;
	    for (instruction = (Instruction) iterator.next();
		 instruction.getOpcode() == 0 && iterator.hasNext();
		 instruction = (Instruction) iterator.next()) {
		/* empty */
	    }
	    if (bool) {
		if (instruction.getOpcode() < 172
		    || instruction.getOpcode() > 176)
		    return false;
		kind = 9;
	    } else {
		if (instruction.getOpcode() != 177)
		    return false;
		kind = 3;
	    }
	    this.reference = reference;
	    return true;
	}
	if (instruction.getOpcode() == 183) {
	    Reference reference = instruction.getReference();
	    ClassInfo classinfo_8_
		= TypeSignature.getClassInfo(reference.getClazz());
	    if (!classinfo_8_.superClassOf(classinfo))
		return false;
	    MethodInfo methodinfo
		= classinfo_8_.findMethod(reference.getName(),
					  reference.getType());
	    MethodType methodtype = Type.tMethod(reference.getType());
	    if ((methodinfo.getModifiers() & 0x9) != 0
		|| methodtype.getParameterTypes().length != i)
		return false;
	    for (instruction = (Instruction) iterator.next();
		 instruction.getOpcode() == 0 && iterator.hasNext();
		 instruction = (Instruction) iterator.next()) {
		/* empty */
	    }
	    if (methodtype.getReturnType() == Type.tVoid) {
		if (instruction.getOpcode() != 177)
		    return false;
	    } else if (instruction.getOpcode() < 172
		       || instruction.getOpcode() > 176)
		return false;
	    this.reference = reference;
	    kind = 4;
	    return true;
	}
	return false;
    }
    
    public boolean checkConstructorAccess() {
	ClassInfo classinfo = method.getClazzInfo();
	BytecodeInfo bytecodeinfo = method.getBytecode();
	String[] strings = TypeSignature.getParameterTypes(method.getType());
	Handler[] handlers = bytecodeinfo.getExceptionHandlers();
	if (handlers != null && handlers.length != 0)
	    return false;
	Iterator iterator = bytecodeinfo.getInstructions().iterator();
	Instruction instruction;
	for (instruction = (Instruction) iterator.next();
	     instruction.getOpcode() == 0 && iterator.hasNext();
	     instruction = (Instruction) iterator.next()) {
	    /* empty */
	}
	int i = 0;
	int i_9_ = 0;
	for (/**/;
	     instruction.getOpcode() >= 21 && instruction.getOpcode() <= 25;
	     instruction = (Instruction) iterator.next()) {
	    if (instruction.getLocalSlot() > i_9_ && unifyParam == -1 && i > 0
		&& strings[i - 1].charAt(0) == 'L') {
		unifyParam = i;
		i++;
		i_9_++;
	    }
	    if (instruction.getLocalSlot() != i_9_)
		return false;
	    i++;
	    i_9_ = i_9_ + ((instruction.getOpcode() == 22
			    || instruction.getOpcode() == 24)
			   ? 2 : 1);
	}
	if (i > 0 && instruction.getOpcode() == 183) {
	    if (unifyParam == -1 && i <= strings.length
		&& strings[i - 1].charAt(0) == 'L')
		unifyParam = i++;
	    Reference reference = instruction.getReference();
	    ClassInfo classinfo_10_
		= TypeSignature.getClassInfo(reference.getClazz());
	    if (classinfo_10_ != classinfo)
		return false;
	    MethodInfo methodinfo
		= classinfo_10_.findMethod(reference.getName(),
					   reference.getType());
	    MethodType methodtype = Type.tMethod(reference.getType());
	    if ((methodinfo.getModifiers() & 0x9) != 0
		|| !methodinfo.getName().equals("<init>") || unifyParam == -1
		|| methodtype.getParameterTypes().length != i - 2)
		return false;
	    instruction = (Instruction) iterator.next();
	    if (instruction.getOpcode() != 177)
		return false;
	    this.reference = reference;
	    kind = 8;
	    return true;
	}
	return false;
    }
}
