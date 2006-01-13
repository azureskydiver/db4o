package com.db4o.j2me.bloat;

import EDU.purdue.cs.bloat.context.PersistentBloatContext;
import EDU.purdue.cs.bloat.editor.ClassEditor;
import EDU.purdue.cs.bloat.editor.EditorContext;
import EDU.purdue.cs.bloat.editor.Label;
import EDU.purdue.cs.bloat.editor.LocalVariable;
import EDU.purdue.cs.bloat.editor.MemberRef;
import EDU.purdue.cs.bloat.editor.MethodEditor;
import EDU.purdue.cs.bloat.editor.Opcode;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.file.ClassFileLoader;
import EDU.purdue.cs.bloat.reflect.ClassInfo;
import EDU.purdue.cs.bloat.reflect.MethodInfo;
import EDU.purdue.cs.bloat.reflect.Modifiers;

import com.db4o.test.reflect.self.Animal;
import com.db4o.test.reflect.self.Dog;

public class ClassEnhancer extends Enhancer {
	public ClassEditor loadClass(ClassFileLoader loader, String classPath,
			String className) {
		loader.appendClassPath(classPath);
		try {
			ClassInfo info = loader.loadClass(className);
			EditorContext context = new PersistentBloatContext(info.loader());
			return context.editClass(info);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean inspectNoArgConstr(ClassEditor ce, MethodInfo[] methods) {
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

	public void addNoArgConstructor(ClassEditor ce) {
		MethodEditor init = new MethodEditor(ce, Modifiers.PUBLIC, Type
				.getType("()V"), "<init>", new Type[0], new Type[0]);
		MemberRef mr = methodRef(ce.getClass(), "<init>", new Class[0],
				void.class);
		init.addLabel(new Label(0));
		init.addInstruction(Opcode.opcx_aload, init.paramAt(0));
		init.addInstruction(Opcode.opcx_invokespecial, mr);
		init.addInstruction(Opcode.opcx_return);
		init.commit();
	}

	public void generateSelf_get(ClassEditor ce) {
		MethodEditor me = createMethod(ce, Modifiers.PUBLIC, Object.class,
				"self_get", new Class[] { String.class }, new Class[0]);
		MemberRef mr = methodRef(ce.getClass(), "self_get",
				new Class[] { String.class }, Object.class);
		LocalVariable[] localVars = createLocalVariables(1);
		Label[] labels = createLabels(7);
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
		me.addLabel(labels[0]);
		// ALOAD 1: fieldName
		me.addInstruction(Opcode.opc_aload, localVars[1]);
		// LDC "_age"
		me.addInstruction(Opcode.opc_ldc, "_age");
		// INVOKEVIRTUAL String.equals(Object) : boolean
		me.addInstruction(Opcode.opc_invokevirtual, methodRef(String.class,
				"equals", new Class[] { Object.class }, Boolean.class));
		// IFEQ L1
		me.addInstruction(Opcode.opc_ifeq, labels[1]);
		// L2 (5)
		me.addLabel(labels[2]);
		// NEW Integer
		me.addInstruction(Opcode.opc_new, getType(Integer.class));
		// DUP
		me.addInstruction(Opcode.opc_dup);
		// ALOAD 0: this
		me.addInstruction(Opcode.opc_aload, localVars[0]);
		// GETFIELD Dog._age : int
		me.addInstruction(Opcode.opc_getfield, fieldRef(Dog.class,
				Integer.class, "_age"));
		// INVOKESPECIAL Integer.<init>(int) : void
		me.addInstruction(Opcode.opc_invokespecial, methodRef(Integer.class,
				"<init>", new Class[] { Integer.class }, void.class));
		// ARETURN
		me.addInstruction(Opcode.opc_areturn);
		// L1 (12)
		me.addLabel(labels[1]);
		// ALOAD 1: fieldName
		me.addInstruction(Opcode.opc_aload, localVars[1]);
		// LDC "_parents"
		me.addInstruction(Opcode.opc_ldc, "_parents");
		// INVOKEVIRTUAL String.equals(Object) : boolean
		me.addInstruction(Opcode.opc_invokevirtual, methodRef(String.class,
				"equals", new Class[] { Object.class }, Boolean.class));
		// IFEQ L3
		me.addInstruction(Opcode.opc_ifeq, labels[3]);
		// L4 (17)
		me.addLabel(labels[4]);
		// ALOAD 0: this
		me.addInstruction(Opcode.opc_aload, localVars[0]);
		// GETFIELD Dog._parents : Dog[]
		me.addInstruction(Opcode.opc_getfield, fieldRef(Dog.class, Dog[].class,
				"_parents"));
		// ARETURN
		me.addInstruction(Opcode.opc_areturn);
		// L3 (21)
		me.addLabel(labels[3]);
		// ALOAD 1: fieldName
		me.addInstruction(Opcode.opc_aload, localVars[1]);
		// LDC "_prices"
		me.addInstruction(Opcode.opc_ldc, "_prices");
		// INVOKEVIRTUAL String.equals(Object) : boolean
		me.addInstruction(Opcode.opc_invokevirtual, methodRef(String.class,
				"equals", new Class[] { Object.class }, Boolean.class));
		// IFEQ L5
		me.addInstruction(Opcode.opc_ifeq, labels[5]);
		// L6 (26)
		me.addLabel(labels[6]);
		// ALOAD 0: this
		me.addInstruction(Opcode.opc_aload, localVars[0]);
		// GETFIELD Dog._prices : int[]
		me.addInstruction(Opcode.opc_getfield, fieldRef(Dog.class,
				Integer[].class, "_prices"));
		// ARETURN
		me.addInstruction(Opcode.opc_areturn);
		// L5 (30)
		me.addLabel(labels[5]);
		// ALOAD 0: this
		me.addInstruction(Opcode.opc_aload, localVars[0]);
		// ALOAD 1: fieldName
		me.addInstruction(Opcode.opc_aload, localVars[1]);
		// INVOKESPECIAL Animal.self_get(String) : Object
		me.addInstruction(Opcode.opc_invokespecial, methodRef(Animal.class,
				"self_get", new Class[] { String.class }, Object.class));
		// ARETURN
		me.addInstruction(Opcode.opc_areturn);
		// L7 (35)
		me.addLabel(labels[7]);
		me.commit();
	}

	public void generateSelf_set(ClassEditor ce) {
		MethodEditor me = new MethodEditor(ce, Modifiers.PUBLIC, Type.VOID,
				"self_set", new Type[] { getType(String.class),
						getType(Object.class) }, new Type[0]);
		MemberRef mr = methodRef(ce.getClass(), "self_set", new Class[] {
				String.class, Object.class }, void.class);
		LocalVariable[] localVars = createLocalVariables(2);
		Label[] labels = createLabels(11);

		// TODO: instructions:
		/*
		 * 
		 * public void self_set(String fieldName,Object value) {
		 * if(fieldName.equals("_age")) { _age=((Integer)value).intValue();
		 * return; } if(fieldName.equals("_parents")) { _parents=(Dog[])value;
		 * return; } if(fieldName.equals("_prices")) { _prices=(int[])value;
		 * return; } super.self_set(fieldName,value); }
		 */
		// access flags 1
		// public self_set(String,Object) : void
		// L0 (0)
		me.addLabel(labels[0]);
		// ALOAD 1: fieldName
		me.addInstruction(Opcode.opc_aload, localVars[1]);
		// LDC "_age"
		me.addInstruction(Opcode.opc_ldc, "_age");
		// INVOKEVIRTUAL String.equals(Object) : boolean
		me.addInstruction(Opcode.opc_invokevirtual, methodRef(String.class,
				"equals", new Class[] { Object.class }, boolean.class));
		// IFEQ L1
		me.addInstruction(Opcode.opc_ifeq, labels[1]);
		// L2 (5)
		me.addLabel(labels[2]);
		// ALOAD 0: this
		me.addInstruction(Opcode.opc_aload, localVars[0]);
		// ALOAD 2: value
		me.addInstruction(Opcode.opc_aload, localVars[2]);
		// CHECKCAST Integer
		me.addInstruction(Opcode.opc_checkcast, getType(Integer.class));
		// INVOKEVIRTUAL Integer.intValue() : int
		me.addInstruction(Opcode.opc_invokevirtual, methodRef(Integer.class,
				"intValue", new Class[0], Integer.class));
		// PUTFIELD Dog._age : int
		me.addInstruction(Opcode.opc_putfield, fieldRef(Dog.class,
				Integer.class, "_age"));
		// L3 (11)
		me.addLabel(labels[3]);
		// RETURN
		me.addInstruction(Opcode.opc_return);
		// L1 (13)
		me.addLabel(labels[1]);
		// ALOAD 1: fieldName
		me.addInstruction(Opcode.opc_aload, localVars[1]);
		// LDC "_parents"
		me.addInstruction(Opcode.opc_ldc, "_parents");
		// INVOKEVIRTUAL String.equals(Object) : boolean
		me.addInstruction(Opcode.opc_invokevirtual, methodRef(String.class,
				"equals", new Class[] { Object.class }, Boolean.class));
		// IFEQ L4
		me.addInstruction(Opcode.opc_ifeq, labels[4]);
		// L5 (18)
		me.addLabel(labels[5]);
		// ALOAD 0: this
		me.addInstruction(Opcode.opc_aload, localVars[0]);
		// ALOAD 2: value
		me.addInstruction(Opcode.opc_aload, localVars[2]);
		// CHECKCAST Dog[]
		me.addInstruction(Opcode.opc_checkcast, getType(Dog[].class));
		// PUTFIELD Dog._parents : Dog[]
		me.addInstruction(Opcode.opc_putfield, fieldRef(Dog.class, Dog[].class,
				"_parents"));
		// L6 (23)
		me.addLabel(labels[6]);
		// RETURN
		me.addInstruction(Opcode.opc_return);
		// L4 (25)
		me.addLabel(labels[4]);
		// ALOAD 1: fieldName
		me.addInstruction(Opcode.opc_aload, localVars[1]);
		// LDC "_prices"
		me.addInstruction(Opcode.opc_ldc, "_prices");
		// INVOKEVIRTUAL String.equals(Object) : boolean
		me.addInstruction(Opcode.opc_invokevirtual, methodRef(String.class,
				"equals", new Class[] { Object.class }, Boolean.class));
		// IFEQ L7
		me.addInstruction(Opcode.opc_ifeq, labels[7]);
		// L8 (30)
		me.addLabel(labels[8]);
		// ALOAD 0: this
		me.addInstruction(Opcode.opc_aload, localVars[0]);
		// ALOAD 2: value
		me.addInstruction(Opcode.opc_aload, localVars[2]);
		// CHECKCAST int[]
		me.addInstruction(Opcode.opc_checkcast, getType(Integer[].class));
		// PUTFIELD Dog._prices : int[]
		me.addInstruction(Opcode.opc_putfield, fieldRef(Dog.class,
				Integer[].class, "_prices"));
		// L9 (35)
		me.addLabel(labels[9]);
		// RETURN
		me.addInstruction(Opcode.opc_return);
		// L7 (37)
		me.addLabel(labels[7]);
		// ALOAD 0: this
		me.addInstruction(Opcode.opc_aload, localVars[0]);
		// ALOAD 1: fieldName
		me.addInstruction(Opcode.opc_aload, localVars[1]);
		// ALOAD 2: value
		me.addInstruction(Opcode.opc_aload, localVars[2]);
		// INVOKESPECIAL Animal.self_set(String,Object) : void
		me.addInstruction(Opcode.opc_invokevirtual, methodRef(Animal.class,
				"self_set", new Class[] { String.class, Object.class },
				void.class));
		// L10 (42)
		me.addLabel(labels[10]);
		// RETURN
		me.addInstruction(Opcode.opc_return);
		// L11 (44)
		me.addLabel(labels[11]);
		me.commit();
	}
}
