package com.db4o.internal.handlers;

import java.math.*;

import com.db4o.internal.reflect.*;
import com.db4o.reflect.*;

/**
 * @sharpen.ignore
 * @exclude
 */
public class BigIntegerTypeHandler extends BigNumberTypeHandler<BigInteger> {

	@Override
	protected BigInteger fromByteArray(byte[] data) {
	    return new BigInteger(data);
    }

	@Override
	protected byte[] toByteArray(BigInteger value) {
	    return value.toByteArray();
    }

	public boolean canHold(ReflectClass type) {
		return ReflectClasses.areEqual(BigInteger.class, type);
    }

	@Override
    protected int compare(BigInteger x, BigInteger y) {
		return x.compareTo(y);
    }
}
