/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.drs.test;

import java.util.*;

public class CollectionHolder {
	public String name;
	public Map map = new HashMap();
	public List list = new ArrayList();
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
