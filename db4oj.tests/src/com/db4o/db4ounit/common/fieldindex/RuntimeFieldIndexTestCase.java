package com.db4o.db4ounit.common.fieldindex;

import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class RuntimeFieldIndexTestCase extends AbstractDb4oTestCase implements OptOutCS {
	
	private static final String FIELDNAME = "_id";

	public static class Data {
		public int _id;
	}
	
	protected void store() throws Exception {
		store(new Data());
	}
	
	public void testCreateIndexAtRuntime() {
		FieldMetadata field = (FieldMetadata) db().storedClass(Data.class).storedField(FIELDNAME,null);
		Assert.isFalse(field.hasIndex());
		field.createIndex();
		Assert.isTrue(field.hasIndex());
	}

}
