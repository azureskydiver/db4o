/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Queries
{
	public class QueryExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public static void Main(string[] args)
		{
			StorePilot();
			UpdatePilotWrong();
			UpdatePilot();
			DeletePilot();
			File.Delete(YapFileName);
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try
			{
				RetrievePilotByName(db);
				RetrievePilotByExactPoints(db);
				RetrieveByNegation(db);
				RetrieveByConjunction(db);
				RetrieveByDisjunction(db);
				RetrieveByComparison(db);
				RetrieveByDefaultFieldValue(db);
				RetrieveSorted(db); 
			}
			finally
			{
				db.Close();
			}
		}
		// end Main
    
		public static void StorePilot()
		{
			File.Delete(YapFileName);
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try
			{
				Pilot pilot = new Pilot("Michael Schumacher", 0);
				db.Set(pilot);
				// change pilot and resave updated
				pilot.AddPoints(10);
				db.Set(pilot);
				Console.WriteLine("Stored {0}", pilot);
			}
			finally 
			{
				db.Close();
			}
			RetrieveAllPilots();
		}
		// end StorePilot

		public static void UpdatePilotWrong()
		{
			StorePilot();
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try
			{
				// Even completely identical Pilot object
				// won't work for update of the saved pilot
				Pilot pilot = new Pilot("Michael Schumacher",0);
				pilot.AddPoints(11);
				db.Set(pilot);
				Console.WriteLine("Added 11 points to {0}", pilot);
			}
			finally 
			{
				db.Close();
			}
			RetrieveAllPilots();
		}
		// end UpdatePilotWrong

		public static void UpdatePilot()
		{
			StorePilot();
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try
			{
				// first retrieve the object from the database
				IObjectSet result = db.Get(new Pilot("Michael Schumacher",10));
				Pilot found=(Pilot)result.Next();
				found.AddPoints(11);
				db.Set(found);
				Console.WriteLine("Added 11 points to {0}", found);
			}
			finally 
			{
				db.Close();
			}
			RetrieveAllPilots();
		}
		// end UpdatePilot

		public static void DeletePilot() 
		{
			StorePilot();
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
			try 
			{
				// first retrieve the object from the database
				IObjectSet result=db.Get(new Pilot("Michael Schumacher",10));
				Pilot found=(Pilot)result.Next();
				db.Delete(found);
				System.Console.WriteLine("Deleted "+found);
			} 
			finally 
			{
				db.Close();
			}
			RetrieveAllPilots();
		}
		// end DeletePilot

		
		public static void RetrieveAllPilots()
		{
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try
			{
				IQuery query = db.Query();
				query.Constrain(typeof(Pilot));
				IObjectSet result = query.Execute();
				ListResult(result);
			}
			finally 
			{
				db.Close();
			}
		}
		// end RetrieveAllPilots
    
		public static void RetrievePilotByName(IObjectContainer db)
		{
			IQuery query = db.Query();
			query.Constrain(typeof(Pilot));
			query.Descend("_name").Constrain("Michael Schumacher");
			IObjectSet result = query.Execute();
			ListResult(result);
		}
		// end RetrievePilotByName
        
		public static void RetrievePilotByExactPoints(IObjectContainer db)
		{
			IQuery query = db.Query();
			query.Constrain(typeof(Pilot));
			query.Descend("_points").Constrain(100);
			IObjectSet result = query.Execute();
			ListResult(result);
		}
		// end RetrievePilotByExactPoints
    
		public static void RetrieveByNegation(IObjectContainer db)
		{
			IQuery query = db.Query();
			query.Constrain(typeof(Pilot));
			query.Descend("_name").Constrain("Michael Schumacher").Not();
			IObjectSet result = query.Execute();
			ListResult(result);
		}
		// end RetrieveByNegation
    
		public static void RetrieveByConjunction(IObjectContainer db)
		{
			IQuery query = db.Query();
			query.Constrain(typeof(Pilot));
			IConstraint constr = query.Descend("_name")
				.Constrain("Michael Schumacher");
			query.Descend("_points")
				.Constrain(99).And(constr);
			IObjectSet result = query.Execute();
			ListResult(result);
		}
		// end RetrieveByConjunction
    
		public static void RetrieveByDisjunction(IObjectContainer db)
		{
			IQuery query = db.Query();
			query.Constrain(typeof(Pilot));
			IConstraint constr = query.Descend("_name")
				.Constrain("Michael Schumacher");
			query.Descend("_points")
				.Constrain(99).Or(constr);
			IObjectSet result = query.Execute();
			ListResult(result);
		}
		// end RetrieveByDisjunction
    
		public static void RetrieveByComparison(IObjectContainer db)
		{
			IQuery query = db.Query();
			query.Constrain(typeof(Pilot));
			query.Descend("_points")
				.Constrain(99).Greater();
			IObjectSet result = query.Execute();
			ListResult(result);
		}
		// end RetrieveByComparison
    
		public static void RetrieveByDefaultFieldValue(IObjectContainer db)
		{
			Pilot somebody = new Pilot("Somebody else", 0);
			db.Set(somebody);
			IQuery query = db.Query();
			query.Constrain(typeof(Pilot));
			query.Descend("_points").Constrain(0);
			IObjectSet result = query.Execute();
			ListResult(result);
			db.Delete(somebody);
		}
		// end RetrieveByDefaultFieldValue
        
		public static void RetrieveSorted(IObjectContainer db)
		{
			IQuery query = db.Query();
			query.Constrain(typeof(Pilot));
			query.Descend("_name").OrderAscending();
			IObjectSet result = query.Execute();
			ListResult(result);
			query.Descend("_name").OrderDescending();
			result = query.Execute();
			ListResult(result);
		}
		// end RetrieveSorted
    
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
