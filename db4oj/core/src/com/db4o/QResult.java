/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;

/**
 * @exclude
 */
class QResult extends IntArrayList implements ObjectSet, ExtObjectSet, Visitor4 {
	Tree i_candidates;
	boolean i_checkDuplicates;
	// Iterator4 i_iterator;
	final Transaction i_trans;

	QResult(Transaction a_trans) {
		i_trans = a_trans;
	}

	final Object activate(Object obj){
		YapStream stream = i_trans.i_stream;
		stream.beginEndActivation();
		stream.activate2(i_trans, obj, stream.i_config.i_activationDepth);
		stream.beginEndActivation();
		return obj;
	}

	final void checkDuplicates(){
		i_checkDuplicates = true;
	}

	public ExtObjectSet ext() {
		return this;
	}

	public long[] getIDs() {
		synchronized (streamLock()) {
		    return asLong();
		}
	}

	public boolean hasNext() {
		synchronized (streamLock()) {
			return super.hasNext();
		}
	}

	public Object next() {
		synchronized (streamLock()) {
			YapStream stream = i_trans.i_stream;
			stream.checkClosed();
			if (super.hasNext()) {
				Object ret = stream.getByID2(i_trans, nextInt());
				if (ret == null) {
					return next();
				}
				return activate(ret);
			}
			return null;
		}
	}

	public void reset() {
		synchronized (streamLock()) {
		    super.reset();
		}
	}

	public void visit(Object a_tree) {
		QCandidate candidate = (QCandidate) a_tree;
		if (candidate.include()) {
		    addKeyCheckDuplicates(candidate.i_key);
		}
	}
	
	void addKeyCheckDuplicates(int a_key){
	    if(i_checkDuplicates){
	        TreeInt newNode = new TreeInt(a_key);
	        i_candidates = Tree.add(i_candidates, newNode);
	        if(newNode.i_size == 0){
	            return;
	        }
	    }
	    
	    // TODO: It would be more efficient to hold TreeInts
	    // here only but it won't work, in case an ordering
	    // is applied. Modify to hold a tree here, in case
	    // there is no ordering.
	    
	    add(a_key);
	    
	}
	
	protected Object streamLock(){
		return i_trans.i_stream.i_lock;
	}

}
