/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.slots.*;

/**
 * @exclude
 * 
 * @renameto com.db4o.inside.PersistentBase
 */
public abstract class YapMeta {

	/**
	 * @moveto new com.db4o.inside.Persistent interface
	 * all four of the following abstract methods  
	 */
	public abstract byte getIdentifier();
	
	public abstract int ownLength();
	
	public abstract void readThis(Transaction trans, Buffer reader);
	
	public abstract void writeThis(Transaction trans, Buffer writer);

    
    protected int i_id; // UID and address of pointer to the object in our file

    protected int i_state = 2; // DIRTY and ACTIVE

    final boolean beginProcessing() {
        if (bitIsTrue(YapConst.PROCESSING)) {
            return false;
        }
        bitTrue(YapConst.PROCESSING);
        return true;
    }

    final void bitFalse(int bitPos) {
        i_state &= ~(1 << bitPos);
    }
    
    final boolean bitIsFalse(int bitPos) {
        return (i_state | (1 << bitPos)) != i_state;
    }

    final boolean bitIsTrue(int bitPos) {
        return (i_state | (1 << bitPos)) == i_state;
    }

    final void bitTrue(int bitPos) {
        i_state |= (1 << bitPos);
    }

    void cacheDirty(Collection4 col) {
        if (!bitIsTrue(YapConst.CACHED_DIRTY)) {
            bitTrue(YapConst.CACHED_DIRTY);
            col.add(this);
        }
    }

    public void endProcessing() {
        bitFalse(YapConst.PROCESSING);
    }

    public int getID() {
        return i_id;
    }

    public final boolean isActive() {
        return bitIsTrue(YapConst.ACTIVE);
    }

    public boolean isDirty() {
        return bitIsTrue(YapConst.ACTIVE) && (!bitIsTrue(YapConst.CLEAN));
    }
    
    public final boolean isNew(){
        return i_id == 0;
    }

    public int linkLength() {
        return YapConst.ID_LENGTH;
    }

    final void notCachedDirty() {
        bitFalse(YapConst.CACHED_DIRTY);
    }

    public void read(Transaction trans) {
        try {
            if (beginProcessing()) {
                Buffer reader = trans.stream().readReaderByID(trans, getID());
                if (reader != null) {
                    if (Deploy.debug) {
                        reader.readBegin(getIdentifier());
                    }
                    readThis(trans, reader);
                    setStateOnRead(reader);
                }
                endProcessing();
            }
        } catch (Throwable t) {
            if (Debug.atHome) {
                t.printStackTrace();
            }
        }
    }
    
    public void setID(int a_id) {
    	if(DTrace.enabled){
    		DTrace.YAPMETA_SET_ID.log(a_id);
    	}
        i_id = a_id;
    }

    public final void setStateClean() {
        bitTrue(YapConst.ACTIVE);
        bitTrue(YapConst.CLEAN);
    }

    public final void setStateDeactivated() {
        bitFalse(YapConst.ACTIVE);
    }

    public void setStateDirty() {
        bitTrue(YapConst.ACTIVE);
        bitFalse(YapConst.CLEAN);
    }

    void setStateOnRead(Buffer reader) {
        if (Deploy.debug) {
            reader.readEnd();
        }
        if (bitIsTrue(YapConst.CACHED_DIRTY)) {
            setStateDirty();
        } else {
            setStateClean();
        }
    }

    public final void write(Transaction trans) {
        
        if (! writeObjectBegin()) {
            return;
        }
        
        if(DTrace.enabled){
        	DTrace.YAPMETA_WRITE.log(getID());
        }
            
        YapFile stream = (YapFile)trans.stream();
        
        int address = 0;
        int length = ownLength();
        
        Buffer writer = new Buffer(length);
        
        if(isNew()){
            Pointer4 ptr = stream.newSlot(trans, length);
            setID(ptr._id);
            address = ptr._address;
            
            // FIXME: Free everything on rollback here ?
        }else{
            address = stream.getSlot(length);
            trans.slotFreeOnRollbackCommitSetPointer(i_id, address, length);
        }
        
        if (Deploy.debug) {
            writer.writeBegin(getIdentifier());
        }

        writeThis(trans, writer);

        if (Deploy.debug) {
            writer.writeEnd();
        }

        writer.writeEncrypt(stream, address, 0);

        if (isActive()) {
            setStateClean();
        }
        endProcessing();

    }

    public boolean writeObjectBegin() {
        if (isDirty()) {
            return beginProcessing();
        }
        return false;
    }

    public void writeOwnID(Transaction trans, Buffer writer) {
        write(trans);
        writer.writeInt(getID());
    }


}
