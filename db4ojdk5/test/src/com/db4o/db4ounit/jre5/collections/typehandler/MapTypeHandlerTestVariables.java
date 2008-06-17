package com.db4o.db4ounit.jre5.collections.typehandler;

import java.util.*;

import db4ounit.fixtures.*;

public class MapTypeHandlerTestVariables {

	public final static FixtureVariable MAP_IMPLEMENTATION = new FixtureVariable("map");
	
	public final static FixtureProvider MAP_FIXTURE_PROVIDER = 
		new SimpleFixtureProvider(
			MAP_IMPLEMENTATION,
			new Object[] {
				new UntypedHashMapItemFactory(),
				new TypedHashMapItemFactory(),
				new HashtableItemFactory(),
				new NamedHashMapItemFactory(),
			}
		);

	public static final FixtureVariable MAP_KEYS_SPEC = new FixtureVariable("keys");
	
	public static final FixtureProvider MAP_KEYS_PROVIDER = 
		new SimpleFixtureProvider(
				MAP_KEYS_SPEC,
				new Object[]{
					ListTypeHandlerTestVariables.STRING_ELEMENTS_SPEC,
					ListTypeHandlerTestVariables.INT_ELEMENTS_SPEC,
					ListTypeHandlerTestVariables.OBJECT_ELEMENTS_SPEC,
				}
		);

	public static final FixtureVariable MAP_VALUES_SPEC = new FixtureVariable("values");

	public static final FixtureProvider MAP_VALUES_PROVIDER = 
		new SimpleFixtureProvider(
				MAP_VALUES_SPEC,
				new Object[]{
					ListTypeHandlerTestVariables.STRING_ELEMENTS_SPEC,
					ListTypeHandlerTestVariables.INT_ELEMENTS_SPEC,
					ListTypeHandlerTestVariables.OBJECT_ELEMENTS_SPEC,
				}
		);

	private static class HashtableItemFactory extends AbstractMapItemFactory implements Labeled {
		
		private static class Item {
			public Map _map = new Hashtable();
		}

		public Class containerClass() {
			return Hashtable.class;
		}

		public Class itemClass() {
			return Item.class;
		}

		public Object newItem() {
			return new Item();
		}

		public String label() {
			return "Hashtable";
		}
		
	}
	
	private static class TypedHashMapItemFactory extends AbstractMapItemFactory implements Labeled {
		
		private static class Item {
			public HashMap _map = new HashMap();
		}

		public Class containerClass() {
			return HashMap.class;
		}

		public Class itemClass() {
			return Item.class;
		}

		public Object newItem() {
			return new Item();
		}

		public String label() {
			return "HashMap Typed";
		}
		
	}
	
	private static class NamedHashMapItemFactory extends AbstractMapItemFactory implements Labeled {
		
		private static class Item {
			public Map _map = new NamedHashMap();
		}

		public Class containerClass() {
			return NamedHashMap.class;
		}

		public Class itemClass() {
			return Item.class;
		}

		public Object newItem() {
			return new Item();
		}

		public String label() {
			return "NamedHashMap";
		}
		
	}
			
	private static class UntypedHashMapItemFactory extends AbstractMapItemFactory implements Labeled{
		
		private static class Item {
			public Map _map = new HashMap();
		}

		public Class containerClass() {
			return HashMap.class;
		}

		public Class itemClass() {
			return Item.class;
		}

		public Object newItem() {
			return new Item();
		}

		public String label() {
			return "HashMap Untyped";
		}
		
	}
}
