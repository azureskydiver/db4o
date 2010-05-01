/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.foundation;

import com.db4o.internal.*;

/**
 * @exclude
 */
public final class SimpleCheckSum {
	
	private static final long upperBound = 900000000000000000L;
	
	private static final long lowerBound = 900000L;
	
	public final static long checkSum(byte[] bytes, int start, int length){
		long checkSum = 0;
		int end = start + length;
		for (int i = start; i < end; i++) {
			checkSum = addToCheckSum(checkSum, bytes[i]);
		}
		return checkSum;
	}
	
	public final static long addToCheckSum(long checkSum, byte b){
		if(checkSum < lowerBound){
			checkSum += lowerBound;
		}
		
		checkSum = checkSum ^ Platform4.toSByte(b);
		
		if(checkSum < 0){
			checkSum = -checkSum;
		}
		checkSum = checkSum << 1;
		if(checkSum > upperBound){
			checkSum -= upperBound;
		}
		return checkSum;
	}

}
