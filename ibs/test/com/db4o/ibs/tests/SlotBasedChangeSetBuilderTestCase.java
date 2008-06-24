package com.db4o.ibs.tests;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.ibs.*;
import com.db4o.ibs.engine.*;
import com.db4o.ibs.tests.mocking.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class SlotBasedChangeSetBuilderTestCase extends AbstractDb4oTestCase {
	
	static class Item {
		
		public String stringValue;
		public int intValue;
		public Item itemValue;
		
		public Item(String stringValue_, int intValue_) {
			stringValue = stringValue_;
			intValue = intValue_;
		}
	}
	
	final MockChangeSetListener listener = new MockChangeSetListener();
	
	final Item item = new Item("foo", 42);
	
	@Override
	protected void db4oSetupAfterStore() throws Exception {
		commitItem();
		setUpChangeSetPublisher();
	}

	public void testNoUpdates() {
		commitItem();
		Assert.areEqual(0, changeSets().size());
	}
	
	public void testUpdateOnlyIntField() {
		item.intValue = -1;
		commitItem();
		assertSingleFieldChange("intValue", new Integer(-1));
	}
	
	public void testUpdateOnlyStringField() {
		item.stringValue = "bar";
		commitItem();
		assertSingleFieldChange("stringValue", "bar");
	}
	
	public void testUpdateStringAndIntFields() {
		item.stringValue = "bar";
		item.intValue = -1;
		commitItem();
		assertFieldChanges(
				new FieldChangeExpectation("intValue", -1),
				new FieldChangeExpectation("stringValue", "bar"));
	}
	
	public void testReferenceFieldChangeFromNull() {
		
		final Item itemValue = new Item("value", 42);
		commitItem(itemValue);
		
		changeSets().clear();

		item.itemValue = itemValue;
		commitItem();
		
		assertSingleFieldChange("itemValue", itemValue);
	}
	
	public void testReferenceFieldChangeToNull() {
		
		item.itemValue = new Item("value", 42);
		commitItem();
		
		changeSets().clear();
		
		item.itemValue = null;
		commitItem();
		
		assertSingleFieldChange("itemValue", null);
	}
	
	public void testReferenceFieldChange() {
		item.itemValue = new Item("value", 42);
		commitItem();
		
		final Item newItem = new Item("newItem", 42);
		commitItem(newItem);
		
		changeSets().clear();
		
		item.itemValue = newItem;
		commitItem();
		assertSingleFieldChange("itemValue", newItem);
	}
	
	public void testReferenceFieldDoesntChange() {
		
		item.itemValue = new Item("value", 42);
		commitItem();
		
		changeSets().clear();
		
		commitItem();
		
		Assert.areEqual(0, changeSets().size());
	}
	
	public void testNewObject() {
		final Item newItem = new Item("value", 42);
		commitItem(newItem);
		
		final SlotBasedChangeSet changeSet = assertSingleChangeSet();
		Assert.areEqual(1, changeSet.changes().size());
		
		final NewObjectChange change = (NewObjectChange)changeSet.changes().get(0);
		Assert.areEqual(uuidFor(newItem), change.object().getUUID());
	}
	
	public void testDeletedObject() {
		final Db4oUUID expectedUUID = uuidFor(item);
		db().delete(item);
		db().commit();
		
		final SlotBasedChangeSet changeSet = assertSingleChangeSet();
		Assert.areEqual(1, changeSet.changes().size());
		
		final DeleteChange change = (DeleteChange)changeSet.changes().get(0);
		Assert.areEqual(expectedUUID, change.object().getUUID());
	}
	
	private List<ChangeSet> changeSets() {
		return listener.changeSets();
	}
			
	private void assertFieldChanges(FieldChangeExpectation... expected) {
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

	static class FieldChangeExpectation {
		private final String _expectedFieldName;
		private final Object _expectedValue;

		public FieldChangeExpectation(String expectedFieldName, Object expectedValue) {
			_expectedFieldName = expectedFieldName;
			_expectedValue = expectedValue;
		}
		
		public void check(FieldChange change) {
			Assert.areEqual(_expectedFieldName, change.field().getName());
			Assert.areEqual(_expectedValue, change.currentValue());
		}
	}

	private void assertSingleFieldChange(final String expectedFieldName, final Object expectedValue) {
		assertFieldChanges(new FieldChangeExpectation(expectedFieldName, expectedValue));
	}

	private UpdateChange assertSingleUpdateChange() {
		final SlotBasedChangeSet changeSet = assertSingleChangeSet();
		Assert.areEqual(1, changeSet.changes().size());
		
		final UpdateChange update = (UpdateChange) changeSet.changes().get(0);
		Assert.areEqual(uuidFor(item), update.uuid());
		
		return update;
	}

	private SlotBasedChangeSet assertSingleChangeSet() {
		Assert.areEqual(1, changeSets().size());
		
		final SlotBasedChangeSet changeSet = (SlotBasedChangeSet)changeSets().get(0);
		return changeSet;
	}

	private void setUpChangeSetPublisher() {
		new ChangeSetPublisher(new SlotBasedChangeSetEngine(), listener).monitor(db());
	}

	private Db4oUUID uuidFor(final Object object) {
		return objectInfoFor(object).getUUID();
	}

	private ObjectInfo objectInfoFor(final Object object) {
		return db().ext().getObjectInfo(object);
	}
	
	private void commitItem() {
		commitItem(item);
	}

	private void commitItem(Item i) {
		db().store(i);
		db().commit();
	}
}
