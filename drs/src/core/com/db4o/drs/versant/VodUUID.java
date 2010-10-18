/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import com.db4o.drs.foundation.*;

public class VodUUID implements DrsUUID {
	
	private final Signature _signature;
	
	private final long _loid;
	
	public VodUUID(Signature signature, long loid){
		_signature = signature;
		_loid = loid;
	}
	
	public VodUUID(Signature signature, VodId vodId){
		this(signature, vodId.loid);
	}

	/**
	 * db4o format without database ID !!!
	 */
	public long getLongPart() {
		return UuidConverter.longPartFromVod(_loid);
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
		return _loid == uuid._loid 
				&& _signature.equals(uuid._signature);
	}
	
	@Override
	public int hashCode() {
		return (int) (_loid ^ _signature.hashCode());
	}
	
	@Override
	public String toString() {
		
		// VOD format
		return this.getClass().getSimpleName() + " _loid:" + _loid + " _signature:" + _signature;
		
		// db4o format
		// return this.getClass().getSimpleName() + " sig: " + _signature.toString() + " long: " + getLongPart(); 
		
	}

	
}
