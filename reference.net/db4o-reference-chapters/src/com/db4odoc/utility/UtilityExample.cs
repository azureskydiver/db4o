/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;


namespace Db4objects.Db4odoc.Utility
{
	public class UtilityExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public static void Main(string[] args) 
		{
			TestDescend();
			CheckActive();
			CheckStored();
		}
		// end Main

		public static void StoreSensorPanel()
		{
			File.Delete(YapFileName);
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
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
	
		public static void TestDescend()
		{
			StoreSensorPanel();
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				db.Ext().Configure().ActivationDepth(1);
				System.Console.WriteLine("Object container activation depth = 1");
				IObjectSet result = db.Get(new SensorPanel(1));
				SensorPanel spParent = (SensorPanel)result[0];
				SensorPanel spDescend = (SensorPanel)db.Ext().Descend((Object)spParent, new String[]{"_next","_next","_next","_next","_next"});
				db.Ext().Activate(spDescend, 5);
				System.Console.WriteLine(spDescend);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end TestDescend
	
		public static void CheckActive()
		{
			StoreSensorPanel();
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				db.Ext().Configure().ActivationDepth(2);
				System.Console.WriteLine("Object container activation depth = 2");
				IObjectSet result = db.Get(new SensorPanel(1));
				SensorPanel sensor = (SensorPanel)result[0]; 
				SensorPanel next = sensor.Next;
				while (next != null)
				{
					System.Console.WriteLine("Object " + next +" is active: " + db.Ext().IsActive(next));
					next = next.Next;
				}
			} 
			finally 
			{
				db.Close();
			}
		}
		// end CheckActive
	
		public static void CheckStored()
		{
			// create a linked list with length 10
			SensorPanel list = new SensorPanel().CreateList(10);
			File.Delete(YapFileName);
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				// store all elements with one statement, since all elements are new		
				db.Set(list);
				Object sensor = (Object)list.Sensor;
				SensorPanel sp5 = list.Next.Next.Next.Next;
				System.Console.WriteLine("Root element "+list+" isStored: " + db.Ext().IsStored(list));
				System.Console.WriteLine("Simple type  "+sensor+" isStored: " + db.Ext().IsStored(sensor));
				System.Console.WriteLine("Descend element  "+sp5+" isStored: " + db.Ext().IsStored(sp5));
				db.Delete(list);
				System.Console.WriteLine("Root element "+list+" isStored: " + db.Ext().IsStored(list));
			} 
			finally 
			{
				db.Close();
			}
		}
		// end CheckStored
	}
}
