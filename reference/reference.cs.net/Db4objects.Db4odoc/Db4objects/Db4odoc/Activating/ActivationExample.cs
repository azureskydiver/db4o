/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using System.Collections;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Types;

namespace Db4objects.Db4odoc.Activating
{

	public class ActivationExample
	{
		private const string Db4oFileName = "reference.db4o";

		public static void Main(string[] args)
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
		// end Main
	
		private static void StoreSensorPanel()
		{
			File.Delete(Db4oFileName);
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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
		// end StoreSensorPanel
	
		private static void TestActivationConfig()
		{
			StoreSensorPanel();
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ActivationDepth(1);
			IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
			try 
			{
				Console.WriteLine("Object container activation depth = 1");
				IObjectSet result = db.Get(new SensorPanel(1));
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
		// end TestActivationConfig

        private static void TestActivationDefault()
		{
			StoreSensorPanel();
            IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				Console.WriteLine("Default activation depth");
				IObjectSet  result = db.Get(new SensorPanel(1));
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
		// end TestActivationDefault

        private static void TestCascadeActivate()
		{
			StoreSensorPanel();
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(SensorPanel)).CascadeOnActivate(true);
			IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
			try 
			{
				Console.WriteLine("Cascade activation");
				IObjectSet result = db.Get(new SensorPanel(1));
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
		// end TestCascadeActivate

        private static void TestMinActivate()
		{
			StoreSensorPanel();
			// note that the minimum applies for *all* instances in the hierarchy
			// the system ensures that every instantiated List object will have it's 
			// members set to a depth of 1
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(SensorPanel)).MinimumActivationDepth(1);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
			try 
			{
				Console.WriteLine("Minimum activation depth = 1");
				IObjectSet result = db.Get(new SensorPanel(1));
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
		// end TestMinActivate

        private static void TestMaxActivate() 
		{
			StoreSensorPanel();
			// note that the maximum is applied to the retrieved root object and limits activation
			// further down the hierarchy
			IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(SensorPanel)).MaximumActivationDepth(2);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
            try 
			{
				Console.WriteLine("Maximum activation depth = 2 (default = 5)");
				IObjectSet result = db.Get(new SensorPanel(1));
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
			}
		
		}
		// end TestMaxActivate

        private static void TestActivateDeactivate()
		{
			StoreSensorPanel();
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ActivationDepth(0);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
            try 
			{
				Console.WriteLine("Object container activation depth = 0" );
				IObjectSet result = db.Get(new SensorPanel(1));
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
		// end TestActivateDeactivate

        private static void TestActivated(SensorPanel sensor)
		{
			SensorPanel next = sensor;
			do 
			{
				next = next.Next;
				Console.WriteLine(next);
			} while (next != null);
		}
		// end TestActivated

        // This is not used anymore as IDb4oList is deprecated
        private static void StoreCollection()
		{
			File.Delete(Db4oFileName);    
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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
		// end StoreCollection

        // This is not used anymore as IDb4oList is deprecated
        private static void TestCollectionDef()
		{
			StoreCollection();
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ActivationDepth(5);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
            try 
			{
                IObjectSet result = db.Get(typeof(IList));
				ListResult(result);
				IDb4oList list = (IDb4oList)result[0];
				foreach (object element in list)
				{
					Console.WriteLine("List element: " + element);
				}
			} 
			finally 
			{
				db.Close();
			} 
		}
		// end TestCollectionDef


        // This is not used anymore as IDb4oList is deprecated
        private static void TestCollectionActivation()
		{
			StoreCollection();
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ActivationDepth(0);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
			try 
			{
				IObjectSet result = db.Get(typeof(IList));
				ListResult(result);
				
				IDb4oList  list = (IDb4oList)result[0];
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
		// end TestCollectionActivation

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
