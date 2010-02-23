/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ids;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

public class GlobalIdSystemTestSuite extends FixtureBasedTestSuite {
	
	private static final int SLOT_LENGTH = 10;
	
	public static void main(String[] args) {
		new ConsoleTestRunner(new GlobalIdSystemTestSuite()).run();
	}
	
	public static class GlobalIdSystemTestUnit extends AbstractDb4oTestCase implements OptOutMultiSession, Db4oTestCase {
	
		private GlobalIdSystem _idSystem;
		
		@Override
		protected void db4oSetupAfterStore() throws Exception {
			_idSystem = _fixture.value().apply((LocalObjectContainer)db());
		}
		
		public void testSlotForNewIdDoesNotExist(){
			int newId = _idSystem.newId();
			Slot oldSlot = _idSystem.committedSlot(newId);
			Assert.isFalse(isValid(oldSlot));
		}
		
		public void testSingleNewSlot(){
			int id = _idSystem.newId();
			Assert.areEqual(allocateNewSlot(id), _idSystem.committedSlot(id));
		}
		
		public void testSingleSlotUpdate(){
			int id = _idSystem.newId();
			allocateNewSlot(id);
			
			SlotChange slotChange = SlotChangeFactory.USER_OBJECTS.newInstance(id);
			Slot updatedSlot = localContainer().allocateSlot(SLOT_LENGTH);
			slotChange.notifySlotUpdated(freespaceManager(), updatedSlot);
			commit(slotChange);
			
			Assert.areEqual(updatedSlot, _idSystem.committedSlot(id));
		}
		
		public void testSingleSlotDelete(){
			int id = _idSystem.newId();
			allocateNewSlot(id);
			
			SlotChange slotChange = SlotChangeFactory.USER_OBJECTS.newInstance(id);
			slotChange.notifyDeleted(freespaceManager());
			commit(slotChange);
			
			Assert.isFalse(isValid( _idSystem.committedSlot(id)));
		}
	
		private Slot allocateNewSlot(int newId) {
			SlotChange slotChange = SlotChangeFactory.USER_OBJECTS.newInstance(newId);
			Slot allocatedSlot = localContainer().allocateSlot(SLOT_LENGTH);
			slotChange.notifySlotCreated(allocatedSlot);
			commit(slotChange);
			return allocatedSlot;
		}
	
		private void commit(final SlotChange ...slotChanges ) {
			_idSystem.commit(new Visitable<SlotChange>() {
				public void accept(Visitor4<SlotChange> visitor) {
					for(SlotChange slotChange : slotChanges){
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
			return slot != null && ! slot.isNull();
		}
		
		private FreespaceManager freespaceManager(){
			return localContainer().freespaceManager();
		}
		
		
	}
	
	private static FixtureVariable <Function4<LocalObjectContainer, GlobalIdSystem>> _fixture = FixtureVariable.newInstance("IdSystem"); 


	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[]{
				new Db4oFixtureProvider(),
				new SimpleFixtureProvider<Function4<LocalObjectContainer, GlobalIdSystem>>(_fixture, 
						new Function4<LocalObjectContainer, GlobalIdSystem>() {
							public GlobalIdSystem apply(LocalObjectContainer container) {
								return new PointerBasedIdSystem(container);
							}
						}
						/*
						  
						,
						new Function4<LocalObjectContainer, GlobalIdSystem>() {
							public GlobalIdSystem apply(LocalObjectContainer container) {
								return new InMemoryIdSystem();
							}
						}*/
				)
		};
	}

	@Override
	public Class[] testUnits() {
		return new Class[] {
				GlobalIdSystemTestUnit.class,
		};
	}

}