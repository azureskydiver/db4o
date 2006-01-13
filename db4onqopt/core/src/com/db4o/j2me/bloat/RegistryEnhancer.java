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
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.reflect.self.ClassInfo;
import com.db4o.reflect.self.FieldInfo;
import com.db4o.reflect.self.SelfReflectionRegistry;
import com.db4o.test.reflect.self.Animal;
import com.db4o.test.reflect.self.Dog;
import com.db4o.test.reflect.self.RegressionDogSelfReflectionRegistry;

public class RegistryEnhancer extends Enhancer {

	public void generateCLASSINFOField(ClassEditor ce) {
		FieldEditor fe = createField(ce, 26, Type.getType(Hashtable.class),
				"CLASSINFO");

		Label[] labels = createLabels(20);
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
		MethodEditor me = createMethod(ce, Modifiers.STATIC, void.class,
				"<clinit>", new Class[0], new Class[0]);
		// L0 (0)
		me.addLabel(labels[0]);
		// NEW Hashtable
		me.addInstruction(Opcode.opc_new, getType(Hashtable.class));// ??????
		// DUP
		me.addInstruction(Opcode.opc_dup);
		// ICONST_2
		me.addInstruction(Opcode.opc_ldc,new Integer(2)/*new Constant(Constant.INTEGER,new Integer(2))*/);
		// INVOKESPECIAL Hashtable.<init>(int) : void
		me.addInstruction(Opcode.opc_invokespecial, methodRef(Hashtable.class,
				"<init>", new Class[] { Integer.class }, void.class));
		// PUTSTATIC RegressionDogSelfReflectionRegistry.CLASSINFO : Hashtable
		me.addInstruction(Opcode.opc_putstatic, fieldRef(ce.name(),
				Hashtable.class, "CLASSINFO"));
		// L1 (6)
		me.addLabel(labels[1]);
		// GETSTATIC RegressionDogSelfReflectionRegistry.CLASSINFO : Hashtable
		me.addInstruction(Opcode.opc_getstatic, fieldRef(ce.name(),
				Hashtable.class, "CLASSINFO"));
		// LDC Lcom/db4o/bloat/Animal;.class
		invokeClassGetter(me, Animal.class);
		//me.addInstruction(Opcode.opc_ldc, getType(Animal.class));
		// NEW ClassInfo
		me.addInstruction(Opcode.opc_new, getType(ClassInfo.class));
		// DUP
		me.addInstruction(Opcode.opc_dup);
		// ICONST_1
		me.addInstruction(Opcode.opc_ldc,new Integer(1));
		// LDC Ljava/lang/Object;.class
		invokeClassGetter(me, Object.class);
		// L2 (13)
		me.addLabel(labels[2]);
		// ICONST_1
		me.addInstruction(Opcode.opc_ldc,new Integer(1));
		// ANEWARRAY FieldInfo
		me.addInstruction(Opcode.opc_newarray, getType(FieldInfo.class));
		// DUP
		me.addInstruction(Opcode.opc_dup);
		// ICONST_0
		me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// L3 (18)
		me.addLabel(labels[3]);
		// NEW FieldInfo
		me.addInstruction(Opcode.opc_new, getType(FieldInfo.class));
		// DUP
		me.addInstruction(Opcode.opc_dup);
		// LDC "_name"
		me.addInstruction(Opcode.opc_ldc, "_name");
		// LDC Ljava/lang/String;.class
		invokeClassGetter(me, String.class);
		// ICONST_1
		me.addInstruction(Opcode.opc_ldc,new Integer(1));
		// L4 (24)
		me.addLabel(labels[4]);
		// ICONST_0
		me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// ICONST_0
		me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// INVOKESPECIAL FieldInfo.<init>(String,Class,boolean,boolean,boolean)
		// : void
		me.addInstruction(Opcode.opc_invokespecial, methodRef(FieldInfo.class,
				"<init>", new Class[] { String.class, Class.class,
						Boolean.class, Boolean.class, Boolean.class },
				void.class));
		// AASTORE
		me.addInstruction(Opcode.opc_aastore);
		// INVOKESPECIAL ClassInfo.<init>(boolean,Class,FieldInfo[]) : void
		me.addInstruction(Opcode.opc_invokespecial, methodRef(ClassInfo.class,
				"<init>", new Class[] { Boolean.class, Class.class,
						FieldInfo[].class }, void.class));
		// L5 (30)
		me.addLabel(labels[5]);
		// INVOKEVIRTUAL Hashtable.put(Object,Object) : Object
		me.addInstruction(Opcode.opc_invokevirtual,
				methodRef(Hashtable.class, "put", new Class[] { Object.class,
						Object.class }, Object.class));
		// POP
		me.addInstruction(Opcode.opc_pop);
		// L6 (33)
		me.addLabel(labels[6]);
		// GETSTATIC RegressionDogSelfReflectionRegistry.CLASSINFO : Hashtable
		me.addInstruction(Opcode.opc_getstatic, fieldRef(ce.name(),
				Hashtable.class, "CLASSINFO"));
		// LDC Lcom/db4o/bloat/Dog;.class
		invokeClassGetter(me, Dog.class);
		// L7 (36)
		me.addLabel(labels[7]);
		// NEW ClassInfo
		invokeClassGetter(me, ClassInfo.class);
		// DUP
		me.addInstruction(Opcode.opc_dup);
		// ICONST_0
		me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// LDC Lcom/db4o/bloat/Animal;.class
		invokeClassGetter(me, Animal.class);
		// L8 (41)
		me.addLabel(labels[8]);
		// ICONST_3
		me.addInstruction(Opcode.opc_ldc,new Integer(3));
		// ANEWARRAY FieldInfo
		me.addInstruction(Opcode.opc_newarray, getType(FieldInfo.class));
		// DUP
		me.addInstruction(Opcode.opc_dup);
		// ICONST_0
		me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// L9 (46)
		me.addLabel(labels[9]);
		// NEW FieldInfo
		me.addInstruction(Opcode.opc_new, getType(FieldInfo.class));
		// DUP
		me.addInstruction(Opcode.opc_dup);
		// LDC "_age"
		me.addInstruction(Opcode.opc_ldc, "_age");
		// LDC Ljava/lang/Integer;.class
		invokeClassGetter(me, Integer.class);
		// ICONST_1
		me.addInstruction(Opcode.opc_ldc, new Integer(1));
		// L10 (52)
		me.addLabel(labels[10]);
		// ICONST_0
		me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// ICONST_0
		me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// L11 (55)
		me.addLabel(labels[11]);
		// INVOKESPECIAL FieldInfo.<init>(String,Class,boolean,boolean,boolean)
		// : void
		me.addInstruction(Opcode.opc_invokespecial, methodRef(FieldInfo.class,
				"<init>", new Class[] { String.class, Class.class,
						Boolean.class, Boolean.class, Boolean.class },
				void.class));
		// AASTORE
		me.addInstruction(Opcode.opc_aastore);
		// DUP
		me.addInstruction(Opcode.opc_dup);
		// ICONST_1
		me.addInstruction(Opcode.opc_ldc,new Integer(1));
		// L12 (60)
		me.addLabel(labels[12]);
		// NEW FieldInfo
		me.addInstruction(Opcode.opc_new, getType(FieldInfo.class));
		// DUP
		me.addInstruction(Opcode.opc_dup);
		// LDC "_parents"
		me.addInstruction(Opcode.opc_ldc, "_parents");
		// LDC [Lcom/db4o/bloat/Dog;.class
		invokeClassGetter(me, Dog.class);
		// ICONST_1
		me.addInstruction(Opcode.opc_ldc,new Integer(1));
		// L13 (66)
		me.addLabel(labels[13]);
		// ICONST_0
		me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// ICONST_0
		me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// L14 (69)
		me.addLabel(labels[14]);
		// INVOKESPECIAL FieldInfo.<init>(String,Class,boolean,boolean,boolean)
		// : void
		me.addInstruction(Opcode.opc_invokespecial, methodRef(FieldInfo.class,
				"<init>", new Class[] { String.class, Class.class,
						Boolean.class, Boolean.class, Boolean.class },
				void.class));
		// AASTORE
		me.addInstruction(Opcode.opc_aastore);
		// DUP
		me.addInstruction(Opcode.opc_dup);
		// ICONST_2
		me.addInstruction(Opcode.opc_ldc,new Integer(2));
		// L15 (74)
		me.addLabel(labels[15]);
		// NEW FieldInfo
		me.addInstruction(Opcode.opc_new, getType(FieldInfo.class));
		// DUP
		me.addInstruction(Opcode.opc_dup);
		// LDC "_prices"
		me.addInstruction(Opcode.opc_ldc, "_prices");
		// LDC [I.class
		invokeClassGetter(me, Integer.class);
		// ICONST_1
		me.addInstruction(Opcode.opc_ldc,new Integer(1));
		// L16 (80)
		me.addLabel(labels[16]);
		// ICONST_0
		me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// ICONST_0
		me.addInstruction(Opcode.opc_ldc,new Integer(0));
		// L17 (83)
		me.addLabel(labels[17]);
		// INVOKESPECIAL FieldInfo.<init>(String,Class,boolean,boolean,boolean)
		// : void
		me.addInstruction(Opcode.opc_invokespecial, methodRef(FieldInfo.class,
				"<init>", new Class[] { String.class, Class.class,
						Boolean.class, Boolean.class, Boolean.class },
				void.class));
		// AASTORE
		me.addInstruction(Opcode.opc_aastore);
		// L18 (86)
		me.addLabel(labels[18]);
		// INVOKESPECIAL ClassInfo.<init>(boolean,Class,FieldInfo[]) : void
		me.addInstruction(Opcode.opc_invokespecial, methodRef(ClassInfo.class,
				"<init>", new Class[] { Boolean.class, Class.class,
						FieldInfo[].class }, void.class));
		// L19 (88)
		me.addLabel(labels[19]);
		// INVOKEVIRTUAL Hashtable.put(Object,Object) : Object
		me.addInstruction(Opcode.opc_invokevirtual,
				methodRef(Hashtable.class, "put", new Class[] { Object.class,
						Object.class }, Object.class));
		// POP
		me.addInstruction(Opcode.opc_pop);
		// L20 (91)
		me.addLabel(labels[20]);
		// RETURN
		me.addInstruction(Opcode.opc_return);
		me.commit();
		fe.commit();

	}

