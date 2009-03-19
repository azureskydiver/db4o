/* Copyright (C) 2009  db4objects Inc.  http://www.db4o.com */

package com.db4o.collections;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.ta.*;

/**
 * extends ArrayList with Transparent Activation and
 * Transparent Persistence support
 * @exclude
 * @sharpen.ignore
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableArrayList <E> extends ArrayList<E> implements Activatable {
	
	private transient Activator _activator;

	public ActivatableArrayList() {
	}

	public ActivatableArrayList(int size) {
		super(size);
	}

	public ActivatableArrayList(Collection<E> list) {
		super(list);
	}

	public void activate(ActivationPurpose purpose) {
		if(_activator != null) {
			_activator.activate(purpose);
		}
	}

	public void bind(Activator activator) {
    	if (_activator == activator) {
    		return;
    	}
    	if (activator != null && _activator != null) {
            throw new IllegalStateException();
        }
		_activator = activator;
	}
	
	@Override
	public boolean add(E e) {
		activate(ActivationPurpose.WRITE);
		return super.add(e);
	};
	
	public void add(int index, E element) {
		activate(ActivationPurpose.WRITE);
		super.add(index, element);
	};
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		activate(ActivationPurpose.WRITE);
		return super.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		activate(ActivationPurpose.WRITE);
		return super.addAll(index, c);
	}
	
	@Override
	public void clear() {
		activate(ActivationPurpose.WRITE);
		super.clear();
	}

	@Override
	public Object clone() {
		activate(ActivationPurpose.READ);
		ActivatableArrayList<E> cloned = (ActivatableArrayList<E>) super.clone();
		cloned._activator = null;
		return cloned;
	}
	
	@Override
	public boolean contains(Object o) {
		activate(ActivationPurpose.READ);
		return super.contains(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		activate(ActivationPurpose.READ);
		return super.containsAll(c);
	}
	
	@Override
	public boolean equals(Object o) {
		activate(ActivationPurpose.READ);
		return super.equals(o);
	}
	
	@Override
	public E get(int index) {
		activate(ActivationPurpose.READ);
		return super.get(index);
	}
	
	@Override
	public int hashCode() {
		activate(ActivationPurpose.READ);
		return super.hashCode();
	}
	
	@Override
	public int indexOf(Object o) {
		activate(ActivationPurpose.READ);
		return super.indexOf(o);
	}
	
	@Override
	public Iterator<E> iterator() {
		activate(ActivationPurpose.READ);
		return new ActivatingIterator(super.iterator());
	}
	
	@Override
	public boolean isEmpty() {
		activate(ActivationPurpose.READ);
		return super.isEmpty();
	}
	
	@Override
	public int lastIndexOf(Object o) {
		activate(ActivationPurpose.READ);
		return super.lastIndexOf(o);
	}
	
	@Override
	public ListIterator<E> listIterator() {
		activate(ActivationPurpose.READ);
		return new ActivatingListIterator(super.listIterator());
	}
	
	@Override
	public ListIterator<E> listIterator(int index) {
		activate(ActivationPurpose.READ);
		return new ActivatingListIterator(super.listIterator(index));
	}
	
	@Override
	public E remove(int index) {
		activate(ActivationPurpose.WRITE);		
		return super.remove(index);
	}
	
	@Override
	public boolean remove(Object o) {
		activate(ActivationPurpose.WRITE);
		return super.remove(o);
	}
	
	@Override
	public E set(int index, E element) {
		activate(ActivationPurpose.WRITE);
		return super.set(index, element);
	};
	
	@Override
	public int size() {
		activate(ActivationPurpose.READ);
		return super.size();
	}
	
	@Override
	public Object[] toArray() {
		activate(ActivationPurpose.READ);
		return super.toArray();
	}
	
	@Override
	public <T extends Object> T[] toArray(T[] a) {
		activate(ActivationPurpose.READ);
		return super.toArray(a);
	};
	
	@Override
	public boolean removeAll(Collection<?> c) {
		activate(ActivationPurpose.WRITE);
		return super.removeAll(c);
	}

	private class ActivatingIterator implements Iterator<E> {

		protected final Iterator<E> _iterator;
		
		public ActivatingIterator(Iterator<E> iterator) {
			_iterator = iterator;
		}
		
		public boolean hasNext() {
			return _iterator.hasNext();
		}

		public E next() {
			return _iterator.next();
		}

		public void remove() {
			activate(ActivationPurpose.WRITE);
			_iterator.remove();
		}
	}

	private class ActivatingListIterator extends ActivatingIterator implements ListIterator<E> {

		public ActivatingListIterator(Iterator<E> iterator) {
			super(iterator);
		}

		public void add(E o) {
			activate(ActivationPurpose.WRITE);
			listIterator().add(o);
		}

		public boolean hasPrevious() {
			return listIterator().hasPrevious();
		}

		public int nextIndex() {
			return listIterator().nextIndex();
		}

		public E previous() {
			return listIterator().previous();
		}

		public int previousIndex() {
			return listIterator().previousIndex();
		}

		public void set(E o) {
			activate(ActivationPurpose.WRITE);
			listIterator().set(o);
		}
		
		private ListIterator<E> listIterator() {
			return (ListIterator<E>) _iterator;
		}
	}
}
