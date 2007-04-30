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
	
	private PersistentIntegerArray _btreeIDs;
	
	
	public BTreeFreespaceManager(LocalObjectContainer file) {
		super(file);
	}

	public void free(Slot slot) {
		
		if(! started()){
			return;
		}
		
        if(DTrace.enabled){
            DTrace.FREE.logLength(slot._address, slot._length);
        }
        
        if (slot._length <= discardLimit()) {
            return;
        }

        Slot newFreeSlot = toBlocked(slot);
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
	    	_file.overwriteDeletedSlot(slot);
	    }

	}


	public void freeSelf() {
		// TODO Auto-generated method stub

	}

	public Slot getSlot (int length) {
		
		if(! started()){
			return null;
		}
		
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
		return _btreeIDs.getID();
	}
	
	private Transaction transaction(){
		return _file.systemTransaction();
	}
	
	public void start(int slotAddress) throws IOException {
		if(slotAddress == 0){
			createBTrees(new int[]{0 , 0});
			_slotsByAddress.write(transaction());
			_slotsByLength.write(transaction());
			int[] ids = new int[] {_slotsByAddress.getID(), _slotsByLength.getID()};
			_btreeIDs = new PersistentIntegerArray(ids);
			_btreeIDs.write(transaction());
			return;
		}
		_btreeIDs = new PersistentIntegerArray(slotAddress);
		_btreeIDs.read(transaction());
		createBTrees(_btreeIDs.array());
	}

	private void createBTrees(int[] ids) {
		_slotsByAddress = new BTree(transaction(), ids[0], new AddressKeySlotHandler());
		_slotsByLength = new BTree(transaction(), ids[1], new LengthKeySlotHandler());
	}

	private boolean started(){
		return _btreeIDs != null;
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

	public void read(int freeSpaceID) {
		// TODO: FB remove
	}

}
