/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.internal;

import com.db4o.foundation.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ClassMetadataIntegrationTestCase extends AbstractDb4oTestCase {
	
	public static class SuperClazz {
		public int _id;
		public String _name;
	}

	public static class SubClazz extends SuperClazz {
		public int _age;
	}

	protected void store() throws Exception {
		store(new SubClazz());
	}
	
	public void testForEachField() {		
		final Collection4 expectedNames=new Collection4(new ArrayIterator4(new String[]{"_id","_name","_age"}));
		ClassMetadata classMetadata = classMetadataFor(SubClazz.class);
		classMetadata.forEachField(new Procedure4() {
			public void apply(Object arg) {
				FieldMetadata curField=(FieldMetadata)arg;
				Assert.isNotNull(expectedNames.remove(curField.getName()));
			}
		});
		Assert.isTrue(expectedNames.isEmpty());
	} 
}
