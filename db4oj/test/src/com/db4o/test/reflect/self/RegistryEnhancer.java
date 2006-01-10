package com.db4o.test.reflect.self;

import java.util.Hashtable;

import EDU.purdue.cs.bloat.editor.ClassEditor;
import EDU.purdue.cs.bloat.editor.FieldEditor;
import EDU.purdue.cs.bloat.editor.MemberRef;
import EDU.purdue.cs.bloat.editor.MethodEditor;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.reflect.Modifiers;

import com.db4o.reflect.self.ClassInfo;

public class RegistryEnhancer extends Enhancer{

	private void generateCLASSINFOField(ClassEditor ce) {
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
	}

	private void generateInfoForMethod(ClassEditor ce) {
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
	}

	private void generateArrayForMethod(ClassEditor ce) {
		MethodEditor me = createMethod(ce, Modifiers.PUBLIC, Object.class,
				"arrayFor", new Class[] { Class.class, Integer.class },
				new Class[0]);
		MemberRef mr = methodRef(ce.getClass(), "arrayFor", new Class[] {
				Class.class, Integer.class }, Object.class);
		// TODO: inject instructions:
		/*
		 * public Object arrayFor(Class clazz, int length) { if
		 * (Dog.class.isAssignableFrom(clazz)) { return new Dog[length]; } if
		 * (Animal.class.isAssignableFrom(clazz)) { return new Animal[length]; }
		 * return super.arrayFor(clazz, length); }
		 * 
		 */
	}

	private void generateComponentTypeMethod(ClassEditor ce) {
		MethodEditor me = createMethod(ce, Modifiers.PUBLIC, Class.class,
				"componentType", new Class[] { Class.class }, new Class[0]);
		MemberRef mr = methodRef(ce.getClass(), "componentType",
				new Class[] { Class.class }, Class.class);
		// TODO:inject instructions:
		/*
		 * public Class componentType(Class clazz) { if
		 * (Dog[].class.isAssignableFrom(clazz)) { return Dog.class; } if
		 * (Animal[].class.isAssignableFrom(clazz)) { return Animal.class; }
		 * return super.componentType(clazz); }
		 */
	}

	public static void main(String[] args) {

	}

}
