/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import java.io.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public class BTreeFreespaceManager extends AbstractFreespaceManager {
	
	private BTree _slotsByAddress;
	
	private BTree _slotsByLength;

	
	public BTreeFreespaceManager(LocalObjectContainer file) {
		super(file);
		_slotsByAddress = new BTree(transaction(), 0, new AddressKeySlotHandler());
		_slotsByLength = new BTree(transaction(), 0, new LengthKeySlotHandler());
	}

	public void free(Slot slot) {
		
        if(DTrace.enabled){
            DTrace.FREE.logLength(slot._address, slot._length);
        }
        
        if (slot._length <= discardLimit()) {
            return;
        }

        Slot newFreeSlot = toBlocked(slot);
        int blockedLength = newFreeSlot._length;
        
		BTreeNodeSearchResult searchResult = 
			_slotsByAddress.searchLeaf(transaction(), slot, SearchTarget.LOWEST);
		BTreePointer pointer = searchResult.firstValidPointer();
		if(pointer != null){
			BTreePointer previousPointer = pointer.previous();
			if(previousPointer != null){
				Slot previousSlot = (Slot) previousPointer.key();
				if(previousSlot.isDirectlyPreceding(newFreeSlot)){
					removeSlot(previousSlot);
					newFreeSlot = previousSlot.append(newFreeSlot);
				}
			}
		}
        
		searchResult = 
			_slotsByAddress.searchLeaf(transaction(), slot, SearchTarget.HIGHEST);
		pointer = searchResult.firstValidPointer();
		if(pointer != null){
			Slot nextSlot = (Slot) pointer.key();
			if(newFreeSlot.isDirectlyPreceding(nextSlot)){
				removeSlot(nextSlot);
				newFreeSlot = newFreeSlot.append(nextSlot);
			}
		}
		addSlot(newFreeSlot);
	    if(! Debug.freespaceChecker){
	    	_file.overwriteDeletedBytes(slot._address, blockedLength * blockSize());
	    }

	}


	public void freeSelf() {
		// TODO Auto-generated method stub

	}

	public Slot getSlot (int length) {
		int requiredLength = _file.blocksFor(length);
		
		
		BTreeNodeSearchResult searchResult = 
			_slotsByLength.searchLeaf(transaction(), new Slot(0, requiredLength), SearchTarget.HIGHEST);
		
		BTreePointer pointer = searchResult.firstValidPointer();
		
		if(pointer == null){
			return null;
		}
		
		Slot slot = (Slot) pointer.key();
		
		removeSlot(slot);
		
		if(slot._length == requiredLength){
			return toNonBlocked(slot);
		}
		
		addSlot(slot.subSlot(requiredLength));
		
		return toNonBlocked(slot.truncate(requiredLength)); 
	}
	
	private void addSlot(Slot slot) {
		_slotsByLength.add(transaction(), slot);
		_slotsByAddress.add(transaction(), slot);
	}

	private void removeSlot(Slot slot) {
		_slotsByLength.remove(transaction(), slot);
		_slotsByAddress.remove(transaction(), slot);
	}

	public void read(int freeSpaceID) {
		// TODO Auto-generated method stub

	}

	public int slotCount() {
		return _slotsByAddress.size(transaction());
	}

	public byte systemType() {
		return FM_BTREE;
	}

	public void traverse(final Visitor4 visitor) {
		_slotsByAddress.traverseKeys(transaction(), visitor);
	}

	public int write() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private Transaction transaction(){
		return _file.systemTransaction();
	}
	
	public void start(int slotAddress) throws IOException {
		// TODO: FB remove
	}

	public void beginCommit() {
		// TODO: FB remove

	}

	public int onNew(LocalObjectContainer file) {
		// TODO: FB remove
		return 0;
	}

	public void endCommit() {
		// TODO: FB remove
	}

}
