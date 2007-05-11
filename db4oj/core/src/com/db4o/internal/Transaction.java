/* Copyright (C) 2004 - 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.ix.IndexTransaction;
import com.db4o.internal.slots.*;
import com.db4o.reflect.Reflector;


/**
 * @exclude
 */
public abstract class Transaction {
	
    // contains DeleteInfo nodes
    protected Tree i_delete;

    private List4 i_dirtyFieldIndexes;
    
    protected final Transaction _systemTransaction;

    private final ObjectContainerBase _container;
    
    private List4 i_transactionListeners;

    public Transaction(ObjectContainerBase container, Transaction systemTransaction) {
        _container = container;
        _systemTransaction = systemTransaction;
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
    
    protected final void clearAll() {
        clear();
        i_dirtyFieldIndexes = null;
        i_transactionListeners = null;
    }
    
    protected abstract void clear(); 

    public void close(boolean rollbackOnClose) {
		if (stream() != null) {
			checkSynchronization();
			stream().releaseSemaphores(this);
		}
		if (rollbackOnClose) {
			rollback();
		}
	}
    
    public abstract void commit();    
    
    protected void freeOnCommit() {
	}
    
    protected void commit4FieldIndexes(){
        if(_systemTransaction != null){
            _systemTransaction.commit4FieldIndexes();
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
		return _systemTransaction == null;
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
    
    public abstract void rollback();

	protected void rollbackFieldIndexes() {
		if (i_dirtyFieldIndexes != null) {
		    Iterator4 i = new Iterator4Impl(i_dirtyFieldIndexes);
		    while (i.moveNext()) {
		        ((IndexTransaction) i.current()).rollback();
		    }
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

    public abstract void setPointer(int a_id, Slot slot);
    
    public void slotDelete(int id, Slot slot) {
    }

    public void slotFreeOnCommit(int id, Slot slot) {
    }

    public void slotFreeOnRollback(int id, Slot slot) {
    }

    void slotFreeOnRollbackCommitSetPointer(int id, Slot slot) {
    }

    void produceUpdateSlotChange(int id, Slot slot) {
    }
    
    public void slotFreePointerOnCommit(int a_id) {
    }
    
    void slotFreePointerOnCommit(int a_id, Slot slot) {
    }
    
    public void slotFreePointerOnRollback(int a_id) {
    }

    boolean supportsVirtualFields(){
        return true;
    }
    
    public Transaction systemTransaction(){
        if(_systemTransaction != null){
            return _systemTransaction;
        }
        return this;
    }

    public String toString() {
        return stream().toString();
    }
    

    public abstract void writeUpdateDeleteMembers(int id, ClassMetadata clazz, int typeInfo, int cascade);

    public final ObjectContainerBase stream() {
        return _container;
    }

    public Transaction parentTransaction() {
		return _systemTransaction;
	}

}