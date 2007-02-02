/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public class ClassMarshaller2 extends ClassMarshaller {
    
    protected void readIndex(ObjectContainerBase stream, ClassMetadata clazz, Buffer reader) {
        int indexID = reader.readInt();
        clazz.index().read(stream, indexID);
    }
    
    protected int indexIDForWriting(int indexID){
        return indexID;
    }

}
