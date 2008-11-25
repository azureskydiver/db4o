/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.transactionlog;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public abstract class TransactionLogHandler {
	
	
	protected LocalObjectContainer file(LocalTransaction trans) {
		return trans.file();
	}
	
    protected void flushDatabaseFile(LocalTransaction trans) {
		trans.flushFile();
	}
    
	protected void appendSlotChanges(LocalTransaction trans, final ByteArrayBuffer writer){
		trans.traverseSlotChanges(new Visitor4() {
			public void visit(Object obj) {
				((SlotChange)obj).write(writer);
			}
		});
    }
    
    protected int transactionLogSlotLength(LocalTransaction trans){
    	// slotchanges * 3 for ID, address, length
    	// 2 ints for slotlength and count
    	return ((countSlotChanges(trans) * 3) + 2) * Const4.INT_LENGTH;
    }

	protected int countSlotChanges(LocalTransaction trans){
        final IntByRef count = new IntByRef();
        trans.traverseSlotChanges(new Visitor4() {
			public void visit(Object obj) {
                SlotChange slot = (SlotChange)obj;
                if(slot.isSetPointer()){
                    count.value++;
                }
			}
		});
        return count.value;
	}

	public abstract Slot allocateSlot(LocalTransaction trans, boolean append);

	public abstract void applySlotChanges(LocalTransaction trans, Slot reservedSlot);

	public abstract boolean checkForInterruptedTransaction(LocalTransaction trans, ByteArrayBuffer reader);

	public abstract void completeInterruptedTransaction(LocalTransaction trans);

	public abstract void close();
	

}
