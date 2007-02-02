/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class ClassMetadataTestCase extends AbstractDb4oTestCase {
	
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
	
	public void testFieldIterator() {		
		Collection4 expectedNames=new Collection4(new ArrayIterator4(new String[]{"_id","_name","_age"}));
		ClassMetadata clazz=stream().getYapClass(reflector().forClass(SubClazz.class));
		Iterator4 fieldIter=clazz.fields();
		while(fieldIter.moveNext()) {
			FieldMetadata curField=(FieldMetadata)fieldIter.current();
			Assert.isNotNull(expectedNames.remove(curField.getName()));
		}
		Assert.isTrue(expectedNames.isEmpty());
	}
}
