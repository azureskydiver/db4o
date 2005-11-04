package com.db4o.f1.chapter6;

import com.db4o.f1.chapter3.*;
import com.db4o.query.*;

public class EvenHistoryEvaluation implements Evaluation {
  public void evaluate(Candidate candidate) {
    Car car=(Car)candidate.getObject();
    candidate.include(car.getHistory().size() % 2 == 0);
  }
}
