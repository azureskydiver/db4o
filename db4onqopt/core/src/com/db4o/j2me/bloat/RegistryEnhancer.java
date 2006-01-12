package com.db4o.j2me.bloat;

import java.util.Hashtable;

import EDU.purdue.cs.bloat.editor.ClassEditor;
import EDU.purdue.cs.bloat.editor.FieldEditor;
import EDU.purdue.cs.bloat.editor.Label;
import EDU.purdue.cs.bloat.editor.LocalVariable;
import EDU.purdue.cs.bloat.editor.MemberRef;
import EDU.purdue.cs.bloat.editor.MethodEditor;
import EDU.purdue.cs.bloat.editor.Opcode;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.reflect.Modifiers;

import com.db4o.reflect.self.ClassInfo;
import com.db4o.reflect.self.SelfReflectionRegistry;

public class RegistryEnhancer extends Enhancer {

	public void generateCLASSINFOField(ClassEditor ce) {
		FieldEditor fe = createField(ce, 26, Type.getType(Hashtable.class),
				"CLASSINFO");
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
		// L0 (0)
		// NEW Hashtable
		// DUP
		// ICONST_2
		// INVOKESPECIAL Hashtable.<init>(int) : void
		// PUTSTATIC RegressionDogSelfReflectionRegistry.CLASSINFO : Hashtable
		// L1 (6)
		// GETSTATIC RegressionDogSelfReflectionRegistry.CLASSINFO : Hashtable
		// LDC Lcom/db4o/bloat/Animal;.class
		// NEW ClassInfo
		// DUP
		// ICONST_1
		// LDC Ljava/lang/Object;.class
		// L2 (13)
		// ICONST_1
		// ANEWARRAY FieldInfo
		// DUP
		// ICONST_0
		// L3 (18)
		// NEW FieldInfo
		// DUP
		// LDC "_name"
		// LDC Ljava/lang/String;.class
		// ICONST_1
		// L4 (24)
		// ICONST_0
		// ICONST_0
		// INVOKESPECIAL FieldInfo.<init>(String,Class,boolean,boolean,boolean)
		// : void
		// AASTORE
		// INVOKESPECIAL ClassInfo.<init>(boolean,Class,FieldInfo[]) : void
		// L5 (30)
		// INVOKEVIRTUAL Hashtable.put(Object,Object) : Object
		// POP
		// L6 (33)
		// GETSTATIC RegressionDogSelfReflectionRegistry.CLASSINFO : Hashtable
		// LDC Lcom/db4o/bloat/Dog;.class
		// L7 (36)
		// NEW ClassInfo
		// DUP
		// ICONST_0
		// LDC Lcom/db4o/bloat/Animal;.class
		// L8 (41)
		// ICONST_3
		// ANEWARRAY FieldInfo
		// DUP
		// ICONST_0
		// L9 (46)
		// NEW FieldInfo
		// DUP
		// LDC "_age"
		// LDC Ljava/lang/Integer;.class
		// ICONST_1
		// L10 (52)
		// ICONST_0
		// ICONST_0
		// L11 (55)
		// INVOKESPECIAL FieldInfo.<init>(String,Class,boolean,boolean,boolean)
		// : void
		// AASTORE
		// DUP
		// ICONST_1
		// L12 (60)
		// NEW FieldInfo
		// DUP
		// LDC "_parents"
		// LDC [Lcom/db4o/bloat/Dog;.class
		// ICONST_1
		// L13 (66)
		// ICONST_0
		// ICONST_0
		// L14 (69)
		// INVOKESPECIAL FieldInfo.<init>(String,Class,boolean,boolean,boolean)
		// : void
		// AASTORE
		// DUP
		// ICONST_2
		// L15 (74)
		// NEW FieldInfo
		// DUP
		// LDC "_prices"
		// LDC [I.class
		// ICONST_1
		// L16 (80)
		// ICONST_0
		// ICONST_0
		// L17 (83)
		// INVOKESPECIAL FieldInfo.<init>(String,Class,boolean,boolean,boolean)
		// : void
		// AASTORE
		// L18 (86)
		// INVOKESPECIAL ClassInfo.<init>(boolean,Class,FieldInfo[]) : void
		// L19 (88)
		// INVOKEVIRTUAL Hashtable.put(Object,Object) : Object
		// POP
		// L20 (91)
		// RETURN
	}

