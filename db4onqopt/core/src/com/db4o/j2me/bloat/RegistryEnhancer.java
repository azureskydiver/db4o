package com.db4o.j2me.bloat;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.editor.ClassEditor;
import EDU.purdue.cs.bloat.editor.FieldEditor;
import EDU.purdue.cs.bloat.editor.MemberRef;
import EDU.purdue.cs.bloat.editor.Opcode;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.file.ClassFileLoader;
import EDU.purdue.cs.bloat.reflect.ClassFormatException;
import EDU.purdue.cs.bloat.reflect.FieldInfo;
import EDU.purdue.cs.bloat.reflect.Modifiers;

import com.db4o.reflect.self.ClassInfo;
import com.db4o.reflect.self.SelfReflectionRegistry;

public class RegistryEnhancer {
	// FIXME: Move primitive conversion stuff from ClassEnhancer and here to a class of its own
	
	private final static Map PRIMITIVES;

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
	}

	private ClassEditor ce;

	private Class[] clazzes;

	private Enhancer context;

	public RegistryEnhancer(Enhancer context, ClassEditor ce, Class clazz) {
		this.ce = ce;
		this.clazzes = createClasses(clazz);
		this.context = context;
	}

	private static Class[] createClasses(Class concrete) {
		List list = new ArrayList();
		Class cur = concrete;
		while (cur != Object.class) {
			list.add(cur);
			cur = cur.getSuperclass();
		}
		return (Class[]) list.toArray(new Class[list.size()]);
	}

	public void generate() {
		addNoArgConstructor();
		generateCLASSINFOField();
		generateInfoForMethod();
		generateArrayForMethod();
		generateComponentTypeMethod();
	}

	private void generateCLASSINFOField() {
		FieldEditor fe = context.createField(ce, 26, Type
				.getType(Hashtable.class), "CLASSINFO");

		/*
		 * static { CLASSINFO = new Hashtable(2); CLASSINFO.put(Animal.class,
		 * new ClassInfo(true, Object.class, new FieldInfo[] { new
		 * FieldInfo("_name", String.class, true, false, false) }));
		 * CLASSINFO.put(Dog.class, new ClassInfo(false, Animal.class, new
		 * FieldInfo[] { new FieldInfo("_age", Integer.class, true, false,
		 * false), new FieldInfo("_parents", Dog[].class, true, false, false),
		 * new FieldInfo("_prices", int[].class, true, false, false), })); //
		 * FIELDINFO.put(P1Object.class, new FieldInfo[]{}); }
		 */

		MethodBuilder builder = new MethodBuilder(context, ce,
				Modifiers.STATIC, void.class, "<clinit>", new Class[0],
				new Class[0]);
		builder.newRef(Hashtable.class);
		builder.dup();
		builder.invoke(Opcode.opc_invokespecial, Hashtable.class, "<init>",
				new Class[0], Void.TYPE);
		builder.putstatic(ce.type(), Hashtable.class, "CLASSINFO");
		for (int classIdx = 0; classIdx < clazzes.length; classIdx++) {
			builder.getstatic(ce.type(), Hashtable.class, "CLASSINFO");
			builder.invokeLoadClassConstMethod(clazzes[classIdx]);
			builder.newRef(com.db4o.reflect.self.ClassInfo.class);
			builder.dup();
//			if (clazzes[classIdx].getSuperclass() != null) {
				FieldInfo[] fieldsInf = collectFieldsOfClass(clazzes[classIdx]);
				builder.ldc(isAbstractClass(clazzes[classIdx]));
				builder.invokeLoadClassConstMethod(clazzes[classIdx]);
				builder.ldc(fieldsInf.length);
				builder.anewarray(com.db4o.reflect.self.FieldInfo.class);
				for (int i = 0; i < fieldsInf.length; i++) {
					builder.dup();
					builder.ldc(i);
					builder.newRef(com.db4o.reflect.self.FieldInfo.class);
					builder.dup();
					FieldEditor f = fieldEditor(classIdx, fieldsInf, i);
					builder.ldc(f.name());
					Class wrapper=(Class)PRIMITIVES.get(f.type());
					if(wrapper!=null) {
						builder.getstatic(wrapper, Class.class, "TYPE");
					}
					else {
						builder.invokeLoadClassConstMethod(f.type().className());
					}
					builder.ldc(f.isPublic());
					builder.ldc(f.isStatic());
					builder.ldc(f.isTransient());
					builder
							.invoke(Opcode.opc_invokespecial, com.db4o.reflect.self.FieldInfo.class,
									"<init>", new Class[] { String.class,
											Class.class, Boolean.TYPE,
											Boolean.TYPE, Boolean.TYPE },
									Void.TYPE);
					builder.aastore();

				}// for fieldsInf
				builder.invoke(Opcode.opc_invokespecial, ClassInfo.class,
						"<init>", new Class[] { Boolean.TYPE, Class.class,
								com.db4o.reflect.self.FieldInfo[].class },
						Void.TYPE);
//			}// if
			builder.invoke(Opcode.opc_invokevirtual, Hashtable.class, "put",
					new Class[] { Object.class, Object.class }, Object.class);
		}// for clazzes

		builder.returnInstruction();
		builder.commit();
		fe.commit();

	}

	private FieldEditor fieldEditor(int classIdx, FieldInfo[] fieldsInf, int i) {
		FieldEditor f = null;

		try {
			f = new FieldEditor(new ClassEditor(null, new ClassFileLoader()
					.loadClass(clazzes[classIdx].getName())), fieldsInf[i]);
		} catch (ClassFormatException e) {
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return f;
	}

	private FieldInfo[] collectFieldsOfClass(Class clazz) {
		ClassEditor ce = null;
		FieldInfo[] fields = null;
		try {
			ce = new ClassEditor(null, new ClassFileLoader().loadClass(clazz
					.getName()));
			fields = ce.fields();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return fields;
	}

	private boolean isAbstractClass(Class clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}

	private void generateInfoForMethod() {
		MethodBuilder builder = new MethodBuilder(context, ce,
				Modifiers.PUBLIC, com.db4o.reflect.self.ClassInfo.class,
				"infoFor", new Class[] { Class.class }, null);
		builder.getstatic(ce.type(), Hashtable.class, "CLASSINFO");
		builder.aload(1);
		builder.invoke(Opcode.opc_invokevirtual, Hashtable.class, "get",
				new Class[] { Object.class }, Object.class);
		builder.checkcast(ClassInfo.class);
		builder.areturn();
		builder.commit();

	}

	private void generateArrayForMethod() {
		MethodBuilder builder = new MethodBuilder(context, ce,
				Modifiers.PUBLIC, Object.class, "arrayFor", new Class[] {
						Class.class, Integer.TYPE }, null);
		int labelIdx = 1;
		for (int classIdx = 0; classIdx < clazzes.length; classIdx++) {
			builder.invokeLoadClassConstMethod(clazzes[classIdx]);
			builder.aload(1);
			builder.invoke(Opcode.opc_invokevirtual, Class.class,
					"isAssignableFrom", new Class[] { Class.class },
					Boolean.TYPE);
			builder.ifeq(labelIdx);
			builder.iload(2);
			builder.newarray(clazzes[classIdx]);
			builder.areturn();
			builder.label(labelIdx);
			labelIdx++;
		}
		builder.aload(0);
		builder.aload(1);
		builder.iload(2);
		builder.invoke(Opcode.opc_invokespecial, SelfReflectionRegistry.class,
				"arrayFor", new Class[] { Class.class, Integer.TYPE },
				Object.class);
		builder.areturn();
		builder.commit();
	}

	private void generateComponentTypeMethod() {
		MethodBuilder builder = new MethodBuilder(context, ce,
				Modifiers.PUBLIC, Class.class, "componentType",
				new Class[] { Class.class }, new Class[0]);
		int labelId = 1;
		for (int classIdx = 0; classIdx < clazzes.length; classIdx++) {
			builder.invokeLoadClassConstMethod(clazzes[classIdx]);
			builder.aload(1);
			builder.invoke(Opcode.opc_invokevirtual, Class.class,
					"isAssignableFrom", new Class[] { Class.class },
					Boolean.TYPE);
			builder.ifeq(labelId);
			builder.invokeLoadClassConstMethod(clazzes[classIdx]);
			builder.areturn();
			builder.label(labelId);
			labelId++;
		}
		builder.aload(0);
		builder.aload(1);
		builder.invoke(Opcode.opc_invokespecial,
				com.db4o.reflect.self.SelfReflectionRegistry.class,
				"componentType", new Class[] { Class.class }, Class.class);
		builder.areturn();
		builder.commit();

	}
	
	// for testing only
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
}
