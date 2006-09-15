/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.other;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionHolder {
	public String name;
	public Map map = new HashMap();
	public List list = new LinkedList();
	public Set set = new HashSet();

	public CollectionHolder() {
	}

	public CollectionHolder(String name) {
		this.name = name;
	}

	public String toString() {
		return name + ", hashcode = " + hashCode();
	}

}
