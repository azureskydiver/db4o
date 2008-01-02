/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;



/**
 * Common base class for StringHandler and ArrayHandler:
 * The common pattern for both is that a slot  is one indirection in the database file to this.
 * 
 * @exclude
 */
public abstract class VariableLengthTypeHandler implements TypeHandler4 {
    
    private final ObjectContainerBase _container;
    
    public VariableLengthTypeHandler(ObjectContainerBase container) {
        _container = container;
    }
    
    public final int linkLength(){
        
        // TODO:  Now that array and string are embedded into their parent
        //        object from marshaller family 1 on, the length part is no
        //        longer needed. To stay compatible with marshaller family 0
        //        it was considered a bad idea to change this value.
        
        return Const4.INT_LENGTH + Const4.ID_LENGTH;
    }
    
    // redundant, only added to make Sun JDK 1.2's java happy :(
    
    public abstract void defragment(DefragmentContext context);
    
    public ObjectContainerBase container(){
        return _container;
    }
    
    protected BufferImpl readIndirectedBuffer(ReadContext readContext) {
        InternalReadContext context = (InternalReadContext) readContext;
        int address = context.readInt();
        int length = context.readInt();
        if(address == 0){
            return null;
        }
        return context.container().bufferByAddress(address, length);
    }
    
}
