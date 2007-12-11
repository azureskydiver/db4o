/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.query.result;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.query.processor.*;
import com.db4o.query.*;


/**
 * @exclude
 */
public abstract class AbstractQueryResult implements QueryResult {
	
	protected final Transaction _transaction;

	public AbstractQueryResult(Transaction transaction) {
		_transaction = transaction;
	}
	
	public final Object activate(Object obj) {
		stream().activate(_transaction, obj);
		return obj;
	}

	public final Object activatedObject(int id) {
	    ObjectContainerBase stream = stream();
	    Object ret = stream.getActivatedObjectFromCache(_transaction, id);
	    if(ret != null){
	        return ret;
	    }
	    return stream.readActivatedObjectNotInCache(_transaction, id);
	}

	public Object lock() {
		final ObjectContainerBase stream = stream();
		stream.checkClosed();
		return stream.lock();
	}

	public ObjectContainerBase stream() {
		return _transaction.container();
	}
	
	public Transaction transaction(){
		return _transaction;
	}

	public ExtObjectContainer objectContainer() {
	    return transaction().objectContainer().ext();
	}
	
    public Iterator4 iterator() {
    	return new MappingIterator(iterateIDs()){
    		protected Object map(Object current) {
    			if(current == null){
    				return MappingIterator.SKIP;
    			}
    			synchronized (lock()) {
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
    	return new IdTreeQueryResult(transaction(), iterateIDs());
    }
    
	public Config4Impl config(){
		return stream().config();
	}

	public int size() {
		throw new NotImplementedException();
	}

	public void sort(QueryComparator cmp) {
		throw new NotImplementedException();
	}

	public Object get(int index) {
		throw new NotImplementedException();
	}
	
    /** @param i */
	public int getId(int i) {
		throw new NotImplementedException();
	}

	public int indexOf(int id) {
		throw new NotImplementedException();
	}

    /** @param c */
	public void loadFromClassIndex(ClassMetadata c) {
		throw new NotImplementedException();
	}

    /** @param i */
	public void loadFromClassIndexes(ClassMetadataIterator i) {
		throw new NotImplementedException();
	}

    /** @param r */
	public void loadFromIdReader(BufferImpl r) {
		throw new NotImplementedException();
	}

    /** @param q */
	public void loadFromQuery(QQuery q) {
		throw new NotImplementedException();
	}

}
