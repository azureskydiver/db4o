/* SimpleAnalyzer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator.modules;
import java.util.Iterator;
import java.util.ListIterator;

import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.ClassInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.Opcodes;
import jode.bytecode.Reference;
import jode.bytecode.TypeSignature;
import jode.obfuscator.ClassIdentifier;
import jode.obfuscator.CodeAnalyzer;
import jode.obfuscator.FieldIdentifier;
import jode.obfuscator.Identifier;
import jode.obfuscator.Main;
import jode.obfuscator.MethodIdentifier;

public class SimpleAnalyzer implements CodeAnalyzer, Opcodes
{
    private ClassInfo canonizeIfaceRef(ClassInfo classinfo,
				       Reference reference) {
	for (/**/; classinfo != null; classinfo = classinfo.getSuperclass()) {
	    if (classinfo.findMethod(reference.getName(), reference.getType())
		!= null)
		return classinfo;
	    ClassInfo[] classinfos = classinfo.getInterfaces();
	    for (int i = 0; i < classinfos.length; i++) {
		ClassInfo classinfo_0_
		    = canonizeIfaceRef(classinfos[i], reference);
		if (classinfo_0_ != null)
		    return classinfo_0_;
	    }
	}
	return null;
    }
    
    public Identifier canonizeReference(Instruction instruction) {
	Reference reference = instruction.getReference();
	Identifier identifier = Main.getClassBundle().getIdentifier(reference);
	String string = reference.getClazz();
	String string_1_;
	if (identifier != null) {
	    ClassIdentifier classidentifier
		= (ClassIdentifier) identifier.getParent();
	    string_1_
		= "L" + classidentifier.getFullName().replace('.', '/') + ";";
	} else {
	    ClassInfo classinfo;
	    if (string.charAt(0) == '[')
		classinfo = ClassInfo.javaLangObject;
	    else
		classinfo = ClassInfo.forName(string.substring
						  (1, string.length() - 1)
						  .replace('/', '.'));
	    if (instruction.getOpcode() == 185)
		classinfo = canonizeIfaceRef(classinfo, reference);
	    else if (instruction.getOpcode() >= 182) {
		for (/**/; classinfo != null;
		     classinfo = classinfo.getSuperclass()) {
		    if (classinfo.findMethod(reference.getName(),
					     reference.getType())
			!= null)
			break;
		}
	    } else {
		for (/**/;
		     (classinfo != null
		      && classinfo.findField(reference.getName(),
					     reference.getType()) == null);
		     classinfo = classinfo.getSuperclass()) {
		    /* empty */
		}
	    }
	    if (classinfo == null) {
		GlobalOptions.err
		    .println("WARNING: Can't find reference: " + reference);
		string_1_ = string;
	    } else
		string_1_ = "L" + classinfo.getName().replace('.', '/') + ";";
	}
	if (!string_1_.equals(reference.getClazz())) {
	    reference = Reference.getReference(string_1_, reference.getName(),
					       reference.getType());
	    instruction.setReference(reference);
	}
	return identifier;
    }
    
    public void analyzeCode(MethodIdentifier methodidentifier,
			    BytecodeInfo bytecodeinfo) {
	Iterator iterator = bytecodeinfo.getInstructions().iterator();
	while (iterator.hasNext()) {
	    Instruction instruction = (Instruction) iterator.next();
	    switch (instruction.getOpcode()) {
	    case 192:
	    case 193:
	    case 197: {
		String string = instruction.getClazzType();
		int i;
		for (i = 0; i < string.length() && string.charAt(i) == '[';
		     i++) {
		    /* empty */
		}
		if (i < string.length() && string.charAt(i) == 'L') {
		    string = string.substring(i + 1, string.length() - 1)
				 .replace('/', '.');
		    Main.getClassBundle().reachableClass(string);
		}
		break;
	    }
	    case 179:
	    case 181:
	    case 182:
	    case 183:
	    case 184:
	    case 185:
		methodidentifier.setGlobalSideEffects();
		/* fall through */
	    case 178:
	    case 180: {
		Identifier identifier = canonizeReference(instruction);
		if (identifier != null) {
		    if (instruction.getOpcode() == 179
			|| instruction.getOpcode() == 181) {
			FieldIdentifier fieldidentifier
			    = (FieldIdentifier) identifier;
			if (fieldidentifier != null
			    && !fieldidentifier.isNotConstant())
			    fieldidentifier.setNotConstant();
		    } else if (instruction.getOpcode() == 182
			       || instruction.getOpcode() == 185)
			((ClassIdentifier) identifier.getParent())
			    .reachableReference
			    (instruction.getReference(), true);
		    else
			identifier.setReachable();
		}
		break;
	    }
	    }
	}
	Handler[] handlers = bytecodeinfo.getExceptionHandlers();
	for (int i = 0; i < handlers.length; i++) {
	    if (handlers[i].type != null)
		Main.getClassBundle().reachableClass(handlers[i].type);
	}
    }
    
    public void transformCode(BytecodeInfo bytecodeinfo) {
	ListIterator listiterator
	    = bytecodeinfo.getInstructions().listIterator();
	while (listiterator.hasNext()) {
	    Instruction instruction = (Instruction) listiterator.next();
	    if (instruction.getOpcode() == 179
		|| instruction.getOpcode() == 181) {
		Reference reference = instruction.getReference();
		FieldIdentifier fieldidentifier
		    = ((FieldIdentifier)
		       Main.getClassBundle().getIdentifier(reference));
		if (fieldidentifier != null && (Main.stripping & 0x1) != 0
		    && !fieldidentifier.isReachable()) {
		    int i = instruction.getOpcode() == 179 ? 0 : 1;
		    i += TypeSignature.getTypeSize(reference.getType());
		    switch (i) {
		    case 1:
			listiterator.set(new Instruction(87));
			break;
		    case 2:
			listiterator.set(new Instruction(88));
			break;
		    case 3:
			listiterator.set(new Instruction(88));
			listiterator.add(new Instruction(87));
			break;
		    }
		}
	    }
	}
    }
}
