package com.db4o.cs.server;

import db4ounit.extensions.AbstractDb4oTestCase;
import db4ounit.extensions.Db4oTestSuiteBuilder;
import db4ounit.extensions.fixtures.Db4oSolo;
import db4ounit.TestRunner;
import db4ounit.Assert;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.db4o.cs.client.Db4oClient;
import com.db4o.cs.client.batch.UpdateSet;
import com.db4o.cs.client.batch.FieldValue;
import model.*;
import com.db4o.query.Query;
import com.db4o.ObjectContainer;

/**
 * NOTE: Start Db4oServerForTesting in a separate process before running this.
 * <p/>
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

	/* (non-Javadoc)
				 * @see db4ounit.extensions.Db4oTestCase#fixture()
				 */

	public void xxtestLogin() throws IOException {
		System.out.println("testLogin");
		Db4oClient client = new Db4oClient("localhost", Db4oServerForTesting.PORT);
		client.connect();
		boolean successful = client.login("test", "test");
		System.out.println("login successful? " + successful);
		client.close();
	}

	public void xxtestSet() throws IOException {
		System.out.println("testSet");
		Db4oClient client = new Db4oClient("localhost", Db4oServerForTesting.PORT);
		client.connect();
		client.login("test", "test");
		// now save objects
		persistPersons(client, 1);
		client.close();
	}

	public void xxtestUpdate() throws IOException, ClassNotFoundException {
		// insert a Person, get the Person, change the name, set it, then get it again to check that it's changed
		System.out.println("testUpdate");
		Db4oClient client;
		client = new Db4oClient("localhost", Db4oServerForTesting.PORT);
		client.connect();
		client.login("test", "test");
		// now save objects
		persistPersons(client, 1);
		client.close();

		pause(1000);

		// get, update, then save
		client = new Db4oClient("localhost", Db4oServerForTesting.PORT);
		client.connect();
		List results = client.query(Person.class);
		int sizeBefore = results.size();
		System.out.println("results size before update: " + results.size());
		for (int i = 0; i < results.size(); i++) {
			Person person = (Person) results.get(i);
			System.out.println("person: " + person);
			if (person.getId() == 0) person.setId(i);
			else person.setId(person.getId() + 1);
			person.setName("Updated name " + person.getId());
			client.set(person);
		}
		client.close();

		pause(1000);

		// get, update, then save
		System.out.println("Checking if changed all good");
		client = new Db4oClient("localhost", Db4oServerForTesting.PORT);
		client.connect();
		results = client.query(Person.class);
		System.out.println("results size after update: " + results.size());
		for (int i = 0; i < results.size(); i++) {
			Person person = (Person) results.get(i);
			System.out.println("person: " + person);
		}
		Assert.areEqual(sizeBefore, results.size());

		client.close();
	}

	private void pause(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void xxtestSODAQuery() throws IOException {
		System.out.println("testSODAQuery");
		Db4oClient client = new Db4oClient("localhost", Db4oServerForTesting.PORT);
		client.connect();
		client.login("test", "test");
		persistPersons(client, 1);
		persistCars(client, 1);
		Query q = client.query();
		q.constrain(Person.class);
		//q.descend("name").constrain("name0");
		q.descend("car").descend("name").constrain("friend0");
		List results = q.execute();
		System.out.println("got " + results.size() + " results");
		for (int i = 0; i < results.size(); i++) {
			Person person = (Person) results.get(i);
			System.out.println(person);
		}

		client.close();

	}

	public void xxtestDelete() throws IOException, ClassNotFoundException {
		System.out.println("testDelete");
		Db4oClient client = new Db4oClient("localhost", Db4oServerForTesting.PORT);
		client.connect();
		client.login("test", "test");
		// now save objects
		persistPersons(client, 5);

		List results = client.query(Person.class);
		Assert.areEqual(8, results.size());

		for (int i = 0; i < results.size(); i++) {
			Object o = results.get(i);
			client.delete(o);
		}

		results = client.query(Person.class);
		Assert.areEqual(0, results.size());

		client.close();
	}

	public void testSetComplex() throws IOException {
		System.out.println("testSetComplex");
		Db4oClient client = new Db4oClient("localhost", Db4oServerForTesting.PORT);
		client.connect();
		client.login("test", "test");
		// now save objects
		persistHierarchy(client, 5);
		client.close();
	}
	
	public void testGetIDAndGetByID() throws IOException {
		Db4oClient client = new Db4oClient("localhost", Db4oServerForTesting.PORT);
		client.connect();
		client.login("test", "test");
		// now save objects
		Person p = new Person();
		p.setId(0);
		p.setName("name");
		p.setFriend(new Person("friend0"));
		p.setCar(new Car("car"));
		client.set(p);
		long id = client.getID(p);
		System.out.println("id = " + id);
		Assert.isGreater(0, id);
		client.close();
		
		Db4oClient client2 = new Db4oClient("localhost", Db4oServerForTesting.PORT);
		client2.connect();
		client2.login("test", "test");
		Person person = (Person) client2.getByID(id);
		Assert.areEqual("name", person.getName());
	}

	public void xxtestBatchUpdate() throws IOException {
		Db4oClient client = new Db4oClient("localhost", Db4oServerForTesting.PORT);
		client.connect();
		client.login("test", "test");
		persistPersons(client, 10);
		client.close();

		client = new Db4oClient("localhost", Db4oServerForTesting.PORT);
		client.connect();
		client.login("test", "test");

		UpdateSet set = new UpdateSet();
		set.add(new FieldValue("name", "new name"));
		Query q = client.query();
		q.constrain(Person.class);
		q.descend("id").constrain(5).greater();
		client.batch(set, q);
		client.commit();
		client.close();
	}


	private static List persistCars(Db4oClient client, int count) {
		List ret = new ArrayList();
		for (int i = 0; i < count; i++) {
			Car p = new Car();
			p.setName("carname" + i);
			client.set(p);
			ret.add(p);
		}
		client.commit();
		return ret;
	}

	public static List persistPersons(Db4oClient client, int count) throws IOException {
		List ret = new ArrayList();
		for (int i = 0; i < count; i++) {
			Person p = new Person();
			p.setId(i);
			p.setName("name" + i);
			p.setFriend(new Person("friend" + i));
			p.setCar(new Car("car" + i));
			client.set(p);
			System.out.println("set p" + i);
			ret.add(p);
		}
		client.commit();
		return ret;
	}

	public static int persistHierarchy(ObjectContainer client, int count) {
		// one top level parent object
		int total = 1;
		Level1 l1 = new Level1(0, "string" + 0);
		for (int j = 0; j < count; j++) {
			Level2 l2 = new Level2(1.1 * j, (long) (j * 100));
			l1.addLevel2(l2);
			total++;
			for (int k = 0; k < count; k++) {
				Level3 l3 = new Level3(k, j);
				l2.addLevel3(l3);
				total++;
				Level3a l3a = new Level3a("string" + k, k);
				l2.addLevel3a(l3a);
				total++;
				for(int m = 0; m < count; m++){
					Level4 l4 = new Level4("level4-" + m);
					l3.addLevel4(l4);
					total++;
					l4 = new Level4("level4b-" + m);
					l3a.addLevel4(l4);
					total++;
				}
			}
		}
		client.set(l1);
		return total;
	}
}
