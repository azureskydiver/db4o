package com.db4o.cs.generic;

import java.util.Date;
import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.generic.GenericClass;
import com.db4o.reflect.generic.GenericField;
import com.db4o.reflect.generic.GenericReflector;
import com.db4o.reflect.jdk.JdkReflector;

import db4ounit.Assert;
import db4ounit.TestRunner;
import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.Db4oTestSuiteBuilder;
import db4ounit.extensions.Db4oUtil;
import db4ounit.extensions.fixtures.Db4oSolo;
import model.Person;

/**
 * 
 * @author treeder, Andrew
 * 
 */
public class GenericObjectsTest1 extends AbstractDb4oTestCase {
	private String PERSON_CLASSNAME = "com.acme.Person";

	public static void main(String[] args) {
		new TestRunner(new Db4oTestSuiteBuilder(new Db4oSolo(),
				GenericObjectsTest1.class)).run();
	}

	public void testCreate() throws Exception {

		initGenericObjects();
		// fixture().reopen();
		ExtObjectContainer oc = fixture().db();
		// now check to see if person was saved
		ReflectClass rc = getReflectClass(oc, PERSON_CLASSNAME);
		Assert.isNotNull(rc);
		Query q = oc.query();
		q.constrain(rc);
		List results = q.execute();
		Assert.isTrue(results.size() == 1);
		Db4oUtil.dumpResults(fixture().db(), results);

	}

	private Object initGenericObjects() {
		GenericClass personClass = initGenericClass();
		ReflectField surname = personClass.getDeclaredField("surname");
		ReflectField birthdate = personClass.getDeclaredField("birthdate");
		ReflectField nArray = personClass.getDeclaredField("nArray");
		Object person = personClass.newInstance();
		surname.set(person, "John");
		int[][] arrayData = new int[2][2];
		nArray.set(person, arrayData); 
		// FIXME: nArray doesn't work
		birthdate.set(person, new Date());
		fixture().db().set(person);
		fixture().db().commit();
		return person;
	}

	private GenericClass initGenericClass() {
		GenericReflector reflector = new GenericReflector(
				null,
				new JdkReflector(Thread.currentThread().getContextClassLoader()));
		GenericClass _objectIClass = (GenericClass) reflector
				.forClass(Object.class);
		GenericClass result = new GenericClass(reflector, null,
				PERSON_CLASSNAME, _objectIClass);
		result.initFields(fields(result, reflector));
		return result;
	}

	private GenericField[] fields(GenericClass personClass,
			GenericReflector reflector) {
		return new GenericField[] {
				new GenericField("surname", reflector.forClass(String.class),
						false, false, false),
				new GenericField("birthdate", reflector.forClass(Date.class),
						false, false, false),
				new GenericField("bestFriend", personClass, false, false, false),
				new GenericField("nArray", reflector.forClass(int[][].class),
						true, true, true) };
	}

	public void testUpdate() {
		System.out.println("testUpdate");
		ExtObjectContainer oc = fixture().db();
		initGenericObjects();
		// insertPersons(oc, 1);
		Db4oUtil.dump(oc);
		ReflectClass rc = getReflectClass(oc, PERSON_CLASSNAME);
		Assert.isNotNull(rc);
		Query q = oc.query();
		q.constrain(rc);
		List results = q.execute();
		Db4oUtil.dumpResults(oc, results);
		Assert.isTrue(results.size() == 1);

	}

	public static void insertPersons(ObjectContainer oc, int count) {
		for (int i = 0; i < count; i++) {
			Person p = new Person();
			p.setName("name" + i);
			oc.set(p);
		}
		oc.commit();
	}

	public void testQuery() {
		ExtObjectContainer oc = fixture().db();
		initGenericObjects();
		ReflectClass rc = getReflectClass(oc, PERSON_CLASSNAME);
		Assert.isNotNull(rc);
		// now query to make sure there are none left
		Query q = oc.query();
		q.constrain(rc);
		q.descend("surname").constrain("John");
		List results = q.execute();
		Assert.isTrue(results.size() == 1);
	}

	public void testDelete() {
		ExtObjectContainer oc = fixture().db();
		initGenericObjects();
		ReflectClass rc = getReflectClass(oc, PERSON_CLASSNAME);
		Assert.isNotNull(rc);
		Query q = oc.query();
		q.constrain(rc);
		List results = q.execute();
		for (int i = 0; i < results.size(); i++) {
			Object o = results.get(i);
			oc.delete(o);
		}
		oc.commit();

		// now query to make sure there are none left
		q = oc.query();
		q.constrain(rc);
		q.descend("surname").constrain("John");
		results = q.execute();
		Assert.isTrue(results.size() == 0);
	}

	private ReflectClass getReflectClass(ExtObjectContainer oc, String className) {
		// FIXME: If GenericReflector#knownClasses is not called, the test will
		// fail.
		ReflectClass[] classes = oc.reflector().knownClasses();
		/*
		 * for (int i = 0; i < classes.length; i++) { System.out.println("known
		 * classes = " + classes[i]); }
		 */
		return oc.reflector().forName(className);
	}
}
