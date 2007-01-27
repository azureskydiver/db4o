/* Copyright (C) 2004 - 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.inside.ix.IndexTransaction;
import com.db4o.inside.marshall.ObjectHeader;
import com.db4o.inside.slots.Slot;
import com.db4o.reflect.Reflector;

/**
 * @exclude
 */
public abstract class Transaction {

    protected int             i_address;                                  // only used to pass address to Thread
    
    protected final byte[]          _pointerBuffer = new byte[YapConst.POINTER_LENGTH];

    // contains DeleteInfo nodes
    public Tree          i_delete;  // public for .NET conversion

    private List4           i_dirtyFieldIndexes;
    
    public final YapFile           i_file;

    final Transaction       i_parentTransaction;

    protected final YapWriter i_pointerIo;    

    private final YapStream         i_stream;
    
    private List4           i_transactionListeners;
    
    protected Tree			i_writtenUpdateDeletedMembers;
    
    // TODO: join _dirtyBTree and _enlistedIndices
    private final Collection4 _participants = new Collection4(); 

    public Transaction(YapStream a_stream, Transaction a_parent) {
        i_stream = a_stream;
        i_file = (a_stream instanceof YapFile) ? (YapFile) a_stream : null;
        i_parentTransaction = a_parent;
        i_pointerIo = new YapWriter(this, YapConst.POINTER_LENGTH);
    }

    public void addDirtyFieldIndex(IndexTransaction a_xft) {
        i_dirtyFieldIndexes = new List4(i_dirtyFieldIndexes, a_xft);
    }

	public final void checkSynchronization() {
		if(Debug.checkSychronization){
            stream().i_lock.notify();
        }
	}

    public void addTransactionListener(TransactionListener a_listener) {
        i_transactionListeners = new List4(i_transactionListeners, a_listener);
    }
    
    protected void clearAll() {
        i_dirtyFieldIndexes = null;
        i_transactionListeners = null;
        disposeParticipants();
        _participants.clear();
    }

	private void disposeParticipants() {
		Iterator4 iterator = _participants.iterator();
        while (iterator.moveNext()) {
        	((TransactionParticipant)iterator.current()).dispose(this);
        }
	}

