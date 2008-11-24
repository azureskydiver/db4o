/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.exceptions;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class InvalidSlotExceptionTestCase extends AbstractDb4oTestCase {
	
	private static final int INVALID_ID = 3;
	
	private static final int OUT_OF_MEMORY_ID = 4;

	public static void main(String[] args) {
		new InvalidSlotExceptionTestCase().runAll();
	}
	
	
	protected void configure(Configuration config) throws Exception {
		config.storage(new MockStorage());
	}
	
	public void testInvalidSlotException() throws Exception {
		Assert.expect(InvalidIDException.class, InvalidSlotException.class, new CodeBlock(){
			public void run() throws Throwable {
				db().getByID(INVALID_ID);		
			}
		});
	}
	
	public void testDbNotClosedOnOutOfMemory(){
		Class expectedException = isClientServer() && ! isEmbeddedClientServer() 
		? InvalidIDException.class : OutOfMemoryError.class;
		Assert.expect(expectedException, new CodeBlock(){
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
	
	public static class MockStorage extends StorageDecorator {
				
		public MockStorage(){
			super(new FileStorage());
		}

		@Override
		protected Bin decorate(Bin bin) {
			return new MockBin(bin);
		}
		
		private static class MockBin extends BinDecorator {
			private boolean _deliverInvalidSlot;

			public MockBin(Bin bin) {
				super(bin);
			}
			
			public int read(long pos, byte[] bytes, int length) throws Db4oIOException {
				seek(pos);
				if(_deliverInvalidSlot){
					ByteArrayBuffer buffer = new ByteArrayBuffer(Const4.POINTER_LENGTH);
					buffer.writeInt(1);
					buffer.writeInt(Integer.MAX_VALUE);
					System.arraycopy(buffer._buffer, 0, bytes, 0, Const4.POINTER_LENGTH);
					return length;
				}
				return super.read(pos, bytes, length);
			}

			private void seek(long pos) throws Db4oIOException {
				if(pos == OUT_OF_MEMORY_ID){
					throw new OutOfMemoryError();
				}
				if(pos == INVALID_ID){
					_deliverInvalidSlot = true;
					return;
				}
				_deliverInvalidSlot = false;
			}
		}
	}

}
