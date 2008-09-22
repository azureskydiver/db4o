/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.collections;

import java.util.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.handlers.*;
import com.db4o.marshall.*;

/**
 * @decaf.ignore
 * @sharpen.ignore
 */
public class BigSet<E> implements Set<E> {
	
	private BTree _bTree;
	
	private Transaction _transaction;

	public BigSet(ObjectContainer db) {
		if(db == null){
			return;
		}
		_transaction = ((LocalObjectContainer)db).transaction();
		_bTree = newBTree(0);
	}

	private BTree newBTree(int id) {
		return new BTree(systemTransaction(), id, new IntHandler());
	}
	
	private ObjectContainerBase container(){
		return transaction().container();
	}

	public boolean add(E obj) {
		int id = getID(obj);
		if(id == 0){
			container().store(obj);
			id = getID(obj);
		}
		_bTree.add(_transaction, new Integer(id));
		return true;
	}

	private int getID(Object obj) {
		return (int) container().getID(obj);
	}

	public boolean addAll(Collection<? extends E> collection) {
		for (E element : collection) {
			add(element);
		}
		return true;
	}

	public void clear() {
		throw new UnsupportedOperationException("Create new BigSet instead.");
	}

	public boolean contains(Object obj) {
		int id = getID(obj);
		if(id == 0){
			return false;
		}
		BTreeRange range = _bTree.search(transaction(), new Integer(id));
		return ! range.isEmpty();
	}

	public boolean containsAll(Collection<?> collection) {
		for (Object element : collection) {
			if(! contains(element)){
				return false;
			}
		}
		return true;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public Iterator<E> iterator() {
		MappingIterator i = new MappingIterator(_bTree.iterator(transaction())){
			protected Object map(Object current) {
				int id = ((Integer)current).intValue();
				Object obj = container().getByID(transaction(), id);
				container().activate(obj);
				return obj; 
			}
		};
		return new Iterator4JdkIterator(i);
	}

	public boolean remove(Object obj) {
		if(!contains(obj)){
			return false;
		}
		int id = getID(obj);
		_bTree.remove(transaction(), new Integer(id));
		return true;
	}

	public boolean removeAll(Collection<?> collection) {
		boolean res = false;
		for (Object element : collection) {
			if(remove(element)){
				res = true;
			}
		}
		return res;
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return _bTree.size(transaction());
	}

	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	public void write(WriteContext context) {
		int id = _bTree.getID();
		if(id == 0){
			_bTree.write(container().systemTransaction());
		}
		context.writeInt(_bTree.getID());
	}

	public void read(ReadContext context) {
		int id = context.readInt();
		if(_transaction == null){
			_transaction = context.transaction();
		}
		if(_bTree == null){
			_bTree = newBTree(id);
		}
	}
	
	private Transaction transaction(){
		return _transaction;
	}
	
	private Transaction systemTransaction(){
		return container().systemTransaction();
	}
	

}