    public void close(boolean a_rollbackOnClose) {
        try {
            if (stream() != null) {
                checkSynchronization();
                stream().releaseSemaphores(this);
            }
        } catch (Exception e) {
            if (Debug.atHome) {
                e.printStackTrace();
            }
        }
        if (a_rollbackOnClose) {
            try {
                rollback();
            } catch (Exception e) {
                if (Debug.atHome) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void commit() {
        synchronized (stream().i_lock) {
            i_file.freeSpaceBeginCommit();
            commitExceptForFreespace();
            i_file.freeSpaceEndCommit();
        }
    }

    private void commitExceptForFreespace(){
        
        if(DTrace.enabled){
            boolean systemTrans = (i_parentTransaction == null);
            DTrace.TRANS_COMMIT.logInfo( "server == " + stream().isServer() + ", systemtrans == " +  systemTrans);
        }
        
        commit2Listeners();
        
        commit3Stream();
        
        commit4FieldIndexes();
        
        commit5Participants();
        
        stream().writeDirty();
        
        commit6WriteChanges();
        
        freeOnCommit();
        
        commit7ClearAll();
    }
    
    protected void freeOnCommit() {
	}

	protected void commit6WriteChanges() {
	}
	
	private void commit7ClearAll(){
        commit7ParentClearAll();
        clearAll();
    }

	private void commit7ParentClearAll() {
		if(i_parentTransaction != null){
            i_parentTransaction.commit7ClearAll();
        }
	}

	private void commit2Listeners(){
        commit2ParentListeners(); 
        commitTransactionListeners();
    }

	private void commit2ParentListeners() {
		if (i_parentTransaction != null) {
            i_parentTransaction.commit2Listeners();
        }
	}
    
    private void commit3Stream(){
        stream().checkNeededUpdates();
        stream().writeDirty();
        stream().classCollection().write(stream().getSystemTransaction());
    }
    
    private void commit4FieldIndexes(){
        if(i_parentTransaction != null){
            i_parentTransaction.commit4FieldIndexes();
        }
        if (i_dirtyFieldIndexes != null) {
            Iterator4 i = new Iterator4Impl(i_dirtyFieldIndexes);
            while (i.moveNext()) {
                ((IndexTransaction) i.current()).commit();
            }
        }
    }
    
    private void commit5Participants() {
        if (i_parentTransaction != null) {
            i_parentTransaction.commit5Participants();
        }
        
        Iterator4 iterator = _participants.iterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).commit(this);
		}
    }
    
    protected void commitTransactionListeners() {
        checkSynchronization();
        if (i_transactionListeners != null) {
            Iterator4 i = new Iterator4Impl(i_transactionListeners);
            while (i.moveNext()) {
                ((TransactionListener) i.current()).preCommit();
            }
            i_transactionListeners = null;
        }
    }   
    
    public abstract boolean isDeleted(int id);

    public boolean delete(YapObject ref, int id, int cascade) {
        checkSynchronization();
        
        if(ref != null){
	        if(! i_stream.flagForDelete(ref)){
	        	return false;
	        }
        }
        
        if(DTrace.enabled){
            DTrace.TRANS_DELETE.log(id);
        }
        
        DeleteInfo info = (DeleteInfo) TreeInt.find(i_delete, id);
        if(info == null){
            info = new DeleteInfo(id, ref, cascade);
            i_delete = Tree.add(i_delete, info);
            return true;
        }
        info._reference = ref;
        if(cascade > info._cascade){
            info._cascade = cascade;
        }
        return true;
    }
    
    public void dontDelete(int a_id) {
        if(DTrace.enabled){
            DTrace.TRANS_DONT_DELETE.log(a_id);
        }
        if(i_delete == null){
        	return;
        }
        i_delete = TreeInt.removeLike((TreeInt)i_delete, a_id);
    }
    
    void dontRemoveFromClassIndex(int a_yapClassID, int a_id) {
        // If objects are deleted and rewritten during a cascade
        // on delete, we dont want them to be gone.        
        checkSynchronization();
        YapClass yapClass = stream().getYapClass(a_yapClassID);
        yapClass.index().add(this, a_id);
    }    
    
    public Object[] objectAndYapObjectBySignature(final long a_uuid, final byte[] a_signature) {
        checkSynchronization();  
        return stream().getUUIDIndex().objectAndYapObjectBySignature(this, a_uuid, a_signature);
    }
    
	public abstract void processDeletes();    
	
    public Reflector reflector(){
    	return stream().reflector();
    }

    public void rollback() {
        synchronized (stream().i_lock) {
            
            rollbackParticipants();
            
            rollbackFieldIndexes();
            
            rollbackSlotChanges();
            
            rollBackTransactionListeners();
            
            clearAll();
        }
    }

	protected void rollbackSlotChanges() {
	}

	private void rollbackFieldIndexes() {
		if (i_dirtyFieldIndexes != null) {
		    Iterator4 i = new Iterator4Impl(i_dirtyFieldIndexes);
		    while (i.moveNext()) {
		        ((IndexTransaction) i.current()).rollback();
		    }
		}
	}
    
    private void rollbackParticipants() {
    	Iterator4 iterator = _participants.iterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).rollback(this);
		}
	}

	protected void rollBackTransactionListeners() {
        checkSynchronization();
        if (i_transactionListeners != null) {
            Iterator4 i = new Iterator4Impl(i_transactionListeners);
            while (i.moveNext()) {
                ((TransactionListener) i.current()).postRollback();
            }
            i_transactionListeners = null;
        }
    }

    void setAddress(int a_address) {
        i_address = a_address;
    }

    public abstract void setPointer(int a_id, int a_address, int a_length);
    
    void slotDelete(int a_id, int a_address, int a_length) {
    }

    public void slotFreeOnCommit(int a_id, int a_address, int a_length) {
    }

    void slotFreeOnRollback(int a_id, int a_address, int a_length) {
    }

    void slotFreeOnRollbackCommitSetPointer(int a_id, int newAddress, int newLength) {
    }

    void slotFreeOnRollbackSetPointer(int a_id, int a_address, int a_length) {
    }
    
    public void slotFreePointerOnCommit(int a_id) {
    }
    
    void slotFreePointerOnCommit(int a_id, int a_address, int a_length) {
    }

    boolean supportsVirtualFields(){
        return true;
    }
    
    public Transaction systemTransaction(){
        if(i_parentTransaction != null){
            return i_parentTransaction;
        }
        return this;
    }

    public String toString() {
        return stream().toString();
    }
    

    public void writePointer(int a_id, int a_address, int a_length) {
        if(DTrace.enabled){
            DTrace.WRITE_POINTER.log(a_id);
            DTrace.WRITE_POINTER.logLength(a_address, a_length);
        }
        checkSynchronization();
        i_pointerIo.useSlot(a_id);
        if (Deploy.debug) {
            i_pointerIo.writeBegin(YapConst.YAPPOINTER);
        }
        i_pointerIo.writeInt(a_address);
        i_pointerIo.writeInt(a_length);
        if (Deploy.debug) {
            i_pointerIo.writeEnd();
        }
        if (Debug.xbytes && Deploy.overwrite) {
            i_pointerIo.setID(YapConst.IGNORE_ID);
        }
        i_pointerIo.write();
    }
    
    public abstract void writeUpdateDeleteMembers(int id, YapClass clazz, int typeInfo, int cascade);

    public final YapStream stream() {
        return i_stream;
    }

	public void enlist(TransactionParticipant participant) {
		if (null == participant) {
			throw new ArgumentNullException("participant");
		}
		checkSynchronization();	
		if (!_participants.containsByIdentity(participant)) {
			_participants.add(participant);
		}
	}
    
    public static Transaction readInterruptedTransaction(YapFile file, YapReader reader) {
        int transactionID1 = reader.readInt();
        int transactionID2 = reader.readInt();
        if( (transactionID1 > 0)  &&  (transactionID1 == transactionID2)){
            Transaction transaction = file.newTransaction(null);
            transaction.setAddress(transactionID1);
            return transaction;
        }
        return null;
    }

	public Transaction parentTransaction() {
		return i_parentTransaction;
	}

}