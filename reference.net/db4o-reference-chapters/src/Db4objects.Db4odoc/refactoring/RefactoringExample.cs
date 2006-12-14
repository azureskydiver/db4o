/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Refactoring
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
			IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
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
			IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IObjectSet result = oc.Get(typeof(Pilot));
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
			IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
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
			Db4oFactory.Configure().ObjectClass(typeof(Pilot)).Rename("Db4objects.Db4odoc.Refactoring.PilotNew, db4o-reference-chapters");
			Db4oFactory.Configure().ObjectClass(typeof(PilotNew)).ObjectField("_name").Rename("_identity");
			IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
			oc.Close();
		}
		// end ChangeClass
	
		public static void RetrievePilotNew()
		{
			IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IQuery q = oc.Query();
				q.Constrain(typeof(PilotNew));
				IObjectSet result = q.Execute();
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
