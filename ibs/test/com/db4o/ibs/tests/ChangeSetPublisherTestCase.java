package com.db4o.ibs.tests;

import java.util.*;

import com.db4o.ext.*;
import com.db4o.ibs.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ChangeSetPublisherTestCase extends AbstractDb4oTestCase {
	
	public static class Contact {

		public String email;

		public Contact(String email_) {
			email = email_;
		}
	}
	
	final MockChangeSetListener listener = new MockChangeSetListener();
	final MockChangeSetEngine engine = new MockChangeSetEngine();
	
	@Override
	protected void db4oSetupAfterStore() throws Exception {
		new ChangeSetPublisher(engine, listener).monitor(db());
	}
	
	public void testCommitWithNoChange() {
		db().commit();
		Assert.areEqual(0, changeSets().size());
	}
	
	public void testAddingAnObjectCreatesNewObjectChange() {
		
		storeNewContact();
		assertSingleChange(MockChangeSet.NewObjectChange.class);
	}
	
	public void testDeletingAnObjectCreatesDeleteChange() {
		
		final Contact contact = storeNewContact();
		changeSets().clear(); 
		
		db().delete(contact);
		db().commit();
		
		assertSingleChange(MockChangeSet.DeleteObjectChange.class);
	}
	
	public void testUpdatingAnObjectCreatesUpdateChange() {
		final Contact contact = storeNewContact();
		changeSets().clear();
		
		contact.email = "foo@foo.com";
		db().store(contact);
		db().commit();
		
		assertSingleChange(MockChangeSet.UpdateObjectChange.class);
	}

	private Contact storeNewContact() {

		ExtObjectContainer container = db();
		final Contact contact = new Contact("foo@bar.com");
		container.store(contact);
		container.commit();
		return contact;
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
