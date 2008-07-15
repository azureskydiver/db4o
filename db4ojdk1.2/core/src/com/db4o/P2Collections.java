/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.internal.*;
import com.db4o.types.*;

/**
 * @persistent
 * @exclude
 * @deprecated since 7.0
 * @decaf.ignore.jdk11
 */
public class P2Collections implements Db4oCollections{
    
    private final Transaction _transaction;
    
    public P2Collections(Transaction transaction){
        _transaction = transaction;
    }

    public Db4oList newLinkedList() {
        synchronized(lock()) {
        	if(canCreateCollection(container())){
	            Db4oList l = new P2LinkedList();
	            container().store(_transaction, l);
	            return l;
	        }
	        return null;
        }
    }

    public Db4oMap newHashMap(int a_size) {
        synchronized(lock()) {
        	if(canCreateCollection(container())){
	            return new P2HashMap(a_size);
	        }
	        return null;
        }
    }
    
    public Db4oMap newIdentityHashMap(int a_size) {
        synchronized(lock()) {
	        if(canCreateCollection(container())){
	            P2HashMap m = new P2HashMap(a_size);
	            m.i_type = 1;
	            container().store(_transaction, m);
	            return m;
	        }
	        return null;
        }
    }
    
    private Object lock(){
        return container().lock();
    }
    
    private ObjectContainerBase container(){
        return _transaction.container();
    }
    
	private boolean canCreateCollection(ObjectContainerBase container){
	    container.checkClosed();
	    return ! container.isInstantiating();
	}

}
