package com.db4o.query;

import java.io.*;

public interface QueryComparator<Target> extends Serializable {
	int compare(Target first,Target second);
}
