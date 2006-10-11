/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.inside.query.*;
import com.db4o.query.*;

/**
 * @exclude
 */
class QueryResultImpl extends IntArrayList implements Visitor4, QueryResult {
    
	Tree i_candidates;
	boolean i_checkDuplicates;
    
	final Transaction i_trans;
	
//	private final IntArrayList _ids = new IntArrayList();

	QueryResultImpl(Transaction a_trans) {
		i_trans = a_trans;
	}
    
    QueryResultImpl(Transaction trans, int initialSize){
        super(initialSize);
        i_trans = trans;
    }
    
    public IntIterator4 iterateIDs() {
    	return intIterator();
    }
    
    public Iterator4 iterator() {
    	return new MappingIterator(super.iterator()) {
    		protected Object map(Object current) {
    			synchronized (streamLock()) {
    				return activatedObject(((Integer)current).intValue());
				}
    		}
    	};
    }

	final Object activate(Object obj){
		YapStream stream = stream();
		stream.activate1(i_trans, obj, stream.configImpl().activationDepth());
		return obj;
	}
    
    private final Object activatedObject(int id){
        YapStream stream = stream();
        Object ret = stream.getActivatedObjectFromCache(i_trans, id);
        if(ret != null){
            return ret;
        }
        return stream.readActivatedObjectNotInCache(i_trans, id);
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

	final void checkDuplicates(){
		i_checkDuplicates = true;
	}

	public void visit(Object a_tree) {
		QCandidate candidate = (QCandidate) a_tree;
		if (candidate.include()) {
		    addKeyCheckDuplicates(candidate._key);
		}
	}
	
	void addKeyCheckDuplicates(int a_key){
	    if(i_checkDuplicates){
	        TreeInt newNode = new TreeInt(a_key);
	        i_candidates = Tree.add(i_candidates, newNode);
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
		return stream.i_lock;
	}

	private YapStream stream() {
		return i_trans.stream();
	}

    public ObjectContainer objectContainer() {
        return stream();
    }

	public void sort( QueryComparator cmp) {
		sort(cmp,0,size()-1);
	}

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
}
