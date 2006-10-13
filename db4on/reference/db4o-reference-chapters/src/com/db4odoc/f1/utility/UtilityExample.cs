/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using com.db4o;


namespace com.db4odoc.f1.utility
{
	public class UtilityExample
	{
		public static void main(String[] args) 
		{
			TestDescend();
			CheckActive();
			CheckStored();
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
	
		public static void TestDescend()
		{
			StoreSensorPanel();
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			try 
			{
				db.Ext().Configure().ActivationDepth(1);
				System.Console.WriteLine("Object container activation depth = 1");
				ObjectSet result = db.Get(new SensorPanel(1));
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
	
		public static void CheckActive()
		{
			StoreSensorPanel();
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			try 
			{
				db.Ext().Configure().ActivationDepth(2);
				System.Console.WriteLine("Object container activation depth = 2");
				ObjectSet result = db.Get(new SensorPanel(1));
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
	
		public static void CheckStored()
		{
			// create a linked list with length 10
			SensorPanel list = new SensorPanel().CreateList(10);
			File.Delete(Util.YapFileName);
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
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
	}
}
