/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
package com.db4odoc.evaluations;

import java.io.*;

import com.db4o.*;
import com.db4o.query.*;

public class EvaluationExample {
	private final static String DB4O_FILE_NAME = "reference.db4o";

	public static void main(String[] args) {
		new File(DB4O_FILE_NAME).delete();
		ObjectContainer container = Db4o.openFile(DB4O_FILE_NAME);
		try {
			storeCars(container);
			queryWithEvaluation(container);
		} finally {
			container.close();
		}
	}
	// end main

	private static void storeCars(ObjectContainer container) {
		Pilot pilot1 = new Pilot("Michael Schumacher", 100);
		Car car1 = new Car("Ferrari");
		car1.setPilot(pilot1);
		car1.snapshot();
		container.store(car1);
		Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
		Car car2 = new Car("BMW");
		car2.setPilot(pilot2);
		car2.snapshot();
		car2.snapshot();
		container.store(car2);
	}
	// end storeCars

	private static void queryWithEvaluation(ObjectContainer container) {
		Query query = container.query();
		query.constrain(Car.class);
		query.constrain(new EvenHistoryEvaluation());
		ObjectSet result = query.execute();
		listResult(result);
	}
	// end queryWithEvaluation

	private static void listResult(ObjectSet result) {
		System.out.println(result.size());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}
	// end listResult
}
