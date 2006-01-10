package com.db4o.j2me.bloat;


import EDU.purdue.cs.bloat.editor.ClassEditor;
import EDU.purdue.cs.bloat.editor.MemberRef;
import EDU.purdue.cs.bloat.editor.MethodEditor;
import EDU.purdue.cs.bloat.editor.Opcode;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.reflect.MethodInfo;
import EDU.purdue.cs.bloat.reflect.Modifiers;

public class ClassEnhancer extends Enhancer {
	private boolean inspectNoArgsConstr(ClassEditor ce, MethodInfo[] methods) {
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

	private void addNoArgConstructor(ClassEditor ce) {
		MethodEditor init = new MethodEditor(ce, Modifiers.PUBLIC, Type
				.getType("()V"), "<init>", new Type[0], new Type[0]);
		MemberRef mr = methodRef(ce.getClass(), "<init>", new Class[0],
				void.class);
		init.addLabel(init.newLabel());
		init.addInstruction(Opcode.opcx_aload, init.paramAt(0));
		init.addInstruction(Opcode.opcx_invokespecial, mr);
		init.addInstruction(Opcode.opcx_return);
		init.commit();
	}

	private void generateSelf_get(ClassEditor ce) {
		MethodEditor me = createMethod(ce, Modifiers.PUBLIC, Object.class,
				"self_get", new Class[] { String.class }, new Class[0]);
		MemberRef mr = methodRef(ce.getClass(), "self_get",
				new Class[] { String.class }, Object.class);
		// TODO: instructions:
		/*
		 * public Object self_get(String fieldName) {
		 * if(fieldName.equals("_age")) { return new Integer(_age); }
		 * if(fieldName.equals("_parents")) { return _parents; }
		 * if(fieldName.equals("_prices")) { return _prices; } return
		 * super.self_get(fieldName); }
		 * 
		 */
	}

	private void generateSelf_set(ClassEditor ce) {
		MethodEditor me = new MethodEditor(ce, Modifiers.PUBLIC, Type.VOID,
				"self_set", new Type[] { getType(String.class),
						getType(Object.class) }, new Type[0]);
		MemberRef mr = methodRef(ce.getClass(), "self_set", new Class[] {
				String.class, Object.class }, void.class);
		// TODO: instructions:
		/*
		 * 
		 * public void self_set(String fieldName,Object value) {
		 * if(fieldName.equals("_age")) { _age=((Integer)value).intValue();
		 * return; } if(fieldName.equals("_parents")) { _parents=(Dog[])value;
		 * return; } if(fieldName.equals("_prices")) { _prices=(int[])value;
		 * return; } super.self_set(fieldName,value); }
		 */
	}


public static void main(String[] args) {
	
}
}
