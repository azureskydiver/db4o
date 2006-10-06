/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ext.ExtObjectContainer;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class ExtMethods extends ClientServerTestCase {

	public void conc(ExtObjectContainer oc) {

		ExtMethods em = new ExtMethods();
		oc.set(em);
		Assert.isFalse(oc.isClosed());

		Assert.isTrue(oc.isActive(em));
		Assert.isTrue(oc.isStored(em));

		oc.deactivate(em, 1);
		Assert.isTrue(!oc.isActive(em));

		oc.activate(em, 1);
		Assert.isTrue(oc.isActive(em));

		long id = oc.getID(em);
		Assert.isTrue(oc.isCached(id));

		oc.purge(em);
		Assert.isFalse(oc.isCached(id));
		Assert.isFalse(oc.isStored(em));
		Assert.isFalse(oc.isActive(em));

		oc.bind(em, id);
		Assert.isTrue(oc.isCached(id));
		Assert.isTrue(oc.isStored(em));
		Assert.isTrue(oc.isActive(em));

		ExtMethods em2 = (ExtMethods) oc.getByID(id);
		Assert.areSame(em, em2);

		// Purge all and try again
		oc.purge();

		Assert.isTrue(oc.isCached(id));
		Assert.isTrue(oc.isStored(em));
		Assert.isTrue(oc.isActive(em));

		em2 = (ExtMethods) oc.getByID(id);
		Assert.areSame(em, em2);

		oc.delete(em2);
		oc.commit();
		Assert.isFalse(oc.isCached(id));
		Assert.isFalse(oc.isStored(em2));
		Assert.isFalse(oc.isActive(em2));

		// Null checks
		Assert.isFalse(oc.isStored(null));
		Assert.isFalse(oc.isActive(null));
		Assert.isFalse(oc.isCached(0));

	}

}