	protected void invokeClassGetter(MethodEditor me,Class clazz) {
		MemberRef classGetter=methodRef(getType("L"+me.declaringClass().name()+";"), "db4o$class$", new Class[]{String.class}, Class.class);
		me.addInstruction(Opcode.opc_ldc,clazz.getName());
		me.addInstruction(Opcode.opc_invokestatic,classGetter);
	}
	
	public void generateInfoForMethod(ClassEditor ce) {
		MethodEditor me = createMethod(ce, Modifiers.PUBLIC,
				com.db4o.reflect.self.ClassInfo.class, "infoFor",
				new Class[] { Class.class }, new Class[0]);
		MemberRef mr = methodRef(ce.getClass(), "infoFor",
				new Class[] { Class.class }, ClassInfo.class);
		LocalVariable[] localVars = createLocalVariables(1);
		Label[] labels = createLabels(7);
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
		me.addLabel(labels[0]);
		me.addInstruction(Opcode.opc_getstatic, fieldRef(ce.name(),
				Hashtable.class, "CLASSINFO"));
		me.addInstruction(Opcode.opc_aload, localVars[1]);
		me.addInstruction(Opcode.opc_invokevirtual, methodRef(Hashtable.class,
				"get", new Class[] { Object.class }, Object.class));
		me.addInstruction(Opcode.opc_checkcast, getType(ClassInfo.class));
		me.addInstruction(Opcode.opc_areturn);
		me.addLabel(labels[1]);
		me.commit();

	}

