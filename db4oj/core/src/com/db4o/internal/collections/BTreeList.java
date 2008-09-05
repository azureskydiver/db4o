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
 */
public class BTreeList<E> extends PersistentBase  {

	

	private BTree _index;
	
	private BTree _payload;

	public BTreeList (Transaction trans){
		_index = new BTree(trans, new IntHandler());
		_payload = new BTree(trans, new PayloadKeyHandler());
	}


	public BTreeList(Transaction trans, int id) {
		setID(id);
		read(trans);
	}

	public byte getIdentifier() {
		return Const4.BTREE_LIST;
	}

	public int ownLength() {
		return Const4.ID_LENGTH * 2;  
	}

	public void readThis(Transaction trans, ByteArrayBuffer reader) {
		_index = new BTree(trans, reader.readInt(), new IntHandler());
		_payload = new BTree(trans, reader.readInt(), new PayloadKeyHandler());
	}

	public void writeThis(Transaction trans, ByteArrayBuffer writer) {
		writer.writeIDOf(trans, _index);
		writer.writeIDOf(trans, _payload);
	}
	
	public boolean add(Transaction trans, E obj) {
		PreparedComparison preparedComparison = new PreparedComparison() {
			public int compareTo(Object obj) {
				return -1;
			}
		};
		_payload.add(trans,preparedComparison, obj);
		return true;
	}

	public void add(int index, E element) {
		// TODO Auto-generated method stub
		
	}

	public boolean addAll(Collection<? extends E> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public E get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @sharpen.ignore
	 */
	public ListIterator<E> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @sharpen.ignore
	 */
	public ListIterator<E> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	public E remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public E set(int index, E element) {
		// TODO Auto-generated method stub
		return null;
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<E> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	public BTree index() {
		return _index;
	}

	public BTree payload() {
		return _payload;
	}
	
	
	private final class PayloadKeyHandler implements Indexable4 {
		public PreparedComparison prepareComparison(Context context, Object obj) {
			// TODO Auto-generated method stub
			return null;
		}

		public void writeIndexEntry(ByteArrayBuffer writer, Object obj) {
			// TODO Auto-generated method stub

		}

		public Object readIndexEntry(ByteArrayBuffer reader) {
			// TODO Auto-generated method stub
			return null;
		}

		public int linkLength() {
			// TODO Auto-generated method stub
			return 0;
		}

		public void defragIndexEntry(DefragmentContextImpl context) {
			// TODO Auto-generated method stub

		}
	}




}
