/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.defragment;

import java.util.*;

import com.db4o.db4ounit.common.api.*;
import com.db4o.defragment.*;
import com.db4o.defragment.DatabaseIdMapping.*;
import com.db4o.foundation.*;
import com.db4o.internal.slots.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

@decaf.Remove(decaf.Platform.JDK11)
public class IdMappingTestSuite extends FixtureBasedTestSuite {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(IdMappingTestSuite.class).run();
		
	}
	
	public static class IdMappingTestCase extends TestWithTempFile implements OptOutMultiSession {
		
		private IdMapping _idMapping;

		@Override
		public void setUp() throws Exception {
			_idMapping = _fixture.value().apply(tempFile());
			_idMapping.open();
		}
		
		@Override
		public void tearDown() throws Exception {
			_idMapping.close();
			super.tearDown();
		}
		
		public void testSimpleMapping(){
			assertMapping(true);
			assertMapping(false);
		}

		private void assertMapping(boolean useClassId) {
			_idMapping.mapId(1, 2, useClassId);
			int mappedId = _idMapping.mappedId(1, useClassId);
			Assert.areEqual(2, mappedId);
		}
		
		public void testLenientMapping(){
			_idMapping.mapId(10, 20, false);
			_idMapping.mapId(30, 50, false);
			int mappedId = _idMapping.mappedId(2, false);
			Assert.areEqual(0, mappedId);
			mappedId = _idMapping.mappedId(20, true);
			Assert.areEqual(30, mappedId);
		}
		
		public void testSlotMapping(){
			
			List<TestableIdSlotMapping> expected = new ArrayList<TestableIdSlotMapping>();
			expected.add(new TestableIdSlotMapping(1, 10, 100));
			expected.add(new TestableIdSlotMapping(4, 44, 400));
			expected.add(new TestableIdSlotMapping(8, 800, 888));
			for(TestableIdSlotMapping testableIdSlotMapping : expected){
				_idMapping.mapId(testableIdSlotMapping._id, testableIdSlotMapping.slot());
			}
			
			final List<TestableIdSlotMapping> actual = new ArrayList<TestableIdSlotMapping>();
			_idMapping.slotChanges().accept(new Visitor4<SlotChange>() {
				public void visit(SlotChange slotChange) {
					Assert.isTrue(slotChange.slotModified());
					Slot slot = slotChange.newSlot();
					actual.add(new TestableIdSlotMapping(slotChange._key, slot.address(), slot.length()));
				}
			});
			IteratorAssert.sameContent(expected, actual);
		}
		
	}
	
	public static class TestableIdSlotMapping extends IdSlotMapping{

		public TestableIdSlotMapping(int id, int address, int length) {
			super(id, address, length);
		}
		
		@Override
		public boolean equals(Object obj) {
			TestableIdSlotMapping other = (TestableIdSlotMapping) obj;
			return _id == other._id && _address == other._address && _length == other._length;
		}
		
		
	}

	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[]{
				new SimpleFixtureProvider<Function4<String, IdMapping>>(_fixture, 
				new Function4<String, IdMapping>() {
					public IdMapping apply(String fileName) {
						return new DatabaseIdMapping(fileName);
					}
				},
				new Function4<String, IdMapping>() {
					public IdMapping apply(String fileName) {
						return new InMemoryIdMapping();
					}
				}
		)};
	}

	@Override
	public Class[] testUnits() {
		return new Class[]{IdMappingTestCase.class};
	}
		
	private static FixtureVariable <Function4<String, IdMapping>> _fixture = FixtureVariable.newInstance("IdMapping");

}
