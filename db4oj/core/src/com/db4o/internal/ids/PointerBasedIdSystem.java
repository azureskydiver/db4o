/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;
import com.db4o.internal.transactionlog.*;

/**
 * @exclude
 */
public class PointerBasedIdSystem {
	
	private final TransactionLogHandler _transactionLogHandler;
	
	private final LocalObjectContainer _container;

	public PointerBasedIdSystem(LocalObjectContainer container) {
		_container = container;
		_transactionLogHandler = newTransactionLogHandler(container);
	}

	public int acquireId() {
		return _container.allocatePointerSlot();
	}

	public Slot slot(int id) {
		return null;
	}

	IdSystemCommitContext prepareCommit(final int slotChangeCount) {
		return new IdSystemCommitContext() {
			private final Slot reservedSlot = _transactionLogHandler.allocateSlot(false, slotChangeCount);
			public void commit(Visitable<SlotChange> slotChanges,
					int slotChangeCount) {
				_transactionLogHandler.applySlotChanges(slotChanges, slotChangeCount, reservedSlot);
			}
		};
	}

	public void returnUnusedIds(Visitable<Integer> visitable) {
		visitable.accept(new Visitor4<Integer>() {
			public void visit(Integer id) {
				_container.free(id, Const4.POINTER_LENGTH);
			}
		});
	}
	
	private TransactionLogHandler newTransactionLogHandler(LocalObjectContainer container) {
		boolean fileBased = container.config().fileBasedTransactionLog() && container instanceof IoAdaptedObjectContainer;
		if(! fileBased){
			return new EmbeddedTransactionLogHandler(container);
		}
		String fileName = ((IoAdaptedObjectContainer)container).fileName();
		return new FileBasedTransactionLogHandler(container, fileName); 
	}

	public void close() {
		_transactionLogHandler.close();
	}

	public InterruptedTransactionHandler interruptedTransactionHandler(
			ByteArrayBuffer reader) {
		return _transactionLogHandler.interruptedTransactionHandler(reader);
	}

}
