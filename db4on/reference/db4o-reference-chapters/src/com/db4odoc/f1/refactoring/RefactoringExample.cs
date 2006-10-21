/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using com.db4o;
using com.db4o.query;

namespace com.db4odoc.f1.refactoring
{
	public class RefactoringExample
	{
		public readonly static string YapFileName = "formula1.yap";
	
		public static void Main(string[] args) 
		{
			System.Console.WriteLine("Correct sequence of actions: ");
			SetObjects();
			CheckDB();	
			ChangeClass();
			SetNewObjects();
			RetrievePilotNew();

			/*System.Console.WriteLine("Incorrect sequence of actions: ");
			SetObjects();
			CheckDB();	
			SetNewObjects();
			ChangeClass();
			RetrievePilotNew();*/
		}
		// end Main

		public static void SetObjects()
		{
			File.Delete(YapFileName);
			ObjectContainer oc = Db4o.OpenFile(YapFileName);
			try 
			{
				Pilot pilot = new Pilot("Rubens Barrichello");
				oc.Set(pilot);
				pilot = new Pilot("Michael Schumacher");
				oc.Set(pilot);
			} 
			finally 
			{
				oc.Close();
			}
		}
		// end SetObjects

		public static void CheckDB()
		{
			ObjectContainer oc = Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet result = oc.Get(typeof(Pilot));
				for (int i=0; i< result.Size();i++)
				{
					Pilot pilot = (Pilot)result[i];
					System.Console.WriteLine("Pilot="+ pilot);
				}
			} 
			finally 
			{
				oc.Close();
			}
		}
		// end CheckDB

		public static void SetNewObjects()
		{
			ObjectContainer oc = Db4o.OpenFile(YapFileName);
			try 
			{
				PilotNew pilot = new PilotNew("Rubens Barrichello",99);
				oc.Set(pilot);
				pilot = new PilotNew("Michael Schumacher",100);
				oc.Set(pilot);
			} 
			finally 
			{
				oc.Close();
			}
		}
		// end SetNewObjects

		public static void ChangeClass()
		{
			Db4o.Configure().ObjectClass(typeof(Pilot)).Rename("com.db4odoc.f1.refactoring.PilotNew, db4o-reference-chapters");
			Db4o.Configure().ObjectClass(typeof(PilotNew)).ObjectField("_name").Rename("_identity");
			ObjectContainer oc = Db4o.OpenFile(YapFileName);
			oc.Close();
		}
		// end ChangeClass
	
		public static void RetrievePilotNew()
		{
			ObjectContainer oc = Db4o.OpenFile(YapFileName);
			try 
			{
				Query q = oc.Query();
				q.Constrain(typeof(PilotNew));
				ObjectSet result = q.Execute();
				for (int i=0; i< result.Size();i++)
				{
					PilotNew pilot = (PilotNew)result[i];
					System.Console.WriteLine("Pilot="+ pilot);
				} 
			}
			finally 
			{
				oc.Close();
			}
		}
		// end RetrievePilotNew
	}
}
