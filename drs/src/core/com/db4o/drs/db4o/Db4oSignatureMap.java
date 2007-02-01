/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.drs.db4o;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.ObjectContainerBase;


class Db4oSignatureMap {
    
    private final ObjectContainerBase _stream;
    
    private final Hashtable4 _identities;
    
    Db4oSignatureMap(ObjectContainerBase stream){
        _stream = stream;
        _identities = new Hashtable4();
    }
    
    Db4oDatabase produce(byte[] signature, long creationTime){
        Db4oDatabase db = (Db4oDatabase) _identities.get(signature);
        if(db != null){
            return db;
        }
        db = new Db4oDatabase(signature, creationTime);
        db.bind(_stream.getTransaction());
        _identities.put(signature, db);
        return db;
    }
    
    public void put(Db4oDatabase db){
        Db4oDatabase existing = (Db4oDatabase) _identities.get(db.getSignature());
        if(existing == null){
            _identities.put(db.getSignature(), db);
        }
    }

}
