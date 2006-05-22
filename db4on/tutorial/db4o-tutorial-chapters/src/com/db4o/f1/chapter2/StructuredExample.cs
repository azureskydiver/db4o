using System;
using System.IO;
using com.db4o;
using com.db4o.f1;
using com.db4o.query;

namespace com.db4o.f1.chapter2
{	
	public class StructuredExample : Util
	{
		public static void Main(String[] args)
		{
			File.Delete(Util.YapFileName);
            
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			try
			{
				StoreFirstCar(db);
				StoreSecondCar(db);
				RetrieveAllCarsQBE(db);
				RetrieveAllPilotsQBE(db);
				RetrieveCarByPilotQBE(db);
				RetrieveCarByPilotNameQuery(db);
				RetrieveCarByPilotProtoQuery(db);
				RetrievePilotByCarModelQuery(db);
				UpdateCar(db);
				UpdatePilotSingleSession(db);
				UpdatePilotSeparateSessionsPart1(db);
				db.Close();
				db=Db4o.OpenFile(Util.YapFileName);
				UpdatePilotSeparateSessionsPart2(db);
				db.Close();
				UpdatePilotSeparateSessionsImprovedPart1(db);
				db=Db4o.OpenFile(Util.YapFileName);
				UpdatePilotSeparateSessionsImprovedPart2(db);
				db.Close();
				db=Db4o.OpenFile(Util.YapFileName);
				UpdatePilotSeparateSessionsImprovedPart3(db);
				DeleteFlat(db);
				db.Close();
				DeleteDeepPart1(db);
				db=Db4o.OpenFile(Util.YapFileName);
				DeleteDeepPart2(db);
				DeleteDeepRevisited(db);
			}
			finally
			{
				db.Close();
			}
		}
        
		public static void StoreFirstCar(ObjectContainer db)
		{
			Car car1 = new Car("Ferrari");
			Pilot pilot1 = new Pilot("Michael Schumacher", 100);
			car1.Pilot = pilot1;
			db.Set(car1);
		}
        
		public static void StoreSecondCar(ObjectContainer db)
		{
			Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
			db.Set(pilot2);
			Car car2 = new Car("BMW");
			car2.Pilot = pilot2;
			db.Set(car2);
		}

		public static void RetrieveAllCarsQBE(ObjectContainer db)
		{
			Car proto = new Car(null);
			ObjectSet result = db.Get(proto);
			ListResult(result);
		}
        
		public static void RetrieveAllPilotsQBE(ObjectContainer db)
		{
			Pilot proto = new Pilot(null, 0);
			ObjectSet result = db.Get(proto);
			ListResult(result);
		}
        
		public static void RetrieveCarByPilotQBE(ObjectContainer db)
		{
			Pilot pilotproto = new Pilot("Rubens Barrichello",0);
			Car carproto = new Car(null);
			carproto.Pilot = pilotproto;
			ObjectSet result = db.Get(carproto);
			ListResult(result);
		}
        
		public static void RetrieveCarByPilotNameQuery(ObjectContainer db)
		{
			Query query = db.Query();
			query.Constrain(typeof(Car));
			query.Descend("_pilot").Descend("_name")
				.Constrain("Rubens Barrichello");
			ObjectSet result = query.Execute();
			ListResult(result);
		}
        
		public static void RetrieveCarByPilotProtoQuery(ObjectContainer db)
		{
			Query query = db.Query();
			query.Constrain(typeof(Car));
			Pilot proto = new Pilot("Rubens Barrichello", 0);
			query.Descend("_pilot").Constrain(proto);
			ObjectSet result = query.Execute();
			ListResult(result);
		}
        
		public static void RetrievePilotByCarModelQuery(ObjectContainer db) 
		{
			Query carQuery = db.Query();
			carQuery.Constrain(typeof(Car));
			carQuery.Descend("_model").Constrain("Ferrari");
			Query pilotQuery = carQuery.Descend("_pilot");
			ObjectSet result = pilotQuery.Execute();
			ListResult(result);
		}
        
