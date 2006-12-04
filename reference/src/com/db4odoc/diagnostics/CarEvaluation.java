package com.db4odoc.diagnostics;

import com.db4o.query.*;

public class CarEvaluation implements Evaluation {
	public void evaluate(Candidate candidate)
	{
		Car car=(Car)candidate.getObject();
		candidate.include(car.getModel().endsWith("2002"));
	}
}
