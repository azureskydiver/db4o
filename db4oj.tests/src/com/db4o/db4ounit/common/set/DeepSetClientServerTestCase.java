/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.set;

import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DeepSetClientServerTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] args) {
		new DeepSetClientServerTestCase().runClientServer();
	}
	
	public class Item {
		public Item child;
		public String name;
	}

	private Item _item;
	
	public void store() {
		_item = new Item();
		_item.name = "1";
		_item.child = new Item();
		_item.child.name = "2";
		_item.child.child = new Item();
		_item.child.child.name = "3";
		store(_item);
	}
	
	public void test() throws Exception {
		ExtObjectContainer oc1 = openNewClient();
		ExtObjectContainer oc2 = openNewClient();
		ExtObjectContainer oc3 = openNewClient();
		Item example = new Item();
		example.name = "1";
		try {
			Item item1 = (Item) oc1.get(example).next();
			Assert.areEqual("1", item1.name);
			Assert.areEqual("2", item1.child.name);
			Assert.areEqual("3", item1.child.child.name);

			Item item2 = (Item) oc2.get(_item).next();
			Assert.areEqual("1", item2.name);
			Assert.areEqual("2", item2.child.name);
			Assert.areEqual("3", item2.child.child.name);

			item1.child.name = "12";
			item1.child.child.name = "13";
			oc1.set(item1, 2);
			oc1.commit();

			// check result
			Item item = (Item) oc1.get(example).next();
			Assert.areEqual("1", item.name);
			Assert.areEqual("12", item.child.name);
			Assert.areEqual("13", item.child.child.name);

			item = (Item) oc2.get(example).next();
			oc2.refresh(item, 3);
			Assert.areEqual("1", item.name);
			Assert.areEqual("12", item.child.name);
			Assert.areEqual("3", item.child.child.name);

			item = (Item) oc3.get(example).next();
			Assert.areEqual("1", item.name);
			Assert.areEqual("12", item.child.name);
			Assert.areEqual("3", item.child.child.name);
		} finally {
			oc1.close();
			oc2.close();
			oc3.close();
		}
	}

}
