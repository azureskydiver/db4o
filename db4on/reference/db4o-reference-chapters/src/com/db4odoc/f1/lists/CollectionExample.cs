/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using System.Collections;
using  com.db4o;
using  com.db4o.query;
using com.db4odoc.f1.evaluations;

namespace com.db4odoc.f1.lists
{
	public class CollectionExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public static void Main(string[] args)
		{
			SetTeam();
			UpdateTeam();
		}
		// end Main

		public static void SetTeam()
		{
			File.Delete(YapFileName);
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				Team ferrariTeam = new Team();
				ferrariTeam.Name = "Ferrari";
			   
				Pilot pilot1 = new Pilot("Michael Schumacher", 100);
				ferrariTeam.AddPilot(pilot1);
				Pilot pilot2 = new Pilot("David Schumacher", 98);
				ferrariTeam.AddPilot(pilot2);
				
				db.Set(ferrariTeam);
				IList protoList = CollectionFactory.newList();
				ObjectSet result = db.Get(protoList);
				ListResult(result);
			}  
			finally 
			{
				db.Close();
			} 
		}
		// end SetTeam

		public static void UpdateTeam()
		{
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				Query query =db.Query(); 
				query.Constrain(typeof(Team));
				query.Descend("_name").Constrain("Ferrari");
				ObjectSet result = query.Execute();
				if (result.HasNext()) 
				{
					Team ferrariTeam = (Team)result.Next();

					Pilot pilot = new Pilot("David Schumacher", 100);
					ferrariTeam.UpdatePilot(1,pilot);

					db.Set(ferrariTeam);
				}
				IList protoList = CollectionFactory.newList();
				result = db.Get(protoList);
				ListResult(result);
			}  
			finally 
			{
				db.Close();
			} 
		}
		// end UpdateTeam

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
