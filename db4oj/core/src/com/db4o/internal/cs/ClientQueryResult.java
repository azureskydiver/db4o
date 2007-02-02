/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs;

import com.db4o.*;
import com.db4o.foundation.Iterator4;
import com.db4o.internal.*;
import com.db4o.internal.query.*;
import com.db4o.internal.query.result.*;

/**
 * @exclude
 */
public class ClientQueryResult extends IdListQueryResult {

	public ClientQueryResult(Transaction ta) {
		super(ta);
	}
    
    public ClientQueryResult(Transaction ta, int initialSize) {
        super(ta, initialSize);
    }
    
    public Iterator4 iterator() {
    	return new ClientQueryResultIterator(this);
    }	
}
