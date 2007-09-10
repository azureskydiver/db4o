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
public abstract class BuiltinTypeHandler implements TypeHandler4 {
    
    private final ObjectContainerBase _container;
    
    public BuiltinTypeHandler(ObjectContainerBase container) {
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
    public abstract Comparable4 prepareComparison(Object obj);
    public abstract int compareTo(Object obj);
    
    public abstract void defrag(MarshallerFamily mf, BufferPair readers, boolean redirect);
    
    public ObjectContainerBase container(){
        return _container;
    }
    
    protected Buffer readIndirectedBuffer(ReadContext readContext) {
        UnmarshallingContext context = (UnmarshallingContext) readContext;
        return context.container().bufferByAddress(context.readInt(), context.readInt());
    }
    
}
