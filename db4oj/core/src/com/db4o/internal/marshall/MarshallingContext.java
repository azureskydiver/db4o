/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
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
    
    private static final byte MARSHALLER_FAMILY_VERSION = (byte)2;
    
    private static final int NO_INDIRECTION = 3; // and number above 2 
    
    private final Transaction _transaction;
    
    private final ObjectReference _reference;
    
    private int _updateDepth;
    
    private final boolean _isNew;
    
    private final BitMap4 _nullBitMap;
    
    private final MarshallingBuffer _writeBuffer;
    
    private MarshallingBuffer _currentBuffer;
    
    private int _fieldWriteCount;
    
    private Buffer _debugPrepend;
    
    private Object _currentMarshalledObject;
    
    private Object _currentIndexEntry;
    

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
    
    private StatefulBuffer createNewBuffer(int length) {
        Slot slot = new Slot(-1, length);
        if(_transaction instanceof LocalTransaction){
            slot = ((LocalTransaction)_transaction).file().getSlot(length);
            _transaction.slotFreeOnRollback(objectID(), slot);
        }
        _transaction.setPointer(objectID(), slot);
        return createUpdateBuffer( slot.address(), length);
    }

    private StatefulBuffer createUpdateBuffer(int address, int length) {
        StatefulBuffer buffer = new StatefulBuffer(_transaction, length);
        buffer.useSlot(objectID(), address, length);
        buffer.setUpdateDepth(_updateDepth);
        if(( address == 0) && (transaction() instanceof LocalTransaction)){
            ((LocalTransaction)transaction()).file().getSlotForUpdate(buffer);
        }
        return buffer;
    }

    public StatefulBuffer ToWriteBuffer() {
        
        int length = container().blockAlignedBytes(marshalledLength());
        
        StatefulBuffer buffer = isNew() ? createNewBuffer(length) : createUpdateBuffer(0, length);
        
        _writeBuffer.mergeChildren(this, buffer.getAddress(), writeBufferOffset());
        
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
        int length = writeBufferOffset();
        _writeBuffer.checkBlockAlignment(this, null, new IntByRef(length));
        return length + _writeBuffer.marshalledLength() + Const4.BRACKETS_BYTES;
    }
    
    public int requiredLength(MarshallingBuffer buffer, boolean align) {
        if(! align){
            return buffer.length();
        }
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
    
    public void writeLong(long l) {
        preWrite();
        _currentBuffer.writeLong(l);
        postWrite();
    }

    
	private void preWrite() {
        _fieldWriteCount++;
        if(isSecondWriteToField()){
            createChildBuffer(true, true);
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

    private void createChildBuffer(boolean transferLastWrite, boolean storeLengthInLink) {
        MarshallingBuffer childBuffer = _currentBuffer.addChild(false, storeLengthInLink);
        if(transferLastWrite){
            _currentBuffer.transferLastWriteTo(childBuffer, storeLengthInLink);
        }
        _currentBuffer.reserveChildLinkSpace(storeLengthInLink);
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
        
        _currentMarshalledObject = obj;
        _currentIndexEntry = new Integer(id);
    }
    
    public void writeObject(TypeHandler4 handler, Object obj){
        
        MarshallingBuffer tempBuffer = _currentBuffer;
        int tempFieldWriteCount = _fieldWriteCount;
        
        if(obj == null){
            
            // TODO: This should never happen. All handlers should take care
            //       of nulls on a higher level, otherwise primitive wrappers
            //       default to their primitive values.
            
            //       Consider to throw an IllegalArgumentException here to
            //       prevent users from calling with null arguments.
            
            writeNullObject(handler);
            
        } else{
            _fieldWriteCount = 0;
            handler.write(this, obj);
        }
        
        _fieldWriteCount = tempFieldWriteCount;
        _currentBuffer = tempBuffer;
    }
    
    private void writeNullObject(TypeHandler4 handler){
        if( handlerRegistry().isVariableLength(handler)){
            doNotIndirectWrites();
            writeNullLink();
            return;
        }
        
        if (handler instanceof PrimitiveHandler){
            PrimitiveHandler primitiveHandler = (PrimitiveHandler) handler;
            handler.write(this, primitiveHandler.nullRepresentationInUntypedArrays());
            return;
        }
            
        handler.write(this, null);
    }
    
    private void writeNullLink(){
        writeInt(0);
        writeInt(0);
    }
    
    public void writeAny(Object obj){

        if(obj == null){
            writeInt(0);
            return;
        }
        
        ClassMetadata classMetadata = ClassMetadata.forObject(transaction(), obj, true);
        if(classMetadata == null){
            writeInt(0);
            return;
        }
        
        MarshallingBuffer tempBuffer = _currentBuffer;
        int tempFieldWriteCount = _fieldWriteCount;
        createChildBuffer(false, false);
        writeInt(classMetadata.getID());
        
        if(classMetadata.isArray()){

            // TODO: This will force the array to produce another indirection.
            // This indirection is unneccessary, but it is required by the 
            // current old reading format. Try to remove.  
            
            _fieldWriteCount = 0;
            
        }else{
            doNotIndirectWrites();
        }
        classMetadata.write(this, obj);
        _fieldWriteCount = tempFieldWriteCount;
        _currentBuffer = tempBuffer;
    }


    public void addIndexEntry(FieldMetadata fieldMetadata, Object obj) {
        if(! _currentBuffer.hasParent()){
            Object indexEntry = (obj == _currentMarshalledObject) ? _currentIndexEntry : obj; 
            fieldMetadata.addIndexEntry(transaction(), objectID(), indexEntry);
            return;
        }
        _currentBuffer.requestIndexEntry(fieldMetadata);
    }
    
    public ObjectReference reference(){
        return _reference;
    }
    
    public void doNotIndirectWrites(){
        _fieldWriteCount = NO_INDIRECTION;
    }
    
    private HandlerRegistry handlerRegistry(){
        return container().handlers();
    }

    public void createIndirection(TypeHandler4 handler) {
        if(handlerRegistry().isVariableLength(handler)){
            createChildBuffer(false, true);
            doNotIndirectWrites();
        }
    }

}
