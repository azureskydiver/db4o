/* MethodIdentifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;

import jode.AssertError;
import jode.GlobalOptions;
import jode.bytecode.BytecodeInfo;
import jode.bytecode.Handler;
import jode.bytecode.Instruction;
import jode.bytecode.MethodInfo;
import jode.bytecode.Opcodes;

public class MethodIdentifier extends Identifier implements Opcodes
{
    ClassIdentifier clazz;
    MethodInfo info;
    String name;
    String type;
    boolean globalSideEffects;
    BitSet localSideEffects;
    CodeAnalyzer codeAnalyzer;
    boolean wasTransformed = false;
    
    public MethodIdentifier(ClassIdentifier classidentifier,
			    MethodInfo methodinfo) {
	super(methodinfo.getName());
	name = methodinfo.getName();
	type = methodinfo.getType();
	clazz = classidentifier;
	info = methodinfo;
	BytecodeInfo bytecodeinfo = methodinfo.getBytecode();
	if (bytecodeinfo != null) {
	    if ((Main.stripping & 0x4) != 0)
		methodinfo.getBytecode().setLocalVariableTable(null);
	    if ((Main.stripping & 0x8) != 0)
		methodinfo.getBytecode().setLineNumberTable(null);
	    codeAnalyzer = Main.getClassBundle().getCodeAnalyzer();
	    CodeTransformer[] codetransformers
		= Main.getClassBundle().getPreTransformers();
	    for (int i = 0; i < codetransformers.length; i++)
		codetransformers[i].transformCode(bytecodeinfo);
	    methodinfo.setBytecode(bytecodeinfo);
	}
    }
    
    public Iterator getChilds() {
	return Collections.EMPTY_LIST.iterator();
    }
    
    public void setSingleReachable() {
	super.setSingleReachable();
	Main.getClassBundle().analyzeIdentifier(this);
    }
    
    public void analyze() {
	if (GlobalOptions.verboseLevel > 1)
	    GlobalOptions.err.println("Analyze: " + this);
	String string = getType();
	int i;
	for (int i_0_ = string.indexOf('L'); i_0_ != -1;
	     i_0_ = string.indexOf('L', i)) {
	    i = string.indexOf(';', i_0_);
	    Main.getClassBundle().reachableClass(string.substring
						     (i_0_ + 1, i)
						     .replace('/', '.'));
	}
	String[] strings = info.getExceptions();
	if (strings != null) {
	    for (int i_1_ = 0; i_1_ < strings.length; i_1_++)
		Main.getClassBundle().reachableClass(strings[i_1_]);
	}
	BytecodeInfo bytecodeinfo = info.getBytecode();
	if (bytecodeinfo != null)
	    codeAnalyzer.analyzeCode(this, bytecodeinfo);
    }
    
    public Identifier getParent() {
	return clazz;
    }
    
    public String getFullName() {
	return clazz.getFullName() + "." + getName() + "." + getType();
    }
    
    public String getFullAlias() {
	return (clazz.getFullAlias() + "." + this.getAlias() + "."
		+ Main.getClassBundle().getTypeAlias(getType()));
    }
    
    public String getName() {
	return name;
    }
    
    public String getType() {
	return type;
    }
    
    public int getModifiers() {
	return info.getModifiers();
    }
    
    public boolean conflicting(String string) {
	return clazz.methodConflicts(this, string);
    }
    
    public String toString() {
	return "MethodIdentifier " + getFullName();
    }
    
    public boolean hasGlobalSideEffects() {
	return globalSideEffects;
    }
    
    public boolean getLocalSideEffects(int i) {
	return globalSideEffects || localSideEffects.get(i);
    }
    
    public void setGlobalSideEffects() {
	globalSideEffects = true;
    }
    
    public void setLocalSideEffects(int i) {
	localSideEffects.set(i);
    }
    
    public void doTransformations() {
	if (wasTransformed)
	    throw new AssertError
		      ("doTransformation called on transformed method");
	wasTransformed = true;
	info.setName(this.getAlias());
	ClassBundle classbundle = Main.getClassBundle();
	info.setType(classbundle.getTypeAlias(type));
	if (codeAnalyzer != null) {
	    BytecodeInfo bytecodeinfo = info.getBytecode();
	    try {
		codeAnalyzer.transformCode(bytecodeinfo);
		CodeTransformer[] codetransformers
		    = classbundle.getPostTransformers();
		for (int i = 0; i < codetransformers.length; i++)
		    codetransformers[i].transformCode(bytecodeinfo);
	    } catch (RuntimeException runtimeexception) {
		runtimeexception.printStackTrace(GlobalOptions.err);
		bytecodeinfo.dumpCode(GlobalOptions.err);
	    }
	    Iterator iterator = bytecodeinfo.getInstructions().iterator();
	    while (iterator.hasNext()) {
		Instruction instruction = (Instruction) iterator.next();
		switch (instruction.getOpcode()) {
		case 182:
		case 183:
		case 184:
		case 185:
		    instruction.setReference
			(Main.getClassBundle()
			     .getReferenceAlias(instruction.getReference()));
		    break;
		case 178:
		case 179:
		case 180:
		case 181:
		    instruction.setReference
			(Main.getClassBundle()
			     .getReferenceAlias(instruction.getReference()));
		    break;
		case 187:
		case 192:
		case 193:
		case 197:
		    instruction.setClazzType(Main.getClassBundle().getTypeAlias
					     (instruction.getClazzType()));
		    break;
		}
	    }
	    Handler[] handlers = bytecodeinfo.getExceptionHandlers();
	    for (int i = 0; i < handlers.length; i++) {
		if (handlers[i].type != null) {
		    ClassIdentifier classidentifier
			= Main.getClassBundle()
			      .getClassIdentifier(handlers[i].type);
		    if (classidentifier != null)
			handlers[i].type = classidentifier.getFullAlias();
		}
	    }
	    info.setBytecode(bytecodeinfo);
	}
	String[] strings = info.getExceptions();
	if (strings != null) {
	    for (int i = 0; i < strings.length; i++) {
		ClassIdentifier classidentifier
		    = Main.getClassBundle().getClassIdentifier(strings[i]);
		if (classidentifier != null)
		    strings[i] = classidentifier.getFullAlias();
	    }
	}
    }
}
