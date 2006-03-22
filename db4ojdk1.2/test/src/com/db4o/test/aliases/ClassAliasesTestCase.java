package com.db4o.test.aliases;

import com.db4o.*;
import com.db4o.config.TypeAlias;
import com.db4o.reflect.ReflectClass;
import com.db4o.test.Test;

public class ClassAliasesTestCase {

	public void testTypeAlias() {
		ObjectContainer container = Test.objectContainer();

		container.set(new Person1("Homer Simpson"));
		container.set(new Person1("John Cleese"));
		
		container = Test.reOpen();
		container.ext().configure().alias(
				// Person1 instances should be read as Person2 objects
				new TypeAlias("com.db4o.test.aliases.Person1",
						"com.db4o.test.aliases.Person2"));
		
		ReflectClass rc = container.ext().reflector().forClass(Person2.class);
		System.out.println(rc.getName());
		
		ObjectSet os = container.query(Person2.class);
		
		Test.ensureEquals(2, os.size());
		ensureContains(os, new Person2("Homer Simpson"));
		ensureContains(os, new Person2("John Cleese"));
	}

	private void ensureContains(ObjectSet actual, Object expected) {
		actual.reset();
		while (actual.hasNext()) {
			Object next = actual.next();
			if (next.equals(expected))
				return;
		}
		Test.ensure(false);
	}
	
	public static void main(String[] args) {
		Test.runSolo(ClassAliasesTestCase.class);
	}

}
