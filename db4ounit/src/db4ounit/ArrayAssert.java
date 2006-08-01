/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit;


public class ArrayAssert {

	public static void areEqual(byte[] expected, byte[] actual) {
		if (expected == actual) return;
		if (expected == null || actual == null) Assert.areSame(expected, actual);
		Assert.areEqual(expected.length, actual.length);
	    for (int i = 0; i < expected.length; i++) {
	        Assert.areEqual(expected[i], actual[i]);
	    }
	}

	public static void areNotEqual(byte[] expected, byte[] actual) {
		Assert.areNotSame(expected, actual);		
		for (int i = 0; i < expected.length; i++) {
	        if (expected[i] != actual[i]) return;
	    }
		Assert.isTrue(false);
	}

}
