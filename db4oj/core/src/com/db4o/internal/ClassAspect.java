/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.activation.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public abstract class ClassAspect {
    
    //  position in ClassMetadata i_fields
    protected int              _arrayPosition;
    
    public abstract AspectType aspectType();
    
    public abstract String getName();
    
    public abstract int extendedLength();

    public abstract void cascadeActivation(Transaction trans, Object obj, ActivationDepth depth);
    
    public abstract int linkLength();
    
    public final void incrementOffset(ReadBuffer buffer) {
        buffer.seek(buffer.offset() + linkLength());
    }

    public abstract void defragAspect(DefragmentContext context);

    public abstract void marshall(MarshallingContext context, Object child);

    public abstract void collectIDs(CollectIdContext context);
    
    public void setArrayPosition(int a_index) {
        _arrayPosition = a_index;
    }

    public abstract void instantiate(UnmarshallingContext context);


}
