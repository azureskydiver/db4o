using System;
using System.IO;
using com.db4odoc.f1;
using com.db4o;
using com.db4o.query;
using System.Drawing;

namespace com.db4odoc.f1.staticfields
{
	public class StaticFieldExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public StaticFieldExample()
		{
		}
		public static void Main(String[] args) 
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
			ObjectContainer db=Db4o.OpenFile(YapFileName);
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
			File.Delete(YapFileName);
			ObjectContainer db=Db4o.OpenFile(YapFileName);
			try 
			{
				db.Set(new Pilot("Michael Schumacher",PilotCategories.WINNER));
				db.Set(new Pilot("Rubens Barrichello",PilotCategories.TALENTED));
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
			Db4o.Configure().ObjectClass(typeof(PilotCategories)).PersistStaticFieldValues();
			File.Delete(YapFileName);
			ObjectContainer db=Db4o.OpenFile(YapFileName);
			try 
			{
				db.Set(new Pilot("Michael Schumacher",PilotCategories.WINNER));
				db.Set(new Pilot("Rubens Barrichello",PilotCategories.TALENTED));
			} 
			finally 
			{
				db.Close();
			}
		}
		// end SetPilotsStatic
	
		public static void CheckPilots()
		{
			ObjectContainer db=Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet result = db.Get(typeof(Pilot));
				for(int x = 0; x < result.Count; x++)
				{
					Pilot pilot = (Pilot )result[x];
					if (pilot.Category == PilotCategories.WINNER)
					{
						Console.WriteLine("Winner pilot: " + pilot);
					} 
					else if (pilot.Category == PilotCategories.TALENTED)
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
			ObjectContainer db=Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet result = db.Get(typeof(Pilot));
				for(int x = 0; x < result.Count; x++)
				{
					Pilot pilot = (Pilot )result[x];
					if (pilot.Category == PilotCategories.WINNER)
					{
						Console.WriteLine("Winner pilot: " + pilot);
						PilotCategories pc = pilot.Category;
						pc.TestChange("WINNER2006");
						db.Set(pilot);
					}
				}
			} 
			finally 
			{
				db.Close();
			}
			PrintCategories();
		}
		// end UpdatePilots
	
		public static void UpdatePilotCategories()
		{
			Console.WriteLine("Updating PilotCategories explicitly:");
			ObjectContainer db=Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet result = db.Get(typeof(PilotCategories));
				for(int x = 0; x < result.Count; x++)
				{
					PilotCategories pc = (PilotCategories)result[x];
					if (pc == PilotCategories.WINNER)
					{
						pc.TestChange("WINNER2006");
						db.Set(pc);
					}
				}
			} 
			finally 
			{
				db.Close();
			}
			PrintCategories();
		}
		// end UpdatePilotCategories
	
		public static void DeleteTest()
		{
			ObjectContainer db=Db4o.OpenFile(YapFileName);
			db.Ext().Configure().ObjectClass(typeof(Pilot)).CascadeOnDelete(true);
			try 
			{
				Console.WriteLine("Deleting Pilots :");
				ObjectSet result = db.Get(typeof(Pilot));
				for(int x = 0; x < result.Count; x++)
				{
					Pilot pilot = (Pilot )result[x];
					db.Delete(pilot);
				}
				PrintCategories();
				Console.WriteLine("Deleting PilotCategories :");
				result = db.Get(typeof(PilotCategories));
				for(int x = 0; x < result.Count; x++)
				{
					db.Delete(result[x]);
				}
				PrintCategories();
			} 
			finally 
			{
				db.Close();
			}
		}
		// end DeleteTest
	
		public static void PrintCategories()
		{
			ObjectContainer db=Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet  result = db.Get(typeof(PilotCategories));
				Console.WriteLine("Stored categories: " + result.Count);
				for(int x = 0; x < result.Count; x++)
				{
					PilotCategories pc = (PilotCategories)result[x];
					Console.WriteLine("Category: "+pc);
				}
			} 
			finally 
			{
				db.Close();
			}
		}
		// end PrintCategories
	
		public static void DeletePilotCategories()
		{
			PrintCategories();
			ObjectContainer db=Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet  result = db.Get(typeof(PilotCategories));
				for(int x = 0; x < result.Count; x++)
				{
					PilotCategories pc = (PilotCategories)result[x];
					db.Delete(pc);
				}
			} 
			finally 
			{
				db.Close();
			}
			PrintCategories();
		}
		// end DeletePilotCategories
	}
}
