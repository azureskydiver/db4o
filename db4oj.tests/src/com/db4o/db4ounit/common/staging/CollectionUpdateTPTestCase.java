/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.staging;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CollectionUpdateTPTestCase extends AbstractDb4oTestCase {

	private final static int ID1 = 1;
	private final static int ID2 = 2;
	
	public static class Item implements Activatable {
		
		public transient Activator _activator;
		public int _id;
		
		public Item(int id) {
			_id = id;
		}

		public void id(int id) {
			_activator.activate(ActivationPurpose.WRITE);
			_id = id;
		}
		
		public int id() {
			_activator.activate(ActivationPurpose.READ);
			return _id;
		}
		
		public void activate(ActivationPurpose purpose) {
			_activator.activate(purpose);
		}

		public void bind(Activator activator) {
			_activator = activator;
		}
		
		@Override
		public String toString() {
			_activator.activate(ActivationPurpose.READ);
			return "Item #" + _id;
		}
	}
	
	public static class Holder {
		
		public List<Item> _list;
		
		public Holder(Item... items) {
			_list = new ArrayList<Item>();
			_list.addAll(Arrays.asList(items));
		}
		
		public Item item(int idx) {
			return _list.get(idx);
		}
		
		public void add(Item item) {
			_list.add(item);
		}
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentPersistenceSupport());
	}

	@Override
	protected void store() throws Exception {
		Holder holder = new Holder(new Item(1), new Item(2));
		store(holder);
	}
	
	public void testStructureUpdate() throws Exception {
		assertItemUpdates(1, new Procedure4<Holder>() {
			public void apply(Holder holder) {
				Item item = new Item(3);
				store(item);
				holder.add(item);
				db().store(holder, Integer.MAX_VALUE);
			}
		});
		reopen();
		assertHolderContent(ID1, ID2, 3);
	}

	public void testElementUpdate() throws Exception {
		assertItemUpdates(1, new Procedure4<Holder>() {
			public void apply(Holder holder) {
				holder.item(0).id(42);
//				db().store(holder, Integer.MAX_VALUE);
			}
		});
		reopen();
		assertHolderContent(42, ID2);
	}

	public void testElementUpdateAndActivation() throws Exception {
		assertItemUpdates(1, new Procedure4<Holder>() {
			public void apply(Holder holder) {
				holder.item(0).id(42);
				holder.item(1).id();
//				db().store(holder, Integer.MAX_VALUE);
			}
		});
		reopen();
		assertHolderContent(42, ID2);
	}

	private void assertItemUpdates(int expectedCount, Procedure4<Holder> block) {
		final IntByRef itemCount = new IntByRef(0);
		eventRegistry().updated().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4<ObjectInfoEventArgs> e, ObjectInfoEventArgs args) {
				if(args.object() instanceof Item) {
					System.out.println(args.object());
					itemCount.value = itemCount.value + 1;
				}
			}
		});
		Holder holder = retrieveOnlyInstance(Holder.class);
		block.apply(holder);
		commit();
		Assert.areEqual(expectedCount, itemCount.value);
	}
	
	private void assertHolderContent(int... ids) {
		Holder holder = retrieveOnlyInstance(Holder.class);
		for (int idx = 0; idx < ids.length; idx++) {
			Assert.areEqual(ids[idx], holder.item(idx).id());
		}
	}

}
