/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.Iterator4;

/**
 * @exclude
 */
class QResultClient extends QueryResultImpl {

	QResultClient(Transaction ta) {
		super(ta);
	}
    
    QResultClient(Transaction ta, int initialSize) {
        super(ta, initialSize);
    }
    
    public Iterator4 iterator() {
    	return new QResultClientIterator(this);
    }	
}
