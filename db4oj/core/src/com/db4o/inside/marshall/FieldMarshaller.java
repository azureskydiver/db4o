/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;


/**
 * @exclude
 */
public abstract class FieldMarshaller {

    public abstract void write(Transaction trans, YapClass clazz, YapField field, YapReader writer);

    public abstract YapField read(YapStream stream, YapField field, YapReader reader);

    public abstract int marshalledLength(YapStream stream, YapField field);

}
