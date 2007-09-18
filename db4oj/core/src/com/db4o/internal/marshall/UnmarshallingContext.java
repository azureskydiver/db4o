/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;


/**
 * @exclude
 */
public class UnmarshallingContext extends AbstractReadContext implements FieldListInfo, MarshallingInfo{
    
    private final ObjectReference _reference;
    
    private Object _object;
    
    private ObjectHeader _objectHeader;
    
    private int _addToIDTree;
    
    private boolean _checkIDTree;
    
    public UnmarshallingContext(Transaction transaction, ObjectReference ref, int addToIDTree, boolean checkIDTree) {
        super(transaction);
        _reference = ref;
        _addToIDTree = addToIDTree;
        _checkIDTree = checkIDTree;
    }

    public StatefulBuffer statefulBuffer() {
        StatefulBuffer buffer = new StatefulBuffer(_transaction, _buffer.length());
        buffer.setID(objectID());
        buffer.setInstantiationDepth(activationDepth());
        _buffer.copyTo(buffer, 0, 0, _buffer.length());
        buffer.offset(_buffer.offset());
        return buffer;
    }
    
    public int objectID(){
        return _reference.getID();
    }
    
    public Object read(){
        if(! beginProcessing()){
            return _object;
        }
        
        readBuffer(objectID());
        
        if(_buffer == null){
            endProcessing();
            return _object;
        }
        
        ClassMetadata classMetadata = readObjectHeader(); 
        
        if(classMetadata == null){
            endProcessing();
            return _object;
        }
        
        _reference.classMetadata(classMetadata);

        
        if(_checkIDTree){
            Object objectInCacheFromClassCreation = _transaction.objectForIdFromCache(objectID());
            if(objectInCacheFromClassCreation != null){
                _object = objectInCacheFromClassCreation;
                endProcessing();
                return _object;
            }
        }
        
        if(peekPersisted()){
            _object = classMetadata().instantiateTransient(this);
        }else{
            _object = classMetadata().instantiate(this);
        }
        
        endProcessing();
        return _object;
    }
    
    public Object readFieldValue (int objectID, FieldMetadata field){
        readBuffer(objectID);
        if(_buffer == null){
            return null;
        }
        ClassMetadata classMetadata = readObjectHeader(); 
        if(classMetadata == null){
            return null;
        }
        if(! _objectHeader.objectMarshaller().findOffset(classMetadata, _objectHeader._headerAttributes, _buffer, field)){
            return null;
        }
        return field.read(this);
    }

    private ClassMetadata readObjectHeader() {
        _objectHeader = new ObjectHeader(container(), _buffer);
        ClassMetadata classMetadata = _objectHeader.classMetadata();
        if(classMetadata == null){
            return null;
        }
        return classMetadata;
    }

    private void readBuffer(int id) {
        if (_buffer == null && id > 0) {
            _buffer = container().readReaderByID(_transaction, id); 
        }
    }
    
    public ClassMetadata classMetadata(){
        return _reference.classMetadata();
    }
        
    private boolean beginProcessing() {
        return _reference.beginProcessing();
    }
    
    private void endProcessing() {
        _reference.endProcessing();
    }

    public void setStateClean() {
        _reference.setStateClean();
    }

    public Object persistentObject() {
        return _object;
    }

    public void setObjectWeak(Object obj) {
        _reference.setObjectWeak(container(), obj);
    }
    
    public Object readObject() {
        int id = readInt();
        int depth = _activationDepth - 1;

        if (peekPersisted()) {
            return container().peekPersisted(transaction(), id, depth);
        }

        Object obj = container().getByID2(transaction(), id);

        if (obj instanceof Db4oTypeImpl) {
            depth = ((Db4oTypeImpl)obj).adjustReadDepth(depth);
        }

        // this is OK for primitive YapAnys. They will not be added
        // to the list, since they will not be found in the ID tree.
        container().stillToActivate(transaction(), obj, depth);

        return obj;
    }

    private boolean peekPersisted() {
        return _addToIDTree == Const4.TRANSIENT;
    }
    
    public Object readObject(TypeHandler4 handlerType) {
        TypeHandler4 handler = correctHandlerVersion(handlerType);
        if(! isIndirected(handler)){
            return handler.read(this);
        }
        int payLoadOffset = readInt();
        readInt(); // length - never used
        if(payLoadOffset == 0){
            return null;
        }
        int savedOffset = offset();
        seek(payLoadOffset);
        Object obj = handler.read(this);
        seek(savedOffset);
        return obj;
    }

    public void adjustInstantiationDepth() {
        Config4Class classConfig = classConfig();
        if(classConfig != null){
            _activationDepth = classConfig.adjustActivationDepth(_activationDepth);
        }
    }
    
    public Config4Class classConfig() {
        return classMetadata().config();
    }

    public ObjectReference reference() {
        return _reference;
    }

    public void addToIDTree() {
        if(_addToIDTree == Const4.ADD_TO_ID_TREE){
            _reference.addExistingReferenceToIdTree(transaction());    
        }
    }

    public void persistentObject(Object obj) {
        _object = obj;
    }

    public ObjectHeaderAttributes headerAttributes(){
        return _objectHeader._headerAttributes;
    }

    public boolean isNull(int fieldIndex) {
        return headerAttributes().isNull(fieldIndex);
    }

    public int handlerVersion() {
        return _objectHeader.handlerVersion();
    }
    
}

