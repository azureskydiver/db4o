/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;


/**
 * @exclude
 */
public interface FieldMarshaller {

    void write(Transaction trans, YapClass clazz, YapField field, YapReader writer);

    RawFieldSpec readSpec(YapStream stream,YapReader reader);
    
    YapField read(YapStream stream, YapField field, YapReader reader);

    int marshalledLength(YapStream stream, YapField field);

	void defrag(YapClass yapClass, YapField yapField, YapStringIO sio,YapReader source, YapReader target, IDMapping mapping) throws CorruptionException;

}