	public void generateArrayForMethod(ClassEditor ce, Class clazz) {
		// clazz==Dog.class;
		MethodEditor me = createMethod(ce, Modifiers.PUBLIC, Object.class,
				"arrayFor", new Class[] { Class.class, Integer.class },
				new Class[0]);
		MemberRef mr = methodRef(ce.getClass(), "arrayFor", new Class[] {
				Class.class, Integer.class }, Object.class);
		LocalVariable[] localVars = createLocalVariables(2);
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
		me.addLabel(new Label(0));
		// LDC Lcom/db4o/bloat/Dog;.class

		// FIXME:
		// Create list of classes, topological sort this list:
		// while (!(clazz.getSuperclass().equals(Object.class)))
		int labelIdx=1;
		while (!(clazz.equals(Object.class))) {
			invokeClassGetter(me, clazz);
			// ALOAD 1: clazz
			me.addInstruction(Opcode.opc_aload, localVars[1]);
			// INVOKEVIRTUAL Class.isAssignableFrom(Class) : boolean
			me.addInstruction(Opcode.opc_invokevirtual, methodRef(Class.class,
					"isAssignableFrom", new Class[] { Class.class },
					Boolean.class));
			// IFEQ L1
			Label label=new Label(labelIdx);
			me.addInstruction(Opcode.opc_ifeq, label); 
			// L2 (5)
			//me.addLabel(labels[2]);
			// ILOAD 2: length
			me.addInstruction(Opcode.opc_iload, localVars[2]);
			// ANEWARRAY Dog
			me.addInstruction(Opcode.opc_newarray, getType(clazz));
			// ARETURN
			me.addInstruction(Opcode.opc_areturn);
			me.addLabel(label);
			labelIdx++;
			clazz = clazz.getSuperclass();
		}
		// L1 (9)
		//me.addLabel(labels[1]);
		// LDC Lcom/db4o/bloat/Animal;.class
		// ALOAD 1: clazz
		// INVOKEVIRTUAL Class.isAssignableFrom(Class) : boolean
		// IFEQ L3
		// L4 (14)
		//me.addLabel(labels[4]);
		// ILOAD 2: length
		// ANEWARRAY Animal
		// ARETURN---------
		// L3 (18)
		//me.addLabel(labels[3]);
		// ALOAD 0: this
		me.addInstruction(Opcode.opc_aload, localVars[0]);
		// ALOAD 1: clazz
		me.addInstruction(Opcode.opc_iload, localVars[1]);
		// ILOAD 2: length
		me.addInstruction(Opcode.opc_iload, localVars[2]);
		// INVOKESPECIAL SelfReflectionRegistry.arrayFor(Class,int) : Object
		me.addInstruction(Opcode.opc_invokespecial, methodRef(
				SelfReflectionRegistry.class, "arrayFor", new Class[] {
						Class.class, Integer.class }, Object.class));
		// ARETURN
		me.addInstruction(Opcode.opc_areturn);
		// L5 (24)
		//me.addLabel(labels[5]);
		me.commit();
	}

