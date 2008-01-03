package com.db4o.f1.chapter6;

import java.io.*;

import com.db4o.*;
import com.db4o.f1.*;
import com.db4o.f1.chapter3.*;
import com.db4o.query.*;

public class EvaluationExample extends Util {
  public static void main(String[] args) {
    new File(Util.DB4OFILENAME).delete();
    ObjectContainer db=Db4o.openFile(Util.DB4OFILENAME);
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
    db.store(car1);
    Pilot pilot2=new Pilot("Rubens Barrichello",99);
    Car car2=new Car("BMW");
    car2.setPilot(pilot2);
    car2.snapshot();
    car2.snapshot();
    db.store(car2);
  }
	
  public static void queryWithEvaluation(ObjectContainer db) {
    Query query=db.query();
    query.constrain(Car.class);
    query.constrain(new EvenHistoryEvaluation());
    ObjectSet result=query.execute();
    Util.listResult(result);
  }
}
