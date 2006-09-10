/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;


/**
 * @exclude
 */
public class ClassMarshaller0 extends ClassMarshaller{

    protected void readIndex(YapStream stream, YapClass clazz, YapReader reader) {
        super.readIndex(stream, clazz, reader);
        
        // FIXME: create old class indexes to BTrees here
        
    }

}
