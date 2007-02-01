/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.*;


/**
 * @exclude
 */
public interface FieldMarshaller {

    void write(Transaction trans, ClassMetadata clazz, YapField field, Buffer writer);

    RawFieldSpec readSpec(ObjectContainerBase stream,Buffer reader);
    
    YapField read(ObjectContainerBase stream, YapField field, Buffer reader);

    int marshalledLength(ObjectContainerBase stream, YapField field);

	void defrag(ClassMetadata yapClass, YapField yapField, YapStringIO sio,ReaderPair readers) throws CorruptionException;

}
