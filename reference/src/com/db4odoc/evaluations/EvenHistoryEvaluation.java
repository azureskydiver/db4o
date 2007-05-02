/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.evaluations;

import com.db4o.query.*;


public class EvenHistoryEvaluation implements Evaluation {
  public void evaluate(Candidate candidate) {
    Car car=(Car)candidate.getObject();
    candidate.include(car.getHistory().size() % 2 == 0);
  }
}
