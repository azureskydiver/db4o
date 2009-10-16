/* Copyright (C) 2007 Versant Inc. http://www.db4o.com */
package com.db4o.foundation;

/**
 * @exclude
 */
public class BlockingQueue implements Queue4 {
    
	protected NonblockingQueue _queue = new NonblockingQueue();

	protected Lock4 _lock = new Lock4();
	
	protected boolean _stopped;

	public void add(final Object obj) {
		if(obj == null){
			throw new IllegalArgumentException();
		}
		_lock.run(new Closure4() {
			public Object run() {
				_queue.add(obj);
				_lock.awake();
				return null;
			}
		});
	}

	public boolean hasNext() {
		Boolean hasNext = (Boolean) _lock.run(new Closure4() {
			public Object run() {
				return new Boolean(_queue.hasNext());
			}
		});
		return hasNext.booleanValue();
	}

	public Iterator4 iterator() {
		return (Iterator4) _lock.run(new Closure4() {
			public Object run() {
				return _queue.iterator();
			}
		});
	}

	public Object next() throws BlockingQueueStoppedException {
		return _lock.run(new Closure4() {
			public Object run() {
				while(true){
					if (_queue.hasNext()) {
						return _queue.next();
					}
					if(_stopped) {
						throw new BlockingQueueStoppedException();
					}
					_lock.snooze(Integer.MAX_VALUE);
				}
			}
		});
	}
	
	public void stop(){
		_lock.run(new Closure4() {
			public Object run() {
				_stopped = true;
				_lock.awake();
				return null;
			}
		});
	}

	public Object nextMatching(final Predicate4 condition) {
		return _lock.run(new Closure4() {
			public Object run() {
				return _queue.nextMatching(condition);
			}
		});
	}
}
