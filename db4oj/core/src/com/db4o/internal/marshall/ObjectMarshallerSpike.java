/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;


/**
 * @exclude
 */
public class ObjectMarshallerSpike extends ObjectMarshaller1 {
    
    public ObjectMarshallerSpike() {
        if(! MarshallingSpike.enabled){
            throw new IllegalStateException();
        }
    }

}
