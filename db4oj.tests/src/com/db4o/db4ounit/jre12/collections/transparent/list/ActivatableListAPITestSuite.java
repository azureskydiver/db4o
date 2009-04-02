/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.transparent.list;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.db4ounit.jre12.collections.*;
import com.db4o.db4ounit.jre12.collections.transparent.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableListAPITestSuite extends FixtureBasedTestSuite implements Db4oTestCase {

	private static FixtureVariable<ListSpec<ActivatableList<CollectionElement>>> LIST_SPEC =
		new FixtureVariable<ListSpec<ActivatableList<CollectionElement>>>("list");

	private static Closure4<ActivatableArrayList<CollectionElement>> activatableArrayListFactory() {
		return new Closure4<ActivatableArrayList<CollectionElement>>() {
				public ActivatableArrayList<CollectionElement> run() {
					return new ActivatableArrayList<CollectionElement>();
				}
		};
	}

	private static Closure4<ActivatableLinkedList<CollectionElement>> activatableLinkedListFactory() {
		return new Closure4<ActivatableLinkedList<CollectionElement>>() {
				public ActivatableLinkedList<CollectionElement> run() {
					return new ActivatableLinkedList<CollectionElement>();
				}
		};
	}
	
	private static Closure4<ActivatableStack<CollectionElement>> activatableStackFactory() {
		return new Closure4<ActivatableStack<CollectionElement>>() {
			public ActivatableStack<CollectionElement> run() {
				return new ActivatableStack<CollectionElement>();
			}			
		};
	}

	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
				new Db4oFixtureProvider(),
				new SimpleFixtureProvider(LIST_SPEC,
						new ListSpec<ActivatableArrayList<CollectionElement>>(ActivatableArrayList.class, activatableArrayListFactory()),
						new ListSpec<ActivatableLinkedList<CollectionElement>>(ActivatableLinkedList.class, activatableLinkedListFactory()),
						new ListSpec<ActivatableStack<CollectionElement>>(ActivatableStack.class, activatableStackFactory())
				),
		};
	}

	@Override
	public Class[] testUnits() {
		return new Class[] {
				ActivatableListAPITestUnit.class
		};
	}
	
	public static class ActivatableListAPITestUnit extends ActivatableListTestCaseBase {
		
		public void testCorrectContent(){
			assertAreEqual(newPlainList(), singleList());
		}

		public void testListIsNotActivated(){
			Assert.isFalse(db().isActive(singleList()));
		}

		public void testActivatableElementsAreNotActivated(){
			// trigger activation of List and
			// all elements that are not Activatable
			singleList().iterator();
			
			long id = ActivatableCollectionTestUtil.anyActivatableElementId(db());
			Object element = db().getByID(id);
			Assert.isFalse(db().isActive(element));
		}

		public void testAdd() throws Exception{
			singleList().add(new Element("four"));
			reopen();
			List<CollectionElement> elements = newPlainList();
			elements.add(new Element("four"));
			IteratorAssert.areEqual(elements.iterator(), singleList().iterator());		
		}
		
		public void testAddAtIndex() throws Exception{
			singleList().add(0, new Element("four"));
			reopen();
			List<CollectionElement> elements = newPlainList();
			elements.add(0, new Element("four"));
			IteratorAssert.areEqual(elements.iterator(), singleList().iterator());		
		}
		
		public void testAddAll() throws Exception{
			singleList().addAll(newPlainList());
			reopen();
			List<CollectionElement> elements = newPlainList();
			elements.addAll(newPlainList());
			IteratorAssert.areEqual(elements.iterator(), singleList().iterator());		
		}
		
		public void testAddAllAtIndex() throws Exception{
			singleList().addAll(2,newPlainList());
			reopen();
			List<CollectionElement> elements = newPlainList();
			elements.addAll(2, newPlainList());
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
			Assert.isTrue(singleList().containsAll(newPlainList()));
			List<CollectionElement> elements = newPlainList();
			elements.add(new Element("four"));
			Assert.isFalse(singleList().containsAll(elements));
		}
		
		public void testEquals(){
			Assert.isTrue(singleList().equals(newPlainList()));
		}
		
		public void testHashCode(){
			Assert.areEqual(newPlainList().hashCode(), singleList().hashCode());
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
			List<CollectionElement> retrieved = singleList();
			Assert.areEqual(2, retrieved.lastIndexOf(new Element("one")));
		}
		
		public void testListIterator(){
			IteratorAssert.areEqual(newPlainList().listIterator(), singleList().listIterator());
		}
		
		public void testListIteratorAtIndex(){
			IteratorAssert.areEqual(newPlainList().listIterator(1), singleList().listIterator(1));
		}
		
		public void testRemove() throws Exception{
			Element element = new Element("one");
			singleList().remove(element);
			reopen();
			List<CollectionElement> list = newPlainList();
			list.remove(element);
			IteratorAssert.areEqual(list.iterator(), singleList().iterator());
		}
		
		public void testRemoveAtIndex() throws Exception{
			singleList().remove(0);
			reopen();
			List<CollectionElement> list = newPlainList();
			list.remove(0);
			IteratorAssert.areEqual(list.iterator(), singleList().iterator());
		}
		
		public void testRemoveAll(){
			List<CollectionElement> remove = new ArrayList<CollectionElement>();
			remove.add(new Element("one"));
			List<CollectionElement> singleList = singleList();
			singleList.removeAll(remove);
			List<CollectionElement> elements = newPlainList(); 
			elements.removeAll(remove);
			assertAreEqual(elements, singleList);
		}
		
		public void testRetainAll(){
			List<CollectionElement> retain = new ArrayList<CollectionElement>();
			retain.add(new Element("one"));
			List<CollectionElement> singleList = singleList();
			singleList.retainAll(retain);
			List<CollectionElement> elements = newPlainList(); 
			elements.retainAll(retain);
			assertAreEqual(elements, singleList);
		}
		
		public void testSet(){
			List<CollectionElement> singleList = singleList();
			Element element = new Element("four");
			singleList.set(1, element);
			List<CollectionElement> elements = newPlainList();
			elements.set(1, element);
			assertAreEqual(elements, singleList);
		}
		
		public void testSize(){
			Assert.areEqual(newPlainList().size(), singleList().size());
		}
		
		public void testSubList(){
			IteratorAssert.areEqual(newPlainList().subList(0,1).iterator(), singleList().subList(0, 1).iterator());
		}
		
		public void testToArray(){
			Object[] singleListArray = singleList().toArray();
			Object[] elementsArray = newPlainList().toArray();
			ArrayAssert.areEqual(elementsArray, singleListArray);
		}
		
		public void testToArrayWithArrayParam(){
			CollectionElement[] singleListArray = new CollectionElement[newPlainList().size()];
			CollectionElement[] elementsArray = new CollectionElement[newPlainList().size()];
			singleList().toArray(singleListArray);
			newPlainList().toArray(elementsArray);
			ArrayAssert.areEqual(elementsArray, singleListArray);
		}
		public void testIteratorRemove() throws Exception {
			List<CollectionElement> list = singleList();
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				iter.next();
				iter.remove();
			}
			reopen();
			List<CollectionElement> retrieved = singleList();
			Assert.isTrue(retrieved.isEmpty());
		}

		public void testListIteratorAdd() throws Exception {
			Element added = new Element("added");
			List<CollectionElement> list = singleList();
			for (ListIterator iter = list.listIterator(); iter.hasNext();) {
				iter.next();
				if(!iter.hasNext()) {
					iter.add(added);
				}
			}
			reopen();
			List<CollectionElement> retrieved = singleList();
			Assert.areEqual(newPlainList().size() + 1, retrieved.size());
			Assert.isTrue(retrieved.contains(added));
			Assert.areEqual(added, retrieved.get(retrieved.size() - 1));
		}

		public void testListIteratorSet() throws Exception {
			Element replaced = new Element("replaced");
			List<CollectionElement> list = singleList();
			for (ListIterator iter = list.listIterator(); iter.hasNext();) {
				iter.next();
				if(iter.previousIndex() == 0) {
					iter.set(replaced);
				}
			}
			reopen();
			List<CollectionElement> retrieved = singleList();
			Assert.areEqual(newPlainList().size(), retrieved.size());
			Assert.isTrue(retrieved.contains(replaced));
			Assert.isFalse(retrieved.contains(new Element(ListSpec.firstName())));
			Assert.areEqual(replaced, retrieved.get(0));
		}

		public void testRepeatedAdd() throws Exception {
			Element four = new Element("four");
			Element five = new Element("five");
			singleList().add(four);
			db().purge();
			singleList().add(five);
			reopen();
			List<CollectionElement> retrieved = singleList();
			Assert.isTrue(retrieved.contains(four));
			Assert.isTrue(retrieved.contains(five));
		}

		protected List<CollectionElement> newActivatableList() {
			return currentListSpec().newActivatableList();
		}

		private List<CollectionElement> newPlainList(){
			return currentListSpec().newPlainList();
		}

		private ListSpec<ActivatableList<CollectionElement>> currentListSpec() {
			return LIST_SPEC.value();
		}
	}
}
