/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.*;


/**
 * @exclude
 */
public class ClassMarshaller2 extends ClassMarshaller {
    
    protected void readIndex(ObjectContainerBase stream, YapClass clazz, Buffer reader) {
        int indexID = reader.readInt();
        clazz.index().read(stream, indexID);
    }
    
    protected int indexIDForWriting(int indexID){
        return indexID;
    }

}
