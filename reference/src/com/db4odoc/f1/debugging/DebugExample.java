/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.debugging;

import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import com.db4odoc.f1.Util;
import com.db4odoc.f1.evaluations.Car;


public class DebugExample extends Util {


	public static void main(String[] args) {
		setCars();
	}

	public static void setCars()
	{
		Db4o.configure().messageLevel(3);
		 new File(Util.YAPFILENAME).delete();
		 ObjectContainer db=Db4o.openFile(Util.YAPFILENAME);
		try {
			Car car1 = new Car("BMW");
			db.set(car1);
			Car car2 = new Car("Ferrari");
			db.set(car2);
			db.deactivate(car1,2);
			Query query = db.query();
			query.constrain(Car.class);
			ObjectSet results = query.execute();
			listResult(results);
		} finally {
			db.close();
		}
	}
}
