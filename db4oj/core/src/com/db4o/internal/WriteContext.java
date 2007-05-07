/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public class WriteContext {
	
	private final Transaction _transaction;
	
	private final Hashtable4 _participants = new Hashtable4();
	
	public WriteContext(Transaction transaction) {
		_transaction = transaction;
	}
	
	public void registerParticipant(PersistentBase participant){
		participant.traverseChildren(new Visitor4() {
			public void visit(Object obj) {
				registerParticipant((PersistentBase) obj);
			}
		});
		if(_participants.containsKey(participant)){
			return;
		}
		if(! participant.isDirty()){
			return;
		}
		int length = participant.ownLength();
		if(participant.isNew()){
            Pointer4 ptr = container().newSlot(_transaction, length);
            participant.setID(ptr._id);
            _participants.put(participant, new WriteContextInfo(true, new Slot(ptr._address, length)));
            return;
		}
		_participants.put(participant, new WriteContextInfo(false, container().getSlot(length)));
	}
	
	public boolean checkParticipants() {
		final MutableBoolean checkFailed = new MutableBoolean();
		
		Iterator4 i = _participants.iterator();
		while(i.moveNext()){
			Entry4 entry = (Entry4) i.current();
			PersistentBase participant = (PersistentBase) entry.key();
			WriteContextInfo info = (WriteContextInfo)entry.value();
			participant.traverseChildren(new Visitor4() {
				public void visit(Object child) {
					if(! _participants.containsKey(child)){
						checkFailed.setTrue();
					}
				}
			});
			
			Slot slot = info.slot();
			if(participant.ownLength() <= slot.length()){
				continue;
			}
			container().free(slot);
			info.slot(container().getSlot(participant.ownLength()));
			checkFailed.setTrue();
		}
		return ! checkFailed.isTrue();
	}
	
	public void freeParticipants() {
		Iterator4 i = _participants.iterator();
		while(i.moveNext()){
			Entry4 entry = (Entry4) i.current();
			WriteContextInfo info = (WriteContextInfo)entry.value();
			if(! info._isNew){
				PersistentBase participant = (PersistentBase) entry.key();
				int id = participant.getID();
				Slot slot = info.slot();
				_transaction.slotFreeOnRollbackCommitSetPointer(id, slot.address(), slot.length());
			}
		}
	}
	
	public void write(){
		Iterator4 i = _participants.iterator();
		while(i.moveNext()){
			Entry4 entry = (Entry4) i.current();
			PersistentBase participant = (PersistentBase) entry.key();
			WriteContextInfo info = (WriteContextInfo)entry.value();
			Slot slot = info.slot();
			writeToFile(participant, slot);
		}
	}
	
	private void writeToFile(PersistentBase participant, Slot slot){
        if (! participant.writeObjectBegin()) {
            return;
        }
        try {
	        WriteContextBuffer writer = new WriteContextBuffer(slot.length());
	        participant.writeToFile(_transaction, writer, slot);
        }finally{
        	participant.endProcessing();
        }
	}
	
	private LocalObjectContainer container(){
		return (LocalObjectContainer) _transaction.stream();
	}

}
