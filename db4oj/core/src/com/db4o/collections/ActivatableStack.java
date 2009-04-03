/* Copyright (C) 2009   db4objects Inc.   http://www.db4o.com */
package com.db4o.collections;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.ta.*;

/**
 * extends Stack with Transparent Activation and
 * Transparent Persistence support.
 * @exclude
 * @sharpen.ignore
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableStack<E> extends Stack<E> implements ActivatableList<E> {

	private transient Activator _activator;

	public ActivatableStack() {
	}

	public void activate(ActivationPurpose purpose) {
		ActivatableSupport.activate(_activator, purpose);
	}

	public void bind(Activator activator) {
		_activator = ActivatableSupport.validateForBind(_activator, activator);
	}	

	public boolean add(E e) {
		activate(ActivationPurpose.WRITE);
		return super.add(e);
	}
	
	public void add(int index, E element) {
		activate(ActivationPurpose.WRITE);
		super.add(index, element);
	}
	
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
		ActivatableStack<E> cloned = (ActivatableStack<E>) super.clone();
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
		return new ActivatingIterator(this, super.iterator());
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
		return new ActivatingListIterator(this, super.listIterator());
	}
	
	@Override
	public ListIterator<E> listIterator(int index) {
		activate(ActivationPurpose.READ);
		return new ActivatingListIterator(this, super.listIterator(index));
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
	}
	
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
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		activate(ActivationPurpose.WRITE);
		return super.removeAll(c);
	}
	
	@Override
	public E push(E item) {
		activate(ActivationPurpose.WRITE);
		return super.push(item);
	}
	
	@Override
	public synchronized E pop() {
		activate(ActivationPurpose.READ);
		return super.pop();
	}
	
	@Override
	public synchronized E peek() {
		activate(ActivationPurpose.READ);
		return super.peek();
	}
}
