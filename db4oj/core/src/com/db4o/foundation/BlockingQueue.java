/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.foundation;

/**
 * @exclude
 */
public class BlockingQueue implements Queue4 {
	private NonblockingQueue _queue = new NonblockingQueue();

	private Lock4 _lock = new Lock4();

	public void add(final Object obj) {
		try {
			_lock.run(new Closure4() {
				public Object run() throws Exception {
					_queue.add(obj);
					_lock.awake();
					return null;
				}
			});
		} catch (Exception e) {
			throw new IllegalStateException();
		}
	}

	public boolean hasNext() {
		try {
			Boolean hasNext = (Boolean) _lock.run(new Closure4() {
				public Object run() throws Exception {
					return new Boolean(_queue.hasNext());
				}
			});
			return hasNext.booleanValue();
		} catch (Exception e) {
			throw new IllegalStateException();
		}
	}

	public Iterator4 iterator() {
		try {
			return (Iterator4) _lock.run(new Closure4() {
				public Object run() throws Exception {
					return _queue.iterator();
				}
			});
		} catch (Exception e) {
			throw new IllegalStateException();
		}
	}

	public Object next() {
		try {
			return _lock.run(new Closure4() {
				public Object run() throws Exception {
					if (_queue.hasNext()) {
						return _queue.next();
					}
					_lock.snooze(Integer.MAX_VALUE);
					return _queue.next();
				}
			});
		} catch (Exception e) {
			throw new IllegalStateException();
		}
	}
}
