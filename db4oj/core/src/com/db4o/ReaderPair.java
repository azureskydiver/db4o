/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.inside.marshall.*;

/**
 * @exclude
 */
public class ReaderPair implements SlotReader {
	private YapReader _source;
	private YapReader _target;
	private DefragContext _mapping;
	private Transaction _systemTrans;
	
	public ReaderPair(YapReader source,DefragContext mapping,Transaction systemTrans) {
		_source = source;
		_mapping=mapping;
		_target = new YapReader(source.getLength());
		_source.copyTo(_target, 0, 0, _source.getLength());
		_systemTrans=systemTrans;
	}
	
	public int offset() {
		return _source.offset();
	}

	public void offset(int offset) {
		_source.offset(offset);
		_target.offset(offset);
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

	public int copyID() {
		return copyID(false,false);
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

	public DefragContext context() {
		return _mapping;
	}

	public static void processCopy(DefragContext context, int sourceID,SlotCopyHandler command) throws CorruptionException {
		processCopy(context, sourceID, command, false);
	}

	public static void processCopy(DefragContext context, int sourceID,SlotCopyHandler command,boolean registerAddressMapping) throws CorruptionException {
		YapReader sourceReader=(
				registerAddressMapping 
					? context.sourceWriterByID(sourceID) 
					: context.sourceReaderByID(sourceID));
		int targetID=context.mappedID(sourceID);
	
		int targetLength = sourceReader.getLength();
		int targetAddress=context.allocateTargetSlot(targetLength);
		
		if(registerAddressMapping) {
			int sourceAddress=((YapWriter)sourceReader).getAddress();
			context.mapIDs(sourceAddress, targetAddress);
		}
		
		YapReader targetPointerReader=new YapReader(YapConst.POINTER_LENGTH);
		targetPointerReader.writeInt(targetAddress);
		targetPointerReader.writeInt(targetLength);
		context.targetWriteBytes(targetPointerReader,targetID);
		
		ReaderPair readers=new ReaderPair(sourceReader,context,context.systemTrans());
		command.processCopy(readers);
		context.targetWriteBytes(readers,targetAddress);
	}

	public void append(byte value) {
		_source.incrementOffset(1);
		_target.append(value);
	}

	public long readLong() {
		long value=_source.readLong();
		_target.incrementOffset(YapConst.LONG_LENGTH);
		return value;
	}

	public void writeLong(long value) {
		_source.incrementOffset(YapConst.LONG_LENGTH);
		_target.writeLong(value);
	}

	public BitMap4 readBitMap(int bitCount) {
		BitMap4 value=_source.readBitMap(bitCount);
		_target.incrementOffset(value.marshalledLength());
		return value;
	}

	public void copyBytes(byte[] target, int sourceOffset,int targetOffset, int length) {
		_source.copyBytes(target, sourceOffset, targetOffset, length);
	}

	public void readEnd() {
		_source.readEnd();
		_target.readEnd();
	}

    public int preparePayloadRead() {
        int newPayLoadOffset = readInt();
        readInt();
        int linkOffSet = offset();
        offset(newPayLoadOffset);
        return linkOffSet;
    }
}
