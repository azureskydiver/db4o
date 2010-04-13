/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
* @exclude
*/
public class IdSlotMapping {
	
	public final int _id;
	
	public final int _address;
	
	public final int _length;
	
	public IdSlotMapping(int id, int address, int length) {
		_id = id;
		_address = address;
		_length = length;
	}
	
	public IdSlotMapping(int id, Slot slot){
		this(id, slot.address(), slot.length());
	}
	
	public Slot slot(){
		return new Slot(_address, _length);
	}

	public void write(ByteArrayBuffer buffer) {
		buffer.writeInt(_id);
		buffer.writeInt(_address);
		buffer.writeInt(_length);
	}
	
	public static IdSlotMapping read(ByteArrayBuffer buffer){
		return new IdSlotMapping(buffer.readInt(), buffer.readInt(), buffer.readInt());
	}
	
	@Override
	public String toString() {
		return "" + _id + ":" + _address + "," + _length;
	}
	
}