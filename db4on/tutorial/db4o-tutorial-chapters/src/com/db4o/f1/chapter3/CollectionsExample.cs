using System;
using System.Collections;
using System.IO;
using com.db4o;
using com.db4o.query;

namespace com.db4o.f1.chapter3
{	
	public class CollectionsExample : Util
	{
		public static void Main(string[] args)
		{
			File.Delete(Util.YapFileName);            
			ObjectContainer db = Db4o.openFile(Util.YapFileName);
			try
			{
				storeFirstCar(db);
				storeSecondCar(db);
				retrieveAllSensorReadouts(db);
				retrieveSensorReadoutQBE(db);
				retrieveCarQBE(db);
				retrieveCollections(db);
				retrieveArrays(db);
				retrieveSensorReadoutQuery(db);
				retrieveCarQuery(db);
				db.close();
				updateCarPart1();
				db = Db4o.openFile(Util.YapFileName);
				updateCarPart2(db);
				updateCollection(db);
				db.close();
				deleteAllPart1();
				db=Db4o.openFile(Util.YapFileName);
				deleteAllPart2(db);
				retrieveAllSensorReadouts(db);
			}
			finally
			{
				db.close();
			}
		}
        
		public static void storeFirstCar(ObjectContainer db)
		{
			Car car1 = new Car("Ferrari");
			Pilot pilot1 = new Pilot("Michael Schumacher", 100);
			car1.Pilot = pilot1;
			db.set(car1);
		}
        
		public static void storeSecondCar(ObjectContainer db)
		{
			Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
			Car car2 = new Car("BMW");
			car2.Pilot = pilot2;
			car2.Snapshot();
			car2.Snapshot();
			db.set(car2);       
		}
        
		public static void retrieveAllSensorReadouts(ObjectContainer db)
		{
			ObjectSet result = db.get(typeof(SensorReadout));
			listResult(result);
		}
        
		public static void retrieveSensorReadoutQBE(ObjectContainer db)
		{
			SensorReadout proto = new SensorReadout(new double[] { 0.3, 0.1 }, DateTime.MinValue, null);
			ObjectSet result = db.get(proto);
			listResult(result);
		}
        
		public static void retrieveCarQBE(ObjectContainer db)
		{
			SensorReadout protoReadout = new SensorReadout(new double[] { 0.6, 0.2 }, DateTime.MinValue, null);
			IList protoHistory = new ArrayList();
			protoHistory.Add(protoReadout);
			Car protoCar = new Car(null, protoHistory);
			ObjectSet result = db.get(protoCar);
			listResult(result);
		}
        
		public static void retrieveCollections(ObjectContainer db)
		{
			ObjectSet result = db.get(new ArrayList());
			listResult(result);
		}
        
		public static void retrieveArrays(ObjectContainer db)
		{
			ObjectSet result = db.get(new double[] { 0.6, 0.4 });
			listResult(result);
		}
        
		public static void retrieveSensorReadoutQuery(ObjectContainer db)
		{
			Query query = db.query();
			query.constrain(typeof(SensorReadout));
			Query valueQuery = query.descend("_values");
			valueQuery.constrain(0.3);
			valueQuery.constrain(0.1);
			ObjectSet results = query.execute();
			listResult(results);
		}
        
		public static void retrieveCarQuery(ObjectContainer db)
		{
			Query query = db.query();
			query.constrain(typeof(Car));
			Query historyQuery = query.descend("_history");
			historyQuery.constrain(typeof(SensorReadout));
			Query valueQuery = historyQuery.descend("_values");
			valueQuery.constrain(0.3);
			valueQuery.constrain(0.1);
			ObjectSet results = query.execute();
			listResult(results);
		}

		public class RetrieveSensorReadoutPredicate : Predicate
		{
			public bool Match(SensorReadout candidate)
			{
				return Array.IndexOf(candidate.Values, 0.3) > -1
					&& Array.IndexOf(candidate.Values, 0.1) > -1;
			}
		}
        
		public static void retrieveSensorReadoutNative(ObjectContainer db) 
		{
			ObjectSet results = db.query(new RetrieveSensorReadoutPredicate());
			listResult(results);
		}

		public class RetrieveCarPredicate : Predicate
		{
			public bool Match(Car car)
			{
				foreach (SensorReadout sensor in car.History)
				{
					if (Array.IndexOf(sensor.Values, 0.3) > -1
						&& Array.IndexOf(sensor.Values, 0.1) > -1)
					{
						return true; 
					}
				}
				return false;
			}
		}

		public static void retrieveCarNative(ObjectContainer db)
		{
			ObjectSet results = db.query(new RetrieveCarPredicate());
			listResult(results);
		}

		public static void updateCarPart1()
		{
			Db4o.configure().objectClass(typeof(Car)).cascadeOnUpdate(true);
		}
        
		public static void updateCarPart2(ObjectContainer db)
		{
			ObjectSet result = db.get(new Car("BMW", null));
			Car car = (Car)result.next();
			car.Snapshot();
			db.set(car);
			retrieveAllSensorReadouts(db);
		}
        
		public static void updateCollection(ObjectContainer db)
		{
			Query query = db.query();
			query.constrain(typeof(Car));
			ObjectSet result = query.descend("_history").execute();
			IList history = (IList)result.next();
			history.RemoveAt(0);
			db.set(history);
			Car proto = new Car(null, null);
			result = db.get(proto);
			foreach (Car car in result)
			{	
				foreach (object readout in car.History)
				{
					Console.WriteLine(readout);
				}
			}
		}
        
		public static void deleteAllPart1()
		{
			Db4o.configure().objectClass(typeof(Car)).cascadeOnDelete(true);
		}

		public static void deleteAllPart2(ObjectContainer db)
		{
			ObjectSet result = db.get(new Car(null, null));
			foreach (object car in result)
			{
				db.delete(car);
			}
			ObjectSet readouts = db.get(new SensorReadout(null, DateTime.MinValue, null));
			foreach (object readout in readouts)
			{
				db.delete(readout);
			}
		}
	}
}
