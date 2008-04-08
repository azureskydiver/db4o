/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.drs.test;

import java.util.*;

public class CollectionHolder {
	public String name;
	public Map map;
	public List list;
	public Set set;
	
	public CollectionHolder() {
		this(new HashMap(), new HashSet(), new ArrayList());
	}

	public CollectionHolder(String name) {
		this();
		this.name = name;
	}

	public CollectionHolder(Map theMap, Set theSet, List theList) {
		map = theMap;
		set = theSet;
		list = theList;
	}

	public String toString() {
		return name + ", hashcode = " + hashCode();
	}

}
