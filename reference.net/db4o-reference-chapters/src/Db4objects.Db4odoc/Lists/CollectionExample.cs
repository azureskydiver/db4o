/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using System.Collections;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Lists
{
	public class CollectionExample
	{
		private const string Db4oFileName = "reference.db4o";

		public static void Main(string[] args)
		{
			SetTeam();
			UpdateTeam();
		}
		// end Main

		private static void SetTeam()
		{
			File.Delete(Db4oFileName);
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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

        private static void UpdateTeam()
		{
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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
