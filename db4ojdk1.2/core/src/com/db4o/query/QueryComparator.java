package com.db4o.query;

import java.util.*;

public interface QueryComparator extends Comparator {
	int compare(Object first,Object second);
}
