/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using com.db4o;
using com.db4o.query;

namespace com.db4odoc.f1.refactoring
{
	public class RefactoringExample: Util
	{
	
		public static void main(string[] args) 
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

		public static void SetObjects()
		{
			File.Delete(Util.YapFileName);
			ObjectContainer oc = Db4o.OpenFile(Util.YapFileName);
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
		public static void CheckDB()
		{
			ObjectContainer oc = Db4o.OpenFile(Util.YapFileName);
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

		public static void SetNewObjects()
		{
			ObjectContainer oc = Db4o.OpenFile(Util.YapFileName);
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
	

		public static void ChangeClass()
		{
			Db4o.Configure().ObjectClass(typeof(Pilot)).Rename("com.db4odoc.f1.refactoring.PilotNew, db4o-reference-chapters");
			Db4o.Configure().ObjectClass(typeof(PilotNew)).ObjectField("_name").Rename("_identity");
			ObjectContainer oc = Db4o.OpenFile(Util.YapFileName);
			oc.Close();
		}
	
		public static void RetrievePilotNew()
		{
			ObjectContainer oc = Db4o.OpenFile(Util.YapFileName);
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
	}
}
