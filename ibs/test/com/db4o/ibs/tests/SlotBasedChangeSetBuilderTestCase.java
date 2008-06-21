package com.db4o.ibs.tests;

import com.db4o.ext.*;
import com.db4o.ibs.*;
import com.db4o.ibs.engine.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class SlotBasedChangeSetBuilderTestCase extends AbstractDb4oTestCase {
	
	static class Item {
		
		public String stringValue;
//		public int intValue;
		
		public Item(String stringValue_, int intValue_) {
			stringValue = stringValue_;
//			intValue = intValue_;
		}
	}
	
	public void testUpdateSingleStringField() {
		
		final Item item = new Item("foo", 42);
		storeAndCommit(item);
		
		final MockChangeSetListener listener = new MockChangeSetListener();
		new ChangeSetPublisher(new SlotBasedChangeSetEngine(), listener).monitor(db());
		
		item.stringValue = "bar";
		storeAndCommit(item);
		
		Assert.areEqual(1, listener.changeSets().size());
		
		final SlotBasedChangeSet changeSet = (SlotBasedChangeSet)listener.changeSets().get(0);
		Assert.areEqual(1, changeSet.changes().size());
		
		UpdateChange update = (UpdateChange) changeSet.changes().get(0);
		Assert.areEqual(uuidFor(item), update.uuid());
		Assert.areEqual(1, update.fields().size());
		
		FieldChange change = update.fields().get(0);
		Assert.areEqual("stringValue", change.field().getName());
		Assert.areEqual("bar", change.currentValue());
		
	}

	private Db4oUUID uuidFor(final Object object) {
		return objectInfoFor(object).getUUID();
	}

	private ObjectInfo objectInfoFor(final Object object) {
		return db().ext().getObjectInfo(object);
	}

	private void storeAndCommit(final Object contact) {
		db().store(contact);
		db().commit();
	}
}
