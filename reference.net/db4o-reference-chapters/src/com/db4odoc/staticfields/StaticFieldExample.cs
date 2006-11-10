using System;
using System.IO;
using com.db4odoc.f1;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;
using System.Drawing;

namespace Db4objects.Db4odoc.StaticFields
{
	public class StaticFieldExample
	{
		public readonly static string YapFileName = "formula1.yap";

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
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
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
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
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
			Db4oFactory.Configure().ObjectClass(typeof(PilotCategories)).PersistStaticFieldValues();
			File.Delete(YapFileName);
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
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
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IObjectSet result = db.Get(typeof(Pilot));
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
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IObjectSet result = db.Get(typeof(Pilot));
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
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IObjectSet result = db.Get(typeof(PilotCategories));
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
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
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
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IObjectSet  result = db.Get(typeof(PilotCategories));
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
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IObjectSet  result = db.Get(typeof(PilotCategories));
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
