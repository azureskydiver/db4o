package com.db4o.cs.server;

import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.Db4oTestSuiteBuilder;
import db4ounit.extensions.fixtures.Db4oSolo;
import db4ounit.TestRunner;
import db4ounit.Assert;

import java.io.IOException;
import java.util.List;

import com.db4o.cs.client.Db4oClient;
import com.db4o.cs.generic.GenericObjectsTest;
import com.db4o.cs.generic.Person;
import com.db4o.ObjectContainer;

/**
 * User: treeder
 * Date: Oct 30, 2006
 * Time: 11:54:06 PM
 */
public class ClientServerTest extends AbstractDb4oTestCase {

	public static void main(String[] args) throws IOException {
		new TestRunner(
				new Db4oTestSuiteBuilder(
						new Db4oSolo(),
						ClientServerTest.class)).run();

	}


	public void xxtestLogin() throws IOException {
		System.out.println("testLogin");
		Db4oClient client = new Db4oClient("localhost");
		client.connect();
		boolean successful = client.login("test", "test");
		System.out.println("login successful? " + successful);
		client.close();
	}

	public void xxtestSet() throws IOException {
		System.out.println("testSet");
		Db4oClient client = new Db4oClient("localhost");
		client.connect();
		client.login("test", "test");
		// now save objects
		persistPersons(client, 1);
		client.close();
	}

	public void testUpdate() throws IOException, ClassNotFoundException {
		// insert a Person, get the Person, change the name, set it, then get it again to check that it's changed
		System.out.println("testUpdate");
		Db4oClient client;
		/*client = new Db4oClient("localhost");
		client.connect();
		client.login("test", "test");
		// now save objects
		persistPersons(client, 1);
		client.close();*/

		// get, update, then save
		client = new Db4oClient("localhost");
		client.connect();
		List results = client.query(Person.class);
		int sizeBefore = results.size();
		System.out.println("results size after retrieval: " + results.size());
		for (int i = 0; i < results.size(); i++) {
			Person person = (Person) results.get(i);
			System.out.println("person: " + person);
			if(person.getIndex() == 0) person.setIndex(i);
			else person.setIndex(person.getIndex() + 1);
			person.setName("Updated name " + person.getIndex());
			client.set(person);
		}
		client.close();

		// get, update, then save
		System.out.println("Checking if changed all good");
		client = new Db4oClient("localhost");
		client.connect();
		results = client.query(Person.class);
		System.out.println("results size after retrieval: " + results.size());
		Assert.isTrue(results.size() == sizeBefore);
		for (int i = 0; i < results.size(); i++) {
			Person person = (Person) results.get(i);
			System.out.println("person: " + person);
		}
		client.close();
	}

	public static void persistPersons(Db4oClient client, int count) throws IOException {
		for (int i = 0; i < count; i++) {
			Person p = new Person();
			p.setName("name" + i);
			client.set(p);
		}
		client.commit();
	}
}
