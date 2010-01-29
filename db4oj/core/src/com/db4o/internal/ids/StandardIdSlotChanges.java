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

    public final SlotChange findSlotChange(int id) {
        return (SlotChange)_slotChanges.find(id);
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
		produceSlotChange(id, slotChangeFactory).notifySlotCreated(slot);
	}
	
	void notifySlotChanged(int id, Slot slot,  SlotChangeFactory slotChangeFactory) {
        produceSlotChange(id, slotChangeFactory).notifySlotChanged(localContainer(), slot);
	}
	
	public void notifySlotDeleted(int id, SlotChangeFactory slotChangeFactory) {
		produceSlotChange(id, slotChangeFactory).notifyDeleted(localContainer());
	}

}
