package com.db4o.cs.performance;

import com.db4o.cs.client.Db4oClient;
import com.db4o.cs.generic.Person;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 1:59:32 AM
 */
public class ClientServerPerformanceTest {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		ClientServerPerformanceTest ocs = new ClientServerPerformanceTest();
		ocs.start();
	}

	public void start() throws IOException, ClassNotFoundException {
		testSet();
		try {
			Thread.sleep(10000); // let the set get committed
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		testQuery();
		try {
			Thread.sleep(10000); // let the set get committed
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		testUpdate();
	}


	private void testSet() throws IOException {
		System.out.println("testSet");
		Db4oClient client = new Db4oClient("localhost");
		client.connect();
		client.login("test", "test");
		long start = System.currentTimeMillis();
		insertPersons(client, 100000);
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("set 100,000 duration new: " + duration);
		client.close();
	}

	public static void insertPersons(Db4oClient oc, int count) throws IOException {
		for (int i = 0; i < count; i++) {
			Person p = new Person();
			p.setName("name" + i);
			oc.set(p);
		}
		oc.commit();
	}

	private void testQuery() throws IOException, ClassNotFoundException {
		System.out.println("testQuery");
		Db4oClient client = new Db4oClient("localhost");
		client.connect();
		client.login("test", "test");
		long start = System.currentTimeMillis();
		List results = client.query(Person.class);
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			Person p = (Person) iterator.next();
		}
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("query 100,000 duration new: " + duration);
		client.close();
	}

	private void testUpdate() throws IOException, ClassNotFoundException {
		System.out.println("testUpdate");
		Db4oClient client = new Db4oClient("localhost");
		client.connect();
		client.login("test", "test");
		long start = System.currentTimeMillis();
		List results = client.query(Person.class);
		int counter = 0;
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			Person p = (Person) iterator.next();
			p.setName("Updated name " + counter++);
			client.set(p);
		}
		client.commit();
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("update 100,000 duration new: " + duration);
		client.close();
	}

}
