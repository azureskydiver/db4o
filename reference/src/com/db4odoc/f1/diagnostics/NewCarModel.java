package com.db4odoc.f1.diagnostics;

import com.db4odoc.f1.evaluations.*;
import com.db4o.query.Predicate;

public class NewCarModel  extends Predicate {
	public boolean match(Object car) {
		return ((Car)car).getModel().endsWith("2002");
	}
}