/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;


/**
 * @exclude
 */
public class ObjectMarshallerSpike extends ObjectMarshaller1 {
    
    public ObjectMarshallerSpike() {
        if(! MarshallingSpike.enabled){
            throw new IllegalStateException();
        }
    }
    
    public StatefulBuffer marshallNew(Transaction trans, ObjectReference ref, int updateDepth){
        
        ObjectHeaderAttributes1 attributes = new ObjectHeaderAttributes1(ref);
        
        StatefulBuffer writer = createWriterForNew(
            trans, 
            ref, 
            updateDepth, 
            attributes.objectLength());
        
        marshall(ref, ref.getObject(), attributes, writer, true);
        
        return writer;
    }
    
    


}
