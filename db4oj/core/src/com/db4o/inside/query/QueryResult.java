/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

/**
 * @exclude
 */
public interface QueryResult extends Iterable4 {

    public Object get(int index);

	public IntIterator4 iterateIDs();
	
    public int size();
    
    public ExtObjectContainer objectContainer();
    
    public int indexOf(int id);

	public void sort(QueryComparator cmp);

}