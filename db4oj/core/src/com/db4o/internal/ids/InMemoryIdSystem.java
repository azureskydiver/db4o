/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class InMemoryIdSystem implements IdSystem {
	
	private final LocalObjectContainer _container;
	
	private IdSlotMapping _ids;
	
	private int _idGenerator;
	
	private Slot _slot;
	
	public InMemoryIdSystem(LocalObjectContainer container){
		_container = container;
		_idGenerator = _container.handlers().lowestValidId();
		SystemData systemData = _container.systemData();
		_slot = new Slot(systemData.transactionPointer1(), systemData.transactionPointer2());
		if(! _slot.isNull()){
			ByteArrayBuffer buffer = _container.readBufferBySlot(_slot);
			_idGenerator = buffer.readInt();
			_ids = (IdSlotMapping) new TreeReader(buffer, new IdSlotMapping(0, null)).read();
		}
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

	public void commit(Visitable<SlotChange> slotChanges, Runnable commitBlock) {
		commitBlock.run();
		if(_slot != null &&  ! _slot.isNull()){
			_container.free(_slot);
		}
		slotChanges.accept(new Visitor4<SlotChange>() {
			public void visit(SlotChange slotChange) {
				if(! slotChange.slotModified()){
					return;
				}
				Slot newSlot = slotChange.newSlot();
				_ids = Tree.add(_ids, new IdSlotMapping(slotChange._key, newSlot));
			}
		});
		writeThis();
	}

	private void writeThis() {
		int slotLength = TreeInt.marshalledLength(_ids) + Const4.INT_LENGTH;
		_slot = _container.allocateSlot(slotLength);
		ByteArrayBuffer buffer = new ByteArrayBuffer(_slot.length());
		buffer.writeInt(_idGenerator);
		TreeInt.write(buffer, _ids);
		_container.writeBytes(buffer, _slot.address(), 0);
		_container.syncFiles();
		_container.writeTransactionPointer(_slot.address(), _slot.length());
		_container.syncFiles();
		_container.systemData().transactionPointer1(_slot.address());
		_container.systemData().transactionPointer2(_slot.length());
	}

	public Slot committedSlot(int id) {
		IdSlotMapping idSlotMapping = (IdSlotMapping) Tree.find(_ids, new TreeInt(id));
		if(idSlotMapping == null){
			throw new InvalidIDException(id);
		}
		return idSlotMapping.slot();
	}

	public void completeInterruptedTransaction(int address,
			int length) {
		// do nothing
	}

	public int newId() {
		return ++ _idGenerator;
	}

	public void returnUnusedIds(Visitable<Integer> visitable) {
		// TODO Auto-generated method stub
		
	}

}
