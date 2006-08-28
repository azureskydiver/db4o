package com.db4o.inside;

import com.db4o.*;
import com.db4o.foundation.KeyValueIterator;
import com.db4o.inside.btree.BTree;

public interface IndexedNode {

	boolean isResolved();

	IndexedNode resolve();
	
	BTree getIndex();
	
	int resultSize();

	//FIXME: do we need this?
	TreeInt toTreeInt();

	// FIXME: we don't need a KeyValueIterator here
	// but we need to fix Iterator to be moveNext(), current()
	KeyValueIterator iterator();
}