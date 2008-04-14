/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Debugging
{
	public class DebugExample
	{
		private const string Db4oFileName = "reference.db4o";

		public static void Main(string[] args) 
		{
			SetCars();
            SetCarsWithFileOutput();
		}
		// end Main

		private static void SetCars()
		{
            // Set the debug message levet to the maximum
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.MessageLevel(3);
            // Do some db4o operations
			File.Delete(Db4oFileName);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
			try 
			{
				Car car1 = new Car("BMW");
				db.Set(car1);
				Car car2 = new Car("Ferrari");
				db.Set(car2);
				db.Deactivate(car1,2);
				IQuery query = db.Query();
				query.Constrain(typeof(Car));
				IObjectSet results = query.Execute();
				ListResult(results);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end SetCars

        private static void SetCarsWithFileOutput()
        {
            // Create StreamWriter for a file
            FileInfo f = new FileInfo("Debug.txt");
            StreamWriter debugWriter = f.CreateText();

            IConfiguration configuration = Db4oFactory.NewConfiguration();
            // Redirect debug output to the specified writer
            configuration.SetOut(debugWriter);

            // Set the debug message levet to the maximum
            configuration.MessageLevel(3);
            // Do some db4o operations
            File.Delete(Db4oFileName);
            IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                Car car1 = new Car("BMW");
                db.Set(car1);
                Car car2 = new Car("Ferrari");
                db.Set(car2);
                db.Deactivate(car1, 2);
                IQuery query = db.Query();
                query.Constrain(typeof(Car));
                IObjectSet results = query.Execute();
                ListResult(results);
            }
            finally
            {
                db.Close();
                debugWriter.Close();
            }
            Db4oFactory.Configure().MessageLevel(0);
        }
        // end SetCarsWithFileOutput

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
