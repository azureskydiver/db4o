/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs;

import com.db4o.*;
import com.db4o.foundation.Iterator4;
import com.db4o.inside.query.*;

/**
 * @exclude
 */
public class QResultClient extends QueryResultImpl {

	public QResultClient(Transaction ta) {
		super(ta);
	}
    
    public QResultClient(Transaction ta, int initialSize) {
        super(ta, initialSize);
    }
    
    public Iterator4 iterator() {
    	return new QResultClientIterator(this);
    }	
}
