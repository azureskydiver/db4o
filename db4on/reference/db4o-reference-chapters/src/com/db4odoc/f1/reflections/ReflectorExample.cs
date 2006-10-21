/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4odoc.f1;
using com.db4o.reflect;
using com.db4o.reflect.net;
using com.db4o.reflect.generic;
using com.db4o.query;
using com.db4o;
using System.IO;

namespace com.db4odoc.f1.reflections
{
	public class ReflectorExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public ReflectorExample()
		{
		}

		public void Main()
		{
			SetCars();
			GetReflectorInfo();
			GetCars();
			GetCarInfo();
		}
		// end Main

		public void SetCars()
		{
			File.Delete(YapFileName);     
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				Car car1 = new Car("BMW");
				db.Set(car1);
				Car car2 = new Car("Ferrari");
				db.Set(car2);
				
				Console.WriteLine("Saved:");
				Query query = db.Query();
				query.Constrain(typeof(Car));
				ObjectSet results = query.Execute();
				ListResult(results);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end SetCars
		
		public  void GetCars()
		{
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				Query query = db.Query();
				query.Constrain(typeof(Car));
				ObjectSet result = query.Execute();
				ListResult(result);
				Car car = (Car)result[0];
				GenericReflector reflector = new GenericReflector(null,db.Ext().Reflector());
				ReflectClass carClass = reflector.ForObject(car);
				Console.WriteLine("Reflected class "+carClass);
				Console.WriteLine("Retrieved with reflector:");
			} 
			finally 
			{
				db.Close();
			}
		}
		// end GetCars
		
		public  void GetCarInfo()
		{
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet result = db.Get(new Car("BMW"));
				if (result.Size() < 1) 
				{
					return;
				}
				Car car = (Car)result[0];
				GenericReflector reflector = new GenericReflector(null,db.Ext().Reflector());
				ReflectClass carClass = reflector.ForObject(car);
				Console.WriteLine("Reflected class "+carClass);
				// public fields
				Console.WriteLine("FIELDS:");
				ReflectField[] fields = carClass.GetDeclaredFields();
				for (int i = 0; i < fields.Length; i++)
					Console.WriteLine(fields[i].GetName());
				
				// constructors
				Console.WriteLine("CONSTRUCTORS:");
				ReflectConstructor[] cons = carClass.GetDeclaredConstructors();
				for (int i = 0; i < cons.Length; i++)
					Console.WriteLine( cons[i]);
				
				// public methods
				Console.WriteLine("METHODS:");
				ReflectMethod method = carClass.GetMethod("ToString",null);
				Console.WriteLine(method.GetType());

			} 
			finally 
			{
				db.Close();
			}
		}
		// end GetCarInfo

		public  void GetReflectorInfo()
		{
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				Console.WriteLine("Reflector in use: " + db.Ext().Reflector());
				Console.WriteLine("Reflector delegate" +db.Ext().Reflector().GetDelegate());
				ReflectClass[] knownClasses = db.Ext().Reflector().KnownClasses();
				int count = knownClasses.Length;
				Console.WriteLine("Known classes: " + count);
				for (int i=0; i <knownClasses.Length; i++)
				{
					Console.WriteLine(knownClasses[i]);
				}
			} 
			finally 
			{
				db.Close();
			}
		}
		// end GetReflectorInfo
		
		public  void TestReflector()
		{
			LoggingReflector logger = new LoggingReflector();
			Db4o.Configure().ReflectWith(logger);
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				Car car = new Car("BMW");
				ReflectClass rc  = db.Ext().Reflector().ForObject(car);
				Console.WriteLine("Reflected class: " + rc);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end TestReflector

		public static void ListResult(ObjectSet result)
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
