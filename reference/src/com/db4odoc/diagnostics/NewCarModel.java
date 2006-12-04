package com.db4odoc.diagnostics;

import com.db4o.query.Predicate;

public class  NewCarModel  extends Predicate<Car> {
	public boolean match(Car car) {
		return ((Car)car).getModel().endsWith("2002");
	}
}
