/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionHolder {
	public String _name;
	public Map _map = new HashMap();
	public List _list = new LinkedList();
	public Set _set = new HashSet();
	public CollectionHolder _h2;

	public CollectionHolder() {
	}

	public CollectionHolder(String name) {
		_name = name;
	}

	public String toString() {
		return _name + ", hashcode = " + hashCode();
	}

}
