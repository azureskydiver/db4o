package com.db4o.query;

import java.util.*;

public interface QueryComparator<T> extends Comparator<T> {
	int compare(T first,T second);
}
