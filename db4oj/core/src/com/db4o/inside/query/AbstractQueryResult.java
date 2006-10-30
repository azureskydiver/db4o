/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.query;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;


/**
 * @exclude
 */
public abstract class AbstractQueryResult implements QueryResult {
	
	protected final Transaction _transaction;

	public AbstractQueryResult(Transaction transaction) {
		_transaction = transaction;
	}

	public final Object activate(Object obj) {
		YapStream stream = stream();
		stream.activate1(_transaction, obj, stream.configImpl().activationDepth());
		return obj;
	}

	public final Object activatedObject(int id) {
	    YapStream stream = stream();
	    Object ret = stream.getActivatedObjectFromCache(_transaction, id);
	    if(ret != null){
	        return ret;
	    }
	    return stream.readActivatedObjectNotInCache(_transaction, id);
	}

	public Object streamLock() {
		final YapStream stream = stream();
		stream.checkClosed();
		return stream.lock();
	}

	public YapStream stream() {
		return _transaction.stream();
	}
	
	public Transaction transaction(){
		return _transaction;
	}

	public ExtObjectContainer objectContainer() {
	    return stream();
	}
	
    public Iterator4 iterator() {
    	return new MappingIterator(iterateIDs()){
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
    
    protected AbstractQueryResult supportSize(){
    	return this;
    }
    
    protected AbstractQueryResult supportSort(){
    	return this;
    }
    
    protected AbstractQueryResult supportElementAccess(){
    	return this;
    }
    
    protected int knownSize(){
    	return size();
    }
    
    protected AbstractQueryResult toIdList(){
    	IdListQueryResult res = new IdListQueryResult(transaction(), knownSize());
    	IntIterator4 i = iterateIDs();
    	while(i.moveNext()){
    		res.add(i.currentInt());
    	}
    	return res;
    }
    
    protected AbstractQueryResult toIdTree(){
    	return new IdTreeQueryResult(transaction(), this);
    }

}
