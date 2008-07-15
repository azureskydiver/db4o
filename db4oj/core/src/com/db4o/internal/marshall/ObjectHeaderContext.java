/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class ObjectHeaderContext extends AbstractReadContext implements MarshallingInfo, HandlerVersionContext{
    
    protected ObjectHeader _objectHeader;
    
    private int _currentSlot;
    
    public ObjectHeaderContext(Transaction transaction, ReadBuffer buffer, ObjectHeader objectHeader) {
        super(transaction, buffer);
        _objectHeader = objectHeader;
    }
    
    public final ObjectHeaderAttributes headerAttributes(){
        return _objectHeader._headerAttributes;
    }

    public final boolean isNull(int fieldIndex) {
        return headerAttributes().isNull(fieldIndex);
    }

    public final int handlerVersion() {
        return _objectHeader.handlerVersion();
    }
    
    public void beginSlot() {
        _currentSlot++;
    }

    public int currentSlot() {
        return _currentSlot;
    }
    
    public ContextState saveState(){
        return new ContextState(offset(), _currentSlot);
    }
    
    public void restoreState(ContextState state){
        seek(state._offset);
        _currentSlot = state._currentSlot;
    }

	public Object readFieldValue(ClassMetadata classMetadata, FieldMetadata field) {
		if(! classMetadata.seekToField(this, field)){
	        return null;
	    }
	   	return field.read(this);
	}

	public ClassMetadata classMetadata(){
	    return _objectHeader.classMetadata(); 
	}

}
