package com.db4o.ibs.tests;

import java.util.*;

import com.db4o.ibs.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ChangeSetPublisherTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new ChangeSetPublisherTestCase().runSolo();
	}
	
	final MockChangeSetListener listener = new MockChangeSetListener();
	final MockChangeSetBuilder builder = new MockChangeSetBuilder();
	
	@Override
	protected void db4oSetupAfterStore() throws Exception {
		new ChangeSetPublisher(builder, listener).monitor(db());
	}
	
	public void testAddingAnObjectCreatesNewObjectChange() {
		
		db().store(new Contact("foo@bar.com"));
		db().commit();
	
		assertSingleChange(MockChangeSet.NewObjectChange.class);
	}
	
	public void testDeletingAnObjectCreatesDeleteChange() {
		
		final Contact contact = new Contact("foo@bar.com");
		db().store(contact);
		db().commit();
		changeSets().clear(); 
		
		db().delete(contact);
		db().commit();
		
		assertSingleChange(MockChangeSet.DeleteObjectChange.class);
	}

	private List<ChangeSet> changeSets() {
		return listener.changeSets();
	}
	

	private void assertSingleChange(final Class<?> expectedChangeClass) {
		Assert.areEqual(1, changeSets().size());
		MockChangeSet cs = (MockChangeSet)changeSets().get(0);
		assertChangeSet(cs, expectedChangeClass);
	}
	
	private void assertChangeSet(MockChangeSet cs, final Class<?> expectedChangeClass) {
		Assert.areEqual(1, cs.changes().size());
		Assert.isInstanceOf(expectedChangeClass, cs.changes().get(0));
	}
}
