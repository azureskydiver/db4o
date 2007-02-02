/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.*;


/**
 * @exclude
 */
public interface FieldMarshaller {

    void write(Transaction trans, ClassMetadata clazz, FieldMetadata field, Buffer writer);

    RawFieldSpec readSpec(ObjectContainerBase stream,Buffer reader);
    
    FieldMetadata read(ObjectContainerBase stream, FieldMetadata field, Buffer reader);

    int marshalledLength(ObjectContainerBase stream, FieldMetadata field);

	void defrag(ClassMetadata yapClass, FieldMetadata yapField, LatinStringIO sio,ReaderPair readers) throws CorruptionException;

}