	public void generateComponentTypeMethod(ClassEditor ce, Class clazz) {
		// clazz== Dog.class;
		MethodEditor me = createMethod(ce, Modifiers.PUBLIC, Class.class,
				"componentType", new Class[] { Class.class }, new Class[0]);
		MemberRef mr = methodRef(ce.getClass(), "componentType",
				new Class[] { Class.class }, Class.class);
		LocalVariable[] localVars = createLocalVariables(1);
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
		me.addLabel(new Label(0));
		// LDC [Lcom/db4o/bloat/Dog;.class

		int labelId=1;
		while (!(clazz.equals(Object.class))) {
			invokeClassGetter(me, clazz);
			// ALOAD 1: clazz
			me.addInstruction(Opcode.opc_aload, localVars[1]);
			// INVOKEVIRTUAL Class.isAssignableFrom(Class) : boolean
			me.addInstruction(Opcode.opc_invokevirtual, methodRef(Class.class,
					"isAssignableFrom", new Class[] { Class.class },
					Boolean.class));
			// IFEQ L1
			Label label=new Label(labelId);
			me.addInstruction(Opcode.opc_ifeq, label);
			// L2 (5)
			// me.addLabel(labels[2]);
			// LDC Lcom/db4o/bloat/Dog;.class
			invokeClassGetter(me, clazz);
			// ARETURN
			me.addInstruction(Opcode.opc_areturn);
			clazz = clazz.getSuperclass();
			me.addLabel(label);
			labelId++;
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
		//me.addLabel(labels[3]);
		// ALOAD 0: this
		me.addInstruction(Opcode.opc_aload, localVars[0]);
		// ALOAD 1: clazz
		me.addInstruction(Opcode.opc_aload, localVars[1]);
		// INVOKESPECIAL SelfReflectionRegistry.componentType(Class) : Class
		me.addInstruction(Opcode.opc_invokespecial, methodRef(
				com.db4o.reflect.self.SelfReflectionRegistry.class,
				"componentType", new Class[] { Class.class }, Class.class));

		// ARETURN
		me.addInstruction(Opcode.opc_areturn);
		// L5 (21)
		//me.addLabel(labels[5]);
		me.commit();

	}

}
