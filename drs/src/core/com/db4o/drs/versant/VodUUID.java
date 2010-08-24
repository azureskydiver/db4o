/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import com.db4o.drs.foundation.*;
import com.db4o.foundation.*;

public class VodUUID implements DrsUUID {
	
	private final Signature _signature;
	
	private final int _databaseId;
	
	private final int _objectId1;
	
	private final long _objectId2;
	
	public VodUUID(Signature signature, int dbId, int id1, long id2){
		_signature = signature;
		_databaseId = dbId;
		_objectId1 = id1;
		_objectId2 = id2;
	}
	
	public VodUUID(Signature signature, VodId vodId){
		this(signature, vodId.databaseId, vodId.objectId1, vodId.objectId2);
	}

	public long getLongPart() {
		return TimeStampIdGenerator.convert48BitIdTo64BitId(_objectId1 << 32  | _objectId2);
	}

	public byte[] getSignaturePart() {
		return _signature.bytes;
	}

	@Override
	public boolean equals(Object other) {
		if(this == other) {
			return true;
		}
		if(other == null || getClass() != other.getClass()) {
			//return false;
			throw new IllegalStateException(); // TODO REMOVE, return false instead
		}
		VodUUID uuid = (VodUUID)other;
		return _databaseId == uuid._databaseId 
				&& _objectId1 == uuid._objectId1 
				&& _objectId2 == uuid._objectId2
				&& _signature.equals(uuid._signature);
	}
	
	@Override
	public int hashCode() {
		return _databaseId ^ _objectId1 ^ (int)_objectId2 ^ _signature.hashCode();
	}
}
