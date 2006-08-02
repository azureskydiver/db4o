package com.db4o.inside;

import com.db4o.Transaction;
import com.db4o.Tree;
import com.db4o.TreeInt;
import com.db4o.YapClass;
import com.db4o.YapReader;
import com.db4o.YapStream;
import com.db4o.foundation.Visitor4;

public class OldClassIndexStrategy extends AbstractClassIndexStrategy  {
	
	private ClassIndex i_index;
	
	public OldClassIndexStrategy(YapClass yapClass) {
		super(yapClass);
	}

	public void read(YapReader reader, YapStream stream) {
		int classIndexId = reader.readInt();
		i_index = createClassIndex(stream);
		if (classIndexId > 0) {
			i_index.setID(classIndexId);
		}
		i_index.setStateDeactivated();
	}

	private ClassIndex createClassIndex(YapStream stream) {
		if (stream.isClient()) {
			return new ClassIndexClient(_yapClass);
		}
		return new ClassIndex(_yapClass);
	}

	public ClassIndex getIndex() {
		if (null != i_index) {
			i_index.ensureActive();
		}
        return i_index;
	}

	public int entryCount(Transaction ta) {
		if (i_index != null) {
            return i_index.entryCount(ta);
        }
		return 0;
	}

	public void initialize(YapStream a_stream) {
		i_index = createClassIndex(a_stream);
	}

	public void purge() {
		if (i_index != null) {
            if (!i_index.isDirty()) {
                i_index.clear();
                i_index.setStateDeactivated();
            }
        }
	}

	public void writeId(YapReader a_writer, Transaction trans) {
		a_writer.writeIDOf(trans, i_index);
	}

	public void add(Transaction a_trans, int a_id) {
		a_trans.addToClassIndex(yapClassID(), a_id);
	}

	public long[] getIds(Transaction trans) {
		final long[] ids;
		Tree tree = getIndex().cloneForYapClass(trans, yapClassID());
		if(tree == null){
		    return new long[0];
		}
		ids = new long[tree.size()];
		final int[] inc = new int[] { 0 };
		tree.traverse(new Visitor4() {
		    public void visit(Object obj) {
		        ids[inc[0]++] = ((TreeInt) obj)._key;
		    }
		});
		return ids;
	}

	public Tree getAll(Transaction a_trans) {
		ClassIndex ci = getIndex();
        if (ci == null) {
        	return null;
        }
        return ci.cloneForYapClass(a_trans, yapClassID());
	}

	public void remove(Transaction ta, int id) {
		ta.removeFromClassIndex(yapClassID(), id);
	}

}
