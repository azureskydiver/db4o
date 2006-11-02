package com.db4o.cs.performance;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.query.Query;
import com.db4o.cs.generic.Person;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 2:00:19 AM
 */
public class OldClientServer {

	public static void main(String[] args) throws IOException {
		OldClientServer ocs = new OldClientServer();
		ocs.start();
	}

	public void start() throws IOException {

		testSet();
		try {
			Thread.sleep(10000); // let it commit
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		testQuery();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		testUpdate();

	}


	private void testSet() throws IOException {
		ObjectContainer oc = Db4o.openClient("localhost", OldServerRunner.PORT, "test", "test");
		long start = System.currentTimeMillis();
		insertPersons(oc, 100000);
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("set 100,000 duration old: " + duration);
		oc.close();
	}

	public static void insertPersons(ObjectContainer oc, int count) {
		for (int i = 0; i < count; i++) {
			Person p = new Person();
			p.setName("name" + i);
			oc.set(p);
		}
		oc.commit();
	}

	private void testQuery() throws IOException {
		ObjectContainer oc = Db4o.openClient("localhost", OldServerRunner.PORT, "test", "test");
		long start = System.currentTimeMillis();
		Query q = oc.query();
		List results = q.execute();
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			Person p = (Person) iterator.next();
		}
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("query 100,000 duration old: " + duration);
		oc.close();
	}
	private void testUpdate() throws IOException {
		ObjectContainer oc = Db4o.openClient("localhost", OldServerRunner.PORT, "test", "test");
		long start = System.currentTimeMillis();
		Query q = oc.query();
		List results = q.execute();
		int counter = 0;
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			Person p = (Person) iterator.next();
			p.setName("Updated name " + counter++);
			oc.set(p);
		}
		oc.commit();
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("update 100,000 duration old: " + duration);
		oc.close();
	}



}
