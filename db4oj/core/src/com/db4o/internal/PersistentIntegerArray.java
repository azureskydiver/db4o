/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;


/**
 * @exclude
 */
public class PersistentIntegerArray extends PersistentBase {
	
	private int[] _ints;
	
	public PersistentIntegerArray(int id) {
		setID(id);
	}
	
	public PersistentIntegerArray(int[] arr){
		_ints = new int[arr.length];
		System.arraycopy(arr, 0, _ints, 0, arr.length);
	}

	public byte getIdentifier() {
		return Const4.INTEGER_ARRAY;
	}

	public int ownLength() {
		return (Const4.INT_LENGTH * (size() + 1)) + Const4.ADDED_LENGTH;
	}

	public void readThis(Transaction trans, BufferImpl reader) {
		int length = reader.readInt();
		_ints = new int[length];
		for (int i = 0; i < length; i++) {
			_ints[i] = reader.readInt();
		}
	}

	public void writeThis(Transaction trans, BufferImpl writer) {
		writer.writeInt(size());
		for (int i = 0; i < _ints.length; i++) {
			writer.writeInt(_ints[i]);
		}
	}
	
	private int size(){
		return _ints.length;
	}
	
	public int[] array(){
		return _ints;
	}

}
