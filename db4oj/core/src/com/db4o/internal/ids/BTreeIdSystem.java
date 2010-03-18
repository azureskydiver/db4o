/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class BTreeIdSystem implements IdSystem {
	
	private final LocalObjectContainer _container;
	
	private final TransactionalIdSystem _transactionalIdSystem;
	
	private final SequentialIdGenerator _idGenerator;
	
	private BTree _bTree;
	
	private PersistentIntegerArray _persistentState;

	public BTreeIdSystem(LocalObjectContainer container, TransactionalIdSystem transactionalIdSystem, int maxValidId) {
		_container = container;
		_transactionalIdSystem = transactionalIdSystem;

		int persistentArrayId = systemData().idSystemID();
		if(persistentArrayId == 0){
			initializeNew();
		} else {
			initializeExisting(persistentArrayId);
		}

		_idGenerator = new SequentialIdGenerator(new Function4<Integer, Integer>() {
			public Integer apply(Integer start) {
				return findFreeId(start);
			}
		},  idGeneratorValue(), _container.handlers().lowestValidId(), maxValidId);
	}
	
	public BTreeIdSystem(LocalObjectContainer container, final IdSystem idSystem){
		this(container, container.newTransactionalIdSystem(null, new Closure4<IdSystem>() {
			public IdSystem run() {
				return idSystem;
			}
		}), Integer.MAX_VALUE);
	}


	private void initializeExisting(int persistentArrayId) {
		_persistentState = new PersistentIntegerArray(_transactionalIdSystem, persistentArrayId);
		_persistentState.read(transaction());
		_bTree = new BTree(transaction(), bTreeConfiguration(), bTreeId(), new IdSlotMappingHandler());
	}

	private BTreeConfiguration bTreeConfiguration() {
		return new BTreeConfiguration(_transactionalIdSystem, 64, false);
	}

	private int idGeneratorValue() {
		return _persistentState.array()[1];
	}
	
	private void idGeneratorValue(int value) {
		_persistentState.array()[1] = value;
	}


	private int bTreeId() {
		return _persistentState.array()[0];
	}


	private SystemData systemData() {
		return _container.systemData();
	}
	

	private void initializeNew() {
		_bTree = new BTree(transaction(), bTreeConfiguration(), new IdSlotMappingHandler());
		int idGeneratorValue = _container.handlers().lowestValidId() - 1;
		_persistentState = new PersistentIntegerArray(_transactionalIdSystem, new int[]{_bTree.getID(), idGeneratorValue });
		_persistentState.write(transaction());
		systemData().idSystemID(_persistentState.getID());
	}
	
	private int findFreeId(int start) {
		throw new NotImplementedException();
	}

	public void close() {
		
	}

	public Slot committedSlot(int id) {
		BTreePointer bTreePointer = _bTree.searchOne(transaction(), new IdSlotMapping(id, 0, 0));
		if(bTreePointer == null){
			throw new InvalidIDException(id);
		}
		IdSlotMapping mapping = (IdSlotMapping) bTreePointer.key();
		return mapping.slot();
	}

	public void completeInterruptedTransaction(
			int transactionId1, int transactionId2) {
		// do nothing
	}

	public int newId() {
		int id = _idGenerator.newId();
		_bTree.add(transaction(), new IdSlotMapping(id, 0, 0));
		return id;
	}
	
	private Transaction transaction(){
		return _container.systemTransaction();
	}

	public void commit(Visitable<SlotChange> slotChanges, Runnable commitBlock) {
		
		commitBlock.run();
		
		slotChanges.accept(new Visitor4<SlotChange>() {
			public void visit(SlotChange slotChange) {
				if(! slotChange.slotModified()){
					return;
				}
				if(slotChange.removeId()){
					_bTree.remove(transaction(), new IdSlotMapping(slotChange._key, 0, 0));
					return;
				}
				
				// TODO: Maybe we want a BTree that doesn't allow duplicates.
				_bTree.remove(transaction(), new IdSlotMapping(slotChange._key, 0, 0));
				
				_bTree.add(transaction(), new IdSlotMapping(slotChange._key, slotChange.newSlot()));
				
				if(DTrace.enabled){
					DTrace.SLOT_MAPPED.logLength(slotChange._key, slotChange.newSlot());
				}
				
			}
		});
		_bTree.commit(transaction());
		idGeneratorValue(_idGenerator.persistentGeneratorValue());
		_persistentState.setStateDirty();
		_persistentState.write(transaction());
		
		_transactionalIdSystem.commit();
		_transactionalIdSystem.clear();
	}

	public void returnUnusedIds(Visitable<Integer> visitable) {
		visitable.accept(new Visitor4<Integer>() {
			public void visit(Integer id) {
				_bTree.remove(transaction(), new IdSlotMapping(id, 0, 0));			
			}
		});
	}
	
	public static class IdSlotMappingHandler implements Indexable4<IdSlotMapping> {

		public void defragIndexEntry(DefragmentContextImpl context) {
			throw new NotImplementedException();
		}

		public IdSlotMapping readIndexEntry(Context context, ByteArrayBuffer buffer) {
			return IdSlotMapping.read(buffer);
		}

		public void writeIndexEntry(Context context, ByteArrayBuffer buffer,
				IdSlotMapping mapping) {
			mapping.write(buffer);
		}

		public PreparedComparison prepareComparison(Context context, final IdSlotMapping sourceMapping) {
			return new PreparedComparison<IdSlotMapping>() {
				public int compareTo(IdSlotMapping targetMapping) {
					return sourceMapping._id == targetMapping._id ? 
							0 : (sourceMapping._id < targetMapping._id ? - 1 : 1); 
				}
			};
		}

		public final int linkLength() {
			return Const4.INT_LENGTH * 3;
		}

	}

}
