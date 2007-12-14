/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;


/**
 * @exclude
 */
public class Null implements Indexable4, PreparedComparison{
    
    public static final Null INSTANCE = new Null();

    public int compareTo(Object a_obj) {
        if(a_obj == null) {
            return 0;
        }
        return -1;
    }
    
    public int linkLength() {
        return 0;
    }

	public Comparable4 prepareComparison(Object obj) {
		// do nothing
		return this;
	}
	
    public Object readIndexEntry(BufferImpl a_reader) {
        return null;
    }

    public void writeIndexEntry(BufferImpl a_writer, Object a_object) {
        // do nothing
    }

	public void defragIndexEntry(DefragmentContextImpl context) {
        // do nothing
	}
}

