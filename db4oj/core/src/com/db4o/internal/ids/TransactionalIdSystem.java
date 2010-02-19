/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class TransactionalIdSystem {
	
	private StandardIdSlotChanges _slotChanges;

	private TransactionalIdSystem _systemIdSystem;
	
	private final GlobalIdSystem _globalIdSystem;
	
	private final FreespaceManager _freespaceManager;
	
	public TransactionalIdSystem(FreespaceManager freespaceManager, GlobalIdSystem globalIdSystem, TransactionalIdSystem systemIdSystem){
		_freespaceManager = freespaceManager;
		_globalIdSystem = globalIdSystem;
		// _slotChanges = new StandardIdSlotChanges(localContainer);
		_systemIdSystem = systemIdSystem;
	}
	
	public void collectCallBackInfo(final CallbackInfoCollector collector) {
		if(! _slotChanges.isDirty()){
			return;
		}
		_slotChanges.traverseSlotChanges(new Visitor4<SlotChange>() {
			public void visit(SlotChange slotChange) {
				int id = slotChange._key;
				if (slotChange.isDeleted()) {
					if(! slotChange.isNew()){
						collector.deleted(id);
					}
				} else if (slotChange.isNew()) {
					collector.added(id);
				} else {
					collector.updated(id);
				}
			}
		});
	}

	public boolean isDirty() {
		return _slotChanges.isDirty();
	}

	public void commit() {
		Visitable<SlotChange> slotChangeVisitable = new Visitable<SlotChange>() {
			public void accept(Visitor4<SlotChange> visitor) {
				traverseSlotChanges(visitor);
			}
		};
		_globalIdSystem.commit(slotChangeVisitable, new Runnable() {
			public void run() {
				freeSlotChanges(false);
				freespaceBeginCommit();
				commitFreespace();
				freeSlotChanges(true);
			}
		});
        freespaceEndCommit();
	}

	private void freeSlotChanges(boolean forFreespace) {
		_slotChanges.freeSlotChanges(forFreespace, isSystemTransaction());
		if(! isSystemTransaction()){
			_systemIdSystem.freeSlotChanges(forFreespace);	
		}
	}
	
	private boolean isSystemTransaction() {
		return _systemIdSystem == null;
	}

	public void completeInterruptedTransaction(int transactionId1, int transactionId2) {
		_globalIdSystem.completeInterruptedTransaction(transactionId1, transactionId2);
	}

	public Slot committedSlot(int id) {
        if (id == 0) {
            return null;
        }
		return _globalIdSystem.committedSlot(id);
	}

	public Slot currentSlot(int id) {
        if (id == 0) {
            return null;
        }
        SlotChange change = _slotChanges.findSlotChange(id);
        if (change != null) {
            if(change.slotModified()){
                return change.newSlot();
            }
        }
        
        if(! isSystemTransaction()){
            Slot parentSlot = _systemIdSystem.currentSlot(id); 
            if (parentSlot != null) {
                return parentSlot;
            }
        }
        return committedSlot(id);
	}

	public void rollback() {
		_slotChanges.rollback();
	}

	public void clear() {
		_slotChanges.clear();
	}

	public boolean isDeleted(int id) {
		return _slotChanges.isDeleted(id);
	}

	public void notifySlotUpdated(int id, Slot slot, SlotChangeFactory slotChangeFactory) {
		_slotChanges.notifySlotUpdated(id, slot, slotChangeFactory);
	}

	public void close(){
		if(! isSystemTransaction()){
			return;
		}
		_globalIdSystem.close();
	}

	private void traverseSlotChanges(Visitor4<SlotChange> visitor){
		if(! isSystemTransaction()){
			_systemIdSystem.traverseSlotChanges(visitor);
		}
		_slotChanges.traverseSlotChanges(visitor);
	}

	private void freespaceBeginCommit(){
        if(_freespaceManager == null){
            return;
        }
        _freespaceManager.beginCommit();
    }
    
    private void freespaceEndCommit(){
        if(_freespaceManager == null){
            return;
        }
        _freespaceManager.endCommit();
    }
    
    private void commitFreespace(){
        if(_freespaceManager == null){
            return;
        }
        _freespaceManager.commit();
    }
    
	public int newId(SlotChangeFactory slotChangeFactory) {
		int id = acquireId();
        _slotChanges.produceSlotChange(id, slotChangeFactory).notifySlotCreated(null);
		return id;
	}

	private int acquireId() {
		return _globalIdSystem.newId();
	}

	public int prefetchID() {
		int id = acquireId();
		_slotChanges.addPrefetchedID(id);
		return id;
	}

	public void prefetchedIDConsumed(int id) {
		_slotChanges.prefetchedIDConsumed(id);
	}

	public void notifySlotCreated(int id, Slot slot, SlotChangeFactory slotChangeFactory) {
		_slotChanges.notifySlotCreated(id, slot, slotChangeFactory);
	}
	
	public void notifySlotDeleted(int id, SlotChangeFactory slotChangeFactory) {
		_slotChanges.notifySlotDeleted(id, slotChangeFactory);
	}

}
