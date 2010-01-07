/* Copyright (C) 2008  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.transactionlog;

import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class EmbeddedTransactionLogHandler extends TransactionLogHandler{
	
	public EmbeddedTransactionLogHandler(StandardIdSystem idSystem) {
		super(idSystem);
	}

	public InterruptedTransactionHandler interruptedTransactionHandler(ByteArrayBuffer reader) {
	    final int transactionID1 = reader.readInt();
	    int transactionID2 = reader.readInt();
	    if( (transactionID1 > 0)  &&  (transactionID1 == transactionID2)){
	        return new InterruptedTransactionHandler() {
	        	
	        	private int _addressOfIncompleteCommit = transactionID1;  

				public void completeInterruptedTransaction() {
					StatefulBuffer bytes = new StatefulBuffer(_idSystem.systemTransaction(), _addressOfIncompleteCommit, Const4.INT_LENGTH);
					bytes.read();
			        int length = bytes.readInt();
			        if (length > 0) {
			            bytes = new StatefulBuffer(_idSystem.systemTransaction(), _addressOfIncompleteCommit, length);
			            bytes.read();
			            bytes.incrementOffset(Const4.INT_LENGTH);
			            _idSystem.readWriteSlotChanges(bytes);
			            localContainer().writeTransactionPointer(0);
			            flushDatabaseFile();
			            _idSystem.freeAndClearSystemSlotChanges();
			        } else {
			            localContainer().writeTransactionPointer(0);
			            flushDatabaseFile();
			        }

				}
			};
	    }
		return null;
	}

	public Slot allocateSlot(LocalTransaction transaction, boolean appendToFile) {
		int transactionLogByteCount = transactionLogSlotLength(transaction);
    	FreespaceManager freespaceManager = transaction.freespaceManager();
		if(! appendToFile && freespaceManager != null){
    		int blockedLength = transaction.localContainer().bytesToBlocks(transactionLogByteCount);
    		Slot slot = freespaceManager.allocateTransactionLogSlot(blockedLength);
    		if(slot != null){
    			return transaction.localContainer().toNonBlockedLength(slot);
    		}
    	}
    	return transaction.localContainer().appendBytes(transactionLogByteCount);
	}

	private void freeSlot(Slot slot){
    	if(slot == null){
    		return;
    	}
    	if(_idSystem.freespaceManager() == null){
    	    return;
    	}
    	_idSystem.freespaceManager().freeTransactionLogSlot(localContainer().toBlockedLength(slot));
	}

	public void applySlotChanges(LocalTransaction transaction, Slot reservedSlot) {
		int slotChangeCount = countSlotChanges(transaction);
		if(slotChangeCount > 0){
				
		    Slot transactionLogSlot = slotLongEnoughForLog(transaction, reservedSlot) ? reservedSlot
			    	: allocateSlot(transaction, true);
	
			    final StatefulBuffer buffer = new StatefulBuffer(transaction.systemTransaction(), transactionLogSlot);
			    buffer.writeInt(transactionLogSlot.length());
			    buffer.writeInt(slotChangeCount);
	
			    appendSlotChanges(transaction, buffer);
	
			    buffer.write();
			    flushDatabaseFile();
	
			    localContainer().writeTransactionPointer(transactionLogSlot.address());
			    flushDatabaseFile();
	
			    if (_idSystem.writeSlots(transaction)) {
			    	flushDatabaseFile();
			    }
	
			    localContainer().writeTransactionPointer(0);
			    flushDatabaseFile();
			    
			    if (transactionLogSlot != reservedSlot) {
			    	freeSlot(transactionLogSlot);
			    }
		}
		freeSlot(reservedSlot);
	}
	
	private boolean slotLongEnoughForLog(LocalTransaction transaction, Slot slot){
    	return slot != null  &&  slot.length() >= transactionLogSlotLength(transaction);
    }
    

	public void close() {
		// do nothing
	}

}
