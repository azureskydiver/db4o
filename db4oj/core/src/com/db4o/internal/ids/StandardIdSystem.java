/* Copyright (C) 2009 Versant Corporation http://www.db4o.com */

package com.db4o.internal.ids;

import java.util.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;
import com.db4o.internal.transactionlog.*;

/**
 * @exclude
 */
public class StandardIdSystem implements IdSystem {
	
	private final Map<Transaction, StandardIdSlotChanges> _slotChanges = new HashMap();

	private StandardIdSlotChanges _systemSlotChanges;
	
	private final TransactionLogHandler _transactionLogHandler;
	
	public StandardIdSystem(LocalObjectContainer localContainer){
		_transactionLogHandler = newTransactionLogHandler(localContainer);
	}
	
	public void addTransaction(LocalTransaction transaction){
		addSlotChanges(transaction, new StandardIdSlotChanges(transaction.localContainer()));
	}
	
	public void removeTransaction(LocalTransaction transaction){
		slotChanges(transaction).freePrefetchedIDs();
		removeSlotChanges(transaction);
	}

	private void removeSlotChanges(LocalTransaction transaction) {
		checkSynchronization(transaction);
		_slotChanges.remove(transaction);
	}

	protected void slotFreePointerOnRollback(Transaction transaction, int id) {
		slotChanges(transaction).slotFreePointerOnRollback(id);
	}

	private StandardIdSlotChanges slotChanges(Transaction transaction) {
		return _slotChanges.get(transaction);
	}

	public void collectCallBackInfo(Transaction transaction, CallbackInfoCollector collector) {
		slotChanges(transaction).collectSlotChanges(collector);
	}

	public boolean isDirty(Transaction transaction) {
		return slotChanges(transaction).isDirty();
	}

	public void commit(LocalTransaction transaction) {
		
        Slot reservedSlot = _transactionLogHandler.allocateSlot(transaction, false);
        
        freeSlotChanges(transaction, false);
                
        freespaceBeginCommit();
        
        commitFreespace();
        
        freeSlotChanges(transaction, true);
        
        _transactionLogHandler.applySlotChanges(transaction, reservedSlot);
        
        freespaceEndCommit();

	}

	private void freeSlotChanges(LocalTransaction transaction, boolean forFreespace) {
		if(! isSystemTransaction(transaction)){
			slotChanges(transaction).freeSlotChanges(forFreespace, false);
		}
		_systemSlotChanges.freeSlotChanges(forFreespace, true);
	}
	
	public void freeAndClearSystemSlotChanges(){
		_systemSlotChanges.freeSlotChanges(false, true);
		_systemSlotChanges.clear();
	}

	private boolean isSystemTransaction(LocalTransaction transaction) {
		return slotChanges(transaction) == _systemSlotChanges;
	}

	public InterruptedTransactionHandler interruptedTransactionHandler(ByteArrayBuffer reader) {
		return _transactionLogHandler.interruptedTransactionHandler(reader);
	}

	public Slot getCommittedSlotOfID(LocalTransaction transaction, int id) {
        if (id == 0) {
            return null;
        }
        SlotChange change = slotChanges(transaction).findSlotChange(id);
        if (change != null) {
            Slot slot = change.oldSlot();
            if(slot != null){
                return slot;
            }
        }
        if(! isSystemTransaction(transaction)){
            Slot parentSlot = getCommittedSlotOfID(systemTransaction(), id) ;
            if (parentSlot != null) {
                return parentSlot;
            }
        }
		return localContainer().readPointer(id)._slot;
	}

	public LocalTransaction systemTransaction() {
		return (LocalTransaction) localContainer().systemTransaction();
	}

	public Slot getCurrentSlotOfID(LocalTransaction transaction, int id) {
        if (id == 0) {
            return null;
        }
        SlotChange change = slotChanges(transaction).findSlotChange(id);
        if (change != null) {
            if(change.isSetPointer()){
                return change.newSlot();
            }
        }
        
        if(! isSystemTransaction(transaction)){
            Slot parentSlot = getCurrentSlotOfID(systemTransaction(), id); 
            if (parentSlot != null) {
                return parentSlot;
            }
        }
        return localContainer().readPointer(id)._slot;
	}
	
    public void slotFreeOnRollbackCommitSetPointer(LocalTransaction transaction, int id, Slot newSlot, boolean forFreespace) {
        Slot oldSlot = getCurrentSlotOfID(transaction, id);
        if(oldSlot==null) {
        	return;
        }
        slotChanges(transaction).slotFreeOnRollbackCommitSetPointer(id, oldSlot, newSlot, forFreespace);
    }
    
    public void setPointer(Transaction transaction, int id, Slot slot) {
    	slotChanges(transaction).setPointer(id, slot);
    }
    
