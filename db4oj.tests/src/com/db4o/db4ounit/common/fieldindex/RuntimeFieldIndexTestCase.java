/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.fieldindex;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class RuntimeFieldIndexTestCase extends AbstractDb4oTestCase implements OptOutCS {
	
	private static final String FIELDNAME = "_id";

	public static class Data {
		public int _id;

		public Data(int id) {
			_id = id;
		}		
	}
	
	protected void store() throws Exception {
		for(int i=1; i <= 3; i++) {
			store(new Data(i));
		}
	}
	
	public void testCreateIndexAtRuntime() {
		StoredField field = db().storedClass(Data.class).storedField(FIELDNAME,null);
		Assert.isFalse(field.hasIndex());
		field.createIndex();
		Assert.isTrue(field.hasIndex());
		Query query = newQuery(Data.class);
		query.descend(FIELDNAME).constrain(new Integer(2));
		ObjectSet result = query.execute();
		Assert.areEqual(1, result.size());
		field.createIndex(); // ensure that second call is ignored
	}

}
