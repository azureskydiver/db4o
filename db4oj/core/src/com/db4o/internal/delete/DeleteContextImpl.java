/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.delete;

import com.db4o.*;
import com.db4o.diagnostic.DefragmentRecommendation.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class DeleteContextImpl extends BufferContext implements DeleteContext {
    
    private final ReflectClass _fieldClass;
    
    private final TypeHandler4 _fieldHandler;
    
    private final int _handlerVersion;

    private final Config4Field _fieldConfig;
    
    private int _deleteDepth;
    
	public DeleteContextImpl(ReflectClass fieldClass, TypeHandler4 fieldHandler, int handlerVersion, Config4Field fieldConfig, StatefulBuffer buffer){
		super(buffer.getTransaction(), buffer);
		_fieldHandler = fieldHandler;
		_fieldClass = fieldClass;
		_handlerVersion = handlerVersion;
		_fieldConfig = fieldConfig;
		_deleteDepth = ((StatefulBuffer)_buffer).cascadeDeletes(); 
	}

	public void cascadeDeleteDepth(int depth) {
		_deleteDepth = depth;
	}

	public int cascadeDeleteDepth() {
		return _deleteDepth;
	}
	
    public boolean cascadeDelete() {
        return cascadeDeleteDepth() > 0;
    }

	public void defragmentRecommended() {
        DiagnosticProcessor dp = container()._handlers._diagnosticProcessor;
        if(dp.enabled()){
            dp.defragmentRecommended(DefragmentRecommendationReason.DELETE_EMBEDED);
        }
	}

	public Slot readSlot() {
		return new Slot(_buffer.readInt(), _buffer.readInt());
	}

	public int handlerVersion() {
		return _handlerVersion;
	}
	
	public void delete(){
	    int preservedCascadeDepth = cascadeDeleteDepth();
	    cascadeDeleteDepth(adjustedDepth());
	    
        // correctHandlerVersion(_fieldHandler).delete(DeleteContextImpl.this);

	    
	    SlotFormat.forHandlerVersion(handlerVersion()).doWithSlotIndirection(this, _fieldHandler, new Closure4() {
            public Object run() {
                correctHandlerVersion(_fieldHandler).delete(DeleteContextImpl.this);
                return null;
            }
	    });
        cascadeDeleteDepth(preservedCascadeDepth);
	}
	
	private int adjustedDepth(){
        if(Platform4.isValueType(_fieldClass)){
            return 1;
        }
	    if(_fieldConfig == null){
	        return cascadeDeleteDepth();
	    }
	    if(_fieldConfig.cascadeOnDelete().definiteYes()){
	        return 1;
	    }
	    if(_fieldConfig.cascadeOnDelete().definiteNo()){
	        return 0;
	    }
	    return cascadeDeleteDepth();
	}

}
