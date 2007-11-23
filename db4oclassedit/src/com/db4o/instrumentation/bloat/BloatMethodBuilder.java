/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.instrumentation.bloat;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.instrumentation.api.*;
import com.db4o.instrumentation.util.*;

public class BloatMethodBuilder implements MethodBuilder {
	
	private final MethodEditor methodEditor;
	private final LabelGenerator _labelGen;
	private final BloatReferenceProvider _references;
	private final Map _conversions;

	public BloatMethodBuilder(BloatReferenceProvider references, ClassEditor classEditor, String methodName, Class returnType, Class[] parameterTypes) {
		_references = references;
		methodEditor = new MethodEditor(classEditor, Modifiers.PUBLIC, returnType, methodName, parameterTypes, new Class[]{});
		_labelGen = new LabelGenerator();
		methodEditor.addLabel(_labelGen.createLabel(true));
		_conversions = setUpConversions();
	}

	public void invoke(final MethodRef method) {
		addInstruction(Opcode.opc_invokeinterface, memberRef(method));
	}

	public void ldc(Object value) {
		addInstruction(Opcode.opc_ldc, coerce(value));
	}
	
	public void loadArgument(final int index) {
		addInstruction(Opcode.opc_aload, new LocalVariable(index));
	}
	
	public void pop() {
		addInstruction(Opcode.opc_pop);
	}
	
	private MemberRef memberRef(Object ref) {
		return ((BloatMemberRef)ref).member();
	}

	public void endMethod() {
		addLabel(false);
		addInstruction(Opcode.opc_return);
		addLabel(true);
		methodEditor.commit();
	}

	private void addLabel(final boolean startsBlock) {
		methodEditor.addLabel(_labelGen.createLabel(startsBlock));
	}

	public void addInstruction(final int opcode) {
		methodEditor.addInstruction(opcode);
	}

	public void print(PrintStream out) {
		methodEditor.print(out);
	}

	public void loadArrayElement(Class elementType) {
		addInstruction(arrayElementOpcode(elementType));
	}

	private int arrayElementOpcode(Class elementType) {
		if(elementType==Integer.TYPE) {
			return Opcode.opc_iaload;
		}
		if(elementType==Long.TYPE) {
			return Opcode.opc_laload;
		}
		if(elementType==Float.TYPE) {
			return Opcode.opc_faload;
		}
		if(elementType==Double.TYPE) {
			return Opcode.opc_daload;
		}
		return Opcode.opc_aaload;
	}

	public void addInstruction(Instruction instruction) {
		methodEditor.addInstruction(instruction);
	}
	
	public void addInstruction(int opcode, Object operand) {
		methodEditor.addInstruction(opcode, operand);
	}

	public void add(Class operandType) {
		addInstruction(addOpcode(operandType));
	}

	private int addOpcode(Class operandType) {
		if(operandType==Double.class) {
			return Opcode.opc_dadd;
		}
		if(operandType==Float.class) {
			return Opcode.opc_fadd;
		}
		if(operandType==Long.class) {
			return Opcode.opc_ladd;
		}
		return Opcode.opc_iadd;
	}

	public void subtract(Class operandType) {
		addInstruction(subOpcode(operandType));
	}

	private int subOpcode(Class operandType) {
		if(operandType==Double.class) {
			return Opcode.opc_dsub;
		}
		if(operandType==Float.class) {
			return Opcode.opc_fsub;
		}
		if(operandType==Long.class) {
			return Opcode.opc_lsub;
		}
		return Opcode.opc_isub;
	}

	public void multiply(Class operandType) {
		addInstruction(multOpcode(operandType));
	}

	private int multOpcode(Class operandType) {
		if(operandType==Double.class) {
			return Opcode.opc_dmul;
		}
		if(operandType==Float.class) {
			return Opcode.opc_fmul;
		}
		if(operandType==Long.class) {
			return Opcode.opc_lmul;
		}
		return Opcode.opc_imul;
	}

	public void divide(Class operandType) {
		addInstruction(divOpcode(operandType));
	}