	public void generateInfoForMethod(ClassEditor ce) {
		MethodEditor me = createMethod(ce, Modifiers.PUBLIC,
				com.db4o.reflect.self.ClassInfo.class, "infoFor",
				new Class[] { Class.class }, new Class[0]);
		MemberRef mr = methodRef(ce.getClass(), "infoFor",
				new Class[] { Class.class }, ClassInfo.class);
		// TODO: inject instructions:
		/*
		 * public ClassInfo infoFor(Class clazz) { return (ClassInfo)
		 * CLASSINFO.get(clazz); }
		 * 
		 */
		// access flags 1
		// public infoFor(Class) : ClassInfo
		// L0 (0)
		// GETSTATIC RegressionDogSelfReflectionRegistry.CLASSINFO : Hashtable
		// ALOAD 1: clazz
		// INVOKEVIRTUAL Hashtable.get(Object) : Object
		// CHECKCAST ClassInfo
		// CHECKCAST ClassInfo
		// ARETURN
		// L1 (7)
		me.addLabel(new Label(1));
		me.addInstruction(Opcode.opc_getstatic, fieldRef(ce.name(),
				Hashtable.class, "CLASSINFO"));
		me.addInstruction(Opcode.opc_aload, new LocalVariable(1));
		me.addInstruction(Opcode.opc_invokevirtual, methodRef(Hashtable.class,
				"get", new Class[] { Object.class }, Object.class));
		me.addInstruction(Opcode.opc_checkcast, getType(ClassInfo.class));
		me.addInstruction(Opcode.opc_areturn);
		me.commit();

	}

	public void generateArrayForMethod(ClassEditor ce, Class clazz) {
		// clazz==Dog.class;
		MethodEditor me = createMethod(ce, Modifiers.PUBLIC, Object.class,
				"arrayFor", new Class[] { Class.class, Integer.class },
				new Class[0]);
		MemberRef mr = methodRef(ce.getClass(), "arrayFor", new Class[] {
				Class.class, Integer.class }, Object.class);
		LocalVariable localVariable0 = new LocalVariable(0);
		LocalVariable localVariable1 = new LocalVariable(1);
		LocalVariable localVariable2 = new LocalVariable(2);
		me.addLabel(new Label(1));
		// TODO: inject instructions:
		/*
		 * public Object arrayFor(Class clazz, int length) { if
		 * (Dog.class.isAssignableFrom(clazz)) { return new Dog[length]; } if
		 * (Animal.class.isAssignableFrom(clazz)) { return new Animal[length]; }
		 * return super.arrayFor(clazz, length); }
		 * 
		 */
		// access flags 1
		// public arrayFor(Class,int) : Object
		// L0 (0)
		// LDC Lcom/db4o/bloat/Dog;.class
		// FIXME:
		// Create list of classes, topological sort this list:
		// while (!(clazz.getSuperclass().equals(Object.class)))
		while (!(clazz.getSuperclass().equals(Object.class))) {

			me.addInstruction(Opcode.opc_ldc, clazz);
			// ALOAD 1: clazz
			me.addInstruction(Opcode.opc_aload_1, localVariable1);
			// INVOKEVIRTUAL Class.isAssignableFrom(Class) : boolean
			me.addInstruction(Opcode.opc_invokevirtual, methodRef(Class.class,
					"isAssignableFrom", new Class[] { Class.class },
					Boolean.class));
			// IFEQ L1
			me.addInstruction(Opcode.opc_ifeq); // is that correct?? do we need
												// another parameters???
			// L2 (5)
			// ILOAD 2: length
			me.addInstruction(Opcode.opc_iload_2, localVariable2);
			// ANEWARRAY Dog
			me.addInstruction(Opcode.opc_anewarray, clazz);
			// ARETURN
			me.addInstruction(Opcode.opc_areturn);
			clazz = clazz.getSuperclass();
		}
		// L1 (9)
		// LDC Lcom/db4o/bloat/Animal;.class
		// ALOAD 1: clazz
		// INVOKEVIRTUAL Class.isAssignableFrom(Class) : boolean
		// IFEQ L3
		// L4 (14)
		// ILOAD 2: length
		// ANEWARRAY Animal
		// ARETURN---------
		// L3 (18)
		// ALOAD 0: this
		me.addInstruction(Opcode.opc_aload_0, localVariable0);
		// ALOAD 1: clazz
		me.addInstruction(Opcode.opc_iload_1, localVariable1);
		// ILOAD 2: length
		me.addInstruction(Opcode.opc_iload_2, localVariable2);
		// INVOKESPECIAL SelfReflectionRegistry.arrayFor(Class,int) : Object
		me.addInstruction(Opcode.opc_invokespecial, methodRef(
				SelfReflectionRegistry.class, "arrayFor", new Class[] {
						Class.class, Integer.class }, Object.class));
		// ARETURN
		me.addInstruction(Opcode.opc_areturn);
		// L5 (24)
		me.commit();
	}

