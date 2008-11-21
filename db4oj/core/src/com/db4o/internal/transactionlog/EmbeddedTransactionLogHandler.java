/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.transactionlog;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class EmbeddedTransactionLogHandler implements TransactionLogHandler{
	
	private final LocalTransaction _trans;
	
	public EmbeddedTransactionLogHandler(LocalTransaction trans) {
		_trans = trans;
	}

	public Slot allocateSlot(boolean appendToFile) {
		int transactionLogByteCount = transactionLogSlotLength();
    	FreespaceManager freespaceManager = freespaceManager();
		if(! appendToFile && freespaceManager != null){
    		int blockedLength = file().bytesToBlocks(transactionLogByteCount);
    		Slot slot = freespaceManager.allocateTransactionLogSlot(blockedLength);
    		if(slot != null){
    			return file().toNonBlockedLength(slot);
    		}
    	}
    	return file().appendBytes(transactionLogByteCount);
	}

	private LocalObjectContainer file() {
		return _trans.file();
	}
	
	private void freeSlot(Slot slot){
    	if(slot == null){
    		return;
    	}
    	if(freespaceManager() == null){
    	    return;
    	}
    	freespaceManager().freeTransactionLogSlot(file().toBlockedLength(slot));
	}

	private FreespaceManager freespaceManager() {
		return _trans.freespaceManager();
	}
	
	public void processSlotChanges(Slot reservedSlot) {
		int slotChangeCount = countSlotChanges();
		if(slotChangeCount > 0){
				
		    Slot transactionLogSlot = slotLongEnoughForLog(reservedSlot) ? reservedSlot
			    	: allocateSlot(true);
	
			    final StatefulBuffer buffer = new StatefulBuffer(_trans, transactionLogSlot);
			    buffer.writeInt(transactionLogSlot.length());
			    buffer.writeInt(slotChangeCount);
	
			    appendSlotChanges(buffer);
	
			    buffer.write();
			    flushFile();
	
			    file().writeTransactionPointer(transactionLogSlot.address());
			    flushFile();
	
			    if (_trans.writeSlots()) {
			    	flushFile();
			    }
	
			    file().writeTransactionPointer(0);
			    flushFile();
			    
			    if (transactionLogSlot != reservedSlot) {
			    	freeSlot(transactionLogSlot);
			    }
		}
		freeSlot(reservedSlot);
	}
	
    private void flushFile() {
		_trans.flushFile();
	}

	private boolean slotLongEnoughForLog(Slot slot){
    	return slot != null  &&  slot.length() >= transactionLogSlotLength();
    }
    
	private void appendSlotChanges(final ByteArrayBuffer writer){
		_trans.traverseSlotChanges(new Visitor4() {
			public void visit(Object obj) {
				((SlotChange)obj).write(writer);
			}
		});
    }
    
    private int transactionLogSlotLength(){
    	// slotchanges * 3 for ID, address, length
    	// 2 ints for slotlength and count
    	return ((countSlotChanges() * 3) + 2) * Const4.INT_LENGTH;
    }
    

	private int countSlotChanges(){
        final IntByRef count = new IntByRef();
        _trans.traverseSlotChanges(new Visitor4() {
			public void visit(Object obj) {
                SlotChange slot = (SlotChange)obj;
                if(slot.isSetPointer()){
                    count.value++;
                }
			}
		});
        return count.value;
	}

}
