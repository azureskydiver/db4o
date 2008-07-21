/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;


/**
 * @exclude
 */
public interface FieldMarshaller {

    void write(Transaction trans, ClassMetadata clazz, ClassAspect aspect, ByteArrayBuffer writer);

    RawFieldSpec readSpec(ObjectContainerBase stream,ByteArrayBuffer reader);
    
    FieldMetadata read(ObjectContainerBase stream, FieldMetadata field, ByteArrayBuffer reader);

    int marshalledLength(ObjectContainerBase stream, ClassAspect aspect);

	void defrag(ClassMetadata classMetadata, ClassAspect aspect, LatinStringIO sio,DefragmentContextImpl context) ;

}
