/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public abstract class PersistentBase extends Identifiable implements Persistent, LinkLengthAware {
	
	
	public PersistentBase(){
		
	}

    void cacheDirty(Collection4 col) {
        if (!bitIsTrue(Const4.CACHED_DIRTY)) {
            bitTrue(Const4.CACHED_DIRTY);
            col.add(this);
        }
    }

    public void free(LocalTransaction trans){
    	trans.systemTransaction().idSystem().notifySlotDeleted(getID(), slotChangeFactory());
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
    	return trans.container().readBufferById(trans, getID());
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

    public void write(Transaction trans) {
        if (! writeObjectBegin()) {
            return;
        }
        try {
	            
	        LocalObjectContainer container = (LocalObjectContainer)trans.container();
	        
	        if(DTrace.enabled){
	            DTrace.PERSISTENT_OWN_LENGTH.log(getID());
	        }
	        
	        int length = ownLength();
	        length = container.blockConverter().blockAlignedBytes(length);
	        
	        Slot slot = container.allocateSlot(length);
	        
	        if(isNew()){
	            setID(trans.idSystem().newId(slotChangeFactory()));
                trans.idSystem().notifySlotCreated(_id, slot, slotChangeFactory());
	        }else{
	            trans.idSystem().notifySlotUpdated(_id, slot, slotChangeFactory());
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
    
    public SlotChangeFactory slotChangeFactory(){
    	return SlotChangeFactory.SYSTEM_OBJECTS;
    }
    
}
