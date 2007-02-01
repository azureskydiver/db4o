/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.ix;

import com.db4o.*;
import com.db4o.inside.*;

/**
 * @exclude
 */
public interface Indexable4 extends YapComparable{
    
    Object comparableObject(Transaction trans, Object indexEntry);

    int linkLength();
    
    Object readIndexEntry(Buffer a_reader);
    
    void writeIndexEntry(Buffer a_writer, Object a_object);
    
	void defragIndexEntry(ReaderPair readers);
}

