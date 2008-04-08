/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.typehandler;

import java.util.*;

import db4ounit.fixtures.*;

@SuppressWarnings("unchecked")
public final class ArrayListTypeHandlerTestVariables {
	
	public final static FixtureVariable LIST_IMPLEMENTATION = new FixtureVariable("list");
	public final static FixtureVariable ELEMENTS_SPEC = new FixtureVariable("elements");
	
	public final static FixtureProvider LIST_FIXTURE_PROVIDER = 
			new SimpleFixtureProvider(
				ArrayListTypeHandlerTestVariables.LIST_IMPLEMENTATION,
				new Object[] {
						new ArrayListItemFactory(),
						new LinkedListItemFactory(),
				}
			);

	public final static ArrayListTypeHandlerTestElementsSpec STRING_ELEMENTS_SPEC = 
		new ArrayListTypeHandlerTestElementsSpec(new Object[]{ "zero", "one" }, "two", "zzz");
	public final static ArrayListTypeHandlerTestElementsSpec INT_ELEMENTS_SPEC =
		new ArrayListTypeHandlerTestElementsSpec(new Object[]{ new Integer(0), new Integer(1) }, new Integer(2), new Integer(Integer.MAX_VALUE));
	public final static ArrayListTypeHandlerTestElementsSpec OBJECT_ELEMENTS_SPEC =
		new ArrayListTypeHandlerTestElementsSpec(new Object[]{ new FirstClassElement(0), new FirstClassElement(2) }, new FirstClassElement(2), null);
	
	private ArrayListTypeHandlerTestVariables() {
	}

	public static class FirstClassElement {

		public int _id;
		
		public FirstClassElement(int id) {
			_id = id;
		}
		
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			}
			if(obj == null || getClass() != obj.getClass()) {
				return false;
			}
			FirstClassElement other = (FirstClassElement) obj;
			return _id == other._id;
		}
		
		public int hashCode() {
			return _id;
		}
		
		public String toString() {
			return "FCE#" + _id;
		}

	}
	
	private static class ArrayListItemFactory implements ItemFactory, Labeled {
		private static class Item {
			public ArrayList _list = new ArrayList();
		}
		
		public Object newItem() {
			return new Item();
		}

		public Class itemClass() {
			return Item.class;
		}

		public Class listClass() {
			return ArrayList.class;
		}

		public String label() {
			return "ArrayList";
		}
	}

	private static class LinkedListItemFactory implements ItemFactory, Labeled {
		private static class Item {
			public LinkedList _list = new LinkedList();
		}
		
		public Object newItem() {
			return new Item();
		}

		public Class itemClass() {
			return Item.class;
		}

		public Class listClass() {
			return LinkedList.class;
		}

		public String label() {
			return "LinkedList";
		}
	}
}
