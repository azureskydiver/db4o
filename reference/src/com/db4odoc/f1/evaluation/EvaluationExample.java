package com.db4odoc.f1.evaluation;

import java.io.*;

import com.db4o.*;
import com.db4o.query.*;
import com.db4odoc.f1.*;


public class EvaluationExample extends Util {
  public static void main(String[] args) {
    new File(Util.YAPFILENAME).delete();
    ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
    try {
      storeCars(db);
      queryWithEvaluation(db);
    }
    finally {
      db.close();
    }
  }
	
  public static void storeCars(ObjectContainer db) {
    Pilot pilot1=new Pilot("Michael Schumacher",100);
    Car car1=new Car("Ferrari");
    car1.setPilot(pilot1);
    car1.snapshot();
    db.set(car1);
    Pilot pilot2=new Pilot("Rubens Barrichello",99);
    Car car2=new Car("BMW");
    car2.setPilot(pilot2);
    car2.snapshot();
    car2.snapshot();
    db.set(car2);
  }
	
  public static void queryWithEvaluation(ObjectContainer db) {
    Query query=db.query();
    query.constrain(Car.class);
    query.constrain(new EvenHistoryEvaluation());
    ObjectSet result=query.execute();
    Util.listResult(result);
  }
}
