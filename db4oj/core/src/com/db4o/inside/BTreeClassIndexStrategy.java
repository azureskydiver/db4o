package com.db4o.inside;

import com.db4o.Transaction;
import com.db4o.Tree;
import com.db4o.TreeInt;
import com.db4o.YapClass;
import com.db4o.YapFile;
import com.db4o.YapObject;
import com.db4o.YapReader;
import com.db4o.YapStream;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.btree.BTree;
import com.db4o.inside.convert.conversions.ClassIndexesToBTrees;

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

	public void initialize(YapStream a_stream) {
		createBTreeIndex(a_stream, 0);
	}

	public void purge() {
		// TODO Auto-generated method stub
		
	}

	public void read(YapReader reader, YapStream stream) {
		readBTreeIndex(reader, stream);
	}

	public void writeId(YapReader a_writer, Transaction trans) {
		if (_btreeIndex == null){
            a_writer.writeInt(0);
        } else {
            _btreeIndex.write(trans);
            a_writer.writeInt(- _btreeIndex.getID());
        }
	}

	public void add(Transaction a_trans, int a_id) {
		_btreeIndex.add(a_trans, new Integer(a_id));
	}

	public Tree getAll(Transaction a_trans) {
		 // TODO: Index should work with BTrees only, no more conversion
        // to TreeInt should be necessary.
        
        TreeInt zero = new TreeInt(0);
        final Tree[] tree = new Tree[]{zero};
        _btreeIndex.traverseKeys(a_trans, new Visitor4() {
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

	
	private void createBTreeIndex(final YapStream i_stream, int btreeID){
        if (i_stream.isClient()) {
        	return;
        }
        _btreeIndex = ((YapFile)i_stream).createBTreeClassIndex(_yapClass, btreeID);
        _btreeIndex.setRemoveListener(new Visitor4() {
            public void visit(Object obj) {
                int id = ((Integer)obj).intValue();
                YapObject yo = i_stream.getYapObject(id);
                if (yo != null) {
                    i_stream.yapObjectGCd(yo);
                }
            }
        });
    }
	
	private void readBTreeIndex(YapReader i_reader, YapStream i_stream) {
		int indexId = i_reader.readInt();
		if(! i_stream.isClient() && _btreeIndex == null){
		    YapFile yf = (YapFile)i_stream;
		    if(indexId < 0){
		        createBTreeIndex(i_stream, - indexId);
		    }else{
		        createBTreeIndex(i_stream, 0);
		        new ClassIndexesToBTrees().convert(yf, indexId, _btreeIndex);
		        yf.setDirty(_yapClass);
		    }
		}
	}

	public BTree getIndex() {
		return _btreeIndex;
	}

}
