/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import java.io.*;

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
		_slotsByAddress.add(transaction(), slot);
		_slotsByLength.add(transaction(), slot);
	}

	public void freeSelf() {
		// TODO Auto-generated method stub

	}

	public Slot getSlot(int length) {
		// TODO Auto-generated method stub
		return null;
	}

	public void read(int freeSpaceID) {
		// TODO Auto-generated method stub

	}

	public int slotCount() {
		return _slotsByAddress.size(transaction());
	}

	public byte systemType() {
		// TODO Auto-generated method stub
		return 0;
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
