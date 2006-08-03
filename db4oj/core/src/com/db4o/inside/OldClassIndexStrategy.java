/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class OldClassIndexStrategy extends AbstractClassIndexStrategy  implements TransactionParticipant {
	
	private ClassIndex _index;
	
	private final Hashtable4 _perTransaction = new Hashtable4(1);
	
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

	private ClassIndex getActiveIndex(Transaction transaction) {
		if (null != _index) {
			_index.ensureActive(transaction);
		}
        return _index;
	}

	public int entryCount(Transaction transaction) {
		if (_index != null) {
            return _index.entryCount(transaction);
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

	public void writeId(YapReader writer, Transaction transaction) {
		writer.writeIDOf(transaction, _index);
	}

	private void flushContext(final Transaction transaction) {
		TransactionState context = getState(transaction);
		
		final ClassIndex index = getActiveIndex(transaction);
		context.traverseAdded(new Visitor4() {
            public void visit(Object a_object) {
                index.add(idFromValue(a_object));
            }
        });
        
        context.traverseRemoved(new Visitor4() {
            public void visit(Object a_object) {
                int id = idFromValue(a_object);
                final YapStream stream = transaction.stream();
				YapObject yo = stream.getYapObject(id);
                if (yo != null) {
                    stream.yapObjectGCd(yo);
                }
                index.remove(id);
            }
        });
	}

	private void writeIndex(Transaction transaction) {
		_index.setStateDirty();
		_index.write(transaction);
	}
	
	final static class TransactionState {
		
		private Tree i_addToClassIndex;
	    
	    private Tree i_removeFromClassIndex;
		
		public void add(int id) {
			i_removeFromClassIndex = Tree.removeLike(i_removeFromClassIndex, new TreeInt(id));
			i_addToClassIndex = Tree.add(i_addToClassIndex, new TreeInt(id));
		}

		public void remove(int id) {
			i_addToClassIndex = Tree.removeLike(i_addToClassIndex, new TreeInt(id));
			i_removeFromClassIndex = Tree.add(i_removeFromClassIndex, new TreeInt(id));
		}
		
		public void dontDelete(int id) {
			i_removeFromClassIndex = Tree.removeLike(i_removeFromClassIndex, new TreeInt(id));
		}

	    void traverse(Tree node, Visitor4 visitor) {
	    	if (node != null) {
	    		node.traverse(visitor);
	    	}
	    }
	    
		public void traverseAdded(Visitor4 visitor4) {
			traverse(i_addToClassIndex, visitor4);
		}

		public void traverseRemoved(Visitor4 visitor4) {
			traverse(i_removeFromClassIndex, visitor4);
		}		
	}

	protected void internalAdd(Transaction transaction, int id) {
		getState(transaction).add(id);					
	}

	private TransactionState getState(Transaction transaction) {
		synchronized (_perTransaction) {
			TransactionState context = (TransactionState)_perTransaction.get(transaction);
			if (null == context) {
				context = new TransactionState();
				_perTransaction.put(transaction, context);
				transaction.enlist(this);
			}
			return context;
		}
	}

	public long[] getIds(Transaction transaction) {		
		Tree tree = getAll(transaction);
		if(tree == null){
		    return new long[0];
		}
		final long[] ids = new long[tree.size()];
		final int[] inc = new int[] { 0 };
		tree.traverse(new Visitor4() {
		    public void visit(Object obj) {
		        ids[inc[0]++] = idFromValue(obj);
		    }
		});
		return ids;
	}

	public Tree getAll(Transaction transaction) {
		ClassIndex ci = getActiveIndex(transaction);
        if (ci == null) {
        	return null;
        }
        
        final Tree[] tree = new Tree[] { Tree.deepClone(ci.getRoot(), null) };		
		TransactionState context = getState(transaction);
		context.traverseAdded(new Visitor4() {
		    public void visit(Object obj) {
				tree[0] = Tree.add(tree[0], new TreeInt(idFromValue(obj)));
		    }
		});
		context.traverseRemoved(new Visitor4() {
		    public void visit(Object obj) {
				tree[0] = Tree.removeLike(tree[0], (TreeInt) obj);
		    }
		});
		return tree[0];
	}

	protected void internalRemove(Transaction transaction, int id) {
		getState(transaction).remove(id);
	}

	public void traverseAll(Transaction transaction, Visitor4 command) {
		Tree tree = getAll(transaction);
		if (tree != null) {
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

	public void dontDelete(Transaction transaction, int id) {
		getState(transaction).dontDelete(id);
	}

	public void commit(Transaction trans) {		
		if (null != _index) {
			flushContext(trans);
			writeIndex(trans);
		}
	}

	public void dispose(Transaction transaction) {
		synchronized (_perTransaction) {
			_perTransaction.remove(transaction);
		}
	}

	public void rollback(Transaction transaction) {
		// nothing to do
	}
}
