/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;

import com.db4o.Transaction;
import com.db4o.Tree;
import com.db4o.TreeInt;
import com.db4o.YapClass;
import com.db4o.YapReader;
import com.db4o.YapStream;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class OldClassIndexStrategy extends AbstractClassIndexStrategy  {
	
	private ClassIndex _index;
	
	public OldClassIndexStrategy(YapClass yapClass) {
		super(yapClass);
	}

	public void read(YapReader reader, YapStream stream) {
		int classIndexId = reader.readInt();
		_index = createClassIndex(stream);
		if (classIndexId > 0) {
			_index.setID(classIndexId);
		}
		_index.setStateDeactivated();
	}

	public ClassIndex getIndex() {
		if (null != _index) {
			_index.ensureActive();
		}
        return _index;
	}

	public int entryCount(Transaction ta) {
		if (_index != null) {
            return _index.entryCount(ta);
        }
		return 0;
	}

	public void initialize(YapStream stream) {
		_index = createClassIndex(stream);
	}

	public void purge() {
		if (_index != null) {
            if (!_index.isDirty()) {
                _index.clear();
                _index.setStateDeactivated();
            }
        }
	}

	public void writeId(YapReader writer, Transaction trans) {
		writer.writeIDOf(trans, _index);
	}

	public void add(Transaction trans, int id) {
		trans.addToClassIndex(yapClassID(), id);
	}

	public long[] getIds(Transaction trans) {
		final long[] ids;
		Tree tree = getAll(trans);
		if(tree == null){
		    return new long[0];
		}
		ids = new long[tree.size()];
		final int[] inc = new int[] { 0 };
		tree.traverse(new Visitor4() {
		    public void visit(Object obj) {
		        ids[inc[0]++] = idFromValue(obj);
		    }
		});
		return ids;
	}

	public Tree getAll(Transaction trans) {
		ClassIndex ci = getIndex();
        if (ci == null) {
        	return null;
        }
        return ci.cloneForYapClass(trans, yapClassID());
	}

	public void remove(Transaction ta, int id) {
		ta.removeFromClassIndex(yapClassID(), id);
	}

	public void traverseAll(Transaction ta, Visitor4 command) {
		Tree tree = getAll(ta);
		if(tree!=null) {
			tree.traverse(command);
		}
	}

	public int idFromValue(Object value) {
		return ((TreeInt) value)._key;
	}

	private ClassIndex createClassIndex(YapStream stream) {
		if (stream.isClient()) {
			return new ClassIndexClient(_yapClass);
		}
		return new ClassIndex(_yapClass);
	}
}
