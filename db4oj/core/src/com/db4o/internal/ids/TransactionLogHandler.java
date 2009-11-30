/* Copyright (C) 2008  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public abstract class TransactionLogHandler {
	
	protected LocalObjectContainer file(IdSystem idSystem) {
		return idSystem.file();
	}
	
    protected void flushDatabaseFile(IdSystem idSystem) {
		idSystem.flushFile();
	}
    
	protected void appendSlotChanges(IdSystem idSystem, final ByteArrayBuffer writer){
		idSystem.traverseSlotChanges(new Visitor4() {
			public void visit(Object obj) {
				((SlotChange)obj).write(writer);
			}
		});
    }
    
    protected int transactionLogSlotLength(IdSystem idSystem){
    	// slotchanges * 3 for ID, address, length
    	// 2 ints for slotlength and count
    	return ((countSlotChanges(idSystem) * 3) + 2) * Const4.INT_LENGTH;
    }

	protected int countSlotChanges(IdSystem idSystem){
        final IntByRef count = new IntByRef();
        idSystem.traverseSlotChanges(new Visitor4() {
			public void visit(Object obj) {
                SlotChange slot = (SlotChange)obj;
                if(slot.isSetPointer()){
                    count.value++;
                }
			}
		});
        return count.value;
	}

	public abstract Slot allocateSlot(IdSystem idSystem, boolean append);

	public abstract void applySlotChanges(IdSystem idSystem, Slot reservedSlot);

	public abstract boolean checkForInterruptedTransaction(IdSystem idSystem, ByteArrayBuffer reader);

	public abstract void completeInterruptedTransaction(IdSystem idSystem);

	public abstract void close();
	

}
