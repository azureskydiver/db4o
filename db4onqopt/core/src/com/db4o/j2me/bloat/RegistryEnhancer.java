package com.db4o.j2me.bloat;

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.reflect.self.*;
import com.db4o.reflect.self.ClassInfo;

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
		// access flags 8
		// static <clinit>() : void
		MethodBuilder bld = new MethodBuilder(context, ce, Modifiers.STATIC,
				void.class, "<clinit>", new Class[0], new Class[0]);
		bld.newRef(Hashtable.class);
		bld.dup();
		bld.ldc(2);
		bld.invoke(Opcode.opc_invokespecial, Hashtable.class, "<init>",
				new Class[] { Integer.class }, void.class);
		bld.putstatic(bld.parentType(), Hashtable.class, "CLASSINFO");

		// bld.label(1);
		// bld.getstatic(bld.parentType(),Hashtable.class, "CLASSINFO");
		// invokeClassGetter(bld, Animal.class);
		// bld.newRef(ClassInfo.class);
		// bld.dup();
		// bld.ldc(1);
		// invokeClassGetter(bld, Object.class);
		// bld.label(2);
		// bld.ldc(1);
		// bld.newarray(FieldInfo.class);
		// bld.dup();
		// bld.ldc(0);
		// // L3 (18)
		// me.addLabel(labels[3]);
		// // NEW FieldInfo
		// me.addInstruction(Opcode.opc_new, getType(FieldInfo.class));
		// // DUP
		// me.addInstruction(Opcode.opc_dup);
		// // LDC "_name"
		// me.addInstruction(Opcode.opc_ldc, "_name");
		// // LDC Ljava/lang/String;.class
		// invokeClassGetter(me, String.class);
		// // ICONST_1
		// me.addInstruction(Opcode.opc_ldc,new Integer(1));
		// // L4 (24)
		// me.addLabel(labels[4]);
		// // ICONST_0
		// me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// // ICONST_0
		// me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// // INVOKESPECIAL
		// FieldInfo.<init>(String,Class,boolean,boolean,boolean)
		// // : void
		// me.addInstruction(Opcode.opc_invokespecial,
		// methodRef(FieldInfo.class,
		// "<init>", new Class[] { String.class, Class.class,
		// Boolean.class, Boolean.class, Boolean.class },
		// void.class));
		// // AASTORE
		// me.addInstruction(Opcode.opc_aastore);
		// // INVOKESPECIAL ClassInfo.<init>(boolean,Class,FieldInfo[]) : void
		// me.addInstruction(Opcode.opc_invokespecial,
		// methodRef(ClassInfo.class,
		// "<init>", new Class[] { Boolean.class, Class.class,
		// FieldInfo[].class }, void.class));
		// // L5 (30)
		// me.addLabel(labels[5]);
		// // INVOKEVIRTUAL Hashtable.put(Object,Object) : Object
		// me.addInstruction(Opcode.opc_invokevirtual,
		// methodRef(Hashtable.class, "put", new Class[] { Object.class,
		// Object.class }, Object.class));
		// // POP
		// me.addInstruction(Opcode.opc_pop);
		// // L6 (33)
		// me.addLabel(labels[6]);
		// // GETSTATIC RegressionDogSelfReflectionRegistry.CLASSINFO :
		// Hashtable
		// me.addInstruction(Opcode.opc_getstatic, fieldRef(ce.name(),
		// Hashtable.class, "CLASSINFO"));
		// // LDC Lcom/db4o/bloat/Dog;.class
		// invokeClassGetter(me, Dog.class);
		// // L7 (36)
		// me.addLabel(labels[7]);
		// // NEW ClassInfo
		// invokeClassGetter(me, ClassInfo.class);
		// // DUP
		// me.addInstruction(Opcode.opc_dup);
		// // ICONST_0
		// me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// // LDC Lcom/db4o/bloat/Animal;.class
		// invokeClassGetter(me, Animal.class);
		// // L8 (41)
		// me.addLabel(labels[8]);
		// // ICONST_3
		// me.addInstruction(Opcode.opc_ldc,new Integer(3));
		// // ANEWARRAY FieldInfo
		// me.addInstruction(Opcode.opc_newarray, getType(FieldInfo.class));
		// // DUP
		// me.addInstruction(Opcode.opc_dup);
		// // ICONST_0
		// me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// // L9 (46)
		// me.addLabel(labels[9]);
		// // NEW FieldInfo
		// me.addInstruction(Opcode.opc_new, getType(FieldInfo.class));
		// // DUP
		// me.addInstruction(Opcode.opc_dup);
		// // LDC "_age"
		// me.addInstruction(Opcode.opc_ldc, "_age");
		// // LDC Ljava/lang/Integer;.class
		// invokeClassGetter(me, Integer.class);
		// // ICONST_1
		// me.addInstruction(Opcode.opc_ldc, new Integer(1));
		// // L10 (52)
		// me.addLabel(labels[10]);
		// // ICONST_0
		// me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// // ICONST_0
		// me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// // L11 (55)
		// me.addLabel(labels[11]);
		// // INVOKESPECIAL
		// FieldInfo.<init>(String,Class,boolean,boolean,boolean)
		// // : void
		// me.addInstruction(Opcode.opc_invokespecial,
		// methodRef(FieldInfo.class,
		// "<init>", new Class[] { String.class, Class.class,
		// Boolean.class, Boolean.class, Boolean.class },
		// void.class));
		// // AASTORE
		// me.addInstruction(Opcode.opc_aastore);
		// // DUP
		// me.addInstruction(Opcode.opc_dup);
		// // ICONST_1
		// me.addInstruction(Opcode.opc_ldc,new Integer(1));
		// // L12 (60)
		// me.addLabel(labels[12]);
		// // NEW FieldInfo
		// me.addInstruction(Opcode.opc_new, getType(FieldInfo.class));
		// // DUP
		// me.addInstruction(Opcode.opc_dup);
		// // LDC "_parents"
		// me.addInstruction(Opcode.opc_ldc, "_parents");
		// // LDC [Lcom/db4o/bloat/Dog;.class
		// invokeClassGetter(me, Dog.class);
		// // ICONST_1
		// me.addInstruction(Opcode.opc_ldc,new Integer(1));
		// // L13 (66)
		// me.addLabel(labels[13]);
		// // ICONST_0
		// me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// // ICONST_0
		// me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// // L14 (69)
		// me.addLabel(labels[14]);
		// // INVOKESPECIAL
		// FieldInfo.<init>(String,Class,boolean,boolean,boolean)
		// // : void
		// me.addInstruction(Opcode.opc_invokespecial,
		// methodRef(FieldInfo.class,
		// "<init>", new Class[] { String.class, Class.class,
		// Boolean.class, Boolean.class, Boolean.class },
		// void.class));
		// // AASTORE
		// me.addInstruction(Opcode.opc_aastore);
		// // DUP
		// me.addInstruction(Opcode.opc_dup);
		// // ICONST_2
		// me.addInstruction(Opcode.opc_ldc,new Integer(2));
		// // L15 (74)
		// me.addLabel(labels[15]);
		// // NEW FieldInfo
		// me.addInstruction(Opcode.opc_new, getType(FieldInfo.class));
		// // DUP
		// me.addInstruction(Opcode.opc_dup);
		// // LDC "_prices"
		// me.addInstruction(Opcode.opc_ldc, "_prices");
		// // LDC [I.class
		// invokeClassGetter(me, Integer.class);
		// // ICONST_1
		// me.addInstruction(Opcode.opc_ldc,new Integer(1));
		// // L16 (80)
		// me.addLabel(labels[16]);
		// // ICONST_0
		// me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// // ICONST_0
		// me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// // L17 (83)
		// me.addLabel(labels[17]);
		// // INVOKESPECIAL
		// FieldInfo.<init>(String,Class,boolean,boolean,boolean)
		// // : void
		// me.addInstruction(Opcode.opc_invokespecial,
		// methodRef(FieldInfo.class,
		// "<init>", new Class[] { String.class, Class.class,
		// Boolean.class, Boolean.class, Boolean.class },
		// void.class));
		// // AASTORE
		// me.addInstruction(Opcode.opc_aastore);
		// // L18 (86)
		// me.addLabel(labels[18]);
		// // INVOKESPECIAL ClassInfo.<init>(boolean,Class,FieldInfo[]) : void
		// me.addInstruction(Opcode.opc_invokespecial,
		// methodRef(ClassInfo.class,
		// "<init>", new Class[] { Boolean.class, Class.class,
		// FieldInfo[].class }, void.class));
		// // L19 (88)
		// me.addLabel(labels[19]);
		// // INVOKEVIRTUAL Hashtable.put(Object,Object) : Object
		// me.addInstruction(Opcode.opc_invokevirtual,
		// methodRef(Hashtable.class, "put", new Class[] { Object.class,
		// Object.class }, Object.class));
		// // POP
		// me.addInstruction(Opcode.opc_pop);
		// // L20 (91)
		// me.addLabel(labels[20]);
		// // RETURN
		// me.addInstruction(Opcode.opc_return);
		bld.commit();
		fe.commit();

	}

	private void classForName(MethodBuilder builder, Class clazz) {
		builder.ldc(clazz.getName());
		builder.invoke(Opcode.opc_invokestatic, builder.parentType(),
				"db4o$class$", new Class[] { String.class }, Class.class);
	}

	private void generateInfoForMethod() {
		MethodBuilder bld = new MethodBuilder(context, ce, Modifiers.PUBLIC,
				com.db4o.reflect.self.ClassInfo.class, "infoFor",
				new Class[] { Class.class }, null);
		bld.getstatic(ce.type(), Hashtable.class, "CLASSINFO");
		bld.aload(1);
		bld.invoke(Opcode.opc_invokevirtual, Hashtable.class, "get",
				new Class[] { Object.class }, Object.class);
		bld.checkcast(ClassInfo.class);
		bld.areturn();
		bld.commit();

	}

	private void generateArrayForMethod() {
		MethodBuilder bld = new MethodBuilder(context, ce, Modifiers.PUBLIC,
				Object.class, "arrayFor", new Class[] { Class.class,
						Integer.class }, null);
		int labelIdx = 1;
		for (int classIdx = 0; classIdx < clazzes.length; classIdx++) {
			classForName(bld, clazzes[classIdx]);
			bld.aload(1);
			bld.invoke(Opcode.opc_invokevirtual, Class.class,
					"isAssignableFrom", new Class[] { Class.class },
					Boolean.class);
			bld.ifeq(labelIdx);
			bld.iload(2);
			bld.newarray(clazzes[classIdx]);
			bld.areturn();
			bld.label(labelIdx);
			labelIdx++;
		}
		bld.aload(0);
		bld.aload(1);
		bld.iload(2);
		bld.invoke(Opcode.opc_invokespecial, SelfReflectionRegistry.class,
				"arrayFor", new Class[] { Class.class, Integer.class },
				Object.class);
		bld.areturn();
		bld.commit();
	}

	private void generateComponentTypeMethod() {
		MethodBuilder bld = new MethodBuilder(context, ce, Modifiers.PUBLIC,
				Class.class, "componentType", new Class[] { Class.class },
				new Class[0]);
		int labelId = 1;
		for (int classIdx = 0; classIdx < clazzes.length; classIdx++) {
			classForName(bld, clazzes[classIdx]);
			bld.aload(1);
			bld.invoke(Opcode.opc_invokevirtual, Class.class,
					"isAssignableFrom", new Class[] { Class.class },
					Boolean.class);
			bld.ifeq(labelId);
			classForName(bld, clazzes[classIdx]);
			bld.areturn();
			bld.label(labelId);
			labelId++;
		}
		bld.aload(0);
		bld.aload(1);
		bld.invoke(Opcode.opc_invokespecial,
				com.db4o.reflect.self.SelfReflectionRegistry.class,
				"componentType", new Class[] { Class.class }, Class.class);
		bld.areturn();
		bld.commit();

	}
}
