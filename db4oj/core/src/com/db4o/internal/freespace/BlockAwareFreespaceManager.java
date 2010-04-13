/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class BlockAwareFreespaceManager implements FreespaceManager {
	
	private final FreespaceManager _delegate;
	
	private final BlockConverter _blockConverter;

	public BlockAwareFreespaceManager(FreespaceManager delegate_, BlockConverter blockConverter) {
		_delegate = delegate_;
		_blockConverter = blockConverter;
	}

	public Slot allocateSlot(int length) {
		Slot slot = _delegate.allocateSlot(_blockConverter.bytesToBlocks(length));
		if(slot == null){
			return null;
		}
		return _blockConverter.toNonBlockedLength(slot);
	}

	public Slot allocateTransactionLogSlot(int length) {
		Slot slot = _delegate.allocateTransactionLogSlot(_blockConverter.bytesToBlocks(length));
		if(slot == null){
			return null;
		}
		return _blockConverter.toNonBlockedLength(slot);
	}

	public void beginCommit() {
		_delegate.beginCommit();
	}

	public void commit() {
		_delegate.commit();
	}

	public void endCommit() {
		_delegate.endCommit();
	}

	public void free(Slot slot) {
		_delegate.free(_blockConverter.toBlockedLength(slot));
	}

	public void freeSelf() {
		_delegate.freeSelf();
	}

	public void freeTransactionLogSlot(Slot slot) {
		_delegate.freeTransactionLogSlot(_blockConverter.toBlockedLength(slot));
		
	}

	public void listener(FreespaceListener listener) {
		_delegate.listener(listener);
	}

	public void migrateTo(FreespaceManager fm) {
		throw new IllegalStateException();
	}

	public void read(LocalObjectContainer container, int freeSpaceID) {
		throw new IllegalStateException();		
	}

	public int slotCount() {
		return _delegate.slotCount();
	}

	public void start(int slotAddress) {
		throw new IllegalStateException();
	}

	public byte systemType() {
		return _delegate.systemType();
	}

	public int totalFreespace() {
		return _delegate.totalFreespace();
	}

	public void traverse(Visitor4 visitor) {
		_delegate.traverse(visitor);
	}

	public int write(LocalObjectContainer container) {
		return _delegate.write(container);
	}

	public void slotFreed(Slot slot) {
		_delegate.slotFreed(slot);
	}
}

