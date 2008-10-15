/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.collections;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.handlers.*;
import com.db4o.marshall.*;

/**
 * @exclude
 * @decaf.ignore.jdk11
 * @sharpen.partial
 */
public class BigSet<E> implements Set<E>, BigSetPersistence {
	
	private BTree _bTree;
	
	private Transaction _transaction;

	public BigSet(LocalObjectContainer db) {
		if(db == null){
			return;
		}
		_transaction = db.transaction();
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
		bTree().add(_transaction, new Integer(id));
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
		bTree().clear(transaction());
	}
	
	public boolean contains(Object obj) {
		int id = getID(obj);
		if(id == 0){
			return false;
		}
		BTreeRange range = bTree().search(transaction(), new Integer(id));
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

	/**
	 * @sharpen.property
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * @sharpen.ignore
	 */
	public Iterator<E> iterator() {
		return new Iterator4JdkIterator(elements());
	}

	/**
	 * @sharpen.ignore
	 */
	private Iterator4 elements() {
	    return new MappingIterator(bTreeIterator()) {
			protected Object map(Object current) {
				int id = ((Integer)current).intValue();
				return element(id); 
			}
		};
    }

	private Iterator4 bTreeIterator() {
	    return bTree().iterator(transaction());
    }

	public boolean remove(Object obj) {
		if(!contains(obj)){
			return false;
		}
		int id = getID(obj);
		bTree().remove(transaction(), new Integer(id));
		return true;
	}

	/**
	 * @sharpen.ignore
	 */
	public boolean removeAll(Collection<?> collection) {
		boolean res = false;
		for (Object element : collection) {
			if(remove(element)){
				res = true;
			}
		}
		return res;
	}

	/**
	 * @sharpen.ignore
	 */
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return bTree().size(transaction());
	}

	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
     * @see com.db4o.internal.collections.BigSetPersistence#write(com.db4o.marshall.WriteContext)
     */
	public void write(WriteContext context) {
		int id = bTree().getID();
		if(id == 0){
			bTree().write(container().systemTransaction());
		}
		context.writeInt(bTree().getID());
	}

	/* (non-Javadoc)
     * @see com.db4o.internal.collections.BigSetPersistence#read(com.db4o.marshall.ReadContext)
     */
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

	/* (non-Javadoc)
     * @see com.db4o.internal.collections.BigSetPersistence#invalidate()
     */
	public void invalidate() {
		_bTree = null;
	}

	private BTree bTree() {
		if(_bTree == null){
			throw new IllegalStateException();
		}
		return _bTree;
	}

	private Object element(int id) {
	    Object obj = container().getByID(transaction(), id);
	    container().activate(obj);
	    return obj;
    }
	

}
