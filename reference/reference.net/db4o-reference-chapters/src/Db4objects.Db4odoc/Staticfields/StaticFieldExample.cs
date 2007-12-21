using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Query;
using System.Drawing;

namespace Db4objects.Db4odoc.StaticFields
{
	public class StaticFieldExample
	{
		private const string Db4oFileName = "reference.db4o";

        private static IObjectContainer _container = null;
        private static IConfiguration _configuration = null;

		public StaticFieldExample()
		{
		}

        public static void Main(string[] args) 
		{
            Console.WriteLine("In the default setting, static constants are not continously stored and updated.");
			
			SetPilots();
			CheckPilots();
			//
            Configure();
			SetPilots();
			CheckPilots();
			UpdatePilots();
			UpdatePilotCategories();
			CheckPilots();
            AddDeleteConfiguration();
			DeleteTest();
		}
		// end Main
		
		public static void SetCar()
		{
			IObjectContainer db=Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				Car car = new Car();
				car._color = Color.Green;
				db.Set(car);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end SetCar
		

        private static IObjectContainer Database()
        {
            if (_container == null)
            {
                try
                {
                    if (_configuration == null)
                    {
                        _container = Db4oFactory.OpenFile(Db4oFileName);
                    }
                    else
                    {
                        _container = Db4oFactory.OpenFile(_configuration, Db4oFileName);
                    }
                }
                catch (DatabaseFileLockedException ex)
                {
                    System.Console.WriteLine(ex.Message);
                }
            }
            return _container;
        }

        // end Database

        private static void CloseDatabase()
        {
            if (_container != null)
            {
                _container.Close();
                _container = null;
            }
        }

        // end CloseDatabase

        private static void Configure() {
            System.Console.WriteLine("Saving static fields can be turned on for individual classes.");
            _configuration = Db4oFactory.NewConfiguration();
            _configuration.ObjectClass(typeof(PilotCategories)).PersistStaticFieldValues();
        }
        // end Configure

		private static void SetPilots()
		{
			File.Delete(Db4oFileName);
            IObjectContainer db = Database();
            if (db != null)
            {
                try
                {
                    db.Set(new Pilot("Michael Schumacher", PilotCategories.Winner));
                    db.Set(new Pilot("Rubens Barrichello", PilotCategories.Talented));
                }
                finally
                {
                    CloseDatabase();
                }
            }
		}
		// end SetPilots
	
		
		private static void CheckPilots()
		{
            IObjectContainer db = Database();
            if (db != null)
            {
                try
                {
                    IObjectSet result = db.Get(typeof(Pilot));
                    for (int x = 0; x < result.Count; x++)
                    {
                        Pilot pilot = (Pilot)result[x];
                        if (pilot.Category == PilotCategories.Winner)
                        {
                            Console.WriteLine("Winner pilot: " + pilot);
                        }
                        else if (pilot.Category == PilotCategories.Talented)
                        {
                            Console.WriteLine("Talented pilot: " + pilot);
                        }
                        else
                        {
                            Console.WriteLine("Uncategorized pilot: " + pilot);
                        }
                    }
                }
                finally
                {
                    CloseDatabase();
                }
            }
		}
		// end CheckPilots

        private static void UpdatePilots()
		{
			Console.WriteLine("Updating PilotCategory in pilot reference:");
            IObjectContainer db = Database();
            if (db != null)
            {
                try
                {
                    IObjectSet result = db.Get(typeof(Pilot));
                    for (int x = 0; x < result.Count; x++)
                    {
                        Pilot pilot = (Pilot)result[x];
                        if (pilot.Category == PilotCategories.Winner)
                        {
                            Console.WriteLine("Winner pilot: " + pilot);
                            PilotCategories pc = pilot.Category;
                            pc.TestChange("WINNER2006");
                            db.Set(pilot);
                        }
                    }
                    PrintCategories(db);
                }
                finally
                {
                    CloseDatabase();
                }
            }
		}
		// end UpdatePilots

        private static void UpdatePilotCategories()
		{
			Console.WriteLine("Updating PilotCategories explicitly:");
            IObjectContainer db = Database();
            if (db != null)
            {
                try
                {
                    IObjectSet result = db.Get(typeof(PilotCategories));
                    for (int x = 0; x < result.Count; x++)
                    {
                        PilotCategories pc = (PilotCategories)result[x];
                        if (pc == PilotCategories.Winner)
                        {
                            pc.TestChange("WINNER2006");
                            db.Set(pc);
                        }
                    }
                    PrintCategories(db);
                }
                finally
                {
                    CloseDatabase();
                }
            }
		}
		// end UpdatePilotCategories

        private static void AddDeleteConfiguration() {
            if (_configuration != null) {
                _configuration.ObjectClass(typeof(Pilot)).CascadeOnDelete(true);
            }
        }
        // end AddDeleteConfiguration

		private static void DeleteTest()
		{
            IObjectContainer db = Database();
            if (db != null)
            {
                try
                {
                    Console.WriteLine("Deleting Pilots :");
                    IObjectSet result = db.Get(typeof(Pilot));
                    for (int x = 0; x < result.Count; x++)
                    {
                        Pilot pilot = (Pilot)result[x];
                        db.Delete(pilot);
                    }
                    PrintCategories(db);
                    Console.WriteLine("Deleting PilotCategories :");
                    result = db.Get(typeof(PilotCategories));
                    for (int x = 0; x < result.Count; x++)
                    {
                        db.Delete(result[x]);
                    }
                    PrintCategories(db);
                }
                finally
                {
                    CloseDatabase();
                }
            }
		}
		// end DeleteTest
	
		private static void PrintCategories(IObjectContainer db)
		{
			IObjectSet  result = db.Get(typeof(PilotCategories));
			Console.WriteLine("Stored categories: " + result.Count);
			for(int x = 0; x < result.Count; x++)
			{
				PilotCategories pc = (PilotCategories)result[x];
				Console.WriteLine("Category: "+pc);
			}
		}
		// end PrintCategories
	
		private static void DeletePilotCategories()
		{
            IObjectContainer db = Database();
            if (db != null)
            {
                try
                {
                    PrintCategories(db);
                    IObjectSet result = db.Get(typeof(PilotCategories));
                    for (int x = 0; x < result.Count; x++)
                    {
                        PilotCategories pc = (PilotCategories)result[x];
                        db.Delete(pc);
                    }
                    PrintCategories(db);
                }
                finally
                {
                    CloseDatabase();
                }
            }
		}
		// end DeletePilotCategories
	}
}
