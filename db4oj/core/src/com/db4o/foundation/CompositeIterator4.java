package com.db4o.foundation;


public class CompositeIterator4 implements Iterator4 {

	private final Iterator4 _iterators;	

	private Iterator4 _currentIterator;

	public CompositeIterator4(Iterator4 iterators) {
		if (null == iterators) {
			throw new ArgumentNullException();
		}
		_iterators = iterators;
	}

	public boolean moveNext() {
		if (null == _currentIterator) {
			if (!_iterators.moveNext()) {
				return false;
			}
			_currentIterator = nextIterator(_iterators.current());
		}
		if (!_currentIterator.moveNext()) {
			_currentIterator = null;
			return moveNext();
		}
		return true;
	}

	public Object current() {
		return _currentIterator.current();
	}
	
	protected Iterator4 nextIterator(final Object current) {
		return (Iterator4)current;
	}
}