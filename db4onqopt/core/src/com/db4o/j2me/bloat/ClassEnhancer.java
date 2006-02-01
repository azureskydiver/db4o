package com.db4o.j2me.bloat;

import java.util.*;

import com.sun.org.apache.bcel.internal.generic.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.reflect.*;

// TODO: Add SelfReflectable interface declaration
// if already present, skip instrumentation
public class ClassEnhancer {
	private final static Map PRIMITIVES;

	private final static Map CONVERTIONFUNKTIONS;

	static {
		PRIMITIVES = new HashMap();
		PRIMITIVES.put(Type.BOOLEAN, Boolean.class);
		PRIMITIVES.put(Type.BYTE, Byte.class);
		PRIMITIVES.put(Type.CHARACTER, Character.class);
		PRIMITIVES.put(Type.SHORT, Short.class);
		PRIMITIVES.put(Type.INTEGER, Integer.class);
		PRIMITIVES.put(Type.LONG, Long.class);
		PRIMITIVES.put(Type.FLOAT, Float.class);
		PRIMITIVES.put(Type.DOUBLE, Double.class);

		CONVERTIONFUNKTIONS = new HashMap();
		CONVERTIONFUNKTIONS.put(Byte.class, "byteValue");
		CONVERTIONFUNKTIONS.put(Short.class, "shortValue");
		CONVERTIONFUNKTIONS.put(Integer.class, "intValue");
		CONVERTIONFUNKTIONS.put(Long.class, "longValue");
		CONVERTIONFUNKTIONS.put(Float.class, "floatValue");
		CONVERTIONFUNKTIONS.put(Double.class, "doubleValue");
	}

	private ClassEditor ce;

	private Enhancer context;

	public ClassEnhancer(Enhancer context, ClassEditor ce) {
		this.context = context;
		this.ce = ce;
	}

	public boolean inspectNoArgConstr(MethodInfo[] methods) {
		MethodEditor me;
		for (int i = 0; i < methods.length; i++) {
			me = new MethodEditor(ce, methods[i]);
			if ((me.type().equals(Type.getType("()V")))
					&& (me.name().equalsIgnoreCase("<init>"))) {
				// System.out.println("the class " + ce.classInfo().name()
				// + "already contains a no-args constructor");
				return true;
			}
		}
		return false;
	}

	protected void addNoArgConstructor() {
		MethodEditor init = new MethodEditor(ce, Modifiers.PUBLIC, Type.VOID,
				"<init>", new Type[0], new Type[0]);
		MemberRef mr = context.methodRef(ce.superclass(), "<init>",
				new Class[0], void.class);
		init.addLabel(new Label(0));
		init.addInstruction(Opcode.opcx_aload, init.paramAt(0));
		init.addInstruction(Opcode.opcx_invokespecial, mr);
		init.addInstruction(Opcode.opcx_return);
		init.commit();
	}

