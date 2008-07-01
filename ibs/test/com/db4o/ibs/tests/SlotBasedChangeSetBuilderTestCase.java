package com.db4o.ibs.tests;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.ibs.engine.*;
import com.db4o.ibs.tests.SlotBasedChangeSetTestCaseBase.*;

import db4ounit.*;

public class SlotBasedChangeSetBuilderTestCase extends SlotBasedChangeSetTestCaseBase {
	
	public void testUpdateToInheritedField() {
		final SubItem subItem = new SubItem("sub", 42);
		commitItem(subItem);
		changeSets().clear();
		
		subItem.intValue = -1;
		subItem.integerValue = new Integer(42);
		commitItem(subItem);
		
		assertSingleFieldChange(uuidFor(subItem), "intValue", new Integer(-1));
	}
	
	public void testUpdateToSubClassField() {
		final SubItem subItem = new SubItem("sub", 42);
		commitItem(subItem);
		changeSets().clear();
		
		subItem.integerValue = new Integer(21);
		commitItem(subItem);
		assertSingleFieldChange(uuidFor(subItem), "integerValue", new Integer(21));
	}

	public void testNoUpdates() {
	    Item item = persistentItem();
		commitItem(item);
		Assert.areEqual(0, changeSets().size());
	}
	
	public void testUpdateOnlyIntField() {
	    Item item = persistentItem();
		item.intValue = -1;
		commitItem(item);
		assertSingleItemFieldChange("intValue", new Integer(-1));
	}
	
	public void testUpdateOnlyStringField() {
	    Item item = persistentItem();
		item.stringValue = "bar";
		commitItem(item);
		assertSingleItemFieldChange("stringValue", "bar");
	}
	
	public void testUpdateStringAndIntFields() {
	    Item item = persistentItem();
		item.stringValue = "bar";
		item.intValue = -1;
		commitItem(item);
		assertFieldChanges(uuidFor(item),
				new FieldChangeExpectation("intValue", -1),
				new FieldChangeExpectation("stringValue", "bar"));
	}
	
	public void testReferenceFieldChangeFromNull() {
	    Item item = persistentItem();
		final Item itemValue = new Item("value", 42);
		commitItem(itemValue);
		
		changeSets().clear();
		
		item.itemValue = itemValue;
		commitItem(item);
		
		assertSingleFieldChange(uuidFor(item),"itemValue", itemValue);
		 
	}
	
	public void testReferenceFieldChangeToNull() {
	    Item item = persistentItem();		
		item.itemValue = new Item("value", 42);
		commitItem(item);
		
		changeSets().clear();
		
		item.itemValue = null;
		commitItem(item);
		
		assertSingleFieldChange(uuidFor(item),"itemValue", null);
	}
	
	public void testReferenceFieldChange() {
	    Item item = persistentItem();
	    item.itemValue = new Item("value", 42);
		commitItem(item);
		
		final Item newItem = new Item("newItem", 42);
		commitItem(newItem);
		
		changeSets().clear();
		
		item.itemValue = newItem;
		commitItem(item);
		assertSingleFieldChange(uuidFor(item), "itemValue", newItem);
	}
	
	public void testReferenceFieldDoesntChange() {
		Item item = persistentItem();
		item.itemValue = new Item("value", 42);
		commitItem(item);
		
		changeSets().clear();
		
		commitItem(item);
		
		Assert.areEqual(0, changeSets().size());
	}
	
    public void testNewObject() {
		final Item newItem = new Item("value", 42);
		commitItem(newItem);
		
		final SlotBasedChangeSet changeSet = assertSingleChangeSet();
		Assert.areEqual(1, changeSet.changes().size());
		
		final NewObjectChange change = (NewObjectChange)changeSet.changes().get(0);
		assertAreEqual(uuidFor(newItem), change.uuid());
	}
	
	public void testDeletedObject() {
	    Item item = persistentItem();
		final Db4oUUID expectedUUID = uuidFor(item);
		db().delete(item);
		db().commit();
		
		final SlotBasedChangeSet changeSet = assertSingleChangeSet();
		Assert.areEqual(1, changeSet.changes().size());
		
		final DeleteChange change = (DeleteChange)changeSet.changes().get(0);
		assertAreEqual(expectedUUID, change.uuid());
	}
	
	private void assertFieldChanges(Db4oUUID originalUUID, FieldChangeExpectation... expected) {
		final UpdateChange update = assertSingleUpdateChange(originalUUID);
		final ArrayList<FieldChange> actual = sortedByName(update.fieldChanges());
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
	
	private void assertSingleItemFieldChange(final String expectedFieldName, final Object expectedValue) {
	    assertSingleFieldChange(uuidFor(persistentItem()), expectedFieldName, expectedValue);
	}

	private void assertSingleFieldChange(Db4oUUID originalUUID, final String expectedFieldName, final Object expectedValue) {
		assertFieldChanges(originalUUID, new FieldChangeExpectation(expectedFieldName, expectedValue));
	}

	private UpdateChange assertSingleUpdateChange(Db4oUUID originalUUID) {
		final SlotBasedChangeSet changeSet = assertSingleChangeSet();
		Assert.areEqual(1, changeSet.changes().size());
		
		final UpdateChange update = (UpdateChange) changeSet.changes().get(0);
		assertAreEqual(originalUUID, update.uuid());
		return update;
	}

	private SlotBasedChangeSet assertSingleChangeSet() {
		Assert.areEqual(1, changeSets().size());
		
		final SlotBasedChangeSet changeSet = (SlotBasedChangeSet)changeSets().get(0);
		return changeSet;
	}

	private Db4oUUID uuidFor(final Object object) {
		return objectInfoFor(object).getUUID();
	}

	private ObjectInfo objectInfoFor(final Object object) {
		return db().ext().getObjectInfo(object);
	}
	
	
}
