package com.db4o.db4ounit.common.io;

import com.db4o.io.*;

import db4ounit.*;
import db4ounit.fixtures.*;
import db4ounit.mocking.*;

public class BlockAwareIoTestSuite extends FixtureTestSuiteDescription {
	
	{
		fixtureProviders(new SubjectFixtureProvider(1, 2, 17));
		testUnits(BlockAwareIoTestCase.class);
	}
	
	public static class BlockAwareIoTestCase implements TestLifeCycle {
		
		private final MockStorage _mockStorage = new MockStorage();
		private final BlockAwareIo _subject;
		
		public BlockAwareIoTestCase() {
			_subject = new BlockAwareIo(_mockStorage);
		}
		
		public void testBlockSize() {
			Assert.areEqual(blockSize(), _subject.blockSize());
		}
		
		public void testClose() {
			_subject.close();
			_mockStorage.verify(new MethodCall("close"));
		}
		
		public void testBlockRead() {
//			_subject.blockRead()
		}
		
		private int blockSize() {
			return SubjectFixtureProvider.<Integer>value().intValue();
		}

		public void setUp() throws Exception {
			_subject.blockSize(blockSize());
        }

		public void tearDown() throws Exception {
        }
	}

}
