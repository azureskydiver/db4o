/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import java.util.*;

import com.db4o.foundation.*;

import db4ounit.*;

/**
 * This tests simulates iterating over a random byte array.
 * The referenceCheckSum ignores the first value and we check
 * if calculatedCheckSum and referenceCheckSum ever become
 * the same. If they are the same, this hints that we lost
 * the information of the first value from the checksum.
 * 
 * Althought this algorithm is nonstandard it seems good enough
 * for use in the FileHeader checksum. (The FileHeader also
 * compares versions in addition to the checksum.) 
 */
public class SimpleCheckSumTestCase implements TestCase{
	
	public static void main(String[] args) {
		new ConsoleTestRunner(SimpleCheckSumTestCase.class).run();
	}
	
	// If we use more, our Hashtable runs out of memory.
	private static final int TESTED_BYTE_ARRAY_LENGTH = 1000;

	private Hashtable4 _checkSums = new Hashtable4();
	
	private int _unsafeCount;
	
	public void testRandomBytes(){
		assertSafe(new Function4() {
			public Object apply(Object arg) {
				return randomData((Byte) arg);
			}
		});
	}
	
	public void testSequentialBytes(){
		assertSafe(new Function4() {
			public Object apply(Object arg) {
				return sequentialData((Byte) arg);
			}
		});
	}

	private void assertSafe(Function4 dataProvider) {
		for(byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++ ){
			byte[] data = (byte[]) dataProvider.apply(b);
			testCheckSums(data, b);	
		}
		Assert.areEqual(0, _unsafeCount);
		Iterator4 i = _checkSums.iterator();
		while(i.moveNext()){
			HashtableLongEntry entry = (HashtableLongEntry) i.current(); 
			Integer usages = (Integer) entry._object;
			int usagesInt = usages.intValue();
			
			// This limit is a little bit random, it's heuristic from
			// testing. With different data we may fail sporadically
			// but then we may want to look into the algorithm again.
			if(usagesInt > 5){
				System.out.println("val:" + entry._longKey + " usages:" + usages);
			}
			Assert.isSmaller(6, usagesInt);
			
		}
	}
	
	private byte[] randomData(byte seed){
		Random random = new Random(seed);
		byte[] bytes = new byte[TESTED_BYTE_ARRAY_LENGTH];
		random.nextBytes(bytes);
		return bytes;
	}
	
	private byte[] sequentialData(byte seed){
		byte[] bytes = new byte[TESTED_BYTE_ARRAY_LENGTH];
		for (int i = 0; i < TESTED_BYTE_ARRAY_LENGTH; i++) {
			bytes[i] = (byte)(seed + i);
		}
		return bytes;
	}

	private void testCheckSums(byte[] data, byte seed) {
		long calculatedCheckSum = 0;
		long referenceCheckSum = 0;
		boolean unsafeFound = false;
		for (int i = 0; i < data.length; i++) {
			byte b = data[i];
			calculatedCheckSum = addToCheckSum(calculatedCheckSum, b);
			// can't cast to Integer directly otherwise .NET conversion fails
			Object usagesAsObject = _checkSums.get(calculatedCheckSum);
			if(usagesAsObject == null){
				_checkSums.put(calculatedCheckSum, new Integer(1));
			}else{
				Integer usages = (Integer) usagesAsObject;
				_checkSums.put(calculatedCheckSum, new Integer(usages.intValue() + 1));
			}
			if(i == 0){
				referenceCheckSum = addToCheckSum(referenceCheckSum, (byte)(b + 1));
			} else {
				referenceCheckSum = addToCheckSum(referenceCheckSum, b);
			}
			if(calculatedCheckSum == referenceCheckSum){
				if(! unsafeFound){
					System.err.println("Unsafe initial checksum value: " + seed);
					_unsafeCount++;
				}
				unsafeFound = true;
			}
		}
	}

	private static long addToCheckSum(long checkSum, byte b) {
		return SimpleCheckSum.addToCheckSum(checkSum, b);
	}

}
