/* Copyright (C) 2009  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.transparent;

import java.util.*;

import com.db4o.*;
import com.db4o.activation.*;
import com.db4o.collections.*;
import com.db4o.config.*;
import com.db4o.db4ounit.jre12.collections.*;
import com.db4o.query.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableListTestCase extends AbstractDb4oTestCase {
	
	String[] NAMES = new String[] {"one", "two", "three"};
	
	public static class Item <E>{
		
		public List<E> _list;
		
	}
	
	public static interface CollectionElement{
		
	}
	
	public static class Element implements CollectionElement{
		
		public String _name;
		
		public Element(String name){
			_name = name;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(! (obj instanceof Element)){
				return false;
			}
			Element other = (Element)obj;
			return _name.equals(other._name);
		}
		
		@Override
		public String toString() {
			return "Element " + _name;
		}
		
		@Override
		public int hashCode() {
			return _name.hashCode();
		}
		
	}
	
	public static class ActivatableElement implements Activatable, CollectionElement{
		
		private Activator _activator;

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
		
		public String _name;
		
		public ActivatableElement(String name){
			_name = name;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(! (obj instanceof ActivatableElement)){
				return false;
			}
			activate(ActivationPurpose.READ);
			ActivatableElement other = (ActivatableElement)obj;
			other.activate(ActivationPurpose.READ);
			return _name.equals(other._name);
		}
		
		@Override
		public int hashCode() {
			activate(ActivationPurpose.READ);
			return _name.hashCode();
		}
		
		@Override
		public String toString() {
			activate(ActivationPurpose.READ);
			return "ActivatableElement " + _name;
		}
		
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentPersistenceSupport());
	}
	
	@Override
	protected void store() throws Exception {
		ActivatableArrayList<CollectionElement> list = newActivatableArrayList();
		Item<CollectionElement> item = new Item<CollectionElement>();
		item._list = list;
		store(item);
		super.store();
	}

	private ActivatableArrayList<CollectionElement> newActivatableArrayList() {
		ActivatableArrayList<CollectionElement> list = new ActivatableArrayList<CollectionElement>();
		for (CollectionElement element: elements()) {
			list.add(element);
		}
		return list;
	}
	
	private List<CollectionElement> elements(){
		List elements = new ArrayList();
		for (String name  : NAMES) {
			elements.add(new Element(name));
		}
		for (String name  : NAMES) {
			elements.add(new ActivatableElement(name));
		}
		return elements;
	}
	
	public void testCorrectContent(){
		assertAreEqual(elements(), singleList());
	}

	private void assertAreEqual(List<CollectionElement> elements,
			List<CollectionElement> singleList) {
		IteratorAssert.areEqual(elements.iterator(), singleList.iterator());
	}
	
	public void testListIsNotActivated(){
		Assert.isFalse(db().isActive(singleList()));
	}

	private Item singleItem() {
		return retrieveOnlyInstance(Item.class);
	}
	
	private List<CollectionElement> singleList(){
		return singleItem()._list;
	}
	
	public void testActivatableElementsAreNotActivated(){
		// trigger activation of ArrayList and
		// all elements that are not Activatable
		singleList().iterator();
		
		long id = anyActivatableElementId();
		Object element = db().getByID(id);
		Assert.isFalse(db().isActive(element));
	}

	private long anyActivatableElementId() {
		Query q = newQuery(ActivatableElement.class);
		ObjectSet<Object> objectSet = q.execute();
		long[] ids = objectSet.ext().getIDs();
		return ids[0];
	}
	
	public void testAdd() throws Exception{
		singleList().add(new Element("four"));
		reopen();
		List<CollectionElement> elements = elements();
		elements.add(new Element("four"));
		IteratorAssert.areEqual(elements.iterator(), singleList().iterator());		
	}
	
	public void testAddAtIndex() throws Exception{
		singleList().add(0, new Element("four"));
		reopen();
		List<CollectionElement> elements = elements();
		elements.add(0, new Element("four"));
		IteratorAssert.areEqual(elements.iterator(), singleList().iterator());		
	}
	
	public void testAddAll() throws Exception{
		singleList().addAll(elements());
		reopen();
		List<CollectionElement> elements = elements();
		elements.addAll(elements());
		IteratorAssert.areEqual(elements.iterator(), singleList().iterator());		
	}
	
	public void testAddAllAtIndex() throws Exception{
		singleList().addAll(2,elements());
		reopen();
		List<CollectionElement> elements = elements();
		elements.addAll(2, elements());
		IteratorAssert.areEqual(elements.iterator(), singleList().iterator());		
	}
	
	public void testClear() throws Exception{
		singleList().clear();
		reopen();
		IteratorAssert.areEqual(new ArrayList().iterator(), singleList().iterator());		
	}
	
	public void testContains(){
		Assert.isTrue(singleList().contains(new Element("one")));
		Assert.isFalse(singleList().contains(new Element("four")));
	}
	
	public void testContainsAll(){
		Assert.isTrue(singleList().containsAll(elements()));
		List<CollectionElement> elements = elements();
		elements.add(new Element("four"));
		Assert.isFalse(singleList().containsAll(elements));
	}
	
	public void testEquals(){
		Assert.isTrue(singleList().equals(elements()));
	}
	
	public void testHashCode(){
		Assert.areEqual(elements().hashCode(), singleList().hashCode());
	}
	
	public void testIndexOf(){
		Assert.areEqual(1, singleList().indexOf(new Element("two")));
	}
	
	public void testIsEmpty(){
		Assert.isFalse(singleList().isEmpty());
	}
	
	public void testLastIndexOf() throws Exception{
		List<CollectionElement> list = singleList();
		list.add(2, new Element("one"));
		reopen();
		Assert.areEqual(2, singleList().lastIndexOf(new Element("one")));
	}
	
	public void testListIterator(){
		IteratorAssert.areEqual(elements().listIterator(), singleList().listIterator());
	}
	
	public void testListIteratorAtIndex(){
		IteratorAssert.areEqual(elements().listIterator(1), singleList().listIterator(1));
	}
	
	public void testRemove() throws Exception{
		Element element = new Element("one");
		singleList().remove(element);
		reopen();
		List<CollectionElement> list = elements();
		list.remove(element);
		IteratorAssert.areEqual(list.iterator(), singleList().iterator());
	}
	
	public void testRemoveAtIndex() throws Exception{
		singleList().remove(0);
		reopen();
		List<CollectionElement> list = elements();
		list.remove(0);
		IteratorAssert.areEqual(list.iterator(), singleList().iterator());
	}
	
	public void testRemoveAll(){
		List<CollectionElement> remove = new ArrayList<CollectionElement>();
		remove.add(new Element("one"));
		List<CollectionElement> singleList = singleList();
		singleList.removeAll(remove);
		List<CollectionElement> elements = elements(); 
		elements.removeAll(remove);
		assertAreEqual(elements, singleList);
	}
	
	public void testRetainAll(){
		List<CollectionElement> retain = new ArrayList<CollectionElement>();
		retain.add(new Element("one"));
		List<CollectionElement> singleList = singleList();
		singleList.retainAll(retain);
		List<CollectionElement> elements = elements(); 
		elements.retainAll(retain);
		assertAreEqual(elements, singleList);
	}
	
	public void testSet(){
		List<CollectionElement> singleList = singleList();
		Element element = new Element("four");
		singleList.set(1, element);
		List<CollectionElement> elements = elements();
		elements.set(1, element);
		assertAreEqual(elements, singleList);
	}
	
	public void testSize(){
		Assert.areEqual(elements().size(), singleList().size());
	}
	
	public void testSubList(){
		IteratorAssert.areEqual(elements().subList(0,1).iterator(), singleList().subList(0, 1).iterator());
	}
	
	public void testToArray(){
		Object[] singleListArray = singleList().toArray();
		Object[] elementsArray = elements().toArray();
		ArrayAssert.areEqual(elementsArray, singleListArray);
	}
	
	public void testToArrayWithArrayParam(){
		CollectionElement[] singleListArray = new CollectionElement[elements().size()];
		CollectionElement[] elementsArray = new CollectionElement[elements().size()];
		singleList().toArray(singleListArray);
		elements().toArray(elementsArray);
		ArrayAssert.areEqual(elementsArray, singleListArray);
	}
	
	public void testToString(){
		Assert.areEqual(elements().toString(), singleList().toString());
	}
	
	public void testTrimToSize(){
		List<CollectionElement> singleList = singleList();
		((ArrayList)singleList).trimToSize();
		assertAreEqual(elements(), singleList);
	}
	
	public void testEnsureCapacity(){
		List<CollectionElement> singleList = singleList();
		((ArrayList)singleList).ensureCapacity(10);
		assertAreEqual(elements(), singleList);
	}

}