	protected void generateSelf_get(MemberRef[] fields) {
		MethodBuilder builder = new MethodBuilder(context, ce,
				Modifiers.PUBLIC, Object.class, "self_get",
				new Class[] { String.class },
				/* new Class[0] */null);
		// TODO: instructions:
		/*
		 * public Object self_get(String fieldName) {
		 * if(fieldName.equals("_age")) { return new Integer(_age); }
		 * if(fieldName.equals("_parents")) { return _parents; }
		 * if(fieldName.equals("_prices")) { return _prices; } return
		 * super.self_get(fieldName); }
		 * 
		 */
		// access flags 1
		// public self_get(String) : Object
		// L0 (0)
		for (int fieldIdx = 0; fieldIdx < fields.length; fieldIdx++) {
			Class wrapper = null;
			if (fields[fieldIdx].type().isPrimitive()) {
				wrapper = (Class) PRIMITIVES.get(fields[fieldIdx].type());
			}
			builder.aload(1);
			// System.err.println(fields[fieldIdx]+" /
			// "+fields[fieldIdx].type()+":
			// "+fields[fieldIdx].type().isPrimitive());
			builder.ldc(fields[fieldIdx].name());
			builder.invoke(Opcode.opc_invokevirtual, String.class, "equals",
					new Class[] { Object.class }, Boolean.TYPE);
			builder.ifeq(fieldIdx + 1);
			if (wrapper != null) {
				builder.newRef(wrapper);
				builder.dup();
			}
			builder.aload(0);
			builder.getfield(fields[fieldIdx]);
			if (wrapper != null) {
				builder.invoke(Opcode.opc_invokespecial, context
						.getType(wrapper), "<init>",
						new Type[] { fields[fieldIdx].type() }, Type.VOID);
			}
			builder.areturn();
			builder.label(fieldIdx + 1);
		}
		Type superType = ce.superclass();
		if (instrumentedType(superType)) {
			builder.aload(0);
			builder.aload(1);
			builder.invoke(Opcode.opc_invokespecial, superType, "self_get",
					new Class[] { String.class }, Object.class);
		} else {
			builder.ldc(null);
		}
		builder.areturn();
		builder.commit();
		// // IFEQ L1
		// me.addInstruction(Opcode.opc_ifeq, labels[1]);
		// // L2 (5)
		// me.addLabel(labels[2]);
		// // NEW Integer
		// me.addInstruction(Opcode.opc_new, context.getType(Integer.class));
		// // DUP
		// me.addInstruction(Opcode.opc_dup);
		// // ALOAD 0: this
		// me.addInstruction(Opcode.opc_aload, localVars[0]);
		// // GETFIELD Dog._age : int
		// me.addInstruction(Opcode.opc_getfield, context.fieldRef(Dog.class,
		// Integer.class, "_age"));
		// // INVOKESPECIAL Integer.<init>(int) : void
		// me.addInstruction(Opcode.opc_invokespecial, context.methodRef(
		// Integer.class, "<init>", new Class[] { Integer.class },
		// void.class));
		// // ARETURN
		// me.addInstruction(Opcode.opc_areturn);
		// // L1 (12)
		// me.addLabel(labels[1]);
		// // ALOAD 1: fieldName
		// me.addInstruction(Opcode.opc_aload, localVars[1]);
		// // LDC "_parents"
		// me.addInstruction(Opcode.opc_ldc, "_parents");
		// // INVOKEVIRTUAL String.equals(Object) : boolean
		// me.addInstruction(Opcode.opc_invokevirtual, context.methodRef(
		// String.class, "equals", new Class[] { Object.class },
		// Boolean.class));
		// // IFEQ L3
		// me.addInstruction(Opcode.opc_ifeq, labels[3]);
		// // L4 (17)
		// me.addLabel(labels[4]);
		// // ALOAD 0: this
		// me.addInstruction(Opcode.opc_aload, localVars[0]);
		// // GETFIELD Dog._parents : Dog[]
		// me.addInstruction(Opcode.opc_getfield, context.fieldRef(Dog.class,
		// Dog[].class, "_parents"));
		// // ARETURN
		// me.addInstruction(Opcode.opc_areturn);
		// // L3 (21)
		// me.addLabel(labels[3]);
		// // ALOAD 1: fieldName
		// me.addInstruction(Opcode.opc_aload, localVars[1]);
		// // LDC "_prices"
		// me.addInstruction(Opcode.opc_ldc, "_prices");
		// // INVOKEVIRTUAL String.equals(Object) : boolean
		// me.addInstruction(Opcode.opc_invokevirtual, context.methodRef(
		// String.class, "equals", new Class[] { Object.class },
		// Boolean.class));
		// // IFEQ L5
		// me.addInstruction(Opcode.opc_ifeq, labels[5]);
		// // L6 (26)
		// me.addLabel(labels[6]);
		// // ALOAD 0: this
		// me.addInstruction(Opcode.opc_aload, localVars[0]);
		// // GETFIELD Dog._prices : int[]
		// me.addInstruction(Opcode.opc_getfield, context.fieldRef(Dog.class,
		// Integer[].class, "_prices"));
		// // ARETURN
		// me.addInstruction(Opcode.opc_areturn);
		// // L5 (30)
		// me.addLabel(labels[5]);
		// // ALOAD 0: this
		// me.addInstruction(Opcode.opc_aload, localVars[0]);
		// // ALOAD 1: fieldName
		// me.addInstruction(Opcode.opc_aload, localVars[1]);
		// // INVOKESPECIAL Animal.self_get(String) : Object
		// me.addInstruction(Opcode.opc_invokespecial, context.methodRef(
		// Animal.class, "self_get", new Class[] { String.class },
		// Object.class));
		// // ARETURN
		// me.addInstruction(Opcode.opc_areturn);
		// // L7 (35)
		// me.addLabel(labels[7]);
		// me.commit();
	}

