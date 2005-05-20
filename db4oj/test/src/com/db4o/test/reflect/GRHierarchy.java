package com.db4o.test.reflect;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.reflect.jdk.*;
import com.db4o.test.util.*;

// TODO: Works for solo mode only currently
public class GRHierarchy {
	public static abstract class A {
		private int id;

		public A(int id) {
			this.id = id;
		}
	}

	public static abstract class B {
		private String name;

		public B(String name) {
			this.name = name;
		}
	}

	public static class A1 extends A {
		private Character ch;

		public A1(int id, Character ch) {
			super(id);
			this.ch = ch;
		}
	}

	public static class B1 extends B {
		private A a;

		public B1(String name, A a) {
			super(name);
			this.a = a;
		}
	}

	public void store() {
		A a = new A1(42, Character.valueOf('x'));
		B b = new B1("test", a);
		com.db4o.test.Test.store(b);
	}

	public void test() {
		List excluded=new ArrayList();
		excluded.add(A.class.getName());
		excluded.add(B.class.getName());
		excluded.add(A1.class.getName());
		excluded.add(B1.class.getName());
		ExcludingClassLoader loader=new ExcludingClassLoader(getClass().getClassLoader(),excluded);
		Db4o.configure().reflectWith(new JdkReflector(loader));
		com.db4o.test.Test.reOpen();
		GenericReflector reflector = com.db4o.test.Test.objectContainer().ext().reflector();
		ReflectClass proto=reflector.forName(B.class.getName());
		Query query=com.db4o.test.Test.query();
		query.constrain(proto);
		ObjectSet result=query.execute();
		com.db4o.test.Test.ensure(result.size()==1);
		Object obj=result.next();
		com.db4o.test.Test.ensure(obj instanceof GenericObject);
		
		ReflectClass clazz=reflector.forObject(obj);
		com.db4o.test.Test.ensure(clazz instanceof GenericClass);
		com.db4o.test.Test.ensure(clazz.getName().equals(B1.class.getName()));
		ReflectClass superclazz=clazz.getSuperclass();
		com.db4o.test.Test.ensure(superclazz instanceof GenericClass);
		com.db4o.test.Test.ensure(superclazz.getName().equals(B.class.getName()));
		
		ReflectField[] subfields=clazz.getDeclaredFields();
		com.db4o.test.Test.ensure(subfields.length==1);
		com.db4o.test.Test.ensure(subfields[0].getName().equals("a"));
		ReflectClass fieldtype=reflector.forName(A.class.getName());
		com.db4o.test.Test.ensure(subfields[0].getType().equals(fieldtype));
		Object subfieldvalue=subfields[0].get(obj);
		com.db4o.test.Test.ensure(subfieldvalue instanceof GenericObject);		
		ReflectClass concretetype=reflector.forObject(subfieldvalue);
		com.db4o.test.Test.ensure(concretetype instanceof GenericClass);		
		com.db4o.test.Test.ensure(concretetype.getName().equals(A1.class.getName()));				
		
		ReflectField[] superfields=superclazz.getDeclaredFields();
		com.db4o.test.Test.ensure(superfields.length==1);
		com.db4o.test.Test.ensure(superfields[0].getName().equals("name"));
		fieldtype=reflector.forName(String.class.getName());
		com.db4o.test.Test.ensure(superfields[0].getType().equals(fieldtype));
		Object superfieldvalue=superfields[0].get(obj);
		com.db4o.test.Test.ensure(superfieldvalue.equals("test"));		
	}
}
