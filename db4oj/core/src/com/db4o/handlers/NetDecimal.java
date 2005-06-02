/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.handlers;

import java.math.*;

import com.db4o.*;

/**
 * @exclude
 */
public class NetDecimal extends NetSimpleTypeHandler{
	private final static BigInteger FACTOR=new BigInteger("100",16);

	public NetDecimal(YapStream stream) {
		super(stream, 21, 16);
	}
	
	public String toString(byte[] bytes) {
		//return "no converter for System.Decimal, mscorlib";
		//return bitString(bytes);
		return convert(bytes);
	}
	
	private String bitString(byte[] bytes) {
		StringBuffer str=new StringBuffer();
		for(int i=0;i<bytes.length;i++) {
			for(int j=7;j>=0;j--) {
				int curbit=(bytes[i]>>j)&0x1;
				str.append(String.valueOf(curbit));
			}
		}
		System.err.println(str);
		return str.toString();
	}
	
	private String convert(byte[] bytes) {
		BigInteger mantissa=BigInteger.ZERO;
		for(int blockoffset=8;blockoffset>=0;blockoffset-=4) {
			for(int byteidx=0;byteidx<4;byteidx++) {
				mantissa=mantissa.multiply(FACTOR);
				int idx=blockoffset+byteidx;
				mantissa=mantissa.add(new BigInteger(String.valueOf(bytes[idx]&0xff),10));
			}
		}
		int exponent=bytes[13]&0x1f;
		int sign=bytes[12];
		BigDecimal factor=BigDecimal.ONE.divide(BigDecimal.TEN.pow(exponent));
		BigDecimal val=new BigDecimal(mantissa).multiply(factor);
		if(sign!=0) {
			val=val.negate();
		}
		return val.toString();
	}
}
