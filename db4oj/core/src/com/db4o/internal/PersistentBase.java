/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 * 
 */
public abstract class PersistentBase implements Persistent, LinkLengthAware {

    protected int _id; // UID and address of pointer to the object in our file

    protected int _state = 2; // DIRTY and ACTIVE

    public final boolean beginProcessing() {
        if (bitIsTrue(Const4.PROCESSING)) {
            return false;
        }
        bitTrue(Const4.PROCESSING);
        return true;
    }

    final void bitFalse(int bitPos) {
        _state &= ~(1 << bitPos);
    }
    
    final boolean bitIsFalse(int bitPos) {
        return (_state | (1 << bitPos)) != _state;
    }

    final boolean bitIsTrue(int bitPos) {
        return (_state | (1 << bitPos)) == _state;
    }

    final void bitTrue(int bitPos) {
        _state |= (1 << bitPos);
    }

    void cacheDirty(Collection4 col) {
        if (!bitIsTrue(Const4.CACHED_DIRTY)) {
            bitTrue(Const4.CACHED_DIRTY);
            col.add(this);
        }
    }

    public void endProcessing() {
        bitFalse(Const4.PROCESSING);
    }
    
    public void free(LocalTransaction trans){
    	trans.localContainer().idSystem().notifySlotDeleted(trans.systemTransaction(), getID(), slotChangeFactory());
    }

    public int getID() {
        return _id;
    }

    public final boolean isActive() {
        return bitIsTrue(Const4.ACTIVE);
    }

    public boolean isDirty() {
        return bitIsTrue(Const4.ACTIVE) && (!bitIsTrue(Const4.CLEAN));
    }
    
    public final boolean isNew(){
        return getID() == 0;
    }

    public final int linkLength() {
        return Const4.ID_LENGTH;
    }

    final void notCachedDirty() {
        bitFalse(Const4.CACHED_DIRTY);
    }

    public void read(Transaction trans) {
		if (!beginProcessing()) {
			return;
		}
		try {
			ByteArrayBuffer reader = produceReadBuffer(trans); 
			
			if (Deploy.debug) {
				reader.readBegin(getIdentifier());
			}
			readThis(trans, reader);
			setStateOnRead(reader);
		} finally {
			endProcessing();
		}
	}
    
    protected ByteArrayBuffer produceReadBuffer(Transaction trans){
    	return readBufferById(trans);
    }
    
    protected ByteArrayBuffer readBufferById(Transaction trans){
    	return trans.container().readReaderByID(trans, getID());
    }

    
    public void setID(int a_id) {
    	if(DTrace.enabled){
    		DTrace.PERSISTENTBASE_SET_ID.log(a_id);
    	}
        _id = a_id;
    }

    public final void setStateClean() {
        bitTrue(Const4.ACTIVE);
        bitTrue(Const4.CLEAN);
    }

    public final void setStateDeactivated() {
        bitFalse(Const4.ACTIVE);
    }

    public void setStateDirty() {
        bitTrue(Const4.ACTIVE);
        bitFalse(Const4.CLEAN);
    }

    void setStateOnRead(ByteArrayBuffer reader) {
        if (Deploy.debug) {
            reader.readEnd();
        }
        if (bitIsTrue(Const4.CACHED_DIRTY)) {
            setStateDirty();
        } else {
            setStateClean();
        }
    }

    public final void write(Transaction trans) {
    	
        
        if (! writeObjectBegin()) {
            return;
        }
        try {
	            
	        LocalObjectContainer container = (LocalObjectContainer)trans.container();
	        
	        if(DTrace.enabled){
	            DTrace.PERSISTENT_OWN_LENGTH.log(getID());
	        }
	        
	        int length = ownLength();
	        length = container.blockAlignedBytes(length);
	        
	        Slot slot = container.allocateSlot(length);
	        
	        if(isNew()){
	            setID(container.idSystem().newId(trans, SlotChangeFactory.SYSTEM_OBJECTS));
                container.idSystem().notifySlotCreated(trans, _id, slot, slotChangeFactory());
	        }else{
	            container.idSystem().notifySlotUpdated(trans, _id, slot, slotChangeFactory());
	        }
	        
	        ByteArrayBuffer writer = produceWriteBuffer(trans, length);
	        
	        writeToFile(trans, writer, slot);
        }finally{
        	endProcessing();
        }

    }

	protected ByteArrayBuffer produceWriteBuffer(Transaction trans, int length) {
		return newWriteBuffer(length);
	}
	
	protected ByteArrayBuffer newWriteBuffer(int length) {
		return new ByteArrayBuffer(length);
	}
	
    
    public boolean isFreespaceComponent(){
        return false;
    }
    
	private final void writeToFile(Transaction trans, ByteArrayBuffer writer, Slot slot) {
		
        if(DTrace.enabled){
        	DTrace.PERSISTENTBASE_WRITE.log(getID());
        }
		
		LocalObjectContainer container = (LocalObjectContainer)trans.container();
		
		if (Deploy.debug) {
		    writer.writeBegin(getIdentifier());
		}

		writeThis(trans, writer);

		if (Deploy.debug) {
		    writer.writeEnd();
		}
		
		container.writeEncrypt(writer, slot.address(), 0);

		if (isActive()) {
		    setStateClean();
		}
	}

    public boolean writeObjectBegin() {
        if (isDirty()) {
            return beginProcessing();
        }
        return false;
    }

    public void writeOwnID(Transaction trans, ByteArrayBuffer writer) {
        write(trans);
        writer.writeInt(getID());
    }
    
    public int hashCode() {
    	if(isNew()){
    		throw new IllegalStateException();
    	}
    	return getID();
    }
    
    public SlotChangeFactory slotChangeFactory(){
    	return SlotChangeFactory.SYSTEM_OBJECTS;
    }
    
}
