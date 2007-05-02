/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Structured
{	
	public class StructuredExample
	{
		private const string Db4oFileName = "reference.db4o";

		public static void Main(string[] args)
		{
			File.Delete(Db4oFileName);
            
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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
				db=Db4oFactory.OpenFile(Db4oFileName);
				UpdatePilotSeparateSessionsPart2(db);
				db.Close();
				IConfiguration configuration = UpdatePilotSeparateSessionsImprovedPart1();
                db = Db4oFactory.OpenFile(configuration, Db4oFileName);
				UpdatePilotSeparateSessionsImprovedPart2(db);
				db.Close();
				db=Db4oFactory.OpenFile(Db4oFileName);
				UpdatePilotSeparateSessionsImprovedPart3(db);
				DeleteFlat(db);
				db.Close();
				configuration = DeleteDeepPart1();
                db = Db4oFactory.OpenFile(configuration, Db4oFileName);
				DeleteDeepPart2(db);
				DeleteDeepRevisited(db);
			}
			finally
			{
				db.Close();
			}
		}
		// end Main
        
		private static void StoreFirstCar(IObjectContainer db)
		{
			Car car1 = new Car("Ferrari");
			Pilot pilot1 = new Pilot("Michael Schumacher", 100);
			car1.Pilot = pilot1;
			db.Set(car1);
		}
		// end StoreFirstCar

        private static void StoreSecondCar(IObjectContainer db)
		{
			Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
			db.Set(pilot2);
			Car car2 = new Car("BMW");
			car2.Pilot = pilot2;
			db.Set(car2);
		}
		// end StoreSecondCar

        private static void RetrieveAllCarsQBE(IObjectContainer db)
		{
			Car proto = new Car(null);
			IObjectSet result = db.Get(proto);
			ListResult(result);
		}
		// end RetrieveAllCarsQBE

        private static void RetrieveAllPilotsQBE(IObjectContainer db)
		{
			Pilot proto = new Pilot(null, 0);
			IObjectSet result = db.Get(proto);
			ListResult(result);
		}
		// end RetrieveAllPilotsQBE

        private static void RetrieveCarByPilotQBE(IObjectContainer db)
		{
			Pilot pilotproto = new Pilot("Rubens Barrichello",0);
			Car carproto = new Car(null);
			carproto.Pilot = pilotproto;
			IObjectSet result = db.Get(carproto);
			ListResult(result);
		}
		// end RetrieveCarByPilotQBE

        private static void RetrieveCarByPilotNameQuery(IObjectContainer db)
		{
			IQuery query = db.Query();
			query.Constrain(typeof(Car));
			query.Descend("_pilot").Descend("_name")
				.Constrain("Rubens Barrichello");
			IObjectSet result = query.Execute();
			ListResult(result);
		}
		// end RetrieveCarByPilotNameQuery

        private static void RetrieveCarByPilotProtoQuery(IObjectContainer db)
		{
			IQuery query = db.Query();
			query.Constrain(typeof(Car));
			Pilot proto = new Pilot("Rubens Barrichello", 0);
			query.Descend("_pilot").Constrain(proto);
			IObjectSet result = query.Execute();
			ListResult(result);
		}
		// end RetrieveCarByPilotProtoQuery

        private static void RetrievePilotByCarModelQuery(IObjectContainer db) 
		{
			IQuery carQuery = db.Query();
			carQuery.Constrain(typeof(Car));
			carQuery.Descend("_model").Constrain("Ferrari");
			IQuery pilotQuery = carQuery.Descend("_pilot");
			IObjectSet result = pilotQuery.Execute();
			ListResult(result);
		}
		// end RetrievePilotByCarModelQuery

        private static void RetrieveAllPilots(IObjectContainer db) 
		{
			IObjectSet results = db.Get(typeof(Pilot));
			ListResult(results);
		}
		// end RetrieveAllPilots

        private static void RetrieveAllCars(IObjectContainer db) 
		{
			IObjectSet results = db.Get(typeof(Car));
			ListResult(results);
		}
		// end RetrieveAllCars

        private class RetrieveCarsByPilotNamePredicate : Predicate
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
		// end RetrieveCarsByPilotNamePredicate

        private static void RetrieveCarsByPilotNameNative(IObjectContainer db) 
		{
			string pilotName = "Rubens Barrichello";
			IObjectSet results = db.Query(new RetrieveCarsByPilotNamePredicate(pilotName));
			ListResult(results);
		}
		// end RetrieveCarsByPilotNameNative

        private static void UpdateCar(IObjectContainer db)
		{
			IObjectSet result = db.Get(new Car("Ferrari"));
			Car found = (Car)result.Next();
			found.Pilot = new Pilot("Somebody else", 0);
			db.Set(found);
			result = db.Get(new Car("Ferrari"));
			ListResult(result);
		}
		// end UpdateCar

        private static void UpdatePilotSingleSession(IObjectContainer db)
		{
			IObjectSet result = db.Get(new Car("Ferrari"));
			Car found = (Car)result.Next();
			found.Pilot.AddPoints(1);
			db.Set(found);
			result = db.Get(new Car("Ferrari"));
			ListResult(result);
		}
		// end UpdatePilotSingleSession

        private static void UpdatePilotSeparateSessionsPart1(IObjectContainer db)
		{
			IObjectSet result = db.Get(new Car("Ferrari"));
			Car found = (Car)result.Next();
			found.Pilot.AddPoints(1);
			db.Set(found);
		}
		// end UpdatePilotSeparateSessionsPart1

        private static void UpdatePilotSeparateSessionsPart2(IObjectContainer db)
		{
			IObjectSet result = db.Get(new Car("Ferrari"));
			ListResult(result);
		}
		// end UpdatePilotSeparateSessionsPart2

        private static IConfiguration UpdatePilotSeparateSessionsImprovedPart1()
		{
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(Car)).CascadeOnUpdate(true);
            return configuration;
		}
		// end UpdatePilotSeparateSessionsImprovedPart1

        private static void UpdatePilotSeparateSessionsImprovedPart2(IObjectContainer db)
		{
			IObjectSet result = db.Get(new Car("Ferrari"));
			Car found = (Car)result.Next();
			found.Pilot.AddPoints(1);
			db.Set(found);
		}
		// end UpdatePilotSeparateSessionsImprovedPart2

        private static void UpdatePilotSeparateSessionsImprovedPart3(IObjectContainer db)
		{
			IObjectSet result = db.Get(new Car("Ferrari"));
			ListResult(result);
		}
		// end UpdatePilotSeparateSessionsImprovedPart3

        private static void DeleteFlat(IObjectContainer db)
		{
			IObjectSet result = db.Get(new Car("Ferrari"));
			Car found = (Car)result.Next();
			db.Delete(found);
			result = db.Get(new Car(null));
			ListResult(result);
		}
		// end DeleteFlat

        private static IConfiguration DeleteDeepPart1()
		{
            IConfiguration configuration = Db4oFactory.NewConfiguration();
			configuration.ObjectClass(typeof(Car)).CascadeOnDelete(true);
            return configuration;
		}
		// end DeleteDeepPart1

        private static void DeleteDeepPart2(IObjectContainer db)
		{
			IObjectSet result = db.Get(new Car("BMW"));
			Car found = (Car)result.Next();
			db.Delete(found);
			result = db.Get(new Car(null));
			ListResult(result);
		}
		// end DeleteDeepPart2

        private static void DeleteDeepRevisited(IObjectContainer db)
		{
			IObjectSet result = db.Get(new Pilot("Michael Schumacher", 0));
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
		// end DeleteDeepRevisited

        private static void ListResult(IObjectSet result)
		{
			Console.WriteLine(result.Size());
			while(result.HasNext()) 
			{
				Console.WriteLine(result.Next());
			}
		}
		// end ListResult
	}    
}
