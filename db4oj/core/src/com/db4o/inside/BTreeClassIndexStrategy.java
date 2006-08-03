/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;

import com.db4o.Transaction;
import com.db4o.Tree;
import com.db4o.TreeInt;
import com.db4o.YapClass;
import com.db4o.YapFile;
import com.db4o.YapObject;
import com.db4o.YapReader;
import com.db4o.YapStream;
import com.db4o.foundation.*;
import com.db4o.inside.btree.BTree;
import com.db4o.inside.convert.conversions.ClassIndexesToBTrees;

/**
 * @exclude
 */
public class BTreeClassIndexStrategy extends AbstractClassIndexStrategy {
	
	private BTree _btreeIndex;
	
	public BTreeClassIndexStrategy(YapClass yapClass) {
		super(yapClass);
	}	

	public int entryCount(Transaction ta) {
		return _btreeIndex != null
			? _btreeIndex.size(ta)
			: 0;
	}

	public void initialize(YapStream stream) {
		createBTreeIndex(stream, 0);
	}

	public void purge() {
	}

	public void read(YapReader reader, YapStream stream) {
		readBTreeIndex(reader, stream);
	}

	public void writeId(YapReader writer, Transaction trans) {
		if (_btreeIndex == null){
            writer.writeInt(0);
        } else {
            _btreeIndex.write(trans);
            writer.writeInt(- _btreeIndex.getID());
        }
	}

	public void add(Transaction trans, int id) {
		_btreeIndex.add(trans, new Integer(id));
	}

	public Tree getAll(Transaction trans) {
		 // TODO: Index should work with BTrees only, no more conversion
        // to TreeInt should be necessary.
        
        TreeInt zero = new TreeInt(0);
        final Tree[] tree = new Tree[]{zero};
        _btreeIndex.traverseKeys(trans, new Visitor4() {
            public void visit(Object obj) {
                tree[0] = tree[0].add(new TreeInt(((Integer)obj).intValue()));
            }
        });
        tree[0] = tree[0].removeNode(zero);
        return tree[0];
	}

	public long[] getIds(Transaction trans) {
		return getIdsFromBTreeIndex(trans);
	}

	public void remove(Transaction ta, int id) {
		_btreeIndex.remove(ta, new Integer(id));
	}
		
	public void traverseAll(Transaction ta,Visitor4 command) {
		// better alternatives for this null check? (has been moved as is from YapFile)
		if(_btreeIndex!=null) {
			_btreeIndex.traverseKeys(ta,command);
		}
	}

	public int idFromValue(Object value) {
		return ((Integer)value).intValue();
	}

	private long[] getIdsFromBTreeIndex(Transaction trans) {
		final long[] ids;
		ids = new long[_btreeIndex.size(trans)];
		final int[] count = new int[]{0};
		_btreeIndex.traverseKeys(trans, new Visitor4() {
		    public void visit(Object obj) {
		        int id = ((Integer)obj).intValue();
		        if(id > 0){
		            ids[count[0]] = id;
		            count[0] ++;
		        }
		    }
		});
		return ids;
	}

	private void createBTreeIndex(final YapStream stream, int btreeID){
        if (stream.isClient()) {
        	return;
        }
        _btreeIndex = ((YapFile)stream).createBTreeClassIndex(_yapClass, btreeID);
        _btreeIndex.setRemoveListener(new Visitor4() {
            public void visit(Object obj) {
                int id = ((Integer)obj).intValue();
                YapObject yo = stream.getYapObject(id);
                if (yo != null) {
                    stream.yapObjectGCd(yo);
                }
            }
        });
    }
	
	private void readBTreeIndex(YapReader reader, YapStream stream) {
		int indexId = reader.readInt();
		if(! stream.isClient() && _btreeIndex == null){
		    YapFile yf = (YapFile)stream;
		    if(indexId < 0){
		        createBTreeIndex(stream, - indexId);
		    }else{
		        createBTreeIndex(stream, 0);
		        new ClassIndexesToBTrees().convert(yf, indexId, _btreeIndex);
		        yf.setDirty(_yapClass);
		    }
		}
	}

}
