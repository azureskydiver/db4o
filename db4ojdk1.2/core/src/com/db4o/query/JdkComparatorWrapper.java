package com.db4o.query;

import java.util.*;

public class JdkComparatorWrapper implements QueryComparator {
	private Comparator _comparator;
	
	public JdkComparatorWrapper(Comparator comparator) {
		this._comparator = comparator;
	}

	public int compare(Object first, Object second) {
		return _comparator.compare(first, second);
	}
}
