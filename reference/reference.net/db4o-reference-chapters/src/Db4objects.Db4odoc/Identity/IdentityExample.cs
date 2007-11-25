/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Identity
{
	public class IdentityExample
	{
		private const string Db4oFileName = "reference.db4o";

		public static void Main(string[] args) 
		{
			CheckUniqueness();
			CheckReferenceCache();
			CheckReferenceCacheWithPurge();
			TestBind();
		
			TestCopyingWithPurge();
		}
		// end Main

		private static void SetObjects()
		{
			File.Delete(Db4oFileName);
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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
	
		private static void CheckUniqueness()
		{
			SetObjects();
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IObjectSet cars = db.Get(typeof(Car));
				Car car = (Car)cars[0];
				String pilotName = car.Pilot.Name;
				IObjectSet pilots = db.Get(new Pilot(pilotName));
				Pilot pilot = (Pilot)pilots[0];
				System.Console.WriteLine("Retrieved objects are identical: " + (pilot == car.Pilot));
			} 
			finally 
			{
				db.Close();
			}
		}
		// end CheckUniqueness
	
		private static void CheckReferenceCache()
		{
			SetObjects();
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IObjectSet pilots = db.Get(typeof(Pilot));
				Pilot pilot = (Pilot)pilots[0];
				String pilotName = pilot.Name;
				pilot.Name = "new name";
				System.Console.WriteLine("Retrieving pilot by name: " + pilotName);
				IObjectSet pilots1 = db.Get(new Pilot(pilotName));
				ListResult(pilots1);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end CheckReferenceCache

        private static void CheckReferenceCacheWithPurge()
		{
			SetObjects();
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IObjectSet pilots = db.Get(typeof(Pilot));
				Pilot pilot = (Pilot)pilots[0];
				String pilotName = pilot.Name;
				pilot.Name = "new name";
				System.Console.WriteLine("Retrieving pilot by name: " + pilotName);
				long pilotID = db.Ext().GetID(pilot);
				if (db.Ext().IsCached(pilotID))
				{
					db.Ext().Purge(pilot);
				}
				IObjectSet pilots1 = db.Get(new Pilot(pilotName));
				ListResult(pilots1);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end CheckReferenceCacheWithPurge

        private static void TestCopyingWithPurge()
		{
			SetObjects();
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IObjectSet pilots = db.Get(typeof(Pilot));
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

        private static void TestBind()
		{
			SetObjects();
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IQuery q = db.Query();
				q.Constrain(typeof(Car));
				q.Descend("_model").Constrain("Ferrari");
				IObjectSet result = q.Execute();
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

        private static void ListResult(IObjectSet result)
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
