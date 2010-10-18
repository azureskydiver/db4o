/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import com.db4o.drs.foundation.*;

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

	/**
	 * db4o format without database ID !!!
	 */
	public long getLongPart() {
		return UuidConverter.longPartFromVod(_objectId1, _objectId2);
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
	
	@Override
	public String toString() {
		
		// VOD style
		return this.getClass().getSimpleName() + " _databaseId:" + _databaseId + " _objectId2:" + _objectId2 + "_signature:" + _signature;
		
		// db4o style
		// return this.getClass().getSimpleName() + " sig: " + _signature.toString() + " long: " + getLongPart(); 
		
	}

	
}
