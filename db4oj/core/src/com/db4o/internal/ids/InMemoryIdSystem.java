/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class InMemoryIdSystem implements IdSystem {
	
	private final LocalObjectContainer _container;
	
	private IdSlotTree _ids;
	
	private Slot _slot;
	
	private final SequentialIdGenerator _idGenerator;
	
	/**
	 * for testing purposes only.
	 */
	public InMemoryIdSystem(LocalObjectContainer container, final int maxValidId){
		_container = container;
		_idGenerator = new SequentialIdGenerator(new Function4<Integer, Integer>() {
			public Integer apply(Integer start) {
				return findFreeId(start, maxValidId);
			}
		}, _container.handlers().lowestValidId(), maxValidId);
	}
	
	public InMemoryIdSystem(LocalObjectContainer container){
		this(container, Integer.MAX_VALUE);
		readThis();
	}

	private void readThis() {
		SystemData systemData = _container.systemData();
		_slot = new Slot(systemData.transactionPointer1(), systemData.transactionPointer2());
		if(! _slot.isNull()){
			ByteArrayBuffer buffer = _container.readBufferBySlot(_slot);
			_idGenerator.read(buffer);
			_ids = (IdSlotTree) new TreeReader(buffer, new IdSlotTree(0, null)).read();
		}
	}

	public void close() {
		// do nothing
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
				if(slotChange.removeId()){
					_ids = (IdSlotTree) Tree.removeLike(_ids, new TreeInt(slotChange._key));
					return;
				}
				if(DTrace.enabled){
					DTrace.SLOT_COMMITTED.logLength(slotChange._key, slotChange.newSlot());
				}
				_ids = Tree.add(_ids, new IdSlotTree(slotChange._key, slotChange.newSlot()));
			}
		});
		writeThis();
	}

	private void writeThis() {
		int slotLength = TreeInt.marshalledLength(_ids) + Const4.INT_LENGTH;
		_slot = _container.allocateSlot(slotLength);
		ByteArrayBuffer buffer = new ByteArrayBuffer(_slot.length());
		_idGenerator.write(buffer);
		TreeInt.write(buffer, _ids);
		_container.writeBytes(buffer, _slot.address(), 0);
		Runnable commitHook = _container.commitHook();
		_container.syncFiles();
		_container.writeTransactionPointer(_slot.address(), _slot.length());
		commitHook.run();
		_container.syncFiles();
		_container.systemData().transactionPointer1(_slot.address());
		_container.systemData().transactionPointer2(_slot.length());
	}

	public Slot committedSlot(int id) {
		IdSlotTree idSlotMapping = (IdSlotTree) Tree.find(_ids, new TreeInt(id));
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
		int id = _idGenerator.newId();
		_ids = Tree.add(_ids, new IdSlotTree(id, Slot.ZERO));
		return id;
	}

	private int findFreeId(final int start, final int end) {
		if(_ids == null){
			return start;
		}
		final IntByRef lastId = new IntByRef();
		final IntByRef freeId = new IntByRef();
		Tree.traverse(_ids, new TreeInt(start), new CancellableVisitor4<TreeInt>() {
			public boolean visit(TreeInt node) {
				int id = node._key;
				if(lastId.value == 0){
					if( id > start){
						freeId.value = start;
						return false;
					}
					lastId.value = id;
					return true;
				}
				if(id > lastId.value + 1){
					freeId.value = lastId.value + 1;
					return false;
				}
				lastId.value = id;
				return true;
			}
		});
		if(freeId.value > 0){
			return freeId.value;
		}
		if(lastId.value < end){
			return Math.max(start, lastId.value + 1);
		}
		return 0;
	}

	public void returnUnusedIds(Visitable<Integer> visitable) {
		visitable.accept(new Visitor4<Integer>() {
			public void visit(Integer obj) {
				_ids = (IdSlotTree) Tree.removeLike(_ids, new TreeInt(obj));
			}
		});
	}

}
