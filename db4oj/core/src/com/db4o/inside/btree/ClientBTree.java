/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.inside.ix.*;

/**
 * @exclude
 */
public class ClientBTree extends BTree{
    
    public ClientBTree(int nodeSize, int cacheHeight, Transaction trans, int id, Indexable4 keyHandler, Indexable4 valueHandler){
        super(nodeSize, cacheHeight, trans, id, keyHandler, valueHandler);
    }

}
