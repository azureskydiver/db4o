/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public interface ClassIndexStrategy {
	void read(YapReader reader, YapStream stream);
	int entryCount(Transaction ta);
	void initialize(YapStream stream);
	void purge();
	void writeId(YapReader writer, Transaction trans);
	void add(Transaction trans, int id);
	long[] getIds(Transaction trans);
	Tree getAll(Transaction trans);
	int ownLength();
	void remove(Transaction ta, int id);
	void traverseAll(Transaction ta,Visitor4 command);
	int idFromValue(Object value);
}
