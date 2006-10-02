/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import com.db4o.inside.marshall.*;

/**
 * @exclude
 */
public class ReaderPair {
	private YapReader _source;
	private YapReader _target;
	private IDMapping _mapping;
	private Transaction _systemTrans;
	
	public ReaderPair(YapReader source,IDMapping mapping,Transaction systemTrans) {
		_source = source;
		_mapping=mapping;
		_target = new YapReader(source.getLength());
		_source.copyTo(_target, 0, 0, _source.getLength());
		_systemTrans=systemTrans;
	}
	
	public void incrementOffset(int numBytes) {
		_source.incrementOffset(numBytes);
		_target.incrementOffset(numBytes);
	}

	public void incrementIntSize() {
		incrementIntSize(1);
	}

	public void incrementIntSize(int times) {
		incrementOffset(times*YapConst.INT_LENGTH);
	}

	public int copyID(boolean skipEquals) {
		int id=_source.readInt();
		int mapped=_mapping.mappedID(id);
		if(skipEquals&&(id==mapped)) {
			_target.incrementOffset(YapConst.INT_LENGTH);
		}
		else {
			_target.writeInt(mapped);
		}
		return mapped;
	}

	public int copyID() {
		return copyID(false);
	}
	
	public byte readByte() {
		byte value=_source.readByte();
		_target.incrementOffset(1);
		return value;
	}

	public int readInt() {
		int value=_source.readInt();
		_target.incrementOffset(YapConst.INT_LENGTH);
		return value;
	}

	public void writeInt(int value) {
		_source.incrementOffset(YapConst.INT_LENGTH);
		_target.writeInt(value);
	}
	
	public void write(YapFile file,int address) {
		file.writeBytes(_target,address,0);
	}
	
	public String readShortString(YapStringIO sio) throws CorruptionException {
		String value=StringMarshaller.readShort(sio,false,_source);
		StringMarshaller.readShort(sio,false,_target);
		return value;
	}
	
	public YapReader source() {
		return _source;
	}

	public YapReader target() {
		return _target;
	}
	
	public IDMapping mapping() {
		return _mapping;
	}

	public Transaction systemTrans() {
		return _systemTrans;
	}
}
