/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.marshall.*;

/**
 * @exclude
 */
public interface Indexable4 extends Comparable4{
    
    int linkLength();
    
    Object readIndexEntry(Context context, ByteArrayBuffer reader);
    
    void writeIndexEntry(Context context, ByteArrayBuffer writer, Object obj);
    
	void defragIndexEntry(DefragmentContextImpl context);
	
}

