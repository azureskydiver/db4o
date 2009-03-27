package com.db4o.collections;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.ta.*;

public class ActivatingListIterator<E> extends ActivatingIterator<E> implements ListIterator<E> {

	public ActivatingListIterator(Activatable activatable, Iterator<E> iterator) {
		super(activatable, iterator);
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
