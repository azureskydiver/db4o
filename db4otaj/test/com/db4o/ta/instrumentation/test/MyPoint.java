/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.ta.instrumentation.test;

import java.awt.*;

/**
 * @exclude
 */
public class MyPoint extends Point {

	private int _z;
	
	public MyPoint(int x, int y, int z) {
		super(x,y);
		_z = z;
	}
	
}
