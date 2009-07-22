/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.internal.metadata;

import com.db4o.internal.*;
import com.db4o.internal.marshall.*;

/**
 * @exclude
 */
public abstract class TraverseAspectCommand {
    
    private boolean _cancelled=false;
    
    public int aspectCount(ClassMetadata classMetadata, ByteArrayBuffer reader) {
        return classMetadata.readAspectCount(reader);
    }

    public boolean cancelled() {
        return _cancelled;
    }
    
    protected void cancel() {
        _cancelled=true;
    }
    
    public boolean accept(ClassAspect aspect){
        return true;
    }
    
    public void processAspectOnMissingClass(MarshallingInfo context, FieldListInfo fieldListInfo, ClassAspect aspect,int currentSlot){
		if(fieldListInfo.isNull(currentSlot)){
			return;
		}
    	aspect.incrementOffset(context.buffer());
    }
 
    public abstract void processAspect(ClassAspect aspect,int currentSlot, boolean isNull, ClassMetadata containingClass);
}