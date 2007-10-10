/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import com.db4o.collections.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class ArrayList4TAMultiClientsTestCase extends ArrayList4TATestCaseBase implements OptOutSolo {
	public static void main(String[] args) {
		new ArrayList4TAMultiClientsTestCase().runEmbeddedClientServer();
	}

	public void testAddAdd() throws Exception {
		ExtObjectContainer client1 = openNewClient();
		ExtObjectContainer client2 = openNewClient();
		ArrayList4<Integer> list1 = retrieveAndAssertNullArrayList4(client1);
		ArrayList4<Integer> list2 = retrieveAndAssertNullArrayList4(client2);
		int size = list1.size();
		list1.add(new Integer(100));
		list2.add(new Integer(200));
		client1.set(list1);
		client2.set(list2);
		client1.commit();
		client2.commit();
		client1.close();
		client2.close();
		ArrayList4<Integer> list3 = retrieveAndAssertNullArrayList4();
		Integer lastItem = list3.get(size);
		Assert.areEqual(new Integer(200), lastItem);
	}

	public void testAddRemove() throws Exception {
		ExtObjectContainer client1 = openNewClient();
		ExtObjectContainer client2 = openNewClient();
		ArrayList4<Integer> list1 = retrieveAndAssertNullArrayList4(client1);
		ArrayList4<Integer> list2 = retrieveAndAssertNullArrayList4(client2);
		int size = list1.size();
		list1.add(new Integer(101));
		list2.remove(0);
		Integer firstItem = list2.get(0);
		client1.set(list1);
		client2.set(list2);
		client1.commit();
		client2.commit();
		client1.close();
		client2.close();
		ArrayList4<Integer> list3 = retrieveAndAssertNullArrayList4();
		Assert.areEqual(size-1, list3.size());
		Assert.areEqual(firstItem, list3.get(0));
	}

	public void testAddSet() throws Exception {
		ExtObjectContainer client1 = openNewClient();
		ExtObjectContainer client2 = openNewClient();
		ArrayList4<Integer> list1 = retrieveAndAssertNullArrayList4(client1);
		ArrayList4<Integer> list2 = retrieveAndAssertNullArrayList4(client2);
		int size = list1.size();
		list1.add(new Integer(101));
		list2.set(0, new Integer(1));
		client1.set(list1);
		client2.set(list2);
		client1.commit();
		client2.commit();
		client1.close();
		client2.close();
		ArrayList4<Integer> list3 = retrieveAndAssertNullArrayList4();
		Assert.areEqual(size, list3.size());
		Assert.areEqual(new Integer(1), list3.get(0));
	}

}
