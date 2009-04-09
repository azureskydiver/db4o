package com.db4o.db4ounit.jre12.collections.transparent;

import java.util.*;

import com.db4o.db4ounit.jre12.collections.*;

import db4ounit.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public abstract class ActivatableCollectionAPITestUnit<C extends Collection<CollectionElement>> extends ActivatableCollectionTestCaseBase<C> {
	
	public void testCorrectContent(){
		assertAreEqual(newPlainCollection(), singleCollection());
	}

	public void testListIsNotActivated(){
		Assert.isFalse(db().isActive(singleCollection()));
	}

	public void testActivatableElementsAreNotActivated(){
		// trigger activation of Collection and
		// all elements that are not Activatable
		singleCollection().iterator();
		
		long id = ActivatableCollectionTestUtil.anyActivatableElementId(db());
		Object element = db().getByID(id);
		Assert.isFalse(db().isActive(element));
	}

	public void testAdd() throws Exception{
		singleCollection().add(new Element("four"));
		reopen();
		C elements = newPlainCollection();
		elements.add(new Element("four"));
		IteratorAssert.areEqual(elements.iterator(), singleCollection().iterator());		
	}
	
	public void testAddAll() throws Exception{
		singleCollection().addAll(newPlainCollection());
		reopen();
		C elements = newPlainCollection();
		elements.addAll(newPlainCollection());
		IteratorAssert.areEqual(elements.iterator(), singleCollection().iterator());		
	}
	
	public void testClear() throws Exception{
		singleCollection().clear();
		reopen();
		IteratorAssert.areEqual(new ArrayList().iterator(), singleCollection().iterator());		
	}

	public void testContains(){
		Assert.isTrue(singleCollection().contains(new Element("one")));
		Assert.isFalse(singleCollection().contains(new Element("four")));
	}
	
	public void testContainsAll(){
		Assert.isTrue(singleCollection().containsAll(newPlainCollection()));
		C elements = newPlainCollection();
		elements.add(new Element("four"));
		Assert.isFalse(singleCollection().containsAll(elements));
	}
	
	public void testEquals(){
		Assert.isTrue(singleCollection().equals(newPlainCollection()));
	}
	
	public void testHashCode(){
		Assert.areEqual(newPlainCollection().hashCode(), singleCollection().hashCode());
	}
	
	public void testIsEmpty(){
		Assert.isFalse(singleCollection().isEmpty());
	}
	
	public void testRemove() throws Exception{
		Element element = new Element("one");
		singleCollection().remove(element);
		reopen();
		C list = newPlainCollection();
		list.remove(element);
		IteratorAssert.areEqual(list.iterator(), singleCollection().iterator());
	}
	
	public void testRemoveAll(){
		List<CollectionElement> remove = new ArrayList<CollectionElement>();
		remove.add(new Element("one"));
		C singleList = singleCollection();
		singleList.removeAll(remove);
		C elements = newPlainCollection(); 
		elements.removeAll(remove);
		assertAreEqual(elements, singleList);
	}
	
	public void testRetainAll(){
		List<CollectionElement> retain = new ArrayList<CollectionElement>();
		retain.add(new Element("one"));
		C singleList = singleCollection();
		singleList.retainAll(retain);
		C elements = newPlainCollection(); 
		elements.retainAll(retain);
		assertAreEqual(elements, singleList);
	}
	
	public void testSize(){
		Assert.areEqual(newPlainCollection().size(), singleCollection().size());
	}
	
	public void testToArray(){
		Object[] singleListArray = singleCollection().toArray();
		Object[] elementsArray = newPlainCollection().toArray();
		ArrayAssert.areEqual(elementsArray, singleListArray);
	}
	
	public void testToArrayWithArrayParam(){
		CollectionElement[] singleListArray = new CollectionElement[newPlainCollection().size()];
		CollectionElement[] elementsArray = new CollectionElement[newPlainCollection().size()];
		singleCollection().toArray(singleListArray);
		newPlainCollection().toArray(elementsArray);
		ArrayAssert.areEqual(elementsArray, singleListArray);
	}
	
	public void testIteratorRemove() throws Exception {
		C list = singleCollection();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			iter.next();
			iter.remove();
		}
		reopen();
		C retrieved = singleCollection();
		Assert.isTrue(retrieved.isEmpty());
	}

	public void testRepeatedAdd() throws Exception {
		Element four = new Element("four");
		Element five = new Element("five");
		singleCollection().add(four);
		db().purge();
		singleCollection().add(five);
		reopen();
		C retrieved = singleCollection();
		Assert.isTrue(retrieved.contains(four));
		Assert.isTrue(retrieved.contains(five));
	}

	protected C newActivatableCollection() {
		return currentCollectionSpec().newActivatableCollection();
	}

	protected C newPlainCollection(){
		return currentCollectionSpec().newPlainCollection();
	}

	protected abstract CollectionSpec<C> currentCollectionSpec();
}
