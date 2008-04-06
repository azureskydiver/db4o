/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test.foundation;

import com.db4o.drs.foundation.ObjectSetCollection4Facade;
import com.db4o.foundation.Collection4;

import db4ounit.*;

/**
 * @sharpen.ignore
 */
public class ObjectSetCollection4FacadeTestCase implements TestCase {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(ObjectSetCollection4FacadeTestCase.class).run();
	}
	
	public void testEmpty() {
		ObjectSetCollection4Facade facade = new ObjectSetCollection4Facade(new Collection4());
		Assert.isFalse(facade.hasNext());
		Assert.isFalse(facade.hasNext());
	}
	
	public void testIteration() {
		Collection4 collection = new Collection4();		
		collection.add("bar");
		collection.add("foo");
		
		ObjectSetCollection4Facade facade = new ObjectSetCollection4Facade(collection);
		Assert.isTrue(facade.hasNext());
		Assert.areEqual("bar", facade.next());
		Assert.isTrue(facade.hasNext());
		Assert.areEqual("foo", facade.next());
		Assert.isFalse(facade.hasNext());
		
		facade.reset();
		
		Assert.areEqual("bar", facade.next());
		Assert.areEqual("foo", facade.next());
		Assert.isFalse(facade.hasNext());
	}

}
