/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation.test.data;

import java.util.*;

public class LinkedListClient implements CollectionClient {

	private List _list;
	
	public LinkedListClient() {
		_list = new LinkedList();
	}

	public Object collectionInstance() {
		return _list;
	}
}
