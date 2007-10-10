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

	private static final ArrayList4Operation <Integer> _addOp = new ArrayList4Operation<Integer>() {
		public void operate(ArrayList4<Integer> list) {
			list.add(new Integer(ArrayList4Asserter.CAPACITY));
		}	
	};
	
	private static final ArrayList4Operation<Integer> _removeOp = new ArrayList4Operation<Integer>() {
		public void operate(ArrayList4<Integer> list) {
			list.remove(0);
		}	
	};	
	
	private static final ArrayList4Operation<Integer> _setOp = new ArrayList4Operation<Integer>() {
		public void operate(ArrayList4<Integer> list) {
			list.set(0, new Integer(1));
		}	
	};	
	
	public void testAddAdd() throws Exception {
		ArrayList4Operation<Integer> anotherAddOp = new ArrayList4Operation<Integer>() {
			public void operate(ArrayList4<Integer> list) {
				list.add(new Integer(ArrayList4Asserter.CAPACITY + 42));
			}	
		};	
		operate(anotherAddOp, _addOp);
		checkAdd();
	}

	public void testSetAdd() throws Exception {
		operate(_setOp, _addOp);
		checkAdd();
	}
	
	public void testRemoveAdd() throws Exception {
		operate(_removeOp, _addOp);
		checkAdd();
	}
	
	private void checkAdd() throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.areEqual(ArrayList4Asserter.CAPACITY + 1, list.size());
		for (int i = 0; i <= ArrayList4Asserter.CAPACITY; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}
	}

	public void testAddRemove() throws Exception {
		operate(_addOp, _removeOp);
		checkRemove();
	}
	
	private void checkRemove() throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.areEqual(ArrayList4Asserter.CAPACITY - 1, list.size());
		for (int i = 0; i < ArrayList4Asserter.CAPACITY - 1; ++i) {
			Assert.areEqual(new Integer(i + 1), list.get(i));
		}
	}

	public void testAddSet() throws Exception {
		operate(_addOp, _setOp);
		checkSet();
	}

	private void checkSet() throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.areEqual(ArrayList4Asserter.CAPACITY, list.size());
		Assert.areEqual(new Integer(1), list.get(0));
		for (int i = 1; i < ArrayList4Asserter.CAPACITY; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}
	}
	
	private void operate(ArrayList4Operation <Integer> op1, ArrayList4Operation<Integer> op2) throws Exception {
		ExtObjectContainer client1 = openNewClient();
		ExtObjectContainer client2 = openNewClient();
		ArrayList4<Integer> list1 = retrieveAndAssertNullArrayList4(client1);
		ArrayList4<Integer> list2 = retrieveAndAssertNullArrayList4(client2);
		op1.operate(list1);
		op2.operate(list2);
		client1.set(list1);
		client2.set(list2);
		client1.commit();
		client2.commit();
		client1.close();
		client2.close();
	}

}