	public void generateComponentTypeMethod(ClassEditor ce, Class clazz) {
		// clazz== Dog.class;
		MethodEditor me = createMethod(ce, Modifiers.PUBLIC, Class.class,
				"componentType", new Class[] { Class.class }, new Class[0]);
		MemberRef mr = methodRef(ce.getClass(), "componentType",
				new Class[] { Class.class }, Class.class);
		LocalVariable localVariable0 = new LocalVariable(0);
		LocalVariable localVariable1 = new LocalVariable(1);
		me.addLabel(new Label(1));
		// TODO:inject instructions:
		/*
		 * public Class componentType(Class clazz) { if
		 * (Dog[].class.isAssignableFrom(clazz)) { return Dog.class; } if
		 * (Animal[].class.isAssignableFrom(clazz)) { return Animal.class; }
		 * return super.componentType(clazz); }
		 */
		// access flags 1
		// public componentType(Class) : Class
		// L0 (0)
		// LDC [Lcom/db4o/bloat/Dog;.class
		while (!(clazz.getSuperclass().equals(Object.class))) {

			me.addInstruction(Opcode.opc_ldc, clazz);
			// ALOAD 1: clazz
			me.addInstruction(Opcode.opc_aload_1, localVariable1);
			// INVOKEVIRTUAL Class.isAssignableFrom(Class) : boolean
			me.addInstruction(Opcode.opc_invokevirtual, methodRef(Class.class,
					"isAssignableFrom", new Class[] { Class.class },
					Boolean.class));
			// IFEQ L1
			me.addInstruction(Opcode.opc_ifeq);
			// L2 (5)
			// LDC Lcom/db4o/bloat/Dog;.class
			me.addInstruction(Opcode.opc_ldc, clazz);
			// ARETURN
			me.addInstruction(Opcode.opc_areturn);
			clazz = clazz.getSuperclass();
		}
		// L1 (8)
		// LDC [Lcom/db4o/bloat/Animal;.class
		// ALOAD 1: clazz
		// INVOKEVIRTUAL Class.isAssignableFrom(Class) : boolean
		// IFEQ L3
		// L4 (13)
		// LDC Lcom/db4o/bloat/Animal;.class
		// ARETURN--------------

		// L3 (16)
		// ALOAD 0: this
		me.addInstruction(Opcode.opc_aload_0, localVariable0);
		// ALOAD 1: clazz
		me.addInstruction(Opcode.opc_aload_1, localVariable1);
		// INVOKESPECIAL SelfReflectionRegistry.componentType(Class) : Class
		me.addInstruction(Opcode.opc_invokespecial, methodRef(
				com.db4o.reflect.self.SelfReflectionRegistry.class,
				"componentType", new Class[] { Class.class }, Class.class));

		// ARETURN
		me.addInstruction(Opcode.opc_areturn);
		// L5 (21)
		me.commit();

	}

}
