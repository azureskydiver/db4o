/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class MarshallingContext implements FieldListInfo, WriteContext {
    
    private static final int HEADER_LENGTH = Const4.LEADING_LENGTH 
            + Const4.ID_LENGTH  // YapClass ID
            + 1 // Marshaller Version
            + Const4.INT_LENGTH; // number of fields
    
    private static final byte MARSHALLER_FAMILY_VERSION = (byte)3;
    
    private final Transaction _transaction;
    
    private final ObjectReference _reference;
    
    private int _updateDepth;
    
    private final boolean _isNew;
    
    private final BitMap4 _nullBitMap;
    
    private final MarshallingBuffer _writeBuffer;
    
    private MarshallingBuffer _currentBuffer;
    
    private int _fieldWriteCount;
    
    private Buffer _debugPrepend;
    

    public MarshallingContext(Transaction trans, ObjectReference ref, int updateDepth, boolean isNew) {
        _transaction = trans;
        _reference = ref;
        _nullBitMap = new BitMap4(fieldCount());
        _updateDepth = classMetadata().adjustUpdateDepth(trans, updateDepth);
        _isNew = isNew;
        _writeBuffer = new MarshallingBuffer();
        _currentBuffer = _writeBuffer;
    }

    private int fieldCount() {
        return classMetadata().fieldCount();
    }

    public ClassMetadata classMetadata() {
        return _reference.classMetadata();
    }

    public boolean isNew() {
        return _isNew;
    }

    public boolean isNull(int fieldIndex) {
        // TODO Auto-generated method stub
        return false;
    }

    public void isNull(int fieldIndex, boolean flag) {
        _nullBitMap.set(fieldIndex, flag);
    }

    public Transaction transaction() {
        return _transaction;
    }
    
    private StatefulBuffer createNewBuffer() {
        Slot slot = new Slot(-1, marshalledLength());
        if(_transaction instanceof LocalTransaction){
            slot = ((LocalTransaction)_transaction).file().getSlot(marshalledLength());
            _transaction.slotFreeOnRollback(objectID(), slot);
        }
        _transaction.setPointer(objectID(), slot);
        return createUpdateBuffer( slot.address());
    }

    private StatefulBuffer createUpdateBuffer(int address) {
        int length = _transaction.container().blockAlignedBytes(marshalledLength());
        StatefulBuffer buffer = new StatefulBuffer(_transaction, length);
        buffer.useSlot(objectID(), address, length);
        buffer.setUpdateDepth(_updateDepth);
        return buffer;
    }

    public StatefulBuffer ToWriteBuffer() {
        
        _writeBuffer.mergeChildren(writeBufferOffset());
        
        StatefulBuffer buffer = isNew() ? createNewBuffer() : createUpdateBuffer(0);
        
        if (Deploy.debug) {
            buffer.writeBegin(Const4.YAPOBJECT);
        }
        
        writeObjectClassID(buffer, classMetadata().getID());
        buffer.writeByte(MARSHALLER_FAMILY_VERSION);
        buffer.writeInt(fieldCount());
        buffer.writeBitMap(_nullBitMap);
        
        _writeBuffer.transferContentTo(buffer);
        if (Deploy.debug) {
            buffer.writeEnd();
            buffer.debugCheckBytes();
        }
        return buffer;
    }
    
    private int writeBufferOffset(){
        return HEADER_LENGTH + _nullBitMap.marshalledLength();
    }

    private int marshalledLength() {
        return HEADER_LENGTH + nullBitMapLength() + requiredLength(_writeBuffer);
    }
    
    private int nullBitMapLength(){
        return Const4.INT_LENGTH + _nullBitMap.marshalledLength();
    }

    private int requiredLength(MarshallingBuffer buffer) {
        return container().blockAlignedBytes(buffer.length());
    }
    
    private void writeObjectClassID(Buffer reader, int id) {
        reader.writeInt(-id);
    }

    public Object getObject() {
        return _reference.getObject();
    }

    public Config4Class classConfiguration() {
        return classMetadata().config();
    }

    public int updateDepth() {
        return _updateDepth;
    }

    public void updateDepth(int depth) {
        _updateDepth = depth;
    }

    public int objectID() {
        return _reference.getID();
    }

    public Object currentIndexEntry() {
        // TODO Auto-generated method stub
        return null;
    }

    public ObjectContainerBase container() {
        return transaction().container();
    }

    public ObjectContainer objectContainer() {
        return transaction().objectContainer();
    }

	public void writeByte(byte b) {
	    preWrite();
	    _currentBuffer.writeByte(b);
	    postWrite();
	}
	
	public void writeBytes(byte[] bytes) {
	    preWrite();
	    _currentBuffer.writeBytes(bytes);
	    postWrite();
	}

    public void writeInt(int i) {
        preWrite();
        _currentBuffer.writeInt(i);
        postWrite();
    }
    
	private void preWrite() {
        _fieldWriteCount++;
        if(isSecondWriteToField()){
            createChildBuffer();
        }
        if(Deploy.debug){
            if(_debugPrepend != null){
                for (int i = 0; i < _debugPrepend.offset(); i++) {
                    _currentBuffer.writeByte(_debugPrepend._buffer[i]);
                }
            }
        }
    }
	
	private void postWrite(){
	    if(Deploy.debug){
	        if(_debugPrepend != null){
	            _currentBuffer.debugDecrementLastOffset(_debugPrepend.offset());
	            _debugPrepend = null;
	        }
	    }
	}

    private void createChildBuffer() {
        MarshallingBuffer childBuffer = _currentBuffer.addChild(false);
        _currentBuffer.transferLastWriteTo(childBuffer);
        _currentBuffer.reserveChildLinkSpace();
        _currentBuffer = childBuffer;
    }

    private boolean isSecondWriteToField() {
        return _fieldWriteCount == 2;
    }
    
    public void nextField(){
        _fieldWriteCount = 0;
        _currentBuffer = _writeBuffer;
    }

    public void fieldCount(int fieldCount) {
        _writeBuffer.writeInt(fieldCount);
    }

    public void debugPrependNextWrite(Buffer prepend) {
        if(Deploy.debug){
            _debugPrepend = prepend;
        }
    }

    public void debugWriteEnd(byte b) {
        _currentBuffer.writeByte(b);
    }

    public void writeObject(Object obj) {
        
        // TODO: check if updatedepth is right here. It could maybe be updateDepth -1.
        int id = container().setInternal(transaction(), obj, _updateDepth, true);
        
        writeInt(id);
    }

}
