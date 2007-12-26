/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Refactoring
{
	public class RefactoringExample
	{
		private const string Db4oFileName = "reference.db4o";
	
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

		private static void SetObjects()
		{
			File.Delete(Db4oFileName);
			IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				Pilot pilot = new Pilot("Rubens Barrichello");
				container.Set(pilot);
				pilot = new Pilot("Michael Schumacher");
				container.Set(pilot);
			} 
			finally 
			{
				container.Close();
			}
		}
		// end SetObjects

        private static void CheckDB()
		{
			IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IObjectSet result = container.Get(typeof(Pilot));
				foreach (object obj in result)
				{
					Pilot pilot = (Pilot)obj;
					System.Console.WriteLine("Pilot="+ pilot);
				}
			} 
			finally 
			{
				container.Close();
			}
		}
		// end CheckDB

        private static void SetNewObjects()
		{
			IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				PilotNew pilot = new PilotNew("Rubens Barrichello",99);
				container.Set(pilot);
				pilot = new PilotNew("Michael Schumacher",100);
				container.Set(pilot);
			} 
			finally 
			{
				container.Close();
			}
		}
		// end SetNewObjects

        private static void ChangeClass()
		{
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(Pilot)).Rename("Db4objects.Db4odoc.Refactoring.PilotNew, Db4objects.Db4odoc");
            configuration.ObjectClass(typeof(PilotNew)).ObjectField("_name").Rename("_identity");
            IObjectContainer container = Db4oFactory.OpenFile(configuration, Db4oFileName);
            container.Close();
		}
		// end ChangeClass

        private static void RetrievePilotNew()
		{
			IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IQuery q = container.Query();
				q.Constrain(typeof(PilotNew));
				IObjectSet result = q.Execute();
				foreach (object obj in result)
				{
					PilotNew pilot = (PilotNew)obj;
					System.Console.WriteLine("Pilot="+ pilot);
				} 
			}
			finally 
			{
				container.Close();
			}
		}
		// end RetrievePilotNew
	}
}
