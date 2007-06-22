/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;


/**
 * @exclude
 */
public interface IndexableTypeHandler extends Indexable4, TypeHandler4{
    
    Object indexEntryToObject(Transaction trans, Object indexEntry);

}
