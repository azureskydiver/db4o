package com.db4o.j2me.bloat;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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
		generateCLASSINFOField();
		generateInfoForMethod();
		generateArrayForMethod();
		generateComponentTypeMethod();
	}

	private void generateCLASSINFOField() {
		FieldEditor fe = context.createField(ce, 26, Type
				.getType(Hashtable.class), "CLASSINFO");

		// TODO: inject declaration:
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

		// static <clinit>()V
		// L0 (0)
		MethodBuilder builder = new MethodBuilder(context, ce,
				Modifiers.STATIC, void.class, "<clinit>", new Class[0],
				new Class[0]);
		MemberRef[] fields = context.collectDeclaredFields(ce);

		// LINENUMBER 9 L0
		// NEW java/util/Hashtable
		builder.newRef(Hashtable.class);
		// DUP
		builder.dup();
		// ICONST_2
		// INVOKESPECIAL java/util/Hashtable.<init>(I)V
		builder.invoke(Opcode.opc_invokespecial, Hashtable.class, "<init>",
				new Class[0], Void.TYPE);
		// PUTSTATIC
		// com/db4o/reflect/self/UnitDogSelfReflectionRegistry.CLASSINFO :
		// Ljava/util/Hashtable;
		builder.putstatic(ce.type(), Hashtable.class, "CLASSINFO");
		// L1 (6)
		// LINENUMBER 10 L1
		int labelIdx = 1;// do we need labels anyway?
		for (int classIdx = 0; classIdx < clazzes.length; classIdx++) {
			classForName(builder, clazzes[classIdx]);
			builder.getstatic(ce.type(), Hashtable.class, "CLASSINFO");
			builder.ldc(clazzes[classIdx].getName());
			builder.newRef(com.db4o.reflect.self.ClassInfo.class);
			builder.dup();
			if (clazzes[classIdx].getSuperclass() != null) {
				builder.iconstForBoolean(isAbstractClass(clazzes[classIdx]));
				builder.anewarray(clazzes[classIdx]);
				FieldInfo[] fieldsInf = collectFieldsOfClass(clazzes[classIdx]);
				for (int i = 0; i < fieldsInf.length; i++) {
					builder.newRef(FieldInfo.class);
					builder.dup();
					FieldEditor f = fieldEditor(classIdx, fieldsInf, i);
					builder.ldc(f.name());
					builder.ldc(f.type());
					builder.iconstForBoolean(f.isPublic());
					builder.iconstForBoolean(f.isStatic());
					builder.iconstForBoolean(f.isTransient());
					builder
							.invoke(Opcode.opc_invokespecial, FieldInfo.class,
									"<init>", new Class[] { String.class,
											Class.class, Boolean.class,
											Boolean.class, Boolean.class },
									Void.TYPE);
					builder.aastore();

				}// for fieldsInf
				builder.invoke(Opcode.opc_invokespecial, ClassInfo.class,
						"<init>", new Class[] { Boolean.class, Class.class,
								com.db4o.reflect.self.FieldInfo[].class },
						Void.TYPE);
			}// if
			builder.invoke(Opcode.opc_invokevirtual, Hashtable.class, "put",
					new Class[] { Object.class, Object.class }, Object.class);
			builder.pop();
		}// for clazzes

		builder.returnInstruction();

		// GETSTATIC
		// com/db4o/reflect/self/UnitDogSelfReflectionRegistry.CLASSINFO :
		// Ljava/util/Hashtable;
		// LDC Lcom/db4o/reflect/self/Animal;.class
		// NEW com/db4o/reflect/self/ClassInfo
		// DUP
		// ICONST_1
		// LDC Ljava/lang/Object;.class
		// L2 (13)
		// LINENUMBER 11 L2
		// ICONST_1
		// ANEWARRAY com/db4o/reflect/self/FieldInfo
		// DUP
		// ICONST_0
		// NEW com/db4o/reflect/self/FieldInfo
		// DUP
		// LDC "_name"
		// LDC Ljava/lang/String;.class
		// ICONST_0
		// ICONST_0
		// ICONST_0
		// INVOKESPECIAL
		// com/db4o/reflect/self/FieldInfo.<init>(Ljava/lang/String;Ljava/lang/Class;ZZZ)V
		// AASTORE
		// INVOKESPECIAL
		// com/db4o/reflect/self/ClassInfo.<init>(ZLjava/lang/Class;[Lcom/db4o/reflect/self/FieldInfo;)V
		// L3 (28)
		// LINENUMBER 10 L3
		// INVOKEVIRTUAL
		// java/util/Hashtable.put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
		// POP
		// L4 (31)
		// LINENUMBER 12 L4
		// GETSTATIC
		// com/db4o/reflect/self/UnitDogSelfReflectionRegistry.CLASSINFO :
		// Ljava/util/Hashtable;
		// LDC Lcom/db4o/reflect/self/Dog;.class
		// L5 (34)
		// LINENUMBER 13 L5
		// NEW com/db4o/reflect/self/ClassInfo
		// DUP
		// ICONST_0
		// LDC Lcom/db4o/reflect/self/Animal;.class
		// L6 (39)
		// LINENUMBER 14 L6
		// ICONST_3
		// ANEWARRAY com/db4o/reflect/self/FieldInfo
		// DUP
		// ICONST_0
		// L7 (44)
		// LINENUMBER 15 L7
		// NEW com/db4o/reflect/self/FieldInfo
		// DUP
		// LDC "_age"
		// LDC Ljava/lang/Integer;.class
		// ICONST_1
		// ICONST_0
		// ICONST_0
		// INVOKESPECIAL
		// com/db4o/reflect/self/FieldInfo.<init>(Ljava/lang/String;Ljava/lang/Class;ZZZ)V
		// AASTORE
		// DUP
		// ICONST_1
		// L8 (56)
		// LINENUMBER 16 L8
		// NEW com/db4o/reflect/self/FieldInfo
		// DUP
		// LDC "_parents"
		// LDC [Lcom/db4o/reflect/self/Dog;.class
		// ICONST_1
		// ICONST_0
		// ICONST_0
		// INVOKESPECIAL
		// com/db4o/reflect/self/FieldInfo.<init>(Ljava/lang/String;Ljava/lang/Class;ZZZ)V
		// AASTORE
		// DUP
		// ICONST_2
		// L9 (68)
		// LINENUMBER 17 L9
		// NEW com/db4o/reflect/self/FieldInfo
		// DUP
		// LDC "_prices"
		// LDC [I.class
		// ICONST_0
		// ICONST_0
		// ICONST_0
		// INVOKESPECIAL
		// com/db4o/reflect/self/FieldInfo.<init>(Ljava/lang/String;Ljava/lang/Class;ZZZ)V
		// AASTORE
		// L10 (78)
		// LINENUMBER 13 L10
		// INVOKESPECIAL
		// com/db4o/reflect/self/ClassInfo.<init>(ZLjava/lang/Class;[Lcom/db4o/reflect/self/FieldInfo;)V
		// L11 (80)
		// LINENUMBER 12 L11
		// INVOKEVIRTUAL
		// java/util/Hashtable.put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
		// POP
		// L12 (83)
		// LINENUMBER 5 L12
		// RETURN

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

	private int isAbstractClass(Class clazz) {
		return Modifier.isAbstract(clazz.getModifiers()) ? 1 : 0;
	}

	private void classForName(MethodBuilder builder, Class clazz) {
		builder.ldc(clazz.getName());
		builder.invoke(Opcode.opc_invokestatic, builder.parentType(),
				"db4o$class$", new Class[] { String.class }, Class.class);
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
						Class.class, Integer.class }, null);
		int labelIdx = 1;
		for (int classIdx = 0; classIdx < clazzes.length; classIdx++) {
			classForName(builder, clazzes[classIdx]);
			builder.aload(1);
			builder.invoke(Opcode.opc_invokevirtual, Class.class,
					"isAssignableFrom", new Class[] { Class.class },
					Boolean.class);
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
				"arrayFor", new Class[] { Class.class, Integer.class },
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
			classForName(builder, clazzes[classIdx]);
			builder.aload(1);
			builder.invoke(Opcode.opc_invokevirtual, Class.class,
					"isAssignableFrom", new Class[] { Class.class },
					Boolean.class);
			builder.ifeq(labelId);
			classForName(builder, clazzes[classIdx]);
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
}
