/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.Queries
{
	public class PersistentExample 
	{    
		public readonly static string YapFileName = "formula1.yap";

		public static void Main(string[] args)
		{
			File.Delete(YapFileName);
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try
			{
				StoreFirstPilot(db);
				StoreSecondPilot(db);
				RetrieveAllPilots(db);
				RetrievePilotByName(db);
				RetrievePilotByExactPoints(db);
				UpdatePilot(db);
				DeleteFirstPilotByName(db);
				DeleteSecondPilotByName(db);
			}
			finally
			{
				db.Close();
			}
		}
		// end Main
        
		public static void StoreFirstPilot(IObjectContainer db)
		{
			Pilot pilot1 = new Pilot("Michael Schumacher", 100);
			db.Set(pilot1);
			Console.WriteLine("Stored {0}", pilot1);
		}
		// end StoreFirstPilot
    
		public static void StoreSecondPilot(IObjectContainer db)
		{
			Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
			db.Set(pilot2);
			Console.WriteLine("Stored {0}", pilot2);
		}
		// end StoreSecondPilot
    
		public static void RetrieveAllPilotQBE(IObjectContainer db) 
		{
			Pilot proto = new Pilot(null, 0);
			IObjectSet result = db.Get(proto);
			ListResult(result);
		}
		// end RetrieveAllPilotQBE
    
		public static void RetrieveAllPilots(IObjectContainer db) 
		{
			IObjectSet result = db.Get(typeof(Pilot));
			ListResult(result);
		}
		// end RetrieveAllPilots
    
		public static void RetrievePilotByName(IObjectContainer db)
		{
			Pilot proto = new Pilot("Michael Schumacher", 0);
			IObjectSet result = db.Get(proto);
			ListResult(result);
		}
		// end RetrievePilotByName
        
		public static void RetrievePilotByExactPoints(IObjectContainer db)
		{
			Pilot proto = new Pilot(null, 100);
			IObjectSet result = db.Get(proto);
			ListResult(result);
		}
		// end RetrievePilotByExactPoints
    
		public static void UpdatePilot(IObjectContainer db)
		{
			IObjectSet result = db.Get(new Pilot("Michael Schumacher", 0));
			Pilot found = (Pilot)result.Next();
			found.AddPoints(11);
			db.Set(found);
			Console.WriteLine("Added 11 points for {0}", found);
			RetrieveAllPilots(db);
		}
		// end UpdatePilot
    
		public static void DeleteFirstPilotByName(IObjectContainer db)
		{
			IObjectSet result = db.Get(new Pilot("Michael Schumacher", 0));
			Pilot found = (Pilot)result.Next();
			db.Delete(found);
			Console.WriteLine("Deleted {0}", found);
			RetrieveAllPilots(db);
		}
		// end DeleteFirstPilotByName
    
		public static void DeleteSecondPilotByName(IObjectContainer db)
		{
			IObjectSet result = db.Get(new Pilot("Rubens Barrichello", 0));
			Pilot found = (Pilot)result.Next();
			db.Delete(found);
			Console.WriteLine("Deleted {0}", found);
			RetrieveAllPilots(db);
		}
		// end DeleteSecondPilotByName

		public static void ListResult(IObjectSet result)
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
