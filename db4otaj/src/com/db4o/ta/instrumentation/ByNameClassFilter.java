package com.db4o.ta.instrumentation;

public class ByNameClassFilter implements ClassFilter {

	private String _fullyQualifiedName;
	
	public ByNameClassFilter(String fullyQualifiedName) {
		_fullyQualifiedName = fullyQualifiedName;
	}

	public boolean accept(Class clazz) {
		return _fullyQualifiedName.equals(clazz.getName());
	}

}
