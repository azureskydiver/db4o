/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public interface ClassIndexStrategy {	
	void initialize(YapStream stream);
	void read(YapReader reader, YapStream stream);
	void writeId(YapReader writer, Transaction transaction);
	void add(Transaction transaction, int id);
	void remove(Transaction transaction, int id);
	int entryCount(Transaction transaction);
	int ownLength();
	void purge();
	long[] getIds(Transaction trans);
	Tree getAll(Transaction trans);
	void traverseAll(Transaction ta, Visitor4 command);
	int idFromValue(Object value);
	void dontDelete(Transaction transaction, int id);
}
