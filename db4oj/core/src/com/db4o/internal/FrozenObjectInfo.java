/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;

public class FrozenObjectInfo implements ObjectInfo {
	
    private final Db4oDatabase _sourceDatabase;
    private final long _uuidLongPart;
	private final long _id;
	private final long _version;
	private final Object _object;
	
    public FrozenObjectInfo(Object object, long id, Db4oDatabase sourceDatabase, long uuidLongPart, long version) {
        _sourceDatabase = sourceDatabase;
        _uuidLongPart = uuidLongPart;
        _id = id;
        _version = version;
        _object = object;
    }

    private FrozenObjectInfo(ObjectReference ref, VirtualAttributes virtualAttributes) {
        this(
            ref == null ? null : ref.getObject(), 
            ref == null ? -1 :ref.getID(), 
            virtualAttributes == null ? null : virtualAttributes.i_database, 
            virtualAttributes == null ? -1 : virtualAttributes.i_uuid,  
            ref == null ? 0 :ref.getVersion());      
    }

	public FrozenObjectInfo(Transaction trans, ObjectReference ref) {
	    this(ref, ref == null ? null : ref.virtualAttributes(trans));
	}
	
	public long getInternalID() {
		return _id;
	}

	public Object getObject() {
		return _object;
	}

	public Db4oUUID getUUID() {
	    if(_sourceDatabase == null ){
	        return null;
	    }
	    return new Db4oUUID(_uuidLongPart, _sourceDatabase.getSignature());
	}

	public long getVersion() {
		return _version;
	}

    public long sourceDatabaseId(Transaction trans) {
        if(_sourceDatabase == null){
            return -1;
        }
        return _sourceDatabase.getID(trans);
    }

    public long uuidLongPart() {
        return _uuidLongPart;
    }
}