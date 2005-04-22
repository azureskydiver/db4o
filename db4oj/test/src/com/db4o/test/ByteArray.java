package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

class ByteArrayHolder {
	
	byte[] bytes;
	
	public ByteArrayHolder(byte[] bytes) {
		this.bytes = bytes;
	}
}

public class ByteArray {
	
	static final int ITERATIONS = 50;
	
	static final int INSTANCES = 2;
	
	static final int ARRAY_LENGTH = 2*1024*1024;
	
	public void store() {
		for (int i=0; i<INSTANCES; ++i) {
			Test.store(new ByteArrayHolder(createByteArray()));
		}
	}
	
	public void test() {
		long start = System.currentTimeMillis();
		for (int i=0; i<ITERATIONS; ++i) {
			
			Test.reOpen();
			
			Query query = Test.query();
			query.constrain(ByteArrayHolder.class);
			
			ObjectSet os = query.execute();
			Test.ensure(INSTANCES == os.size());
			
			while (os.hasNext()) {
				Test.ensure(ARRAY_LENGTH == ((ByteArrayHolder)os.next()).bytes.length);
			}
		}
		long end = System.currentTimeMillis();
		System.err.println("" + ITERATIONS + " iterations took " + (end-start) + "ms");
	}
	
	byte[] createByteArray() {
		byte[] bytes = new byte[ARRAY_LENGTH];
		for (int i=0; i<bytes.length; ++i) {
			bytes[i] = (byte)(i % 256);
		}
		return bytes;
	}
}
