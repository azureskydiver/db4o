/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public class FreespaceBTree extends BTree{
	
	public FreespaceBTree(Transaction trans, int id, Indexable4 keyHandler){
		super(trans, id , keyHandler);
	}
	
    protected boolean canEnlistWithTransaction(){
    	return false;
    }
    
    @Override
    public SlotChangeFactory slotChangeFactory() {
    	return SlotChangeFactory.FREE_SPACE;
    }

}
