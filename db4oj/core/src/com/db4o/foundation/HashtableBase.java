/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;


/**
 * @exclude
 */
public class HashtableBase {
	
	private static final float FILL = 0.5F;
	
	// FIELDS ARE PUBLIC SO THEY CAN BE REFLECTED ON IN JDKs <= 1.1

	public int _tableSize;

	public int _mask;

	public int _maximumSize;

	public int _size;

	public HashtableIntEntry[] _table;

	public HashtableBase(int size) {
		size = newSize(size); // legacy for .NET conversion
		_tableSize = 1;
		while (_tableSize < size) {
			_tableSize = _tableSize << 1;
		}
		_mask = _tableSize - 1;
		_maximumSize = (int) (_tableSize * FILL);
		_table = new HashtableIntEntry[_tableSize];
	}

	public HashtableBase() {
		this(1);
	}
	
    /** @param cloneOnlyCtor */
	protected HashtableBase(DeepClone cloneOnlyCtor) {
	}
	
	public void clear() {
		_size = 0;
		Arrays4.fill(_table, null);
	}
	
	private final int newSize(int size) {
		return (int) (size / FILL);
	}

	public int size() {
		return _size;
	}
	
	protected HashtableIntEntry findWithSameKey(HashtableIntEntry newEntry) {
		HashtableIntEntry existing = _table[entryIndex(newEntry)];
		while (null != existing) {
			if (existing.sameKeyAs(newEntry)) {
				return existing;
			}
			existing = existing._next;
		}
		return null;
	}


	protected int entryIndex(HashtableIntEntry entry) {
		return entry._key & _mask;
	}
	
	protected void putEntry(HashtableIntEntry newEntry) {
		HashtableIntEntry existing = findWithSameKey(newEntry);
		if (null != existing) {
			replace(existing, newEntry);
		} else {
			insert(newEntry);
		}
	}
	
	private void insert(HashtableIntEntry newEntry) {
		_size++;
		if (_size > _maximumSize) {
			increaseSize();
		}
		int index = entryIndex(newEntry);
		newEntry._next = _table[index];
		_table[index] = newEntry;
	}
	
	private void replace(HashtableIntEntry existing, HashtableIntEntry newEntry) {
		newEntry._next = existing._next;
		HashtableIntEntry entry = _table[entryIndex(existing)];
		if (entry == existing) {
			_table[entryIndex(existing)] = newEntry;
		} else {
			while (entry._next != existing) {
				entry = entry._next;
			}
			entry._next = newEntry;
		}
	}

	private void increaseSize() {
		_tableSize = _tableSize << 1;
		_maximumSize = _maximumSize << 1;
		_mask = _tableSize - 1;
		HashtableIntEntry[] temp = _table;
		_table = new HashtableIntEntry[_tableSize];
		for (int i = 0; i < temp.length; i++) {
			reposition(temp[i]);
		}
	}

	/**
	 * Iterates through all the {@link Entry4 entries}.
	 *   
	 * @return {@link Entry4} iterator
	 * @see #values();
	 * @see #keys();
	 * #see {@link #valuesIterator()}
	 */
	public Iterator4 iterator(){
		return new HashtableIterator(_table);
	}
	
	private void reposition(HashtableIntEntry entry) {
		if (entry != null) {
			reposition(entry._next);
			entry._next = _table[entryIndex(entry)];
			_table[entryIndex(entry)] = entry;
		}
	}		
	
	public Iterator4 keys() {
		return Iterators.map(iterator(), new Function4() {
			public Object apply(Object current) {
				return ((Entry4)current).key();
			}
		});
	}
	
	public Iterable4 values() {
		return new Iterable4() {
			public Iterator4 iterator() {
				return valuesIterator();
			}
		};
	}

	/**
	 * Iterates through all the values.
	 * 
	 * @return value iterator
	 */
	public Iterator4 valuesIterator() {
		return Iterators.map(iterator(), new Function4() {
			public Object apply(Object current) {
				return ((Entry4)current).value();
			}
		});
	}


	public String toString() {
		return Iterators.join(iterator(), "{", "}", ", ");
	}
}
