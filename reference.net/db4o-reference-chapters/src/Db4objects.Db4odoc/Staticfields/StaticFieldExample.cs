using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;
using System.Drawing;

namespace Db4objects.Db4odoc.StaticFields
{
	public class StaticFieldExample
	{
		private const string Db4oFileName = "reference.db4o";

		public StaticFieldExample()
		{
		}
		public static void Main(string[] args) 
		{
			SetPilotsSimple();
			CheckPilots();
			//
			SetPilotsStatic();
			CheckPilots();
			UpdatePilots();
			UpdatePilotCategories();
			CheckPilots();
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

		public static void SetPilotsSimple()
		{
			Console.WriteLine("In the default setting, static constants are not continously stored and updated.");
			File.Delete(Db4oFileName);
			IObjectContainer db=Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				db.Set(new Pilot("Michael Schumacher",PilotCategories.Winner));
				db.Set(new Pilot("Rubens Barrichello",PilotCategories.Talented));
			} 
			finally 
			{
				db.Close();
			}
		}
		// end SetPilotsSimple
	
		public static void SetPilotsStatic()
		{
			Console.WriteLine("The feature can be turned on for individual classes.");
			Db4oFactory.Configure().ObjectClass(typeof(PilotCategories)).PersistStaticFieldValues();
			File.Delete(Db4oFileName);
			IObjectContainer db=Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				db.Set(new Pilot("Michael Schumacher",PilotCategories.Winner));
				db.Set(new Pilot("Rubens Barrichello",PilotCategories.Talented));
			} 
			finally 
			{
				db.Close();
			}
		}
		// end SetPilotsStatic
	
		public static void CheckPilots()
		{
			IObjectContainer db=Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IObjectSet result = db.Get(typeof(Pilot));
				for(int x = 0; x < result.Count; x++)
				{
					Pilot pilot = (Pilot )result[x];
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
				db.Close();
			}
		}
		// end CheckPilots
	
		public static void UpdatePilots()
		{
			Console.WriteLine("Updating PilotCategory in pilot reference:");
			IObjectContainer db=Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IObjectSet result = db.Get(typeof(Pilot));
				for(int x = 0; x < result.Count; x++)
				{
					Pilot pilot = (Pilot )result[x];
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
				db.Close();
			}
		}
		// end UpdatePilots
	
		public static void UpdatePilotCategories()
		{
			Console.WriteLine("Updating PilotCategories explicitly:");
			IObjectContainer db=Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IObjectSet result = db.Get(typeof(PilotCategories));
				for(int x = 0; x < result.Count; x++)
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
				db.Close();
			}
		}
		// end UpdatePilotCategories
	
		public static void DeleteTest()
		{
			IObjectContainer db=Db4oFactory.OpenFile(Db4oFileName);
			db.Ext().Configure().ObjectClass(typeof(Pilot)).CascadeOnDelete(true);
			try 
			{
				Console.WriteLine("Deleting Pilots :");
				IObjectSet result = db.Get(typeof(Pilot));
				for(int x = 0; x < result.Count; x++)
				{
					Pilot pilot = (Pilot )result[x];
					db.Delete(pilot);
				}
				PrintCategories(db);
				Console.WriteLine("Deleting PilotCategories :");
				result = db.Get(typeof(PilotCategories));
				for(int x = 0; x < result.Count; x++)
				{
					db.Delete(result[x]);
				}
				PrintCategories(db);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end DeleteTest
	
		public static void PrintCategories(IObjectContainer db)
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
	
		public static void DeletePilotCategories()
		{
			IObjectContainer db=Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
                PrintCategories(db);
				IObjectSet  result = db.Get(typeof(PilotCategories));
				for(int x = 0; x < result.Count; x++)
				{
					PilotCategories pc = (PilotCategories)result[x];
					db.Delete(pc);
				}
                PrintCategories(db);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end DeletePilotCategories
	}
}
