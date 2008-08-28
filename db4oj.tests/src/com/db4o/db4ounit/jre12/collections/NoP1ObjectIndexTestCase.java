/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import com.db4o.types.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 * @decaf.ignore.jdk11
 */
public class NoP1ObjectIndexTestCase extends AbstractDb4oTestCase implements OptOutCS, OptOutDefragSolo {

	public static class Data {
		public Db4oList _list;

		public Data(Db4oList list) {
			_list = list;
		}
	}
	
	/**
	 * @deprecated using deprecated api
	 */
	protected void store() throws Exception {
		store(new Data(db().collections().newLinkedList()));
	}
	
	public void testNoIndex() throws Exception {
		fixture().config().readOnly(true);
		reopen();
		db().storedClasses();
	}
	
	public static void main(String[] args) {
		new NoP1ObjectIndexTestCase().runClientServer();
	}
}