	private int divOpcode(Class operandType) {
		if(operandType==Double.class) {
			return Opcode.opc_ddiv;
		}
		if(operandType==Float.class) {
			return Opcode.opc_fdiv;
		}
		if(operandType==Long.class) {
			return Opcode.opc_ldiv;
		}
		return Opcode.opc_idiv;
	}

	public void invoke(Method method) {
		int opcode=((method.getModifiers()&Modifier.STATIC)!=0 ? Opcode.opc_invokestatic : Opcode.opc_invokevirtual);
		addInstruction(opcode, memberRef(_references.forMethod(method.getDeclaringClass(), method.getName(), method.getParameterTypes(), method.getReturnType())));
	}

	public ReferenceProvider references() {
		return _references;
	}

	public void loadField(FieldRef fieldRef) {
		addInstruction(Opcode.opc_getfield, memberRef(fieldRef));
	}

	public void loadStaticField(FieldRef fieldRef) {
		addInstruction(Opcode.opc_getstatic, memberRef(fieldRef));
	}
	
	public void box(Class boxedType) {
		Class[] convSpec=(Class[])_conversions.get(boxedType);
		if (null == convSpec) {
			return;
		}
		
		final Class wrapperType = convSpec[0];
		final Class primitiveType = convSpec[1];
		
		final LocalVariable local = methodEditor.newLocal(typeRef(primitiveType));
		addInstruction(storeOpcode(primitiveType), local);
		addInstruction(Opcode.opc_new, typeRef(wrapperType));
		addInstruction(Opcode.opc_dup);
		addInstruction(loadOpcode(primitiveType), local);
		addInstruction(Opcode.opc_invokespecial, memberRef(_references.forMethod(convSpec[0],"<init>",new Class[]{primitiveType},Void.TYPE)));
	}
	
	private int loadOpcode(Class type) {
		if(type==Long.TYPE) {
			return Opcode.opc_lload;
		}
		if(type==Float.TYPE) {
			return Opcode.opc_fload;
		}
		if(type==Double.TYPE) {
			return Opcode.opc_dload;
		}
		return Opcode.opc_iload;
	}

	private int storeOpcode(Class type) {
		if(type==Long.TYPE) {
			return Opcode.opc_lstore;
		}
		if(type==Float.TYPE) {
			return Opcode.opc_fstore;
		}
		if(type==Double.TYPE) {
			return Opcode.opc_dstore;
		}
		return Opcode.opc_istore;
	}

	private Type typeRef(final Class type) {
		return _references.typeRef(type);
	}
	
	private Map setUpConversions() {
		Map conversions=new HashMap();
		conversions.put(Integer.class,new Class[]{Integer.class,Integer.TYPE});
		conversions.put(Long.class,new Class[]{Long.class,Long.TYPE});
		conversions.put(Short.class,new Class[]{Short.class,Short.TYPE});
		conversions.put(Byte.class,new Class[]{Byte.class,Byte.TYPE});
		conversions.put(Double.class,new Class[]{Double.class,Double.TYPE});
		conversions.put(Float.class,new Class[]{Float.class,Float.TYPE});
		conversions.put(Boolean.class,new Class[]{Boolean.class,Boolean.TYPE});
		// FIXME this must be handled somewhere else -  FieldValue, etc.
		conversions.put(Integer.TYPE,conversions.get(Integer.class));
		conversions.put(Long.TYPE,conversions.get(Long.class));
		conversions.put(Short.TYPE,conversions.get(Short.class));
		conversions.put(Byte.TYPE,conversions.get(Byte.class));
		conversions.put(Double.TYPE,conversions.get(Double.class));
		conversions.put(Float.TYPE,conversions.get(Float.class));
		conversions.put(Boolean.TYPE,conversions.get(Boolean.class));
		return conversions;
	}
	
	private Object coerce(Object value) {
		if(value instanceof Boolean) {
			return ((Boolean)value).booleanValue() ? new Integer(1) : new Integer(0);
		}
		if(value instanceof Character) {
			return new Integer(((Character)value).charValue());
		}
		if(value instanceof Byte || value instanceof Short) {
			return new Integer(((Number)value).intValue());
		}
		return value;
	}
}
