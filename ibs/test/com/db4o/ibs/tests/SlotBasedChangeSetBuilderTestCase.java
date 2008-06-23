package com.db4o.ibs.tests;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.ibs.*;
import com.db4o.ibs.engine.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class SlotBasedChangeSetBuilderTestCase extends AbstractDb4oTestCase {
	
	static class Item {
		
		public String stringValue;
		public int intValue;
		
		public Item(String stringValue_, int intValue_) {
			stringValue = stringValue_;
			intValue = intValue_;
		}
	}
	
	final MockChangeSetListener listener = new MockChangeSetListener();
	
	final Item item = new Item("foo", 42);
	
	@Override
	protected void db4oSetupAfterStore() throws Exception {
		storeAndCommit(item);
		listenToChangeSets(listener);
	}
	
	public void testNoUpdates() {
		storeAndCommit(item);
		Assert.areEqual(0, listener.changeSets().size());
	}
	
	public void testUpdateOnlyIntField() {
		item.intValue = -1;
		storeAndCommit(item);
		assertSingleFieldUpdate("intValue", new Integer(-1));
	}
	
	public void testUpdateOnlyStringField() {
		item.stringValue = "bar";
		storeAndCommit(item);
		assertSingleFieldUpdate("stringValue", "bar");
	}
	
	public void testUpdateStringAndIntFields() {
		item.stringValue = "bar";
		item.intValue = -1;
		storeAndCommit(item);
		assertFieldUpdates(
				new ExpectedFieldChange("intValue", -1),
				new ExpectedFieldChange("stringValue", "bar"));
	}
			
	private void assertFieldUpdates(ExpectedFieldChange... expected) {
		final UpdateChange update = assertSingleUpdateChange();
		final ArrayList<FieldChange> actual = sortedByName(update.fields());
		Assert.areEqual(expected.length, actual.size());
		for (int i = 0; i < expected.length; i++) {
			expected[i].check(actual.get(i));
		}
	}

	private ArrayList<FieldChange> sortedByName(final List<FieldChange> fields) {
		final ArrayList<FieldChange> sortedChanges = new ArrayList<FieldChange>(fields);
		Collections.sort(sortedChanges, new Comparator<FieldChange>() {
			public int compare(FieldChange arg0, FieldChange arg1) {
				return arg0.field().getName().compareTo(arg1.field().getName());
			}
		});
		return sortedChanges;
	}

	static class ExpectedFieldChange {
		private final String _expectedFieldName;
		private final Object _expectedValue;

		public ExpectedFieldChange(String expectedFieldName, Object expectedValue) {
			_expectedFieldName = expectedFieldName;
			_expectedValue = expectedValue;
		}
		
		public void check(FieldChange change) {
			Assert.areEqual(_expectedFieldName, change.field().getName());
			Assert.areEqual(_expectedValue, change.currentValue());
		}
	}

	private void assertSingleFieldUpdate(final String expectedFieldName,
			final Object expectedValue) {
		assertFieldUpdates(new ExpectedFieldChange(expectedFieldName, expectedValue));
	}

	private UpdateChange assertSingleUpdateChange() {
		Assert.areEqual(1, listener.changeSets().size());
		
		final SlotBasedChangeSet changeSet = (SlotBasedChangeSet)listener.changeSets().get(0);
		Assert.areEqual(1, changeSet.changes().size());
		
		final UpdateChange update = (UpdateChange) changeSet.changes().get(0);
		Assert.areEqual(uuidFor(item), update.uuid());
		
		return update;
	}

	private void listenToChangeSets(final MockChangeSetListener listener) {
		new ChangeSetPublisher(new SlotBasedChangeSetEngine(), listener).monitor(db());
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
