/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.*;


/**
 * @exclude
 */
public class ClassMarshaller1 extends ClassMarshaller {
    

    protected void readIndex(YapStream stream, YapClass clazz, Buffer reader) {
        int indexID = reader.readInt();
        clazz.index().read(stream, - indexID);
    }
    
    protected int indexIDForWriting(int indexID){
        return - indexID;
    }


}
