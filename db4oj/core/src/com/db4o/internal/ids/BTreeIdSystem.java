/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class BTreeIdSystem implements IdSystem {
	
	
	/**
	 * state to be stored:
	 * BTree ID
	 * IdGenerator
	 * 
	 */
	
	private final LocalObjectContainer _container;
	
	private IdSystem _idSystem;
	
	private final TransactionalIdSystem _transactionalIdSystem;
	
	private final SequentialIdGenerator _idGenerator;
	
	private BTree _bTree;

	public BTreeIdSystem(LocalObjectContainer container, IdSystem idSystem, TransactionalIdSystem transactionalIdSystem, int maxValidId) {
		_container = container;
		_idSystem = idSystem;
		_transactionalIdSystem = transactionalIdSystem;
		_idGenerator = new SequentialIdGenerator(new Function4<Integer, Integer>() {
			public Integer apply(Integer start) {
				return findFreeId(start);
			}
		}, _container.handlers().lowestValidId(), maxValidId);
	}
	
	public BTreeIdSystem(LocalObjectContainer container, final IdSystem idSystem){
		this(container, idSystem, container.newTransactionalIdSystem(null, new Closure4<IdSystem>() {
			public IdSystem run() {
				return idSystem;
			}
		}), Integer.MAX_VALUE);
		
		

	}
	
	
	private int findFreeId(int start) {
		throw new NotImplementedException();
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

	public Slot committedSlot(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void completeInterruptedTransaction(
			int transactionId1, int transactionId2) {
		// TODO Auto-generated method stub
	}

	public int newId() {
		int id = _idGenerator.newId();
		
		
		return id;
	}

	public void commit(Visitable<SlotChange> slotChanges, Runnable commitBlock) {
		// TODO implement
	}

	public void returnUnusedIds(Visitable<Integer> visitable) {
		// TODO Auto-generated method stub
		
	}

}
