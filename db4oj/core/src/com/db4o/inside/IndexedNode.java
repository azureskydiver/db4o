package com.db4o.inside;

import com.db4o.TreeInt;
import com.db4o.inside.btree.BTree;

public interface IndexedNode {

	boolean isResolved();

	IndexedNode resolve();
	
	BTree getIndex();

	TreeInt toTreeInt();
}