/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5;

import com.db4o.config.annotations.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class IndexedAnnotationTestCase extends AbstractDb4oTestCase {

	private static class DataAnnotated {
		@Indexed
		private int _id;

		public DataAnnotated(int id) {
			this._id = id;
		}
	}

	private static class DataNotAnnotated {
		private int _id;

		public DataNotAnnotated(int id) {
			this._id = id;
		}
	}

	@Override
	protected void store() throws Exception {
		db().set(new DataAnnotated(42));
		db().set(new DataNotAnnotated(43));
	}
	
	public void testIndexed() {
		assertIndexed(DataNotAnnotated.class,false);
		assertIndexed(DataAnnotated.class,true);
	}
	
	private void assertIndexed(Class clazz,boolean expected) {
		StoredClass storedClass=db().storedClass(clazz);
		StoredField storedField=storedClass.storedField("_id",Integer.TYPE);
		Assert.areEqual(expected,storedField.hasIndex());
	}
}
