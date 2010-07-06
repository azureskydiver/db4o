/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.foundation;

import com.db4o.ext.*;

public class DrsUUIDImpl implements DrsUUID {
	
	private final Db4oUUID _db4oUUID;
	
	public DrsUUIDImpl(Db4oUUID db4oUUID){
		_db4oUUID = db4oUUID;
	}

	public long getLongPart() {
		return _db4oUUID.getLongPart();
	}

	public byte[] getSignaturePart() {
		return _db4oUUID.getSignaturePart();
	}

	public Db4oUUID db4oUUID() {
		return _db4oUUID;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( ! (obj instanceof DrsUUIDImpl)){
			return false;
		}
		DrsUUIDImpl other = (DrsUUIDImpl) obj;
		return _db4oUUID.equals(other._db4oUUID);
	}
	
	@Override
	public int hashCode() {
		return _db4oUUID.hashCode();
	}
	
}
