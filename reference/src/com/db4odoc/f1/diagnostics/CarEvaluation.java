package com.db4odoc.f1.diagnostics;

import com.db4o.query.*;
import com.db4odoc.f1.evaluations.*;

public class CarEvaluation implements Evaluation {
	public void evaluate(Candidate candidate)
	{
		Car car=(Car)candidate.getObject();
		candidate.include(car.getModel().endsWith("2002"));
	}
}
