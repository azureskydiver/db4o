/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.internal.*;
import com.db4o.types.*;

/**
 * @persistent
 */
public class P2Collections implements Db4oCollections{
    
    final ObjectContainerBase i_stream;
    
    public P2Collections(ObjectContainerBase a_stream){
        i_stream = a_stream;
    }

    public Db4oList newLinkedList() {
        synchronized(i_stream.i_lock) {
	        if(Unobfuscated.createDb4oList(i_stream)){
	            Db4oList l = new P2LinkedList();
	            i_stream.set(l);
	            return l;
	        }
	        return null;
        }
    }

    public Db4oMap newHashMap(int a_size) {
        synchronized(i_stream.i_lock) {
	        if(Unobfuscated.createDb4oList(i_stream)){
	            return new P2HashMap(a_size);
	        }
	        return null;
        }
    }
    
    public Db4oMap newIdentityHashMap(int a_size) {
        synchronized(i_stream.i_lock) {
	        if(Unobfuscated.createDb4oList(i_stream)){
	            P2HashMap m = new P2HashMap(a_size);
	            m.i_type = 1;
	            i_stream.set(m);
	            return m;
	        }
	        return null;
        }
    }

}
