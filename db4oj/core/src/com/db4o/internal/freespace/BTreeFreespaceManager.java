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
	
	private FreespaceBTree _slotsByAddress;
	
	private FreespaceBTree _slotsByLength;
	
	private PersistentIntegerArray _btreeIDs;
	
	private boolean _writing;
	
	
	public BTreeFreespaceManager(LocalObjectContainer file) {
		super(file);
	}

	public void free(Slot slot) {
		
		if(_writing){
			throw new IllegalStateException();
		}
		
		if(! started()){
			return;
		}
		
        if(DTrace.enabled){
            DTrace.FREE.logLength(slot.address(), slot.length());
        }

        Slot newFreeSlot = slot;
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
		
		if(! canDiscard(newFreeSlot.length())){
			addSlot(newFreeSlot);
		}
		
		_file.overwriteDeletedBlockedSlot(slot);

	}

	public void freeSelf() {
		// TODO Auto-generated method stub

	}

	public Slot getSlot (int length) {
		
		if(_writing){
			throw new IllegalStateException();
		}
		
		if(! started()){
			return null;
		}
		
		BTreeNodeSearchResult searchResult = 
			_slotsByLength.searchLeaf(transaction(), new Slot(0, length), SearchTarget.HIGHEST);
		
		BTreePointer pointer = searchResult.firstValidPointer();
		
		if(pointer == null){
			return null;
		}
		
		Slot slot = (Slot) pointer.key();
		
		removeSlot(slot);
		
		int remainingLength = slot.length() - length;
		
		if(canDiscard(remainingLength)){
			
	        if(DTrace.enabled){
	        	DTrace.GET_FREESPACE.logLength(slot.address(), slot.length());
	        }

			return slot;
		}
		
		addSlot(slot.subSlot(length));
		
		slot = slot.truncate(length);

        if(DTrace.enabled){
        	DTrace.GET_FREESPACE.logLength(slot.address(), slot.length());
        }

		return slot; 
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
			_file.systemData().freespaceAddress(_btreeIDs.getID());
			return;
		}
		_btreeIDs = new PersistentIntegerArray(slotAddress);
		_btreeIDs.read(transaction());
		createBTrees(_btreeIDs.array());
		_slotsByAddress.read(transaction());
		_slotsByLength.read(transaction());
	}

	private void createBTrees(int[] ids) {
		_slotsByAddress = new FreespaceBTree(transaction(), ids[0], new AddressKeySlotHandler());
		_slotsByLength = new FreespaceBTree(transaction(), ids[1], new LengthKeySlotHandler());
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
	
	private boolean commitSequence(WriteContext context){
		_slotsByAddress.commitNodes(transaction());
		_slotsByLength.commitNodes(transaction());
		context.registerParticipant(_slotsByAddress);
		context.registerParticipant(_slotsByLength);
		if(! context.checkParticipants()){
			return false;
		}
		context.freeParticipants();
		if(! context.checkParticipants()){
			return false;
		}
		return true;
	}

	public void commit() {
		
		WriteContext context = new WriteContext(transaction());
		
		while(! commitSequence(context));
		
		_writing = true;
		context.write();
		_writing = false;
	}

}
