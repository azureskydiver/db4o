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
		stream().activate1(_transaction, obj, config().activationDepth());
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
    			if(current == null){
    				return MappingIterator.SKIP;
    			}
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
    
    public AbstractQueryResult supportSize(){
    	return this;
    }
    
    public AbstractQueryResult supportSort(){
    	return this;
    }
    
    public AbstractQueryResult supportElementAccess(){
    	return this;
    }
    
    protected int knownSize(){
    	return size();
    }
    
    public AbstractQueryResult toIdList(){
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
    
	public abstract void loadFromClassIndex(YapClass clazz);

	public abstract void loadFromQuery(QQuery query);

	public abstract void loadFromClassIndexes(YapClassCollectionIterator iterator);

	public abstract void loadFromIdReader(YapReader reader);
	
	public Config4Impl config(){
		return stream().config();
	}

	public abstract int getId(int index);

}
