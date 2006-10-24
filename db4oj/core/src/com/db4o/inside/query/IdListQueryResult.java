/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside.query;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.inside.classindex.*;
import com.db4o.query.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class IdListQueryResult extends IntArrayList implements Visitor4, QueryResult {
    
	private final Transaction _transaction;
	
	private Tree _candidates;
	
	private boolean _checkDuplicates;
    
	public IdListQueryResult(Transaction a_trans) {
		_transaction = a_trans;
	}
    
    protected IdListQueryResult(Transaction trans, int initialSize){
        super(initialSize);
        _transaction = trans;
    }
    
    public IntIterator4 iterateIDs() {
    	return intIterator();
    }
    
    public Iterator4 iterator() {
    	return new MappingIterator(super.iterator()){
    		protected Object map(Object current) {
    			synchronized (streamLock()) {
    				Object obj = activatedObject(((Integer)current).intValue());
    				if(obj == null){
    					return MappingIterator.SKIP;
    				}
    				return obj; 
    			}
    		}
    	};
    }

	public final Object activate(Object obj){
		YapStream stream = stream();
		stream.activate1(_transaction, obj, stream.configImpl().activationDepth());
		return obj;
	}
    
    public final Object activatedObject(int id){
        YapStream stream = stream();
        Object ret = stream.getActivatedObjectFromCache(_transaction, id);
        if(ret != null){
            return ret;
        }
        return stream.readActivatedObjectNotInCache(_transaction, id);
    }
    
    /* (non-Javadoc)
     * @see com.db4o.QueryResult#get(int)
     */
    public Object get(int index) {
        synchronized (streamLock()) {
            if (index < 0 || index >= size()) {
                throw new IndexOutOfBoundsException();
            }
            return activatedObject(i_content[index]);
        }
    }

	public final void checkDuplicates(){
		_checkDuplicates = true;
	}

	public void visit(Object a_tree) {
		QCandidate candidate = (QCandidate) a_tree;
		if (candidate.include()) {
		    addKeyCheckDuplicates(candidate._key);
		}
	}
	
	public void addKeyCheckDuplicates(int a_key){
	    if(_checkDuplicates){
	        TreeInt newNode = new TreeInt(a_key);
	        _candidates = Tree.add(_candidates, newNode);
	        if(newNode._size == 0){
	            return;
	        }
	    }
	    
	    // TODO: It would be more efficient to hold TreeInts
	    // here only but it won't work, in case an ordering
	    // is applied. Modify to hold a tree here, in case
	    // there is no ordering.
	    
	    add(a_key);
	    
	}
	
	public Object streamLock(){
		final YapStream stream = stream();
		stream.checkClosed();
		return stream.lock();
	}

	public YapStream stream() {
		return _transaction.stream();
	}

    public ExtObjectContainer objectContainer() {
        return stream();
    }

	public void sort( QueryComparator cmp) {
		sort(cmp,0,size()-1);
	}

	// TODO: use Algorithms4.qsort
	private void sort(QueryComparator cmp,int from,int to) {
		if(to-from<1) {
			return;
		}
		Object pivot=get(to);
		int left=from;
		int right=to;
		while(left<right) {
			while(left<right&&cmp.compare(pivot,get(left))<0) {
				left++;
			}
			while(left<right&&cmp.compare(pivot,get(right))>=0) {
				right--;
			}
			swap(left, right);
		}
		swap(to, right);
		sort(cmp,from,right-1);
		sort(cmp,right+1,to);
	}

	private void swap(int left, int right) {
		if(left!=right) {
			int swap=i_content[left];
			i_content[left]=i_content[right];
			i_content[right]=swap;
		}
	}

	public void loadFromClassIndex(YapClass clazz) {
		final ClassIndexStrategy index = clazz.index();
		index.traverseAll(_transaction, new Visitor4() {
			public void visit(Object a_object) {
				add(((Integer)a_object).intValue());
			}
		});
	}

	public void loadFromQuery(QQuery query) {
		query.executeLocal(this);
	}
	
	public void loadFromClassIndexes(YapClassCollectionIterator iter){
		
        // duplicates because of inheritance hierarchies
        final Tree[] duplicates = new Tree[1];

        while (iter.moveNext()) {
			final YapClass yapClass = iter.currentClass();
			if (yapClass.getName() != null) {
				ReflectClass claxx = yapClass.classReflector();
				if (claxx == null
						|| !(stream().i_handlers.ICLASS_INTERNAL.isAssignableFrom(claxx))) {
					final ClassIndexStrategy index = yapClass.index();
					index.traverseAll(_transaction, new Visitor4() {
						public void visit(Object obj) {
							int id = ((Integer)obj).intValue();
							TreeInt newNode = new TreeInt(id);
							duplicates[0] = Tree.add(duplicates[0], newNode);
							if (newNode.size() != 0) {
								add(id);
							}
						}
					});
				}
			}
		}
		
	}

	public void loadFromIdReader(YapReader reader) {
		int size = reader.readInt();
		for (int i = 0; i < size; i++) {
			add(reader.readInt());
		}
	}

	
}
