/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.collections;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class BTreeList<E> extends PersistentBase implements BTreeStructureListener{
	
	private BTree _index;
	
	private BTree _payload;

	public BTreeList (Transaction trans){
		index(new BTree(trans, new IndexHandler()));
		payload(new BTree(trans, new PayloadHandler()));
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
		BTree index = new BTree(trans, reader.readInt(), new IndexHandler());
		index.read(trans);
		index(index);
		BTree payload = new BTree(trans, reader.readInt(), new PayloadHandler());
		payload.read(trans);
		payload(payload);
	}
	
	private void index(BTree btree){
		_index = btree;
	}
	
	private void payload(BTree btree){
		_payload = btree;
		_payload.structureListener(this);
	}

	public void writeThis(Transaction trans, ByteArrayBuffer writer) {
		writer.writeIDOf(trans, _index);
		writer.writeIDOf(trans, _payload);
	}
	
	public void commit(Transaction trans){
		_index.commit(trans);
		_payload.commit(trans);
        setStateDirty();
        write(trans.systemTransaction());
	}
	
	public boolean add(Transaction trans, E obj) {
		PreparedComparison preparedComparison = new PreparedComparison() {
			public int compareTo(Object other) {
				return 1;
			}
		};
		_payload.add(trans,preparedComparison, obj);
		return true;
	}

	public void add(Transaction trans, int index, E element) {
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
		
		// _index.remove(trans, key)
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

	public E get(Transaction trans, int index) {
		BTreeRange range = _index.search(trans, new IndexEntry(index, 0));
		BTreePointer pointer = range.lastPointer();
		if(pointer == null){
			pointer = range.smaller().lastPointer();
		}
		IndexEntry entry = (IndexEntry) pointer.key();
		BTreeNode payloadNode = _payload.produceNode(entry._nodeId);
		return (E)payloadNode.key(trans, index - entry._listIndex);
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
	
	public void notifyDeleted(Transaction trans, BTreeNode node) {
		// TODO Auto-generated method stub
		
	}

	public void notifySplit(Transaction trans, BTreeNode originalNode, BTreeNode newRightNode) {
		
		// System.out.println("Split " + originalNode.getID() + " " + newRightNode.getID());
		
		if(! originalNode.isLeaf()){
			return;
		}
		
		final int originalId = originalNode.getID();
		final IntByRef originalListIndex = new IntByRef(-1);
		_index.traverseKeys(trans, new Visitor4() {
			public void visit(Object obj) {
				if(originalListIndex.value >=0){
					return;
				}
				IndexEntry entry = (IndexEntry) obj;
				if(entry._nodeId == originalId){
					originalListIndex.value = entry._listIndex;
				}
			}
		});
		IndexEntry newIndexEntry = new IndexEntry(originalListIndex.value + originalNode.size(trans), newRightNode.getID());
		_index.add(trans, newIndexEntry);
		
		
	}

	public void notifyCountChanged(Transaction trans, final BTreeNode node, int diff) {
		
		// System.out.println("count changed id:" + node.getID() + " count:" + (node.count() - diff) + " new count:" + node.count() );
		
		final BooleanByRef found = new BooleanByRef();
		final Collection4 modifiedEntries = new Collection4();
		_index.traverseKeys(trans, new Visitor4() {
			public void visit(Object obj) {
				if(found.value){
					modifiedEntries.add(obj);
					return;
				} 
				IndexEntry indexEntry = (IndexEntry) obj;
				if(indexEntry._nodeId == node.getID()){
					found.value = true;
				}
			}
		});
		Iterator4 i = modifiedEntries.iterator();
		while(i.moveNext()){
			IndexEntry entry = (IndexEntry) i.current();
			_index.remove(trans, entry);
			_index.add(trans, new IndexEntry(entry._listIndex + diff, entry._nodeId));
		}
		if(! found.value){
			_index.add(trans, new IndexEntry(0, node.getID()));
		}
	}
	
	private static final class PayloadHandler implements Indexable4 {
		public PreparedComparison prepareComparison(Context context, Object obj) {
			throw new NotImplementedException();
		}

		public void writeIndexEntry(ByteArrayBuffer writer, Object obj) {
			writer.writeInt(((Integer)obj).intValue());
		}

		public Object readIndexEntry(ByteArrayBuffer reader) {
			return new Integer(reader.readInt());
		}

		public int linkLength() {
			return Const4.ID_LENGTH;
		}

		public void defragIndexEntry(DefragmentContextImpl context) {
			throw new NotImplementedException();

		}
	}
	
	private static final class IndexHandler implements Indexable4 {
		public PreparedComparison prepareComparison(Context context, Object obj) {
			final IndexEntry indexEntry = (IndexEntry)obj;
			return new PreparedComparison() {
				public int compareTo(Object other) {
					return indexEntry._listIndex - ((IndexEntry)other)._listIndex;
				}
			};
		}

		public void writeIndexEntry(ByteArrayBuffer writer, Object obj) {
			IndexEntry indexEntry = (IndexEntry)obj;
			writer.writeInt(indexEntry._listIndex);
			writer.writeInt(indexEntry._nodeId);
		}

		public Object readIndexEntry(ByteArrayBuffer reader) {
			return new IndexEntry(reader.readInt(), reader.readInt());
		}

		public int linkLength() {
			return Const4.INT_LENGTH + Const4.ID_LENGTH;
		}

		public void defragIndexEntry(DefragmentContextImpl context) {
			throw new NotImplementedException();
		}
	}

	
	public static class IndexEntry {
		
		public int _listIndex;
		
		public int _nodeId;

		public IndexEntry(int listIndex, int nodeId) {
			_listIndex = listIndex;
			_nodeId = nodeId;
		}
		
		public String toString() {
			return "{ix: " + _listIndex + " id:" + _nodeId + "}";
		}

	}

}
