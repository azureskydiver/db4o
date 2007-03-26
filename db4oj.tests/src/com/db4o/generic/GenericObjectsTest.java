package com.db4o.generic;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.generic.GenericArrayClass;
import com.db4o.reflect.generic.GenericClass;
import com.db4o.reflect.generic.GenericField;
import com.db4o.reflect.generic.GenericReflector;
import com.db4o.reflect.jdk.JdkReflector;
import db4ounit.Assert;
import db4ounit.TestRunner;
import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.Db4oTestSuiteBuilder;
import db4ounit.extensions.fixtures.Db4oSolo;
import java.util.Date;

/**
 * 
 * @author treeder, Andrew
 * 
 */
public class GenericObjectsTest extends AbstractDb4oTestCase {
	private String PERSON_CLASSNAME = "com.acme.Person";

	public static void main(String[] args) {
		new TestRunner(new Db4oTestSuiteBuilder(new Db4oSolo(),
				GenericObjectsTest.class)).run();
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
		ObjectSet results = q.execute();
		Assert.isTrue(results.size() == 1);
		//Db4oUtil.dumpResults(fixture().db(), results);

	}

	private Object initGenericObjects() {
		GenericClass personClass = initGenericClass();
		ReflectField surname = personClass.getDeclaredField("surname");
		ReflectField birthdate = personClass.getDeclaredField("birthdate");
		ReflectField nArray = personClass.getDeclaredField("nArray");
		Object person = personClass.newInstance();
		surname.set(person, "John");
		int[][] arrayData = new int[2][2];
		// todo: FIXME: nArray doesn't work
		// nArray.set(person, arrayData);
		birthdate.set(person, new Date());
		fixture().db().set(person);
		fixture().db().commit();
		return person;
	}

	/**
	 * todo: Move the GenericClass creation into a utility/factory class.
	 * @return
	 */
	public GenericClass initGenericClass() {
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
		ExtObjectContainer oc = fixture().db();
		initGenericObjects();
		//Db4oUtil.dump(oc);
		ReflectClass rc = getReflectClass(oc, PERSON_CLASSNAME);
		Assert.isNotNull(rc);
		Query q = oc.query();
		q.constrain(rc);
		ObjectSet results = q.execute();
		//Db4oUtil.dumpResults(oc, results);
		Assert.isTrue(results.size() == 1);

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
		ObjectSet results = q.execute();
		Assert.isTrue(results.size() == 1);
	}

	public void testDelete() {
		ExtObjectContainer oc = fixture().db();
		initGenericObjects();
		ReflectClass rc = getReflectClass(oc, PERSON_CLASSNAME);
		Assert.isNotNull(rc);
		Query q = oc.query();
		q.constrain(rc);
		ObjectSet results = q.execute();
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
		return oc.reflector().forName(className);
	}

	/**
	 * This is to ensure that reflector.forObject(GenericArray) returns an instance of GenericArrayClass instead of GenericClass
	 * http://tracker.db4o.com/jira/browse/COR-376
	 */
	public void testGenericArrayClass() {
		ExtObjectContainer oc = fixture().db();
		initGenericObjects();
		ReflectClass rc = oc.reflector().forName(PERSON_CLASSNAME);

		Object array = reflector().array().newInstance(rc, 5);

		ReflectClass arrayClass = oc.reflector().forObject(array);
		Assert.isTrue(arrayClass.isArray());
		Assert.isTrue(arrayClass instanceof GenericArrayClass);

		arrayClass = oc.reflector().forName(array.getClass().getName());
		Assert.isTrue(arrayClass.isArray());
		Assert.isTrue(arrayClass instanceof GenericArrayClass);

		arrayClass = oc.reflector().forClass(array.getClass());
		Assert.isTrue(arrayClass.isArray());
		Assert.isTrue(arrayClass instanceof GenericArrayClass);
	}
}
