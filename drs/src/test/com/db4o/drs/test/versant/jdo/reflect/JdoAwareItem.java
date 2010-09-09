package com.db4o.drs.test.versant.jdo.reflect;

import java.util.*;

public class JdoAwareItem extends NotImplementedPersistenceCapable {
	
	
	public static class Meta {
		public static List<String> invocations = new ArrayList<String>();
	}

	public JdoAwareItem() {

	}

	public JdoAwareItem(String name) {
		this.name = name;
	}

	private String name;
	private static int staticField;
	private transient int transientField;

	private static String jdoFieldNames[] = { "name" };
	private static Class jdoFieldTypes[] = { String.class };

	private static String jdoGetname(JdoAwareItem obj) {
		Meta.invocations.add("jdoGetname");
		return obj.name;
	}

	private static void jdoSetname(JdoAwareItem obj, String name) {
		Meta.invocations.add("jdoSetname");
		obj.name = name;
	}

}
