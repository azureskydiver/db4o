/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

final class Slot implements ReadWriteable{
	int i_address;
	int i_length;
	int i_references;
	
	Slot(int address, int length){
		i_address = address;
		i_length = length;
	}
	
	public int byteCount(){
		return YapConst.YAPINT_LENGTH * 2;
	}
	
	public void write(YapWriter a_bytes){
		a_bytes.writeInt(i_address);
		a_bytes.writeInt(i_length);
	}
	
	public Object read(YapReader a_bytes){
		int address = a_bytes.readInt();
		int length = a_bytes.readInt();
		return new Slot(address, length);
	}
}
