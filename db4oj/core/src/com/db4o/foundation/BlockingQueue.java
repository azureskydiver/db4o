/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.foundation;

import com.db4o.ext.*;

/**
 * @exclude
 */
public class BlockingQueue implements Queue4 {
	private NonblockingQueue _queue = new NonblockingQueue();

	private Lock4 _lock = new Lock4();

	public void add(final Object obj) {
		_lock.run(new SafeClosure4() {
			public Object run() {
				_queue.add(obj);
				_lock.awake();
				return null;
			}
		});
	}

	public boolean hasNext() {
		Boolean hasNext = (Boolean) _lock.run(new SafeClosure4() {
			public Object run() {
				return new Boolean(_queue.hasNext());
			}
		});
		return hasNext.booleanValue();
	}

	public Iterator4 iterator() {
		return (Iterator4) _lock.run(new SafeClosure4() {
			public Object run() {
				return _queue.iterator();
			}
		});
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
			throw new Db4oUnexpectedException(e);
		}
	}
}
