package com.db4o.db4ounit.common.io;

import com.db4o.io.*;

import db4ounit.*;
import db4ounit.fixtures.*;
import db4ounit.mocking.*;

public class BlockAwareIoTestSuite extends FixtureTestSuiteDescription {
	
	{
		fixtureProviders(new SubjectFixtureProvider(2, 3, 17));
		testUnits(BlockAwareIoTestCase.class);
	}
	
	public static class BlockAwareIoTestCase implements TestLifeCycle {
		
		private final MockStorage _mockStorage = new MockStorage();
		private final BlockAwareIo _subject = new BlockAwareIo(_mockStorage);
		
		public void testBlockSize() {
			Assert.areEqual(blockSize(), _subject.blockSize());
		}
		
		public void testClose() {
			_subject.close();
			verify(new MethodCall("close"));
		}
		
		public void testSync() {
			_subject.sync();
			verify(new MethodCall("sync"));
		}
		
		public void testBlockReadReturnsStorageReturnValue() {

			_mockStorage.returnValueForNextCall(-1);
			Assert.areEqual(-1, _subject.blockRead(0, new byte[10]));
		}
		
		public void testBlockRead() {
			byte[] buffer = new byte[10];
			_subject.blockRead(0, buffer);
			_subject.blockRead(1, buffer, 5);
			_subject.blockRead(42, buffer);
			
			verify(
				new MethodCall("read", 0L, buffer, buffer.length),
				new MethodCall("read", (long)blockSize(), buffer, 5),
				new MethodCall("read", 42L*blockSize(), buffer, buffer.length)
			);
		}
		
		public void testBlockReadWithOffset() {
			byte[] buffer = new byte[10];
			_subject.blockRead(0, 1, buffer);
			_subject.blockRead(1, 3, buffer, 5);
			_subject.blockRead(42, 5, buffer);
			
			verify(
				new MethodCall("read", 1L, buffer, buffer.length),
				new MethodCall("read", 3 + (long)blockSize(), buffer, 5),
				new MethodCall("read", 5 + 42L*blockSize(), buffer, buffer.length)
			);
		}
		
		public void testBlockWrite() {
			byte[] buffer = new byte[10];
			_subject.blockWrite(0, buffer);
			_subject.blockWrite(1, buffer, 5);
			_subject.blockWrite(42, buffer);
			
			verify(
				new MethodCall("write", 0L, buffer, buffer.length),
				new MethodCall("write", (long)blockSize(), buffer, 5),
				new MethodCall("write", 42L*blockSize(), buffer, buffer.length)
			);
		}
		
		public void testBlockWriteWithOffset() {
			byte[] buffer = new byte[10];
			_subject.blockWrite(0, 1, buffer);
			_subject.blockWrite(1, 3, buffer, 5);
			_subject.blockWrite(42, 5, buffer);
			
			verify(
				new MethodCall("write", 1L, buffer, buffer.length),
				new MethodCall("write", 3 + (long)blockSize(), buffer, 5),
				new MethodCall("write", 5 + 42L*blockSize(), buffer, buffer.length)
			);
		}
		
		private void verify(MethodCall... expectedCalls) {
			_mockStorage.verify(expectedCalls);
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
