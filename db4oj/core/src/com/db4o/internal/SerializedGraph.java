/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;


/**
 * @exclude
 */
public class SerializedGraph {
	
	public final int _id;

	public final byte[] _bytes;
	
	public SerializedGraph(int id, byte[] bytes) {
		_id = id;
		_bytes = bytes;
	}
	
	public int length(){
		return _bytes.length;
	}
	
	public int marshalledLength(){
		return (Const4.INT_LENGTH * 2 )+ length();
	}
	
	public void write(BufferImpl buffer){
		buffer.writeInt(_id);
		buffer.writeInt(length());
		buffer.append(_bytes);
	}
	
	public static SerializedGraph read(BufferImpl buffer){
		int id = buffer.readInt();
		int length = buffer.readInt();
		return new SerializedGraph(id, buffer.readBytes(length));
	}
	
}
