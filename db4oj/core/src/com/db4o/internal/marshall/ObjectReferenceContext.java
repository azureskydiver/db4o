/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

/**
 * @exclude
 */
import com.db4o.internal.*;
import com.db4o.marshall.*;

public class ObjectReferenceContext extends ObjectHeaderContext implements FieldListInfo, MarshallingInfo, HandlerVersionContext{

    protected final ObjectReference _reference;

    public ObjectReferenceContext(Transaction transaction, ReadBuffer buffer,
        ObjectHeader objectHeader, ObjectReference reference) {
        super(transaction, buffer, objectHeader);
        _reference = reference;
    }

    protected ByteArrayBuffer byteArrayBuffer() {
        return (ByteArrayBuffer)buffer();
    }

    public StatefulBuffer statefulBuffer() {
        StatefulBuffer statefulBuffer = new StatefulBuffer(transaction(), byteArrayBuffer().length());
        statefulBuffer.setID(objectID());
        statefulBuffer.setInstantiationDepth(activationDepth());
        byteArrayBuffer().copyTo(statefulBuffer, 0, 0, byteArrayBuffer().length());
        statefulBuffer.seek(byteArrayBuffer().offset());
        return statefulBuffer;
    }

    public int objectID() {
        return _reference.getID();
    }

    public ClassMetadata classMetadata() {
        return _reference.classMetadata();
    }

    public ObjectReference objectReference() {
        return _reference;
    }

}