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
	
	private final GlobalIdSystem _globalIdSystem;
	
	public StandardIdSystem(LocalObjectContainer localContainer){
		_globalIdSystem = GlobalIdSystemFactory.createNew(localContainer);
	}
	
	public void addTransaction(LocalTransaction transaction){
		addSlotChanges(transaction, new StandardIdSlotChanges(transaction.localContainer()));
	}
	
	public void removeTransaction(LocalTransaction transaction){
		slotChanges(transaction).freePrefetchedIDs(_globalIdSystem);
		removeSlotChanges(transaction);
	}

	private void removeSlotChanges(LocalTransaction transaction) {
		checkSynchronization(transaction);
		_slotChanges.remove(transaction);
	}

	protected StandardIdSlotChanges slotChanges(Transaction transaction) {
		return _slotChanges.get(transaction);
	}

	public void collectCallBackInfo(Transaction transaction, CallbackInfoCollector collector) {
		slotChanges(transaction).collectSlotChanges(collector);
	}

	public boolean isDirty(Transaction transaction) {
		return slotChanges(transaction).isDirty();
	}

	public void commit(final LocalTransaction transaction) {
		
		IdSystemCommitContext commitContext = _globalIdSystem.prepareCommit(countSlotChanges(transaction));
		
        freeSlotChanges(transaction, false);
                
        freespaceBeginCommit();
        
        commitFreespace();
        
        freeSlotChanges(transaction, true);
        
        Visitable<SlotChange> slotChangeVisitable = new Visitable<SlotChange>() {
        	public void accept(Visitor4<SlotChange> visitor) {
        		traverseSlotChanges(transaction, visitor);
        	}
        };
        
        commitContext.commit(slotChangeVisitable, countSlotChanges(transaction));
        
        freespaceEndCommit();

	}

	private void freeSlotChanges(LocalTransaction transaction, boolean forFreespace) {
		if(! isSystemTransaction(transaction)){
			slotChanges(transaction).freeSlotChanges(forFreespace, false);
		}
		_systemSlotChanges.freeSlotChanges(forFreespace, true);
	}
	
	private boolean isSystemTransaction(LocalTransaction transaction) {
		return slotChanges(transaction) == _systemSlotChanges;
	}

	public InterruptedTransactionHandler interruptedTransactionHandler(int transactionId1, int transactionId2) {
		return _globalIdSystem.interruptedTransactionHandler(transactionId1, transactionId2);
	}

	public Slot committedSlot(int id) {
        if (id == 0) {
            return null;
        }
		return _globalIdSystem.committedSlot(id);
	}

	public LocalTransaction systemTransaction() {
		return (LocalTransaction) localContainer().systemTransaction();
	}

	public Slot currentSlot(LocalTransaction transaction, int id) {
        if (id == 0) {
            return null;
        }
        SlotChange change = slotChanges(transaction).findSlotChange(id);
        if (change != null) {
            if(change.slotModified()){
                return change.newSlot();
            }
        }
        
        if(! isSystemTransaction(transaction)){
            Slot parentSlot = currentSlot(systemTransaction(), id); 
            if (parentSlot != null) {
                return parentSlot;
            }
        }
        return committedSlot(id);
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

	public void notifySlotUpdated(Transaction transaction, int id, Slot slot, SlotChangeFactory slotChangeFactory) {
		slotChanges(transaction).notifySlotUpdated(id, slot, slotChangeFactory);
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
		_globalIdSystem.close();
	}

	private void traverseSlotChanges(LocalTransaction transaction, Visitor4 visitor){
		_systemSlotChanges.traverseSlotChanges(visitor);
		if(transaction == systemTransaction()){
			return;
		}
		slotChanges(transaction).traverseSlotChanges(visitor);
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
    
	public int newId(Transaction transaction, SlotChangeFactory slotChangeFactory) {
		int id = acquireId();
        slotChanges(transaction).produceSlotChange(id, slotChangeFactory).notifySlotCreated(null);
		return id;
	}

	private int acquireId() {
		return _globalIdSystem.newId();
	}

	public int prefetchID(Transaction transaction) {
		int id = acquireId();
		slotChanges(transaction).addPrefetchedID(id);
		return id;
	}

	public void prefetchedIDConsumed(Transaction transaction, int id) {
		StandardIdSlotChanges slotChanges = slotChanges(transaction);
		slotChanges.prefetchedIDConsumed(id);
	}

	public void notifySlotCreated(Transaction transaction, int id, Slot slot, SlotChangeFactory slotChangeFactory) {
		slotChanges(transaction).notifySlotCreated(id, slot, slotChangeFactory);
	}
	
	public final void checkSynchronization(LocalTransaction transaction) {
		if(Debug4.checkSychronization){
			transaction.checkSynchronization();
        }
	}

	public void notifySlotDeleted(Transaction transaction, int id, SlotChangeFactory slotChangeFactory) {
		slotChanges(transaction).notifySlotDeleted(id, slotChangeFactory);
	}
	
	protected final int countSlotChanges(LocalTransaction transaction){
        final IntByRef count = new IntByRef();
        traverseSlotChanges(transaction, new Visitor4() {
			public void visit(Object obj) {
                SlotChange slot = (SlotChange)obj;
                if(slot.slotModified()){
                    count.value++;
                }
			}
		});
        return count.value;
	}

}
