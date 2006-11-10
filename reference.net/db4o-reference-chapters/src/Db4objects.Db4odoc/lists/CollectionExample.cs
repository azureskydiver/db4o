/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using System.Collections;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Lists
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
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
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
				IObjectSet result = db.Get(protoList);
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
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IQuery query =db.Query(); 
				query.Constrain(typeof(Team));
				query.Descend("_name").Constrain("Ferrari");
				IObjectSet result = query.Execute();
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

		public static void ListResult(IObjectSet result)
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
