package com.db4o.inside;

import com.db4o.Transaction;
import com.db4o.Tree;
import com.db4o.YapReader;
import com.db4o.YapStream;

public interface ClassIndexStrategy {
	void read(YapReader reader, YapStream stream);
	int entryCount(Transaction ta);
	void initialize(YapStream a_stream);
	void purge();
	void writeId(YapReader a_writer, Transaction trans);
	void add(Transaction a_trans, int a_id);
	long[] getIds(Transaction trans);
	Tree getAll(Transaction a_trans);
	int ownLength();
	void remove(Transaction ta, int id);
}
