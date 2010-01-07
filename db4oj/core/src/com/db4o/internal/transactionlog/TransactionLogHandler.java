/* Copyright (C) 2008  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.transactionlog;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public abstract class TransactionLogHandler {
	
	protected final StandardIdSystem _idSystem;
	
	protected TransactionLogHandler(StandardIdSystem idSystem){
		_idSystem = idSystem;
	}
	
	protected LocalObjectContainer localContainer() {
		return _idSystem.localContainer();
	}
	
    protected final void flushDatabaseFile() {
		_idSystem.flushFile();
	}
    
	protected final void appendSlotChanges(LocalTransaction transaction, final ByteArrayBuffer writer){
		_idSystem.traverseSlotChanges(transaction, new Visitor4() {
			public void visit(Object obj) {
				((SlotChange)obj).write(writer);
			}
		});
    }
    
    protected final int transactionLogSlotLength(LocalTransaction transaction){
    	// slotchanges * 3 for ID, address, length
    	// 2 ints for slotlength and count
    	return ((countSlotChanges(transaction) * 3) + 2) * Const4.INT_LENGTH;
    }

	protected final int countSlotChanges(LocalTransaction transaction){
        final IntByRef count = new IntByRef();
        _idSystem.traverseSlotChanges(transaction, new Visitor4() {
			public void visit(Object obj) {
                SlotChange slot = (SlotChange)obj;
                if(slot.isSetPointer()){
                    count.value++;
                }
			}
		});
        return count.value;
	}

	public abstract Slot allocateSlot(LocalTransaction transaction, boolean append);

	public abstract void applySlotChanges(LocalTransaction transaction, Slot reservedSlot);

	public abstract InterruptedTransactionHandler interruptedTransactionHandler(ByteArrayBuffer reader);

	public abstract void close();
	

}
