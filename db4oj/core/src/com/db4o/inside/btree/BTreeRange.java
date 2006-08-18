/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.foundation.*;


public interface BTreeRange {
	public BTreePointer first();
    
    public KeyValueIterator iterator();

	public int size();

	public BTreeRange greater();

	public BTreeRange union(BTreeRange other);

}
