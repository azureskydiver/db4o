/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;


/**
 * @exclude
 */
public class HashtableIterator implements Iterator4 {
	
	private final HashtableIntEntry[] _table;
	
	HashtableIntEntry _currentEntry;
	
	int _currentIndex;
	
	public HashtableIterator(HashtableIntEntry[] table) {
		_table = table;
		checkInvalidTable();
	}
	
	private void checkInvalidTable(){
		if(_table == null || _table.length == 0){
			positionBeyondLast();
		}
	}

	public Object current() {
		if (_currentEntry == null) {
			throw new IllegalStateException();
		}
		return _currentEntry;
	}

	public boolean moveNext() {
		if(isBeyondLast()){
			return false;
		}
		if(_currentEntry != null){
			_currentEntry = _currentEntry._next;
		}
		while(_currentEntry == null){
			_currentIndex++;
			if(_currentIndex >= _table.length){
				positionBeyondLast();
				return false;
			}
			_currentEntry = _table[_currentIndex];
		}
		return true;
	}

	public void reset() {
		_currentEntry = null;
		_currentIndex = 0;
		checkInvalidTable();
	}
	
	private boolean isBeyondLast(){
		return _currentIndex == -1;
	}
	
	private void positionBeyondLast(){
		_currentIndex = -1;		
	}

}
