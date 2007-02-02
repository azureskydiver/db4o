/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.classindex;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public class OldClassIndexStrategy extends AbstractClassIndexStrategy  implements TransactionParticipant {
	
	private ClassIndex _index;
	
	private final Hashtable4 _perTransaction = new Hashtable4();
	
	public OldClassIndexStrategy(ClassMetadata yapClass) {
		super(yapClass);
	}

	public void read(ObjectContainerBase stream, int indexID) {
		_index = createClassIndex(stream);
		if (indexID > 0) {
			_index.setID(indexID);
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

	public void initialize(ObjectContainerBase stream) {
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

	public int write(Transaction transaction) {
        if(_index == null){
            return 0;
        }
        _index.write(transaction);
        return _index.getID();
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
                final ObjectContainerBase stream = transaction.stream();
				ObjectReference yo = stream.getYapObject(id);
                if (yo != null) {
                    stream.removeReference(yo);
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

	private Tree getAll(Transaction transaction) {
		ClassIndex ci = getActiveIndex(transaction);
        if (ci == null) {
        	return null;
        }
        
        final Tree.ByRef tree = new Tree.ByRef(Tree.deepClone(ci.getRoot(), null));		
		TransactionState context = getState(transaction);
		context.traverseAdded(new Visitor4() {
		    public void visit(Object obj) {
				tree.value = Tree.add(tree.value, new TreeInt(idFromValue(obj)));
		    }
		});
		context.traverseRemoved(new Visitor4() {
		    public void visit(Object obj) {
				tree.value = Tree.removeLike(tree.value, (TreeInt) obj);
		    }
		});
		return tree.value;
	}

	protected void internalRemove(Transaction transaction, int id) {
		getState(transaction).remove(id);
	}

	public void traverseAll(Transaction transaction, final Visitor4 command) {
		Tree tree = getAll(transaction);
		if (tree != null) {
			tree.traverse(new Visitor4() {
				public void visit(Object obj) {
					command.visit(new Integer(idFromValue(obj)));
				}
			});
		}
	}

	public int idFromValue(Object value) {
		return ((TreeInt) value)._key;
	}

	private ClassIndex createClassIndex(ObjectContainerBase stream) {
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

	public void defragReference(ClassMetadata yapClass, ReaderPair readers,int classIndexID) {
	}

	public int id() {
		return _index.getID();
	}

    
	public Iterator4 allSlotIDs(Transaction trans){
        throw new NotImplementedException();
	}

	public void defragIndex(ReaderPair readers) {
	}
}
