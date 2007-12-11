/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import java.io.IOException;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.mapping.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public final class BufferPair implements SlotBuffer {
	private Buffer _source;
	private Buffer _target;
	private DefragmentServices _mapping;
	private Transaction _systemTrans;
	
	public BufferPair(Buffer source,DefragmentServices mapping,Transaction systemTrans) {
		_source = source;
		_mapping=mapping;
		_target = new Buffer(length());
		_source.copyTo(_target, 0, 0, length());
		_systemTrans=systemTrans;
	}
	
	public int offset() {
		return _source.offset();
	}

	public void seek(int offset) {
		_source.seek(offset);
		_target.seek(offset);
	}

	public void incrementOffset(int numBytes) {
		_source.incrementOffset(numBytes);
		_target.incrementOffset(numBytes);
	}

	public void incrementIntSize() {
		incrementOffset(Const4.INT_LENGTH);
	}

	public int copyUnindexedID() {
		int orig=_source.readInt();
		int mapped=-1;
		try {
			mapped=_mapping.mappedID(orig);
		} catch (MappingNotFoundException exc) {
			mapped=_mapping.allocateTargetSlot(Const4.POINTER_LENGTH).address();
			_mapping.mapIDs(orig,mapped, false);
			_mapping.registerUnindexed(orig);
		}
		_target.writeInt(mapped);
		return mapped;
	}

	public int copyID() {
		// This code is slightly redundant. 
		// The profiler shows it's a hotspot.
		// The following would be non-redudant. 
		// return copy(false, false);
		
		int id = _source.readInt();
		return writeMappedID(id);
	}

	public int copyID(boolean flipNegative,boolean lenient) {
		int id=_source.readInt();
		return internalCopyID(flipNegative, lenient, id);
	}

	public MappedIDPair copyIDAndRetrieveMapping() {
		int id=_source.readInt();
		return new MappedIDPair(id,internalCopyID(false, false, id));
	}

	private int internalCopyID(boolean flipNegative, boolean lenient, int id) {
		if(flipNegative&&id<0) {
			id=-id;
		}
		int mapped=_mapping.mappedID(id,lenient);
		if(flipNegative&&id<0) {
			mapped=-mapped;
		}
		_target.writeInt(mapped);
		return mapped;
	}
	
	public void readBegin(byte identifier) {
		_source.readBegin(identifier);
		_target.readBegin(identifier);
	}
	
	public byte readByte() {
		byte value=_source.readByte();
		_target.incrementOffset(1);
		return value;
	}
	
	public void readBytes(byte[] bytes) {
		_source.readBytes(bytes);
		_target.incrementOffset(bytes.length);
	}

	public int readInt() {
		int value=_source.readInt();
		_target.incrementOffset(Const4.INT_LENGTH);
		return value;
	}

	public void writeInt(int value) {
		_source.incrementOffset(Const4.INT_LENGTH);
		_target.writeInt(value);
	}
	
	public void write(LocalObjectContainer file,int address) {
		file.writeBytes(_target,address,0);
	}
	
	public void incrementStringOffset(LatinStringIO sio) {
	    incrementStringOffset(sio, _source);
	    incrementStringOffset(sio, _target);
	}
	
	private void incrementStringOffset(LatinStringIO sio, Buffer buffer) {
	    int length = buffer.readInt();
	    if(length > 0){
	        sio.read(buffer, length);
	    }
	}
	
	public Buffer source() {
		return _source;
	}

	public Buffer target() {
		return _target;
	}
	
	public IDMapping mapping() {
		return _mapping;
	}

	public Transaction systemTrans() {
		return _systemTrans;
	}

	public DefragmentServices context() {
		return _mapping;
	}

	public static void processCopy(DefragmentServices context, int sourceID,SlotCopyHandler command) throws CorruptionException, IOException {
		processCopy(context, sourceID, command, false);
	}

	public static void processCopy(DefragmentServices context, int sourceID,SlotCopyHandler command,boolean registerAddressMapping) throws CorruptionException, IOException {
		Buffer sourceReader = context.sourceBufferByID(sourceID);
		processCopy(context, sourceID, command, registerAddressMapping, sourceReader);
	}

	public static void processCopy(DefragmentServices context, int sourceID,SlotCopyHandler command,boolean registerAddressMapping, Buffer sourceReader) throws CorruptionException, IOException {
		int targetID=context.mappedID(sourceID);
	
		Slot targetSlot = context.allocateTargetSlot(sourceReader.length());
		
		if(registerAddressMapping) {
			int sourceAddress=context.sourceAddressByID(sourceID);
			context.mapIDs(sourceAddress, targetSlot.address(), false);
		}
		
		Buffer targetPointerReader=new Buffer(Const4.POINTER_LENGTH);
		if(Deploy.debug) {
			targetPointerReader.writeBegin(Const4.YAPPOINTER);
		}
		targetPointerReader.writeInt(targetSlot.address());
		targetPointerReader.writeInt(targetSlot.length());
		if(Deploy.debug) {
			targetPointerReader.writeEnd();
		}
		context.targetWriteBytes(targetPointerReader,targetID);
		
		BufferPair readers=new BufferPair(sourceReader,context,context.systemTrans());
		command.processCopy(readers);
		context.targetWriteBytes(readers,targetSlot.address());
	}

	public void writeByte(byte value) {
		_source.incrementOffset(1);
		_target.writeByte(value);
	}

	public long readLong() {
		long value=_source.readLong();
		_target.incrementOffset(Const4.LONG_LENGTH);
		return value;
	}

	public void writeLong(long value) {
		_source.incrementOffset(Const4.LONG_LENGTH);
		_target.writeLong(value);
	}

	public BitMap4 readBitMap(int bitCount) {
		BitMap4 value=_source.readBitMap(bitCount);
		_target.incrementOffset(value.marshalledLength());
		return value;
	}

	public void readEnd() {
		_source.readEnd();
		_target.readEnd();
	}

    public int preparePayloadRead() {
        int newPayLoadOffset = readInt();
        readInt();
        int linkOffSet = offset();
        seek(newPayLoadOffset);
        return linkOffSet;
    }

    public int writeMappedID(int originalID) {
		int mapped=_mapping.mappedID(originalID,false);
		_target.writeInt(mapped);
		return mapped;
	}

	public int length() {
		return _source.length();
	}

}
