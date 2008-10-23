/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.ta;

import com.db4o.activation.ActivationPurpose;
import com.db4o.events.Event4;
import com.db4o.events.EventArgs;
import com.db4o.events.EventListener4;
import com.db4o.events.ObjectEventArgs;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.foundation.Collection4;
import com.db4o.internal.activation.TransparentActivationDepthProvider;

import db4ounit.Assert;

/**
 * 
 * @sharpen.partial
 *
 */
public class TransparentActivationSupportTestCase extends TransparentActivationTestCaseBase {

	public static void main(String[] args) {
		new TransparentActivationSupportTestCase().runAll();
	}
	
	public void testActivationDepth() {
		Assert.isInstanceOf(TransparentActivationDepthProvider.class, stream().configImpl().activationDepthProvider());
	}
	
	/**
	 * 
	 * @sharpen.partial
	 *
	 */
	public final class Item extends ActivatableImpl {
		public void update() {
			activate(ActivationPurpose.WRITE);
		}
	}
	
	public void testTransparentActivationDoesNotImplyTransparentUpdate() {
		final Item item = new Item();
		db().store(item);
		db().commit();
		
		item.update();
		final Collection4 updated = commitCapturingUpdatedObjects(db());
		Assert.areEqual(0, updated.size());
	}
	
	private Collection4 commitCapturingUpdatedObjects(
			final ExtObjectContainer container) {
		final Collection4 updated = new Collection4();
		eventRegistryFor(container).updated().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				ObjectEventArgs objectArgs = (ObjectEventArgs)args;
				updated.add(objectArgs.object());
			}
		});
		container.commit();
		return updated;
	}
}
