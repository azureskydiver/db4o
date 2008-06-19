package com.db4o.ibs.tests;

import java.util.*;

import com.db4o.ibs.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ChangeSetPublisherTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new ChangeSetPublisherTestCase().runSolo();
	}
	
	public void testAddingAnObjectCreatesNewObjectChange() {
	
		final MockChangeSetListener listener = new MockChangeSetListener();
		final MockChangeSetBuilder builder = new MockChangeSetBuilder();
		
		new ChangeSetPublisher(db(), builder, listener);
		
		db().store(new Contact("foo@bar.com"));
		db().commit();
		
		final List<ChangeSet> changeSets = listener.changeSets();
		Assert.areEqual(1, changeSets.size());
		
		MockChangeSet cs = (MockChangeSet)changeSets.get(0);
		Assert.areEqual(1, cs.changes().size());
	}

}
