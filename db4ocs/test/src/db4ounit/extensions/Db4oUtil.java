/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.reflect.generic.GenericObject;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.foundation.Visitor4;
import com.db4o.query.Query;

import db4ounit.Assert;

import java.util.List;
import java.util.Iterator;

public class Db4oUtil {
	public static Object getOne(ExtObjectContainer oc, Object obj) {
		Query q = oc.query();
		q.constrain(classOf(obj));
		ObjectSet set = q.execute();
		Assert.areEqual(1, set.size());
		return set.next();
	}

	public static int occurrences(ExtObjectContainer oc, Class clazz) {
		Query q = oc.query();
		q.constrain(clazz);
		return q.execute().size();
	}

	public static void assertOccurrences(ExtObjectContainer oc, Class clazz,
										 int expected) {
		Assert.areEqual(expected, occurrences(oc, clazz));
	}

	public static void deleteAll(ObjectContainer oc) {
		deleteObjectSet(oc, oc.get(null));
	}

	public static void deleteAll(ObjectContainer oc, Class clazz) {
		ObjectSet os = oc.query(clazz);
		deleteObjectSet(oc, os);
	}

	public static void deleteObjectSet(ObjectContainer oc, ObjectSet os) {
		while (os.hasNext()) {
			oc.delete(os.next());
		}
	}

	public static void forEach(ExtObjectContainer oc, Object obj, Visitor4 vis) {
		oc.deactivate(obj, Integer.MAX_VALUE);
		ObjectSet set = oc.get(obj);
		while (set.hasNext()) {
			vis.visit(set.next());
		}
	}

	private static Class classOf(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Class) {
			return (Class) obj;
		}
		return obj.getClass();
	}

	public static int dump(ObjectContainer oc) {
		return dump(oc, null);
	}

	public static int dump(ObjectContainer oc, Class aClass) {
		System.out.println("DUMPING: " + oc.ext().identity());
		/*
		System.out.println("Stored Classes:");

		This is throwing:
		com.db4o.cs.generic.GenericObjectsTest.testUpdate: java.lang.NullPointerException
	at com.db4o.reflect.generic.GenericReflector.readAll(Unknown Source)
	at com.db4o.reflect.generic.GenericReflector.knownClasses(Unknown Source)
	
		ReflectClass[] reflectClasses = oc.ext().reflector().knownClasses();
		if (reflectClasses != null) {
			for (int i = 0; i < reflectClasses.length; i++) {
				ReflectClass reflectClass = reflectClasses[i];
				System.out.println("rc: " + reflectClass);
			}
		}*/
		Query q = oc.query();
		if(aClass != null){
			q.constrain(aClass);
		}
		List results = q.execute();
		int counter = 0;
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			Object o = iterator.next();
			System.out.println("o:" + o);
			counter++;
		}
		System.out.println("END DUMP: " + oc.ext().identity());
		return counter;
	}

	public static void dumpResults(ObjectContainer oc, List results) {
		for (int i = 0; i < results.size(); i++) {
			Object o = results.get(i);
			System.out.println("o:" + o + " class:" + o.getClass().getName());
			dumpObject(oc, o);
		}
	}

	private static void dumpObject(ObjectContainer oc, Object o) {
		ReflectClass rc = oc.ext().reflector().forObject(o);
		System.out.println("rc:" + rc.getName() + "  " + rc);
		ReflectField[] fields = rc.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			ReflectField field = fields[i];
			System.out.println("field:" + field.getName() + " value:" + field.get(o));
		}
	}

}
