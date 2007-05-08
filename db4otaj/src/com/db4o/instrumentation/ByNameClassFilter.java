package com.db4o.instrumentation;

public class ByNameClassFilter implements ClassFilter {

	private String[] _fullyQualifiedNames;
	
	public ByNameClassFilter(String fullyQualifiedName) {
		this(new String[]{ fullyQualifiedName });
	}

	public ByNameClassFilter(String[] fullyQualifiedNames) {
		_fullyQualifiedNames = fullyQualifiedNames;		
	}

	public boolean accept(String className) {
		for (int idx = 0; idx < _fullyQualifiedNames.length; idx++) {
			if(_fullyQualifiedNames[idx].equals(className)) {
				return true;
			}
		}
		return false;
	}

}
