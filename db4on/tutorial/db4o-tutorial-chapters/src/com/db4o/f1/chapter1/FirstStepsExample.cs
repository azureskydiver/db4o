using System;
using System.IO;
using com.db4o;
using com.db4o.f1;

namespace com.db4o.f1.chapter1
{
	public class FirstStepsExample : Util
	{    
		public static void Main(string[] args)
		{
			File.Delete(Util.YapFileName);
			AccessDb4o();
			File.Delete(Util.YapFileName);
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
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
        
		public static void AccessDb4o()
		{
			ObjectContainer db=Db4o.OpenFile(Util.YapFileName);
			try
			{
				// do something with db4o
			}
			finally
			{
				db.Close();
			}
		}
        
		public static void StoreFirstPilot(ObjectContainer db)
		{
			Pilot pilot1 = new Pilot("Michael Schumacher", 100);
			db.Set(pilot1);
			Console.WriteLine("Stored {0}", pilot1);
		}
    
		public static void StoreSecondPilot(ObjectContainer db)
		{
			Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
			db.Set(pilot2);
			Console.WriteLine("Stored {0}", pilot2);
		}
    
		public static void RetrieveAllPilotQBE(ObjectContainer db) 
		{
			Pilot proto = new Pilot(null, 0);
			ObjectSet result = db.Get(proto);
			ListResult(result);
		}
    
		public static void RetrieveAllPilots(ObjectContainer db) 
		{
			ObjectSet result = db.Get(typeof(Pilot));
			ListResult(result);
		}
    
		public static void RetrievePilotByName(ObjectContainer db)
		{
			Pilot proto = new Pilot("Michael Schumacher", 0);
			ObjectSet result = db.Get(proto);
			ListResult(result);
		}
        
		public static void RetrievePilotByExactPoints(ObjectContainer db)
		{
			Pilot proto = new Pilot(null, 100);
			ObjectSet result = db.Get(proto);
			ListResult(result);
		}
    
		public static void UpdatePilot(ObjectContainer db)
		{
			ObjectSet result = db.Get(new Pilot("Michael Schumacher", 0));
			Pilot found = (Pilot)result.Next();
			found.AddPoints(11);
			db.Set(found);
			Console.WriteLine("Added 11 points for {0}", found);
			RetrieveAllPilots(db);
		}
    
		public static void DeleteFirstPilotByName(ObjectContainer db)
		{
			ObjectSet result = db.Get(new Pilot("Michael Schumacher", 0));
			Pilot found = (Pilot)result.Next();
			db.Delete(found);
			Console.WriteLine("Deleted {0}", found);
			RetrieveAllPilots(db);
		}
    
		public static void DeleteSecondPilotByName(ObjectContainer db)
		{
			ObjectSet result = db.Get(new Pilot("Rubens Barrichello", 0));
			Pilot found = (Pilot)result.Next();
			db.Delete(found);
			Console.WriteLine("Deleted {0}", found);
			RetrieveAllPilots(db);
		}
	}
}
