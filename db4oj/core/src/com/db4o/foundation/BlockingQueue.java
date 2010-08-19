/* Copyright (C) 2007 Versant Inc. http://www.db4o.com */
package com.db4o.foundation;

/**
 * @exclude
 */
public class BlockingQueue<T> implements BlockingQueue4<T> {
    
	protected NonblockingQueue<T> _queue = new NonblockingQueue<T>();

	protected Lock4 _lock = new Lock4();
	
	protected boolean _stopped;

	public void add(final T obj) {
		if(obj == null){
			throw new IllegalArgumentException();
		}
		_lock.run(new Closure4<Void>() {
			public Void run() {
				_queue.add(obj);
				_lock.awake();
				return null;
			}
		});
	}

	public boolean hasNext() {
		return _lock.run(new Closure4<Boolean>() {
			public Boolean run() {
				return _queue.hasNext();
			}
		});
	}

	public Iterator4<T> iterator() {
		return _lock.run(new Closure4<Iterator4<T>>() {
			public Iterator4<T> run() {
				return _queue.iterator();
			}
		});
	}

	public T next(final long timeout) throws BlockingQueueStoppedException {
		return (T) _lock.run(new Closure4<T>() {
			public T run() {
				long timeLeft = timeout;
				long now = System.currentTimeMillis();
				while (timeLeft > 0) {
					if (_queue.hasNext()) {
						return (T) _queue.next();
					}
					if(_stopped) {
						throw new BlockingQueueStoppedException();
					}
					_lock.snooze(timeLeft);
					long l = now;
					now = System.currentTimeMillis();
					timeLeft -= now-l;
				}
				return null;
			}
		});
	}

	public T next() throws BlockingQueueStoppedException {
		return (T) _lock.run(new Closure4<T>() {
			public T run() {
				while(true){
					if (_queue.hasNext()) {
						return (T) _queue.next();
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
		_lock.run(new Closure4<Void>() {
			public Void run() {
				_stopped = true;
				_lock.awake();
				return null;
			}
		});
	}

	public T nextMatching(final Predicate4<T> condition) {
		return _lock.run(new Closure4<T>() {
			public T run() {
				return _queue.nextMatching(condition);
			}
		});
	}
}
