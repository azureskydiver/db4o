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
		_participants.forEachKey(new Visitor4() {
			public void visit(Object obj) {
				PersistentBase participant = (PersistentBase) obj;
				participant.traverseChildren(new Visitor4() {
					public void visit(Object child) {
						if(! _participants.containsKey(child)){
							checkFailed.setTrue();
						}
					}
				});
				WriteContextInfo info = (WriteContextInfo) _participants.get(participant);
				Slot slot = info.slot();
				if(participant.ownLength() <= slot.length()){
					return;
				}
				container().free(slot);
				info.slot(container().getSlot(participant.ownLength()));
				checkFailed.setTrue();
			}
		});
		return ! checkFailed.isTrue();
	}

	
	LocalObjectContainer container(){
		return (LocalObjectContainer) _transaction.stream();
	}

	public boolean freeParticipants() {
		throw new NotImplementedException();
		// return false;
	}

}
