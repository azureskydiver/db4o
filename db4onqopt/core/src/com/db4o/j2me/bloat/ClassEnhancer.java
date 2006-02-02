package com.db4o.j2me.bloat;

import java.util.*;

import com.db4o.reflect.self.*;
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
		MethodBuilder builder = new MethodBuilder(context, ce,
				Modifiers.PUBLIC, Void.TYPE, "self_set", new Class[] {
						String.class, Object.class }, null);

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
			if (wrapper != null) {
				builder.checkcast(wrapper);
				builder.invoke(Opcode.opc_invokevirtual, context
						.getType(wrapper), (String) CONVERTIONFUNKTIONS
						.get(wrapper), new Type[0], fieldType);
			} else {
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

	}

	public void generate() {
		if (implementsSelfReflectable(ce)) {
			return;
		} else
			addInterfaceIfNeeded();

		if (!(inspectNoArgConstr(ce.methods()))) {
			addNoArgConstructor();
		}
		MemberRef[] declaredFields = context.collectDeclaredFields(ce);
		generateSelf_get(declaredFields);
		generateSelf_set(declaredFields);
	}

	private void addInterfaceIfNeeded() {
		Class clazz = ce.getClass();
		while (!(clazz.getSuperclass().equals(Object.class))) {
			if (!(implementsSelfReflectable(ce))) {
				ce.addInterface(SelfReflectable.class);
			}
			clazz = clazz.getSuperclass();
			ce = context.loadClass(context.getLoader().getClassPath(), clazz
					.getSimpleName());
		}
	}

	private boolean implementsSelfReflectable(ClassEditor ce) {
		Type[] interfaces = ce.interfaces();

		for (int interfIdx = 0; interfIdx < interfaces.length; interfIdx++) {
			if (interfaces[interfIdx].getClass().equals(SelfReflectable.class)) {
				return true;
			}
		}
		return false;
	}

}
