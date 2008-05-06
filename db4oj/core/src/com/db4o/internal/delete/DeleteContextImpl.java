/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.delete;

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
    
    private final int _handlerVersion;

    private final Config4Field _fieldConfig;
    
	public DeleteContextImpl(ReflectClass fieldClass, int handlerVersion, Config4Field fieldConfig, StatefulBuffer buffer){
		super(buffer.getTransaction(), buffer);
		_fieldClass = fieldClass;
		_handlerVersion = handlerVersion;
		_fieldConfig = fieldConfig;
		 
	}

	public void cascadeDeleteDepth(int depth) {
	    ((StatefulBuffer)_buffer).setCascadeDeletes(depth);
	}

	public int cascadeDeleteDepth() {
	    return ((StatefulBuffer)_buffer).cascadeDeletes();
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
	
	public void delete(TypeHandler4 handler){
        final TypeHandler4 fieldHandler = correctHandlerVersion(handler);
	    int preservedCascadeDepth = cascadeDeleteDepth();
	    cascadeDeleteDepth(adjustedDepth());
	    final SlotFormat slotFormat = SlotFormat.forHandlerVersion(handlerVersion());
        slotFormat.doWithSlotIndirection(this, fieldHandler, new Closure4() {
            public Object run() {
                if(slotFormat.handleAsObject(fieldHandler)){
                    deleteObject();
                }else{
                    fieldHandler.delete(DeleteContextImpl.this);    
                }
                return null;
            }
	    });
        cascadeDeleteDepth(preservedCascadeDepth);

	}

    public void deleteObject() {
        int id = _buffer.readInt();
        if(cascadeDelete()){
            container().deleteByID(transaction(), id, cascadeDeleteDepth());
        }
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