	public void slotFreePointerOnCommit(LocalTransaction transaction, int id) {
        Slot slot = getCurrentSlotOfID(transaction, id);
        if(slot == null){
            return;
        }
        
        // FIXME: From looking at this it should call slotFreePointerOnCommit
        //        Write a test case and check.
        
        //        Looking at references, this method is only called from freed
        //        BTree nodes. Indeed it should be checked what happens here.
        
        slotFreeOnCommit(transaction, id, slot);
	}

	public void slotDelete(Transaction transaction, int id, Slot slot) {
		slotChanges(transaction).slotDelete(id, slot);
	}

	public void slotFreeOnCommit(Transaction transaction, int id, Slot slot) {
		slotChanges(transaction).slotFreeOnCommit(id, slot);
	}

	protected void slotFreeOnRollback(Transaction transaction, int id, Slot slot) {
		slotChanges(transaction).slotFreeOnRollback(id, slot);
	}

	public void rollback(Transaction transaction) {
		slotChanges(transaction).rollback();
	}

	public void clear(Transaction transaction) {
		slotChanges(transaction).clear();
	}

	public boolean isDeleted(Transaction transaction, int id) {
		return slotChanges(transaction).isDeleted(id);
	}

	public void notifySlotChanged(Transaction transaction, int id, Slot slot) {
		slotChanges(transaction).produceUpdateSlotChange(id, slot);
	}

	public void systemTransaction(LocalTransaction transaction) {
		_systemSlotChanges = new StandardIdSlotChanges(transaction.localContainer());
		addSlotChanges(transaction, _systemSlotChanges);
	}

	private void addSlotChanges(LocalTransaction transaction, StandardIdSlotChanges slotChanges) {
		checkSynchronization(transaction);
		_slotChanges.put(transaction, slotChanges);
	}
	
	public void close(){
		_transactionLogHandler.close();
	}

	private TransactionLogHandler newTransactionLogHandler(LocalObjectContainer container) {
		boolean fileBased = container.config().fileBasedTransactionLog() && container instanceof IoAdaptedObjectContainer;
		if(! fileBased){
			return new EmbeddedTransactionLogHandler(this);
		}
		String fileName = ((IoAdaptedObjectContainer)container).fileName();
		return new FileBasedTransactionLogHandler(this, fileName); 
	}
	
	public void traverseSlotChanges(LocalTransaction transaction, Visitor4 visitor){
		_systemSlotChanges.traverseSlotChanges(visitor);
		if(transaction == systemTransaction()){
			return;
		}
		slotChanges(transaction).traverseSlotChanges(visitor);
	}

    public void flushFile(){
        if(DTrace.enabled){
            DTrace.TRANS_FLUSH.log();
        }
        localContainer().syncFiles();
    }
    
	public LocalObjectContainer localContainer() {
		return _systemSlotChanges.systemTransaction().localContainer();
	}

	public FreespaceManager freespaceManager() {
		return localContainer().freespaceManager();
	}
	
    private void freespaceBeginCommit(){
        if(freespaceManager() == null){
            return;
        }
        freespaceManager().beginCommit();
    }
    
    private void freespaceEndCommit(){
        if(freespaceManager() == null){
            return;
        }
        freespaceManager().endCommit();
    }
    
    private void commitFreespace(){
        if(freespaceManager() == null){
            return;
        }
        freespaceManager().commit();
    }
    
    public boolean writeSlots(LocalTransaction transaction) {
    	final LocalObjectContainer container = transaction.localContainer();
        final BooleanByRef ret = new BooleanByRef();
        traverseSlotChanges(transaction, new Visitor4() {
			public void visit(Object obj) {
				((SlotChange)obj).writePointer(container);
				ret.value = true;
			}
		});
        return ret.value;
    }
    
	public boolean isReadOnly() {
		return config().isReadOnly();
	}

	public Config4Impl config() {
		return localContainer().config();
	}
	
	public void readWriteSlotChanges(ByteArrayBuffer buffer) {
		_systemSlotChanges.readSlotChanges(buffer);
       if(writeSlots(systemTransaction())){
           flushFile();
       }
	}

	public int newId(Transaction transaction) {
		int id = localContainer().allocatePointerSlot();
        slotFreePointerOnRollback(transaction, id);
		return id;
	}

	public int prefetchID(Transaction transaction) {
		int id = localContainer().allocatePointerSlot();
		slotChanges(transaction).addPrefetchedID(id);
		return id;
	}

	public void prefetchedIDConsumed(Transaction transaction, int id) {
		StandardIdSlotChanges slotChanges = slotChanges(transaction);
		slotChanges.prefetchedIDConsumed(id);
		slotChanges.slotFreePointerOnRollback(id);
	}

	public void notifyNewSlotCreated(Transaction transaction, int id, Slot slot) {
		slotFreeOnRollback(transaction, id, slot);
		setPointer(transaction, id, slot);
	}
	
	public final void checkSynchronization(LocalTransaction transaction) {
		if(Debug4.checkSychronization){
			transaction.checkSynchronization();
        }
	}

}
