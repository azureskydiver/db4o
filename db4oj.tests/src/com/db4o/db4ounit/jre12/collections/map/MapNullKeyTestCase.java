/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.jre12.collections.map;

import java.util.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class MapNullKeyTestCase extends AbstractDb4oTestCase {

	private static final String VALUE = "foo";

	public static class Holder {
		public Map<String, String> _map;
		
		public Holder(String value) {
			_map = new HashMap<String, String>();
			_map.put(null, value);
		}
		
		public String value() {
			return _map.get(null);
		}
	}

	@Override
	protected void store() throws Exception {
		store(new Holder(VALUE));
	}
	
	public void _testNullKey() {
		Holder holder = retrieveOnlyInstance(Holder.class);
		Assert.areEqual(VALUE, holder.value());
	}
}
