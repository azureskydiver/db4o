/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using System;
using System.IO;
using System.Collections;
using com.db4o;
using com.db4o.query;
using com.db4o.types;

namespace com.db4odoc.f1.activating
{

	public class ActivationExample: Util
	{
		public static void main(string[] args)
		{
			TestActivationDefault();
			TestActivationConfig();
			TestCascadeActivate();
			TestMaxActivate();
			TestMinActivate();
			TestActivateDeactivate();
			TestCollectionDef();
			TestCollectionActivation();
		}
	
		public static void StoreSensorPanel()
		{
			File.Delete(Util.YapFileName);
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			try 
			{
				// create a linked list with length 10
				SensorPanel list = new SensorPanel().CreateList(10); 
				// store all elements with one statement, since all elements are new		
				db.Set(list);
			} 
			finally 
			{
				db.Close();
			}
		}
	
		public static void TestActivationConfig()
		{
			StoreSensorPanel();
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			try 
			{
				db.Ext().Configure().ActivationDepth(1);
				Console.WriteLine("Object container activation depth = 1");
				ObjectSet result = db.Get(new SensorPanel(1));
				ListResult(result);
				if (result.Count >0) 
				{
					SensorPanel sensor = (SensorPanel)result[0];
					SensorPanel next = sensor.Next;
					while (next != null)
					{
						Console.WriteLine(next);
						next = next.Next;
					}
				}
			} 
			finally 
			{
				db.Close();
			}
		}

		public static void TestActivationDefault()
		{
			StoreSensorPanel();
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			try 
			{
				Console.WriteLine("Default activation depth");
				ObjectSet  result = db.Get(new SensorPanel(1));
				ListResult(result);
				if (result.Count >0) 
				{
					SensorPanel sensor = (SensorPanel)result[0];
					SensorPanel next = sensor.Next;
					while (next != null)
					{
						Console.WriteLine(next);
						next = next.Next;
					}
				}
			} 
			finally 
			{
				db.Close();
			}
		}
	
		public static void TestCascadeActivate()
		{
			StoreSensorPanel();
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			db.Ext().Configure().ObjectClass(typeof(SensorPanel)).CascadeOnActivate(true);
			try 
			{
				Console.WriteLine("Cascade activation");
				ObjectSet result = db.Get(new SensorPanel(1));
				ListResult(result);
				if (result.Count >0) 
				{
					SensorPanel sensor = (SensorPanel)result[0];
					SensorPanel next = sensor.Next;
					while (next != null)
					{
						Console.WriteLine(next);
						next = next.Next;
					}
				}
			} 
			finally 
			{
				db.Close();
			}
		}
	
		public static void TestMinActivate()
		{
			StoreSensorPanel();
			// note that the minimum applies for *all* instances in the hierarchy
			// the system ensures that every instantiated List object will have it's 
			// members set to a depth of 1
			Db4o.Configure().ObjectClass(typeof(SensorPanel)).MinimumActivationDepth(1);
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			try 
			{
				Console.WriteLine("Minimum activation depth = 1");
				ObjectSet result = db.Get(new SensorPanel(1));
				ListResult(result);
				if (result.Count >0) 
				{
					SensorPanel sensor = (SensorPanel)result[0];
					SensorPanel next = sensor.Next;
					while (next != null)
					{
						Console.WriteLine(next);
						next = next.Next;
					}
				}
			} 
			finally 
			{
				db.Close();
				Db4o.Configure().ObjectClass(typeof(SensorPanel)).MinimumActivationDepth(0);
			}
		}
		
		public static void TestMaxActivate() 
		{
			StoreSensorPanel();
			// note that the maximum is applied to the retrieved root object and limits activation
			// further down the hierarchy
			Db4o.Configure().ObjectClass(typeof(SensorPanel)).MaximumActivationDepth(2);

			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			try 
			{
				Console.WriteLine("Maximum activation depth = 2 (default = 5)");
				ObjectSet result = db.Get(new SensorPanel(1));
				ListResult(result);
				if (result.Count > 0) 
				{
					SensorPanel sensor = (SensorPanel) result[0];
					SensorPanel next = sensor.Next;
					while (next != null) 
					{
						Console.WriteLine(next);
						next = next.Next;
					}
				}
			} 
			finally 
			{
				db.Close();
				Db4o.Configure().ObjectClass(typeof(SensorPanel)).MaximumActivationDepth(Int32.MaxValue);
			}
		
		}
	
		public static void TestActivateDeactivate()
		{
			StoreSensorPanel();
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			db.Ext().Configure().ActivationDepth(0);
			try 
			{
				Console.WriteLine("Object container activation depth = 0" );
				ObjectSet result = db.Get(new SensorPanel(1));
				Console.WriteLine("Sensor1:");
				ListResult(result);
				SensorPanel sensor1 = (SensorPanel)result[0];
				TestActivated(sensor1);
			
				Console.WriteLine("Sensor1 activated:");
				db.Activate(sensor1,4);
				TestActivated(sensor1);
			
				Console.WriteLine("Sensor5 activated:");
				result = db.Get(new SensorPanel(5));
				SensorPanel sensor5 = (SensorPanel)result[0];
				db.Activate(sensor5,4);
				ListResult(result);
				TestActivated(sensor5);
			
				Console.WriteLine("Sensor1 deactivated:");
				db.Deactivate(sensor1,5);
				TestActivated(sensor1);
			
				//			 	DANGER !!!.
				// If you use Deactivate with a higher value than 1
				// make sure that you know whereto members might branch
				// Deactivating list1 also deactivated list5
				Console.WriteLine("Sensor 5 AFTER DEACTIVATE OF Sensor1.");
				TestActivated(sensor5);
			} 
			finally 
			{
				db.Close();
			}
		}
	
		public static void TestActivated(SensorPanel sensor)
		{
			SensorPanel next = sensor;
			do 
			{
				next = next.Next;
				Console.WriteLine(next);
			} while (next != null);
		}
	
		public static void StoreCollection()
		{
			File.Delete(Util.YapFileName);    
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			try 
			{
				IList list = db.Ext().Collections().NewLinkedList(); 
				for (int i =0; i < 10; i++)
				{
					SensorPanel sensor = new SensorPanel(i);
					list.Add(sensor);
				}		
				db.Set(list);
			} 
			finally 
			{
				db.Close();
			}
		}
	
		public static void TestCollectionDef()
		{
			StoreCollection();
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			db.Ext().Configure().ActivationDepth(5);
			try 
			{
				ObjectSet result = db.Get(typeof(IList));
				ListResult(result);
				Db4oList list = (Db4oList)result[0];
				for (int i = 0; i < list.Count; i++)
				{
					Console.WriteLine("List element: " + list[i]);
				}
			} 
			finally 
			{
				db.Close();
			} 
		}
	

		public static void TestCollectionActivation()
		{
			StoreCollection();
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			db.Ext().Configure().ActivationDepth(0);
			try 
			{
				ObjectSet result = db.Get(typeof(IList));
				ListResult(result);
				
				Db4oList  list = (Db4oList)result[0];
				Console.WriteLine("Setting list activation depth to 0 ");
				list.ActivationDepth(0);
				for (int i = 0; i < list.Count; i++)
				{
					Console.WriteLine("List element: " + list[i]);
				}
			} 
			finally 
			{
				db.Close();
			} 
		}
		
	}
}
