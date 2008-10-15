/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.collections;

import com.db4o.defragment.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;

/**
 * @exclude
 * @decaf.ignore.jdk11
 */
public class BigSetTypeHandler implements TypeHandler4{

	public void defragment(DefragmentContext context) {
		int pos = context.offset();
		int id = context.readInt();
		BTree bTree = newBTree(context, id);
		DefragmentServicesImpl services = (DefragmentServicesImpl) context.services();
		IDMappingCollector collector = new IDMappingCollector();
		services.registerBTreeIDs(bTree, collector);
		collector.flush(services);
		context.seek(pos);
		context.copyID();
		bTree.defragBTree(services);
	}

	public void delete(DeleteContext context) throws Db4oIOException {
		invalidBigSet(context);
		int id = context.readInt();
		freeBTree(context, id);
	}

	private void invalidBigSet(DeleteContext context) {
	    BigSetPersistence bigSet = (BigSetPersistence) context.transaction().objectForIdFromCache(context.id());
		if(bigSet != null){
			bigSet.invalidate();
		}
    }

	private void freeBTree(DeleteContext context, int id) {
	    BTree bTree = newBTree(context, id);
		bTree.free(systemTransaction(context));
		bTree = null;
    }

	private static Transaction systemTransaction(Context context) {
		return context.transaction().systemTransaction();
	}

	private BTree newBTree(Context context, int id) {
		return new BTree(systemTransaction(context), id, new IDHandler());
	}

	public Object read(ReadContext context) {
		BigSetPersistence bigSet = (BigSetPersistence)((UnmarshallingContext)context).persistentObject();
		bigSet.read(context);
		return bigSet;
	}

	public void write(WriteContext context, Object obj) {
		BigSetPersistence bigSet = (BigSetPersistence) obj;
		bigSet.write(context);
	}

	public PreparedComparison prepareComparison(Context context, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
