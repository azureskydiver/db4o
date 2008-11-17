package com.db4o.foundation;

/**
 * @decaf.ignore
 */
public class CircularBuffer4<T> {
	
	private final T[] _buffer;
	private int _head;
	private int _tail;

	public CircularBuffer4(int size) {
		_buffer = (T[])new Object[size + 1];
    }

	public void addFirst(T value) {
		final int newHead = circularIndex(_head - 1);
		if (newHead == _tail) {
			throw new IllegalStateException();
		}
		_head = newHead;
		_buffer[index(_head)] = value;
    }

	private int circularIndex(final int index) {
	    return index % _buffer.length;
    }

	private int index(int i) {
		return i < 0 ? _buffer.length + i : i;
    }

	public T removeLast() {
		assertNotEmpty();
		_tail = circularIndex(_tail - 1);
		return erase(_tail);
    }

	private void assertNotEmpty() {
	    if (isEmpty()) {
			throw new IllegalStateException();
		}
    }

	private boolean isEmpty() {
	    return index(_head) == index(_tail);
    }
	
	public T removeFirst() {
		assertNotEmpty();
		final T erased = erase(_head);
		_head = circularIndex(_head + 1);
		return erased;
    }

	private T erase(final int index) {
	    final int bufferIndex = index(index);
		final T erasedValue = _buffer[bufferIndex];
		_buffer[bufferIndex] = null;
		return erasedValue;
    }

	public void remove(T value) {
		int current = index(_head);
		int tail = index(_tail);
		while (current != tail) {
			if (value.equals(_buffer[current])) {
				remove(current);
				return;
			}
			current = circularIndex(current + 1);
		}
		
		throw new IllegalArgumentException();
    }

	private void remove(int index) {
		if (index(_tail - 1) == index) {
			removeLast();
			return;
		}
		
		if (index == index(_head)) {
			removeFirst();
			return;
		}
		int current = index;
		int tail = index(_tail);
		while(current != tail){
			final int next = circularIndex(current + 1);
			_buffer[current] = _buffer[next];
			current = next;
		}
		_tail = circularIndex(_tail - 1);
    }
}
