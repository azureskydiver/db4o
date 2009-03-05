/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.types.*;

/**
 * @persistent
 * @exclude
 * @deprecated since 7.0
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class P2Collections implements Db4oCollections{
    
    private final Transaction _transaction;
    
    public P2Collections(Transaction transaction){
        _transaction = transaction;
    }

    public Db4oList newLinkedList() {
        return container().syncExec(new Closure4<Db4oList>() { public Db4oList run() {
            Db4oList l = new P2LinkedList();
            container().store(_transaction, l);
            return l;
	    }});
    }

    public Db4oMap newHashMap(final int a_size) {
    	return container().syncExec(new Closure4<Db4oMap>() { public Db4oMap run() {
    		
    		return new P2HashMap(a_size);
    		
    	}});
    }
    
    public Db4oMap newIdentityHashMap(final int a_size) {
    	return container().syncExec(new Closure4<Db4oMap>() { public Db4oMap run() {
            P2HashMap m = new P2HashMap(a_size);
            m.i_type = 1;
            container().store(_transaction, m);
            return m;
    	}});
    }
    
    private ObjectContainerBase container(){
        return _transaction.container();
    }

}
