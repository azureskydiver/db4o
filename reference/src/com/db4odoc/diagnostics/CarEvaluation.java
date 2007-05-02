/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.diagnostics;

import com.db4o.query.*;

public class CarEvaluation implements Evaluation {
	public void evaluate(Candidate candidate)
	{
		Car car=(Car)candidate.getObject();
		candidate.include(car.getModel().endsWith("2002"));
	}
}