	private boolean instrumentedType(Type type) {
		String typeName = context.normalizeClassName(type.className());
		System.err.println(typeName);
		return !(typeName.startsWith("java.") || typeName.startsWith("javax.") || typeName
				.startsWith("sun."));

	}

	public boolean isNumberClass(Type type) {
		return Integer.class.equals(PRIMITIVES.get(type))
				|| Short.class.equals(PRIMITIVES.get(type))
				|| Byte.class.equals(PRIMITIVES.get(type))
				|| Long.class.equals(PRIMITIVES.get(type))
				|| Double.class.equals(PRIMITIVES.get(type))
				|| Float.class.equals(PRIMITIVES.get(type));
	}

	protected void generateSelf_set(MemberRef[] fields) {
		MethodBuilder builder = new MethodBuilder(context, ce, Modifiers.PUBLIC,
				Void.TYPE, "self_set", new Class[] { String.class,
						Object.class }, null);

		/*
		 * public void self_set(String fieldName,Object value) {
		 * if(fieldName.equals("_age")) { _age=((Integer)value).intValue();
		 * return; } if(fieldName.equals("_parents")) { _parents=(Dog[])value;
		 * return; } if(fieldName.equals("_prices")) { _prices=(int[])value;
		 * return; } super.self_set(fieldName,value); }
		 */
		for (int fieldIdx = 0; fieldIdx < fields.length; fieldIdx++) {
			Type fieldType = fields[fieldIdx].type();

			Class wrapper = (Class) PRIMITIVES.get(fieldType);
			builder.aload(1);
			builder.ldc(fields[fieldIdx].name());
			builder.invoke(Opcode.opc_invokevirtual, String.class, "equals",
					new Class[] { Object.class }, Boolean.TYPE);
			builder.ifeq(fieldIdx + 1);
			builder.aload(0);
			builder.aload(2);
			if(wrapper!=null) {
				builder.checkcast(wrapper);
				builder.invoke(Opcode.opc_invokevirtual, context.getType(wrapper),
						(String) CONVERTIONFUNKTIONS.get(wrapper),
						new Type[0], fieldType);
			}
			else {
				builder.checkcast(fieldType);
			}
			builder.putfield(fields[fieldIdx]);
			builder.returnInstruction();
			builder.label(fieldIdx + 1);
		}

		Type superType = ce.superclass();
		if (instrumentedType(superType)) {
			builder.aload(0);
			builder.aload(1);
			builder.aload(2);
			builder.invoke(Opcode.opc_invokespecial, superType, "self_set",
					new Class[] { String.class, Object.class }, void.class);

		} else {
			builder.ldc(null);
		}

		builder.returnInstruction();
		builder.commit();

		// access flags 1
		// public self_set(String,Object) : void
		// L0 (0)
		// me.addLabel(labels[0]);
		// // ALOAD 1: fieldName
		// me.addInstruction(Opcode.opc_aload, localVars[1]);
		// // LDC "_age"
		// me.addInstruction(Opcode.opc_ldc, "_age");
		// // INVOKEVIRTUAL String.equals(Object) : boolean
		// me.addInstruction(Opcode.opc_invokevirtual, context.methodRef(
		// String.class, "equals", new Class[] { Object.class },
		// boolean.class));
		// // IFEQ L1
		// me.addInstruction(Opcode.opc_ifeq, labels[1]);
		// // L2 (5)
		// me.addLabel(labels[2]);
		// // ALOAD 0: this
		// me.addInstruction(Opcode.opc_aload, localVars[0]);
		// // ALOAD 2: value
		// me.addInstruction(Opcode.opc_aload, localVars[2]);
		// // CHECKCAST Integer
		// me.addInstruction(Opcode.opc_checkcast,
		// context.getType(Integer.class));
		// // INVOKEVIRTUAL Integer.intValue() : int
		// me.addInstruction(Opcode.opc_invokevirtual, context.methodRef(
		// Integer.class, "intValue", new Class[0], Integer.class));
		// // PUTFIELD Dog._age : int
		// me.addInstruction(Opcode.opc_putfield, context.fieldRef(Dog.class,
		// Integer.class, "_age"));
		// // L3 (11)
		// me.addLabel(labels[3]);
		// // RETURN
		// me.addInstruction(Opcode.opc_return);
		// // L1 (13)
		// me.addLabel(labels[1]);
		// // ALOAD 1: fieldName
		// me.addInstruction(Opcode.opc_aload, localVars[1]);
		// // LDC "_parents"
		// me.addInstruction(Opcode.opc_ldc, "_parents");
		// // INVOKEVIRTUAL String.equals(Object) : boolean
		// me.addInstruction(Opcode.opc_invokevirtual, context.methodRef(
		// String.class, "equals", new Class[] { Object.class },
		// Boolean.class));
		// // IFEQ L4
		// me.addInstruction(Opcode.opc_ifeq, labels[4]);
		// // L5 (18)
		// me.addLabel(labels[5]);
		// // ALOAD 0: this
		// me.addInstruction(Opcode.opc_aload, localVars[0]);
		// // ALOAD 2: value
		// me.addInstruction(Opcode.opc_aload, localVars[2]);
		// // CHECKCAST Dog[]
		// me.addInstruction(Opcode.opc_checkcast,
		// context.getType(Dog[].class));
		// // PUTFIELD Dog._parents : Dog[]
		// me.addInstruction(Opcode.opc_putfield, context.fieldRef(Dog.class,
		// Dog[].class, "_parents"));
		// // L6 (23)
		// me.addLabel(labels[6]);
		// // RETURN
		// me.addInstruction(Opcode.opc_return);
		// // L4 (25)
		// me.addLabel(labels[4]);
		// // ALOAD 1: fieldName
		// me.addInstruction(Opcode.opc_aload, localVars[1]);
		// // LDC "_prices"
		// me.addInstruction(Opcode.opc_ldc, "_prices");
		// // INVOKEVIRTUAL String.equals(Object) : boolean
		// me.addInstruction(Opcode.opc_invokevirtual, context.methodRef(
		// String.class, "equals", new Class[] { Object.class },
		// Boolean.class));
		// // IFEQ L7
		// me.addInstruction(Opcode.opc_ifeq, labels[7]);
		// // L8 (30)
		// me.addLabel(labels[8]);
		// // ALOAD 0: this
		// me.addInstruction(Opcode.opc_aload, localVars[0]);
		// // ALOAD 2: value
		// me.addInstruction(Opcode.opc_aload, localVars[2]);
		// // CHECKCAST int[]
		// me.addInstruction(Opcode.opc_checkcast, context
		// .getType(Integer[].class));
		// // PUTFIELD Dog._prices : int[]
		// me.addInstruction(Opcode.opc_putfield, context.fieldRef(Dog.class,
		// Integer[].class, "_prices"));
		// // L9 (35)
		// me.addLabel(labels[9]);
		// // RETURN
		// me.addInstruction(Opcode.opc_return);
		// // L7 (37)
		// me.addLabel(labels[7]);
		// // ALOAD 0: this
		// me.addInstruction(Opcode.opc_aload, localVars[0]);
		// // ALOAD 1: fieldName
		// me.addInstruction(Opcode.opc_aload, localVars[1]);
		// // ALOAD 2: value
		// me.addInstruction(Opcode.opc_aload, localVars[2]);
		// // INVOKESPECIAL Animal.self_set(String,Object) : void
		// me.addInstruction(Opcode.opc_invokevirtual, context.methodRef(
		// Animal.class, "self_set", new Class[] { String.class,
		// Object.class }, void.class));
		// // L10 (42)
		// me.addLabel(labels[10]);
		// // RETURN
		// me.addInstruction(Opcode.opc_return);
		// // L11 (44)
		// me.addLabel(labels[11]);
		// me.commit();
	}

	public void generate() {
		if (!(inspectNoArgConstr(ce.methods()))) {
			addNoArgConstructor();
		}
		MemberRef[] declaredFields = context.collectDeclaredFields(ce);
		generateSelf_get(declaredFields);
		generateSelf_set(declaredFields);
	}

	
}
