/* Copyright (C) 2004 - 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.ix.IndexTransaction;
import com.db4o.reflect.Reflector;


/**
 * @exclude
 */
public abstract class Transaction {
	
    // contains DeleteInfo nodes
    public Tree i_delete;

    private List4 i_dirtyFieldIndexes;
    
    protected final Transaction _parentTransaction;

    protected final StatefulBuffer i_pointerIo;    

    private final ObjectContainerBase _container;
    
    private List4 i_transactionListeners;
    
    // TODO: join _dirtyBTree and _enlistedIndices
    private final Collection4 _participants = new Collection4(); 

    public Transaction(ObjectContainerBase container, Transaction parent) {
        _container = container;
        _parentTransaction = parent;
        i_pointerIo = new StatefulBuffer(this, Const4.POINTER_LENGTH);
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
    
    public abstract void commit();    
    
    protected void freeOnCommit() {
	}
    
    protected void commitParticipants() {
        if (_parentTransaction != null) {
            _parentTransaction.commitParticipants();
        }
        
        Iterator4 iterator = _participants.iterator();
		while (iterator.moveNext()) {
			((TransactionParticipant)iterator.current()).commit(this);
		}
    }
    
    protected void commit4FieldIndexes(){
        if(_parentTransaction != null){
            _parentTransaction.commit4FieldIndexes();
        }
        if (i_dirtyFieldIndexes != null) {
            Iterator4 i = new Iterator4Impl(i_dirtyFieldIndexes);
            while (i.moveNext()) {
                ((IndexTransaction) i.current()).commit();
            }
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
    
	protected boolean isSystemTransaction() {
		return _parentTransaction == null;
	}

    public boolean delete(ObjectReference ref, int id, int cascade) {
        checkSynchronization();
        
        if(ref != null){
	        if(! _container.flagForDelete(ref)){
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
        ClassMetadata yapClass = stream().classMetadataForId(a_yapClassID);
        yapClass.index().add(this, a_id);
    }    
    
    public HardObjectReference getHardReferenceBySignature(final long a_uuid, final byte[] a_signature) {
        checkSynchronization();  
        return stream().getUUIDIndex().getHardObjectReferenceBySignature(this, a_uuid, a_signature);
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

    public abstract void setPointer(int a_id, int a_address, int a_length);
    
    public void slotDelete(int a_id, int a_address, int a_length) {
    }

    public void slotFreeOnCommit(int a_id, int a_address, int a_length) {
    }

    public void slotFreeOnRollback(int a_id, int a_address, int a_length) {
    }

    void slotFreeOnRollbackCommitSetPointer(int a_id, int newAddress, int newLength) {
    }

    void produceUpdateSlotChange(int a_id, int a_address, int a_length) {
    }
    
    public void slotFreePointerOnCommit(int a_id) {
    }
    
    void slotFreePointerOnCommit(int a_id, int a_address, int a_length) {
    }
    
    public void slotFreePointerOnRollback(int a_id) {
    }

    boolean supportsVirtualFields(){
        return true;
    }
    
    public Transaction systemTransaction(){
        if(_parentTransaction != null){
            return _parentTransaction;
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
            i_pointerIo.writeBegin(Const4.YAPPOINTER);
        }
        i_pointerIo.writeInt(a_address);
        i_pointerIo.writeInt(a_length);
        if (Deploy.debug) {
            i_pointerIo.writeEnd();
        }
        if (Debug.xbytes && Deploy.overwrite) {
            i_pointerIo.setID(Const4.IGNORE_ID);
        }
        i_pointerIo.write();
    }
    
    public abstract void writeUpdateDeleteMembers(int id, ClassMetadata clazz, int typeInfo, int cascade);

    public final ObjectContainerBase stream() {
        return _container;
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
    
    public Transaction parentTransaction() {
		return _parentTransaction;
	}

}