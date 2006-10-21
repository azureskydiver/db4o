/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using com.db4o;
using com.db4o.query;

namespace com.db4odoc.f1.identity
{
	public class IdentityExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public static void Main(String[] args) 
		{
			CheckUniqueness();
			CheckReferenceCache();
			CheckReferenceCacheWithPurge();
			TestBind();
		
			TestCopyingWithPurge();
		}
		// end Main

		public static void SetObjects()
		{
			File.Delete(YapFileName);
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
				db.Set(car);
				car = new Car("Ferrari", new Pilot("Michael Schumacher"));
				db.Set(car);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end SetObjects
	
		public static void CheckUniqueness()
		{
			SetObjects();
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet cars = db.Get(typeof(Car));
				Car car = (Car)cars[0];
				String pilotName = car.Pilot.Name;
				ObjectSet pilots = db.Get(new Pilot(pilotName));
				Pilot pilot = (Pilot)pilots[0];
				System.Console.WriteLine("Retrieved objects are identical: " + (pilot == car.Pilot));
			} 
			finally 
			{
				db.Close();
			}
		}
		// end CheckUniqueness
	
		public static void CheckReferenceCache()
		{
			SetObjects();
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet pilots = db.Get(typeof(Pilot));
				Pilot pilot = (Pilot)pilots[0];
				String pilotName = pilot.Name;
				pilot.Name = "new name";
				System.Console.WriteLine("Retrieving pilot by name: " + pilotName);
				ObjectSet pilots1 = db.Get(new Pilot(pilotName));
				ListResult(pilots1);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end CheckReferenceCache
	
		public static void CheckReferenceCacheWithPurge()
		{
			SetObjects();
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet pilots = db.Get(typeof(Pilot));
				Pilot pilot = (Pilot)pilots[0];
				String pilotName = pilot.Name;
				pilot.Name = "new name";
				System.Console.WriteLine("Retrieving pilot by name: " + pilotName);
				long pilotID = db.Ext().GetID(pilot);
				if (db.Ext().IsCached(pilotID))
				{
					db.Ext().Purge(pilot);
				}
				ObjectSet pilots1 = db.Get(new Pilot(pilotName));
				ListResult(pilots1);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end CheckReferenceCacheWithPurge
	
		public static void TestCopyingWithPurge()
		{
			SetObjects();
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet pilots = db.Get(typeof(Pilot));
				Pilot pilot = (Pilot)pilots[0];
				db.Ext().Purge(pilot);
				db.Set(pilot);
				pilots = db.Get(typeof(Pilot));
				ListResult(pilots);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end TestCopyingWithPurge
	
		public static void TestBind()
		{
			SetObjects();
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				Query q = db.Query();
				q.Constrain(typeof(Car));
				q.Descend("_model").Constrain("Ferrari");
				ObjectSet result = q.Execute();
				Car car1 = (Car)result[0];
				long IdCar1 = db.Ext().GetID(car1);
				Car car2 = new Car("BMW", new Pilot("Rubens Barrichello"));
				db.Ext().Bind(car2,IdCar1);
				db.Set(car2);

				result = db.Get(typeof(Car));
				ListResult(result);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end TestBind

		public static void ListResult(ObjectSet result)
		{
			Console.WriteLine(result.Count);
			foreach (object item in result)
			{
				Console.WriteLine(item);
			}
		}
		// end ListResult
	}
}