		public static void RetrieveAllPilots(ObjectContainer db) 
		{
			ObjectSet results = db.Get(typeof(Pilot));
			ListResult(results);
		}

		public static void RetrieveAllCars(ObjectContainer db) 
		{
			ObjectSet results = db.Get(typeof(Car));
			ListResult(results);
		}
    
		public class RetrieveCarsByPilotNamePredicate : Predicate
		{
			readonly string _pilotName;
    		
			public RetrieveCarsByPilotNamePredicate(string pilotName)
			{
				_pilotName = pilotName;
			}
    		
			public bool Match(Car candidate)
			{
				return candidate.Pilot.Name == _pilotName;
			}
		}
    
		public static void RetrieveCarsByPilotNameNative(ObjectContainer db) 
		{
			string pilotName = "Rubens Barrichello";
			ObjectSet results = db.Query(new RetrieveCarsByPilotNamePredicate(pilotName));
			ListResult(results);
		}
  		
		public static void UpdateCar(ObjectContainer db)
		{
			ObjectSet result = db.Get(new Car("Ferrari"));
			Car found = (Car)result.Next();
			found.Pilot = new Pilot("Somebody else", 0);
			db.Set(found);
			result = db.Get(new Car("Ferrari"));
			ListResult(result);
		}
        
		public static void UpdatePilotSingleSession(ObjectContainer db)
		{
			ObjectSet result = db.Get(new Car("Ferrari"));
			Car found = (Car)result.Next();
			found.Pilot.AddPoints(1);
			db.Set(found);
			result = db.Get(new Car("Ferrari"));
			ListResult(result);
		}
        
		public static void UpdatePilotSeparateSessionsPart1(ObjectContainer db)
		{
			ObjectSet result = db.Get(new Car("Ferrari"));
			Car found = (Car)result.Next();
			found.Pilot.AddPoints(1);
			db.Set(found);
		}
        
		public static void UpdatePilotSeparateSessionsPart2(ObjectContainer db)
		{
			ObjectSet result = db.Get(new Car("Ferrari"));
			ListResult(result);
		}
        
		public static void UpdatePilotSeparateSessionsImprovedPart1(ObjectContainer db)
		{
			Db4o.Configure().ObjectClass(typeof(Car))
				.CascadeOnUpdate(true);        
		}
        
		public static void UpdatePilotSeparateSessionsImprovedPart2(ObjectContainer db)
		{
			ObjectSet result = db.Get(new Car("Ferrari"));
			Car found = (Car)result.Next();
			found.Pilot.AddPoints(1);
			db.Set(found);
		}
        
		public static void UpdatePilotSeparateSessionsImprovedPart3(ObjectContainer db)
		{
			ObjectSet result = db.Get(new Car("Ferrari"));
			ListResult(result);
		}
        
		public static void DeleteFlat(ObjectContainer db)
		{
			ObjectSet result = db.Get(new Car("Ferrari"));
			Car found = (Car)result.Next();
			db.Delete(found);
			result = db.Get(new Car(null));
			ListResult(result);
		}
        
		public static void DeleteDeepPart1(ObjectContainer db)
		{
			Db4o.Configure().ObjectClass(typeof(Car))
				.CascadeOnDelete(true);
		}
        
		public static void DeleteDeepPart2(ObjectContainer db)
		{
			ObjectSet result = db.Get(new Car("BMW"));
			Car found = (Car)result.Next();
			db.Delete(found);
			result = db.Get(new Car(null));
			ListResult(result);
		}
        
		public static void DeleteDeepRevisited(ObjectContainer db)
		{
			ObjectSet result = db.Get(new Pilot("Michael Schumacher", 0));
			Pilot pilot = (Pilot)result.Next();
			Car car1 = new Car("Ferrari");
			Car car2 = new Car("BMW");
			car1.Pilot = pilot;
			car2.Pilot = pilot;
			db.Set(car1);
			db.Set(car2);
			db.Delete(car2);
			result = db.Get(new Car(null));
			ListResult(result);
		}
	}    
}
