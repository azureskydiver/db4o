/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;


public class DrsFixturePair { 
	
	public final DrsFixture a;
	public final DrsFixture b;
	
	public DrsFixturePair(DrsFixture fixtureA, DrsFixture fixtureB) {
		if (null == fixtureA) throw new IllegalArgumentException("fixtureA");
		if (null == fixtureB) throw new IllegalArgumentException("fixtureB");
		
		a = fixtureA;
		b = fixtureB;
	}
}
