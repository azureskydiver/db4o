/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation.test.data;

import java.util.*;

public class HashMapClient implements CollectionClient {

	private Map _map;
	
	public HashMapClient() {
		_map = new HashMap();
	}
	
	public Object collectionInstance() {
		return _map;
	}

}
