/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.qlin;

/**
 * @exclude
 */
public class QLinOrderByDirection {
	
	private final String _direction;
	
	private QLinOrderByDirection(String direction) {
		_direction = direction;
	}

	final static QLinOrderByDirection ASCENDING = new QLinOrderByDirection("ascending");
	
	final static QLinOrderByDirection DESCENDING = new QLinOrderByDirection("descending");
	
	@Override
	public String toString() {
		return super.toString();
	}

}
