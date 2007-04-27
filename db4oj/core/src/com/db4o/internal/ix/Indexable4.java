/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.ix;

import com.db4o.internal.*;

/**
 * @exclude
 */
public interface Indexable4 extends Comparable4{
    
    Object comparableObject(Transaction trans, Object indexEntry);

    int linkLength();
    
    Object readIndexEntry(Buffer reader);
    
    void writeIndexEntry(Buffer writer, Object obj);
    
	void defragIndexEntry(ReaderPair readers);
}

