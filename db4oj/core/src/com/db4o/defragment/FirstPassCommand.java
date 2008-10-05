/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.defragment;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
		
/**
 * First step in the defragmenting process: Allocates pointer slots in the target file for
 * each ID (but doesn't fill them in, yet) and registers the mapping from source pointer address
 * to target pointer address.
 * 
 * @exclude
 */
public final class FirstPassCommand implements PassCommand {
	
	private IDMappingCollector _collector = new IDMappingCollector();
	
	public void processClass(final DefragmentServicesImpl context, ClassMetadata classMetadata,int id,int classIndexID) {
		_collector.process(context,id, true);
		classMetadata.forEachField(new Procedure4() {
            public void apply(Object arg) {
                FieldMetadata field = (FieldMetadata) arg;
                if(!field.isVirtual()&&field.hasIndex()) {
                    processBTree(context,field.getIndex(context.systemTrans()));
                }
            }
        });
	}

	public void processObjectSlot(DefragmentServicesImpl context, ClassMetadata yapClass, int sourceID) {
		_collector.process(context,sourceID, false);
	}

	public void processClassCollection(DefragmentServicesImpl context) throws CorruptionException {
		_collector.process(context,context.sourceClassCollectionID(), false);
	}

	public void processBTree(final DefragmentServicesImpl context, final BTree btree) {
		context.registerBTreeIDs(btree, _collector);
	}

	public void flush(DefragmentServicesImpl context) {
		_collector.flush(context);
	}

}