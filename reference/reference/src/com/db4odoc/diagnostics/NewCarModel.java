/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.diagnostics;

/**
 * @sharpen.ignore
 */
public class  NewCarModel  extends com.db4o.query.Predicate<Car> {
	public boolean match(Car car) {
		return ((Car)car).getModel().endsWith("2002");
	}
}
