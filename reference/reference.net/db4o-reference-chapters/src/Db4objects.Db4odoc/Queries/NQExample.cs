/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.Collections;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Queries
{
	public class NQExample
	{
		public readonly static string YapFileName = "formula1.yap";
		public static void Main(string[] args)
		{
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try
			{
				StorePilots(db);
				RetrieveComplexSODA(db);
				RetrieveComplexNQ(db);
				RetrieveArbitraryCodeNQ(db);
				ClearDatabase(db);
			}
			finally
			{
				db.Close();
			}
		}
		// end Main
    
		public static void PrimitiveQuery(IObjectContainer db)
		{
			IList pilots = db.Query(new PilotHundredPoints()); 
		}
		// end PrimitiveQuery

		public static void StorePilots(IObjectContainer db)
		{
			db.Set(new Pilot("Michael Schumacher", 100));
			db.Set(new Pilot("Rubens Barrichello", 99));
		}
		// end StorePilots
    
		public static void RetrieveComplexSODA(IObjectContainer db)
		{
			IQuery query=db.Query();
			query.Constrain(typeof(Pilot));
			IQuery pointQuery=query.Descend("_points");
			query.Descend("_name").Constrain("Rubens Barrichello")
				.Or(pointQuery.Constrain(99).Greater()
				.And(pointQuery.Constrain(199).Smaller()));
			IObjectSet result=query.Execute();
			ListResult(result);
		}
		// end RetrieveComplexSODA

		public static void RetrieveComplexNQ(IObjectContainer db)
		{
			IObjectSet result = db.Query(new ComplexQuery());
			ListResult(result);
		}
		// end RetrieveComplexNQ

		public static void RetrieveArbitraryCodeNQ(IObjectContainer db)
		{
			IObjectSet result = db.Query(new ArbitraryQuery(new int[]{1,100}));
			ListResult(result);
		}
		// end RetrieveArbitraryCodeNQ
    
		public static void ClearDatabase(IObjectContainer db)
		{
			IObjectSet result = db.Get(typeof(Pilot));
			while (result.HasNext())
			{
				db.Delete(result.Next());
			}
		}
		// end ClearDatabase 

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
