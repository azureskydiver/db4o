/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import java.util.*;

import com.db4o.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class YapClassTestCase extends AbstractDb4oTestCase {
	
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
		Set expectedNames=new HashSet(Arrays.asList(new String[]{"_id","_name","_age"}));
		YapClass clazz=stream().getYapClass(reflector().forClass(SubClazz.class),false);
		Iterator4 fieldIter=clazz.fields();
		while(fieldIter.moveNext()) {
			YapField curField=(YapField)fieldIter.current();
			Assert.isTrue(expectedNames.remove(curField.getName()));
		}
		Assert.isTrue(expectedNames.isEmpty());
	}
}
