/* Copyright (C) 2004 - 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.util.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public abstract class Transaction {
	
    // contains DeleteInfo nodes
    protected Tree _delete;

    protected final Transaction _systemTransaction;

    /**
     * This is the inside representation to operate against, the actual
     * file-based ObjectContainerBase or the client. For all calls 
     * against this ObjectContainerBase the method signatures that take
     * a transaction have to be used.
     */
    private final ObjectContainerBase _container;
    
    /**
     * This is the outside representation to the user. This ObjectContainer
     * should use this transaction as it's main user transation, so it also
     * allows using the method signatures on ObjectContainer without a 
     * transaction.  
     */
    private ObjectContainer _objectContainer;
    
    private List4 _transactionListeners;
    
    private final TransactionalReferenceSystem _referenceSystem;
    
    private final Map<TransactionLocal<?>, Object> _locals = new HashMap<TransactionLocal<?>, Object>();
    
    public Transaction(ObjectContainerBase container, Transaction systemTransaction, TransactionalReferenceSystem referenceSystem) {
        _container = container;
        _systemTransaction = systemTransaction;
        _referenceSystem = referenceSystem;
    }
    
    /**
     * Retrieves the value of a transaction local variables.
     * 
     * If this is the first time the variable is accessed {@link TransactionLocal#initialValueFor(Transaction)}
     * will provide the initial value.
     */
    public <T> ByRef<T> get(TransactionLocal<T> local) {
    	final ByRef<T> existing = (ByRef<T>) _locals.get(local);
    	if (null != existing)
    		return existing;
		final ByRef<T> initialValue = ByRef.newInstance(local.initialValueFor(this));
		_locals.put(local, initialValue);
		return initialValue;
    }

	public final void checkSynchronization() {
		if(Debug.checkSychronization){
            container()._lock.notify();
        }
	}

    public void addTransactionListener(TransactionListener listener) {
        _transactionListeners = new List4(_transactionListeners, listener);
    }
    
    protected final void clearAll() {
        clear();
        _transactionListeners = null;
        _locals.clear();
    }
    
    protected abstract void clear(); 

    public void close(boolean rollbackOnClose) {
		if (container() != null) {
			checkSynchronization();
			container().releaseSemaphores(this);
			if(_referenceSystem != null){
			    container().referenceSystemRegistry().removeReferenceSystem(_referenceSystem);
			}
		}
		if (rollbackOnClose) {
			rollback();
		}
	}
    
    public abstract void commit();    
    
    protected void commitTransactionListeners() {
        checkSynchronization();
        if (_transactionListeners != null) {
            Iterator4 i = new Iterator4Impl(_transactionListeners);
            while (i.moveNext()) {
                ((TransactionListener) i.current()).preCommit();
            }
            _transactionListeners = null;
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
        
        DeleteInfo info = (DeleteInfo) TreeInt.find(_delete, id);
        if(info == null){
            info = new DeleteInfo(id, ref, cascade);
            _delete = Tree.add(_delete, info);
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
        if(_delete == null){
        	return;
        }
        _delete = TreeInt.removeLike((TreeInt)_delete, a_id);
    }
    
    public HardObjectReference getHardReferenceBySignature(final long a_uuid, final byte[] a_signature) {
        checkSynchronization();  
        return container().uUIDIndex().getHardObjectReferenceBySignature(this, a_uuid, a_signature);
    }
    
	public abstract void processDeletes();
	
    public ReferenceSystem referenceSystem() {
        if(_referenceSystem != null){
            return _referenceSystem;
        }
        return parentTransaction().referenceSystem();
    }
	
    public Reflector reflector(){
    	return container().reflector();
    }
    
    public abstract void rollback();

	protected void rollBackTransactionListeners() {
        checkSynchronization();
        
        if (_transactionListeners != null) {
            Iterator4 i = new Iterator4Impl(_transactionListeners);
            while (i.moveNext()) {
                ((TransactionListener) i.current()).postRollback();
            }
            _transactionListeners = null;
        }
    }
    
    public final void setPointer(Pointer4 pointer){
        setPointer(pointer._id, pointer._slot);
    }

    /**
     * @param id
     * @param slot
     */
    public void setPointer(int id, Slot slot){
    }
    
    /**
     * @param id
     * @param slot
     */
    public void slotDelete(int id, Slot slot) {
    }

    /**
     * @param id
     * @param slot
     */
    public void slotFreeOnCommit(int id, Slot slot) {
    }

    /**
     * @param id
     * @param slot
     */
    public void slotFreeOnRollback(int id, Slot slot) {
    }

    /**
     * @param id
     * @param slot
     * @param forFreespace
     */
    void slotFreeOnRollbackCommitSetPointer(int id, Slot slot, boolean forFreespace) {
    }

    /**
     * @param id
     * @param slot
     */
    void produceUpdateSlotChange(int id, Slot slot) {
    }
    
    /** @param id */
    public void slotFreePointerOnCommit(int id) {
    }
    
    /**
     * @param id
     * @param slot
     */
    void slotFreePointerOnCommit(int id, Slot slot) {
    }
    
    /** @param id */
    public void slotFreePointerOnRollback(int id) {
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
        return container().toString();
    }

    public abstract void writeUpdateDeleteMembers(int id, ClassMetadata clazz, int typeInfo, int cascade);

    public final ObjectContainerBase container() {
        return _container;
    }

    public Transaction parentTransaction() {
		return _systemTransaction;
	}

    public void rollbackReferenceSystem() {
        referenceSystem().rollback();
    }

    public void commitReferenceSystem() {
        referenceSystem().commit();
    }

    public void addNewReference(ObjectReference ref) {
        referenceSystem().addNewReference(ref);
    }
    
    public final Object objectForIdFromCache(int id){
        ObjectReference ref = referenceForId(id);
        if (ref == null) {
            return null;
        }
        Object candidate = ref.getObject();
        if(candidate == null){
            removeReference(ref);
        }
        return candidate;
    }

    public final ObjectReference referenceForId(int id) {
        ObjectReference ref = referenceSystem().referenceForId(id);
        if(ref != null){
            return ref;
        }
        if(parentTransaction() != null){
            return parentTransaction().referenceForId(id);
        }
        return null;
    }

    public final ObjectReference referenceForObject(Object obj) {
        ObjectReference ref = referenceSystem().referenceForObject(obj);
        if(ref != null){
            return ref;
        }
        if(parentTransaction() != null){
            return parentTransaction().referenceForObject(obj);
        }
        return null;
    }
    
    public final void removeReference(ObjectReference ref) {
        
        referenceSystem().removeReference(ref);

        // setting the ID to minus 1 ensures that the
        // gc mechanism does not kill the new YapObject
        ref.setID(-1);
        Platform4.killYapRef(ref.getObjectReference());
    }
    
    public final void removeObjectFromReferenceSystem(Object obj){
        ObjectReference ref = referenceForObject(obj);
        if(ref != null){
            removeReference(ref);
        }
    }
    
    public void setOutSideRepresentation(ObjectContainer objectContainer){
        _objectContainer = objectContainer;
    }
    
    public ObjectContainer objectContainer(){
        if(_objectContainer != null){
            return _objectContainer;
        }
        return _container;
    }
    
    public Context context(){
        return new Context(){
            public ObjectContainer objectContainer() {
                return Transaction.this.objectContainer();
            }

            public Transaction transaction() {
                return Transaction.this;
            }
        };
    }

	public void deactivate(int id, ActivationDepth activationDepth) {
		//FIXME: JavaServerCrossplatformTestCase crashes with we remove
		//		  null test.
		final ObjectReference reference = referenceForId(id);
		if (null != reference) {
			reference.deactivate(this, activationDepth);
		}
	}

}