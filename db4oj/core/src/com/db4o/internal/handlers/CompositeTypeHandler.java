/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class CompositeTypeHandler implements FieldAwareTypeHandler{
    
    private FieldAwareTypeHandler _fieldAwareHandler;
    
    private TypeHandler4 _customHandler;

    public CompositeTypeHandler(FieldAwareTypeHandler firstTypeHandler, TypeHandler4 secondTypeHandler) {
        _fieldAwareHandler = firstTypeHandler;
        _customHandler = secondTypeHandler;
    }

    public void defragment(DefragmentContext context) {
        _fieldAwareHandler.defragment(context);
        _customHandler.defragment(context);
    }

    public void delete(DeleteContext context) throws Db4oIOException {
        _fieldAwareHandler.delete(context);
        _customHandler.delete(context);
    }

    public Object read(ReadContext context) {
        UnmarshallingContext unmarshallingContext = (UnmarshallingContext) context;
        _fieldAwareHandler.read(context);
        _customHandler.read(context);
        return unmarshallingContext.persistentObject();
    }

    public void write(WriteContext context, Object obj) {
        _fieldAwareHandler.write(context, obj);
        _customHandler.write(context, obj);
    }

    public PreparedComparison prepareComparison(Context context, Object obj) {
        PreparedComparison preparedComparison = _fieldAwareHandler.prepareComparison(context, obj);
        if(preparedComparison != null){
            return preparedComparison;
        }
        return _customHandler.prepareComparison(context, obj);
    }
    
    public void classMetadata(ClassMetadata classMetadata) {
        _fieldAwareHandler.classMetadata(classMetadata);
    }

    public void collectIDs(CollectIdContext context, String fieldName) {
        _fieldAwareHandler.collectIDs(context, fieldName);
    }

    public TypeHandler4 unversionedTemplate() {
        return new CompositeTypeHandler((FieldAwareTypeHandler)unversionedTemplate(_fieldAwareHandler), unversionedTemplate(_customHandler));
    }

    private TypeHandler4 unversionedTemplate(TypeHandler4 typeHandler) {
        if(typeHandler instanceof VersionedTypeHandler){
            return ((VersionedTypeHandler)typeHandler).unversionedTemplate();
        }
        return typeHandler;
    }

    public Object deepClone(Object context) {
        return new CompositeTypeHandler((FieldAwareTypeHandler)deepClone(_fieldAwareHandler, context), deepClone(_customHandler, context));
    }
    
    private TypeHandler4 deepClone(TypeHandler4 typeHandler, Object context) {
        if(typeHandler instanceof DeepClone){
            return (TypeHandler4) ((DeepClone)typeHandler).deepClone(context);
        }
        return typeHandler;
    }

    public void cascadeActivation(ActivationContext4 context) {
        cascadeActivation(_fieldAwareHandler, context);
        cascadeActivation(_customHandler, context);
    }

    private void cascadeActivation(TypeHandler4 typeHandler, ActivationContext4 context) {
        if(typeHandler instanceof FirstClassHandler){
            ((FirstClassHandler)typeHandler).cascadeActivation(context);
        }
    }

    public void collectIDs(QueryingReadContext context) {
        collectIDs(_fieldAwareHandler, context);
        collectIDs(_customHandler, context);
    }
    
    private void collectIDs(TypeHandler4 typeHandler, QueryingReadContext context) {
        if(typeHandler instanceof FirstClassHandler){
            ((FirstClassHandler)typeHandler).collectIDs(context);
        }
    }

    public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
        TypeHandler4 candidateHandler = readCandidateHandler(_fieldAwareHandler, context);
        if(candidateHandler != null){
            return candidateHandler;
        }
        return readCandidateHandler(_customHandler, context);
    }
    
    private TypeHandler4 readCandidateHandler(TypeHandler4 typeHandler, QueryingReadContext context) {
        if(typeHandler instanceof FirstClassHandler){
            return ((FirstClassHandler)typeHandler).readCandidateHandler(context);
        }
        return null;
    }

    public void readVirtualAttributes(ObjectReferenceContext context) {
        _fieldAwareHandler.readVirtualAttributes(context);
    }

    public void addFieldIndices(ObjectIdContext context, Slot oldSlot) {
        _fieldAwareHandler.addFieldIndices(context, oldSlot);
    }

    public void deleteMembers(ObjectIdContext idContext, DeleteContext deleteContext,  boolean isUpdate) {
        _fieldAwareHandler.deleteMembers(idContext, deleteContext, isUpdate);
        _customHandler.delete(deleteContext);
    }

    public boolean seekToField(ObjectHeaderContext context, FieldMetadata field) {
        return _fieldAwareHandler.seekToField(context, field);
    }

}
