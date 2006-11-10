/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4odoc.f1;
using Db4objects.Db4o.Reflect;
using Db4objects.Db4o.Reflect.Net;
using Db4objects.Db4o.Reflect.Generic;
using Db4objects.Db4o.Query;
using Db4objects.Db4o;
using System.IO;

namespace Db4objects.Db4odoc.Reflections
{
	public class ReflectorExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public ReflectorExample()
		{
		}

        public static void Main(string[] args)
		{
			SetCars();
			GetReflectorInfo();
			GetCars();
			GetCarInfo();
		}
		// end Main

		public static void SetCars()
		{
			File.Delete(YapFileName);     
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				Car car1 = new Car("BMW");
				db.Set(car1);
				Car car2 = new Car("Ferrari");
				db.Set(car2);
				
				Console.WriteLine("Saved:");
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
		
		public  static void GetCars()
		{
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IQuery query = db.Query();
				query.Constrain(typeof(Car));
				IObjectSet result = query.Execute();
				ListResult(result);
				Car car = (Car)result[0];
				GenericReflector reflector = new GenericReflector(null,db.Ext().Reflector());
				IReflectClass carClass = reflector.ForObject(car);
				Console.WriteLine("Reflected class "+carClass);
				Console.WriteLine("Retrieved with reflector:");
			} 
			finally 
			{
				db.Close();
			}
		}
		// end GetCars
		
		public  static void GetCarInfo()
		{
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IObjectSet result = db.Get(new Car("BMW"));
				if (result.Size() < 1) 
				{
					return;
				}
				Car car = (Car)result[0];
				GenericReflector reflector = new GenericReflector(null,db.Ext().Reflector());
				IReflectClass carClass = reflector.ForObject(car);
				Console.WriteLine("Reflected class "+carClass);
				// public fields
				Console.WriteLine("FIELDS:");
				IReflectField[] fields = carClass.GetDeclaredFields();
				for (int i = 0; i < fields.Length; i++)
					Console.WriteLine(fields[i].GetName());
				
				// constructors
				Console.WriteLine("CONSTRUCTORS:");
				IReflectConstructor[] cons = carClass.GetDeclaredConstructors();
				for (int i = 0; i < cons.Length; i++)
					Console.WriteLine( cons[i]);
				
				// public methods
				Console.WriteLine("METHODS:");
                IReflectMethod method = carClass.GetMethod("ToString", new IReflectClass[] { });
                if (method != null)
                {
                    Console.WriteLine(method.GetType());
                }

			} 
			finally 
			{
				db.Close();
			}
		}
		// end GetCarInfo

		public static void GetReflectorInfo()
		{
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				Console.WriteLine("Reflector in use: " + db.Ext().Reflector());
				Console.WriteLine("Reflector delegate" +db.Ext().Reflector().GetDelegate());
				IReflectClass[] knownClasses = db.Ext().Reflector().KnownClasses();
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
			Db4oFactory.Configure().ReflectWith(logger);
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				Car car = new Car("BMW");
				IReflectClass rc  = db.Ext().Reflector().ForObject(car);
				Console.WriteLine("Reflected class: " + rc);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end TestReflector

		public static void ListResult(IObjectSet result)
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
