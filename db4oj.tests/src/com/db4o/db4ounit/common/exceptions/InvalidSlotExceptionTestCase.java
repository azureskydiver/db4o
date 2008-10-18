/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.exceptions;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class InvalidSlotExceptionTestCase extends AbstractDb4oTestCase implements OptOutNetworkingCS {
	
	private static final int INVALID_ID = 3;
	
	private static final int OUT_OF_MEMORY_ID = 4;

	public static void main(String[] args) {
		new InvalidSlotExceptionTestCase().runSolo();
	}
	
	
	protected void configure(Configuration config) throws Exception {
		config.io(new MockIoAdapter());
	}
	
	public void testInvalidSlotException() throws Exception {
		Assert.expect(InvalidIDException.class, InvalidSlotException.class, new CodeBlock(){
			public void run() throws Throwable {
				db().getByID(INVALID_ID);		
			}
		});
	}
	
	public void testDbNotClosedOnOutOfMemory(){
		Assert.expect(OutOfMemoryError.class, new CodeBlock(){
			public void run() throws Throwable {
				db().getByID(OUT_OF_MEMORY_ID);
			}
		});
		Assert.isFalse(db().isClosed());
	}
	
	public static class A{
		
		A _a;
		
		public A(A a) {
			this._a = a;
		}
	}
	
	public static class MockIoAdapter extends VanillaIoAdapter{
		
		
		private boolean _deliverInvalidSlot;
		
		public MockIoAdapter(){
			super(new RandomAccessFileAdapter());
		}
		
		protected MockIoAdapter(String path, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
			super(new RandomAccessFileAdapter(), path, lockFile, initialLength, readOnly);
		}

		public IoAdapter open(String path, boolean lockFile,
				long initialLength, boolean readOnly) throws Db4oIOException {
			// TODO Auto-generated method stub
			return new MockIoAdapter(path, lockFile, initialLength, readOnly);
		}
		
		public void seek(long pos) throws Db4oIOException {
			if(pos == OUT_OF_MEMORY_ID){
				throw new OutOfMemoryError();
			}
			if(pos == INVALID_ID){
				_deliverInvalidSlot = true;
				return;
			}
			_deliverInvalidSlot = false;
			super.seek(pos);
		}
		
		public int read(byte[] bytes, int length) throws Db4oIOException {
			if(_deliverInvalidSlot){
				ByteArrayBuffer buffer = new ByteArrayBuffer(Const4.POINTER_LENGTH);
				buffer.writeInt(1);
				buffer.writeInt(Integer.MAX_VALUE);
				System.arraycopy(buffer._buffer, 0, bytes, 0, Const4.POINTER_LENGTH);
				return length;
			}
			return super.read(bytes, length);
		}
		
	}

}
