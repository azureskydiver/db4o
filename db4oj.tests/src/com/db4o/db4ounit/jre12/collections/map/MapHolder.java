package com.db4o.db4ounit.jre12.collections.map;

import java.util.*;

/**
 * @decaf.ignore.jdk11
 */
public class MapHolder {

	public String name;

	public Map map;

	public MapHolder(String name) {
		this.name = name;
		this.map = new HashMap();
	}

	public String toString() {
		return "name = " + name + ", map = " + map;
	}
}
