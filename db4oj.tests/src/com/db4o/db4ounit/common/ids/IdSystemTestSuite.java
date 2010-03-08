/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ids;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.config.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

public class IdSystemTestSuite extends FixtureBasedTestSuite {

	private static final int SLOT_LENGTH = 10;

	public static void main(String[] args) {
		new ConsoleTestRunner(new IdSystemTestSuite()).run();
	}

	public static class IdSystemTestUnit extends AbstractDb4oTestCase implements
			OptOutMultiSession, Db4oTestCase {

		@Override
		protected void configure(Configuration config) throws Exception {
			IdSystemConfiguration idSystemConfiguration = Db4oLegacyConfigurationBridge
					.asIdSystemConfiguration(config);
			_fixture.value().apply(idSystemConfiguration);
		}

		public void testPersistence() {

		}

		public void testSlotForNewIdDoesNotExist() {
			int newId = idSystem().newId();
			Slot oldSlot = null;
			try {
				oldSlot = idSystem().committedSlot(newId);
			} catch (InvalidIDException ex) {

			}
			Assert.isFalse(isValid(oldSlot));
		}

		public void testSingleNewSlot() {
			int id = idSystem().newId();
			Assert.areEqual(allocateNewSlot(id), idSystem().committedSlot(id));
		}

		public void testSingleSlotUpdate() {
			int id = idSystem().newId();
			allocateNewSlot(id);

			SlotChange slotChange = SlotChangeFactory.USER_OBJECTS
					.newInstance(id);
			Slot updatedSlot = localContainer().allocateSlot(SLOT_LENGTH);
			slotChange.notifySlotUpdated(freespaceManager(), updatedSlot);
			commit(slotChange);

			Assert.areEqual(updatedSlot, idSystem().committedSlot(id));
		}

		public void testSingleSlotDelete() {
			int id = idSystem().newId();
			allocateNewSlot(id);

			SlotChange slotChange = SlotChangeFactory.USER_OBJECTS
					.newInstance(id);
			slotChange.notifyDeleted(freespaceManager());
			commit(slotChange);

			Assert.isFalse(isValid(idSystem().committedSlot(id)));
		}

		private Slot allocateNewSlot(int newId) {
			SlotChange slotChange = SlotChangeFactory.USER_OBJECTS
					.newInstance(newId);
			Slot allocatedSlot = localContainer().allocateSlot(SLOT_LENGTH);
			slotChange.notifySlotCreated(allocatedSlot);
			commit(slotChange);
			return allocatedSlot;
		}

		private void commit(final SlotChange... slotChanges) {
			idSystem().commit(new Visitable<SlotChange>() {
				public void accept(Visitor4<SlotChange> visitor) {
					for (SlotChange slotChange : slotChanges) {
						visitor.visit(slotChange);
					}
				}
			}, new Runnable() {
				public void run() {
					// do nothing
				}
			});
		}

		private LocalObjectContainer localContainer() {
			return (LocalObjectContainer) container();
		}

		private boolean isValid(Slot slot) {
			return slot != null && !slot.isNull();
		}

		private FreespaceManager freespaceManager() {
			return localContainer().freespaceManager();
		}

		private IdSystem idSystem() {
			return localContainer().idSystem();
		}

	}

	private static FixtureVariable<Procedure4<IdSystemConfiguration>> _fixture = FixtureVariable
			.newInstance("IdSystem");

	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
				new Db4oFixtureProvider(),
				new SimpleFixtureProvider<Procedure4<IdSystemConfiguration>>(
						_fixture, new Procedure4<IdSystemConfiguration>() {
							public void apply(
									IdSystemConfiguration idSystemConfiguration) {
								idSystemConfiguration.usePointerBasedSystem();
							}
						}, new Procedure4<IdSystemConfiguration>() {
							public void apply(
									IdSystemConfiguration idSystemConfiguration) {
								idSystemConfiguration.useInMemorySystem();
							}
						}, new Procedure4<IdSystemConfiguration>() {
							public void apply(
									IdSystemConfiguration idSystemConfiguration) {
								// idSystemConfiguration.useBTreeSystem();
								
							}
						}) };
	}

	@Override
	public Class[] testUnits() {
		return new Class[] { IdSystemTestUnit.class, };
	}

}
