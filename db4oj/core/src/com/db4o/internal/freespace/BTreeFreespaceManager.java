/* Copyright (C) 2007   Versant Inc.   http://www.db4o.com */
package com.db4o.internal.freespace;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class BTreeFreespaceManager extends AbstractFreespaceManager {
	
	private RamFreespaceManager _delegate;
	
	private FreespaceBTree _slotsByAddress;
	
	private FreespaceBTree _slotsByLength;
	
	private PersistentIntegerArray _idArray;
    
    private int _delegateIndirectionID;
	
	private int _delegationRequests;

	private FreespaceListener _listener = NullFreespaceListener.INSTANCE;
	
	public BTreeFreespaceManager(LocalObjectContainer file) {
		super(file);
		_delegate = new RamFreespaceManager(file);
	}
	
	private void addSlot(Slot slot) {
		_slotsByLength.add(transaction(), slot);
		_slotsByAddress.add(transaction(), slot);
		_listener.slotAdded(slot.length());
	}
	
	public Slot allocateTransactionLogSlot(int length) {
		return _delegate.allocateTransactionLogSlot(length);
	}

	public void beginCommit() {
        beginDelegation();
	}

	private void beginDelegation(){
        _delegationRequests++;
    }
	
	public void commit() {
		_slotsByAddress.commit(transaction());
		_slotsByLength.commit(transaction());
	}

	private void createBTrees(int addressID, int lengthID) {
		_slotsByAddress = new FreespaceBTree(transaction(), addressID, new AddressKeySlotHandler());
		_slotsByLength = new FreespaceBTree(transaction(), lengthID, new LengthKeySlotHandler());
	}
	
	public void endCommit() {
        endDelegation();
	}
	
	private void endDelegation(){
        _delegationRequests--;        
    }

	public void free(Slot slot) {
		if(! started()){
			return;
		}
		if(isDelegating()){
			_delegate.free(slot);
			return;
		}
		try{
            beginDelegation();
	        if(DTrace.enabled){
	            DTrace.FREESPACEMANAGER_BTREE_FREE.logLength(slot.address(), slot.length());
	        }
            Slot remove[] = new Slot[2];
	        Slot newFreeSlot = slot;
	        BTreePointer pointer = searchBTree(_slotsByAddress, slot, SearchTarget.LOWEST);
			BTreePointer previousPointer = pointer != null ? pointer.previous() : _slotsByAddress.lastPointer(transaction()); 
			if(previousPointer != null){
				Slot previousSlot = (Slot) previousPointer.key();
				if(previousSlot.isDirectlyPreceding(newFreeSlot)){
                    remove[0] = previousSlot;
					newFreeSlot = previousSlot.append(newFreeSlot);
				}
			}
			if(pointer != null){
				Slot nextSlot = (Slot) pointer.key();
				if(newFreeSlot.isDirectlyPreceding(nextSlot)){
                    remove[1] = nextSlot;
					newFreeSlot = newFreeSlot.append(nextSlot);
				}
			}
            for (int i = 0; i < remove.length; i++) {
                if(remove[i] != null){
                    removeSlot(remove[i]);   
                }
            }
			if(! canDiscard(newFreeSlot.length())){
				addSlot(newFreeSlot);
			}
			_file.overwriteDeletedBlockedSlot(slot);
		} finally{
            endDelegation();
		}
	}

    public void freeSelf() {
        _slotsByAddress.free(transaction());
        _slotsByLength.free(transaction());
	}

	public void freeTransactionLogSlot(Slot slot) {
		_delegate.freeTransactionLogSlot(slot);
	}

	public Slot allocateSlot (int length) {
		if(! started()){
			return null;
		}
		if(isDelegating()){
			return _delegate.allocateSlot(length);
		}
		try{
            beginDelegation();
            BTreePointer pointer = searchBTree(_slotsByLength, new Slot(0, length), SearchTarget.HIGHEST);
			if(pointer == null){
				return null;
			}
			Slot slot = (Slot) pointer.key();
			removeSlot(slot);
			int remainingLength = slot.length() - length;
			if(! canDiscard(remainingLength)){
                addSlot(slot.subSlot(length));
                slot = slot.truncate(length);
			}
	        if(DTrace.enabled){
	        	DTrace.FREESPACEMANAGER_GET_SLOT.logLength(slot.address(), slot.length());
	        }
			return slot;
		} finally{
            endDelegation();
		}
	}

	private void initializeExisting(int slotAddress) {
        _idArray = new PersistentIntegerArray(slotAddress);
        _idArray.read(transaction());
        int[] ids = _idArray.array();
        int addressId = ids[0];
        int lengthID = ids[1];
        _delegateIndirectionID = ids[2];
        createBTrees(addressId, lengthID);
        _slotsByAddress.read(transaction());
        _slotsByLength.read(transaction());
        Slot slot = _file.readPointerSlot(_delegateIndirectionID);
		_file.writePointer(_delegateIndirectionID, Slot.ZERO);
		_file.syncFiles();
        _delegate.read(slot);
    }
	
	private void initializeNew() {
        createBTrees(0 , 0);
        _slotsByAddress.write(transaction());
        _slotsByLength.write(transaction());
        _delegateIndirectionID = _file.idSystem().newId(transaction(), SlotChangeFactory.FREE_SPACE);
        int[] ids = new int[] { _slotsByAddress.getID(), _slotsByLength.getID(), _delegateIndirectionID};
        _idArray = new PersistentIntegerArray(ids);
        _idArray.write(transaction());
        _file.systemData().freespaceAddress(_idArray.getID());
    }
	
	private boolean isDelegating(){
		return _delegationRequests > 0;
	}

    public void read(int freeSpaceID) {
        // do nothing
	}

	private void removeSlot(Slot slot) {
		_slotsByLength.remove(transaction(), slot);
		_slotsByAddress.remove(transaction(), slot);
		_listener.slotRemoved(slot.length());
	}

	private BTreePointer searchBTree(BTree bTree, Slot slot, SearchTarget target) {
        BTreeNodeSearchResult searchResult = bTree.searchLeaf(transaction(), slot, target);
        return searchResult.firstValidPointer();
    }

	public int slotCount() {
		return _slotsByAddress.size(transaction()) + _delegate.slotCount();
	}

	public void start(int slotAddress) {
		try{
            beginDelegation();
			if(slotAddress == 0){
				initializeNew();
			}else{
			    initializeExisting(slotAddress);
            }
		}finally{
            endDelegation();
		}
	}

	private boolean started(){
		return _idArray != null;
	}

	public byte systemType() {
		return FM_BTREE;
	}
	
	public String toString() {
		return _slotsByLength.toString();
	}
    
    public int totalFreespace() {
        return super.totalFreespace() + _delegate.totalFreespace();
    }
	
    public void traverse(final Visitor4 visitor) {
		_slotsByAddress.traverseKeys(transaction(), visitor);
	}
    
    public int write() {
        try{
            beginDelegation();
            Slot slot = _file.allocateSlot(_delegate.marshalledLength());
            Pointer4 pointer = new Pointer4(_delegateIndirectionID, slot);
            _delegate.write(pointer);
    		return _idArray.getID();
        }finally{
            endDelegation();
        }
	}

	public void listener(FreespaceListener listener) {
		_listener = listener;
	}

}
