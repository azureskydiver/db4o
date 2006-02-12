package com.db4o.query;

import java.io.*;

public interface QueryComparator extends Serializable {
	int compare(Object first,Object second);
}
