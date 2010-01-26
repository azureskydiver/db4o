/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

public class StandardIdSlotChanges {
	
    private final LockedTree _slotChanges = new LockedTree();
	
	private final LocalObjectContainer _container;
	
	private Tree _prefetchedIDs;

	public StandardIdSlotChanges(LocalObjectContainer container) {
		_container = container;
	}

	public Config4Impl config() {
		return localContainer().config();
	}

	private LocalObjectContainer localContainer() {
		return _container;
	}
	
	public final void freeSlotChanges(final boolean forFreespace, boolean traverseMutable) {
        Visitor4 visitor = new Visitor4() {
            public void visit(Object obj) {
                ((SlotChange)obj).freeDuringCommit(localContainer(), forFreespace);
            }
        };
        if(traverseMutable){
            _slotChanges.traverseMutable(visitor);
        } else {
        	_slotChanges.traverseLocked(visitor);
        }
    }
	
	public void clear() {
		_slotChanges.clear();
	}	
    
	public void rollback() {
		_slotChanges.traverseLocked(new Visitor4() {
            public void visit(Object slotChange) {
                ((SlotChange) slotChange).rollback(localContainer());
            }
        });
	}

	public boolean isDeleted(int id) {
    	return slotChangeIsFlaggedDeleted(id);
    }
	
    public SlotChange produceSlotChange(int id, SlotChangeFactory slotChangeFactory){
    	if(DTrace.enabled){
    		DTrace.PRODUCE_SLOT_CHANGE.log(id);
    	}
        SlotChange slot = slotChangeFactory.newInstance(id);
        _slotChanges.add(slot);
        return (SlotChange)slot.addedOrExisting();
    }    

	
    public SlotChange produceSlotChange(int id){
    	if(DTrace.enabled){
    		DTrace.PRODUCE_SLOT_CHANGE.log(id);
    	}
        SlotChange slot = new SlotChange(id);
        _slotChanges.add(slot);
        SlotChange prevailing = (SlotChange)slot.addedOrExisting();
        if(prevailing == slot){
        	throw new IllegalStateException();
        }
		return prevailing;
    }    
    
    public final SlotChange findSlotChange(int a_id) {
        return (SlotChange)_slotChanges.find(a_id);
    }    

    public void setPointer(int id, Slot slot) {
        if(DTrace.enabled){
            DTrace.SLOT_SET_POINTER.log(id);
            DTrace.SLOT_SET_POINTER.logLength(slot);
        }
        produceSlotChange(id).setPointer(slot);
    }
    
    private boolean slotChangeIsFlaggedDeleted(int id){
        SlotChange slot = findSlotChange(id);
        if (slot != null) {
            return slot.isDeleted();
        }
        return false;
    }
	
	public void traverseSlotChanges(Visitor4 visitor){
        _slotChanges.traverseLocked(visitor);
	}
	
	public void slotDelete(int id, Slot slot) {
        if(DTrace.enabled){
            DTrace.SLOT_DELETE.log(id);
            DTrace.SLOT_DELETE.logLength(slot);
        }
        if (id == 0) {
            return;
        }
        SlotChange slotChange = produceSlotChange(id);
        slotChange.freeOnCommit(localContainer(), slot);
        slotChange.setPointer(Slot.ZERO);
    }
	
    public void slotFreeOnCommit(int id, Slot slot, SlotChangeFactory slotChangeFactory) {
        if(DTrace.enabled){
            DTrace.SLOT_FREE_ON_COMMIT.log(id);
            DTrace.SLOT_FREE_ON_COMMIT.logLength(slot);
        }
        if (id == 0) {
            return;
        }
        produceSlotChange(id, slotChangeFactory).freeOnCommit(localContainer(), slot);
    }

    public void slotFreeOnRollback(int id, Slot slot) {
        if(DTrace.enabled){
            DTrace.SLOT_FREE_ON_ROLLBACK_ID.log(id);
            DTrace.SLOT_FREE_ON_ROLLBACK_ADDRESS.logLength(slot);
        }
        produceSlotChange(id).freeOnRollback(slot);
    }

    void slotFreeOnRollbackCommitSetPointer(int id, Slot oldSlot, Slot newSlot, boolean forFreespace, SlotChangeFactory slotChangeFactory) {
        if(DTrace.enabled){
            DTrace.FREE_ON_ROLLBACK.log(id);
            DTrace.FREE_ON_ROLLBACK.logLength(newSlot);
            DTrace.FREE_ON_COMMIT.log(id);
            DTrace.FREE_ON_COMMIT.logLength(oldSlot);
        }
        
        SlotChange change = produceSlotChange(id, slotChangeFactory);
        change.forFreespace(forFreespace);
        change.freeOnRollbackSetPointer(newSlot);
        change.freeOnCommit(localContainer(), oldSlot);
    }

    public void slotFreePointerOnRollback(int id, SlotChangeFactory slotChangeFactory) {
    	produceSlotChange(id, slotChangeFactory).freePointerOnRollback();
    }

	public boolean isDirty() {
		return _slotChanges != null;
	}

	public void collectSlotChanges(final CallbackInfoCollector collector) {
		if (! isDirty()) {
			return;
		}
		_slotChanges.traverseLocked(new Visitor4() {
			public void visit(Object obj) {
				final SlotChange slotChange = ((SlotChange)obj);
				final int id = slotChange._key;
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
	
	public void readSlotChanges(ByteArrayBuffer buffer) {
		_slotChanges.read(buffer, new SlotChange(0));
	}

	public LocalTransaction systemTransaction() {
		return (LocalTransaction) localContainer().systemTransaction();
	}


	public void addPrefetchedID(int id) {
		_prefetchedIDs = Tree.add(_prefetchedIDs, new TreeInt(id));		
	}
	
	public void prefetchedIDConsumed(int id) {
        _prefetchedIDs = _prefetchedIDs.removeLike(new TreeInt(id));
	}
	
    final void freePrefetchedIDs() {
        if (_prefetchedIDs == null) {
        	return;
        }
    	final LocalObjectContainer container = localContainer();
        _prefetchedIDs.traverse(new Visitor4() {
            public void visit(Object node) {
            	TreeInt intNode = (TreeInt) node;
            	container.free(intNode._key, Const4.POINTER_LENGTH);
            }
        });
        _prefetchedIDs = null;
    }

	public void notifySlotCreated(int id, Slot slot, SlotChangeFactory slotChangeFactory) {
		SlotChange slotChange = produceSlotChange(id, slotChangeFactory);
		slotChange.notifySlotCreated(slot);
	}
	
	void notifySlotChanged(int id, Slot slot,  SlotChangeFactory slotChangeFactory) {
        produceSlotChange(id, slotChangeFactory).notifySlotChanged(localContainer(), slot);
	}
	
    void oldNotifySlotChanged(int id, Slot slot, boolean forFreespace) {
        if(DTrace.enabled){
            DTrace.FREE_ON_ROLLBACK.log(id);
            DTrace.FREE_ON_ROLLBACK.logLength(slot);
        }
        
        final SlotChange slotChange = produceSlotChange(id);
        slotChange.forFreespace(forFreespace);
        slotChange.freeOnRollbackSetPointer(slot);
    }

	public void notifySlotDeleted(int id, SlotChangeFactory slotChangeFactory) {
		produceSlotChange(id, slotChangeFactory).notifyDeleted(localContainer());
	}

}
