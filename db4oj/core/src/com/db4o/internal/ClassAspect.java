/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.activation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public abstract class ClassAspect {
    
    // used for identification when sending in C/S mode 
	protected int              _handle;
    
    private int _disabledFromAspectCountVersion = AspectVersionContextImpl.ALWAYS_ENABLED.aspectCount();
    
    public abstract AspectType aspectType();
    
    public abstract String getName();
    
    public abstract void cascadeActivation(Transaction trans, Object obj, ActivationDepth depth);
    
    public abstract int linkLength();
    
    public final void incrementOffset(ReadBuffer buffer) {
        buffer.seek(buffer.offset() + linkLength());
    }

    public abstract void defragAspect(DefragmentContext context);

    public abstract void marshall(MarshallingContext context, Object child);

    public abstract void collectIDs(CollectIdContext context);
    
    public void setHandle(int handle) {
        _handle = handle;
    }

    public abstract void instantiate(UnmarshallingContext context);

	public abstract void delete(DeleteContextImpl context, boolean isUpdate);
	
	public abstract boolean canBeDisabled();
	
    protected boolean checkEnabled(AspectVersionContext context){
    	if(! enabled(context)){
    		incrementOffset((ReadBuffer)context);
    		return false;
    	}
    	return true;
    }

	
	public void disableFromAspectCountVersion(int aspectCount) {
		if(! canBeDisabled()){
			return;
		}
		if(aspectCount < _disabledFromAspectCountVersion){
			_disabledFromAspectCountVersion = aspectCount;
		}
	}
	
	public boolean enabled(AspectVersionContext context){
		return _disabledFromAspectCountVersion  > context.aspectCount();	
	}

	public abstract void deactivate(Transaction trans, Object obj, ActivationDepth depth);

}
