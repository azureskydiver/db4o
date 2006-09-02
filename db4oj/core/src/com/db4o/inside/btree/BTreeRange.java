/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.foundation.Iterator4;


public interface BTreeRange {
	public BTreePointer first();
    
    public Iterator4 keys();

	public int size();

	public BTreeRange greater();

	public BTreeRange union(BTreeRange other);

	public BTreeRange extendToLast();

	public BTreeRange smaller();

	public BTreeRange extendToFirst();

	public BTreeRange intersect(BTreeRange range);

	public BTreeRange extendToLastOf(BTreeRange upperRange);

	public boolean overlaps(BTreeRange range);

}
