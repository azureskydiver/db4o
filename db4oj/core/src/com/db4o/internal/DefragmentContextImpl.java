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
public final class DefragmentContextImpl implements ReadWriteBuffer, DefragmentContext {
	private BufferImpl _source;
	private BufferImpl _target;
	private DefragmentServices _services;
	private int _handlerVersion;
	
	public DefragmentContextImpl(BufferImpl source, DefragmentContextImpl context) {
		this(source, context._services);
	}

	public DefragmentContextImpl(BufferImpl source,DefragmentServices services) {
		_source = source;
		_services=services;
		_target = new BufferImpl(length());
		_source.copyTo(_target, 0, 0, length());
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
	
	public int copySlotlessID() {
	    return copyUnindexedId(false);
	}

	public int copyUnindexedID() {
	    return copyUnindexedId(true);
	}
	
	private int copyUnindexedId(boolean doRegister){
        int orig=_source.readInt();
        int mapped=-1;
        try {
            mapped=_services.mappedID(orig);
        } catch (MappingNotFoundException exc) {
            mapped=_services.allocateTargetSlot(Const4.POINTER_LENGTH).address();
            _services.mapIDs(orig,mapped, false);
            if(doRegister){
                _services.registerUnindexed(orig);
            }
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

	public int copyIDReturnOriginalID() {
		int id=_source.readInt();
		internalCopyID(false, false, id);
		return id;
	}

	private int internalCopyID(boolean flipNegative, boolean lenient, int id) {
		if(flipNegative&&id<0) {
			id=-id;
		}
		int mapped=_services.mappedID(id,lenient);
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
	
	private void incrementStringOffset(LatinStringIO sio, BufferImpl buffer) {
	    int length = buffer.readInt();
	    if(length > 0){
	        sio.read(buffer, length);
	    }
	}
	
	public BufferImpl sourceBuffer() {
		return _source;
	}

	public BufferImpl targetBuffer() {
		return _target;
	}
	
	public IDMapping mapping() {
		return _services;
	}

	public Transaction systemTrans() {
		return transaction();
	}

	public DefragmentServices services() {
		return _services;
	}

	public static void processCopy(DefragmentServices services, int sourceID,SlotCopyHandler command) throws CorruptionException, IOException {
		processCopy(services, sourceID, command, false);
	}

	public static void processCopy(DefragmentServices context, int sourceID,SlotCopyHandler command,boolean registerAddressMapping) throws CorruptionException, IOException {
		BufferImpl sourceReader = context.sourceBufferByID(sourceID);
		processCopy(context, sourceID, command, registerAddressMapping, sourceReader);
	}

	public static void processCopy(DefragmentServices services, int sourceID,SlotCopyHandler command,boolean registerAddressMapping, BufferImpl sourceReader) throws CorruptionException, IOException {
		int targetID=services.mappedID(sourceID);
	
		Slot targetSlot = services.allocateTargetSlot(sourceReader.length());
		
		if(registerAddressMapping) {
			int sourceAddress=services.sourceAddressByID(sourceID);
			services.mapIDs(sourceAddress, targetSlot.address(), false);
		}
		
		BufferImpl targetPointerReader=new BufferImpl(Const4.POINTER_LENGTH);
		if(Deploy.debug) {
			targetPointerReader.writeBegin(Const4.YAPPOINTER);
		}
		targetPointerReader.writeInt(targetSlot.address());
		targetPointerReader.writeInt(targetSlot.length());
		if(Deploy.debug) {
			targetPointerReader.writeEnd();
		}
		services.targetWriteBytes(targetPointerReader,targetID);
		
		DefragmentContextImpl context=new DefragmentContextImpl(sourceReader,services);
		command.processCopy(context);
		services.targetWriteBytes(context,targetSlot.address());
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

    public int writeMappedID(int originalID) {
		int mapped=_services.mappedID(originalID,false);
		_target.writeInt(mapped);
		return mapped;
	}

	public int length() {
		return _source.length();
	}
	
	public Transaction transaction() {
		return services().systemTrans();
	}
	
	private ObjectContainerBase container() {
	    return transaction().container();
	}

	public TypeHandler4 typeHandlerForId(int id) {
		return container().typeHandlerForId(id);
	}
	
	private int handlerVersion(){
		return _handlerVersion;
	}

	public boolean isLegacyHandlerVersion() {
		return handlerVersion() == 0;
	}

	public int mappedID(int origID) {
		return mapping().mappedID(origID);
	}

	public ObjectContainer objectContainer() {
		return (ObjectContainer) container();
	}

	public void handlerVersion(int version) {
		_handlerVersion = version;
	}

	public TypeHandler4 correctHandlerVersion(TypeHandler4 handler) {
	    return container().handlers().correctHandlerVersion(handler, handlerVersion());
	}

	public Slot allocateTargetSlot(int length) {
		return _services.allocateTargetSlot(length);
	}

	public Slot allocateMappedTargetSlot(int sourceAddress, int length) {
		Slot slot = allocateTargetSlot(length);
		_services.mapIDs(sourceAddress, slot.address(), false);
		return slot;
	}

	public int copySlotToNewMapped(int sourceAddress, int length) throws IOException {
    	Slot slot = allocateMappedTargetSlot(sourceAddress, length);
    	BufferImpl sourceBuffer = sourceBufferByAddress(sourceAddress, length);
    	targetWriteBytes(slot.address(), sourceBuffer);
		return slot.address();
	}

	public void targetWriteBytes(int address, BufferImpl buffer) {
		_services.targetWriteBytes(buffer, address);
	}

	public BufferImpl sourceBufferByAddress(int sourceAddress, int length) throws IOException {
		BufferImpl sourceBuffer = _services.sourceBufferByAddress(sourceAddress, length);
		return sourceBuffer;
	}

	public BufferImpl sourceBufferById(int sourceId) throws IOException {
		BufferImpl sourceBuffer = _services.sourceBufferByID(sourceId);
		return sourceBuffer;
	}

	public void writeToTarget(int address) {
		_services.targetWriteBytes(this, address);
	}

    public void writeBytes(byte[] bytes) {
        _target.writeBytes(bytes);
        _source.incrementOffset(bytes.length);
    }
}
