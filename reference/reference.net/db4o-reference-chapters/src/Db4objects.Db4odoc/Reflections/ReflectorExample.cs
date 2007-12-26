/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */
using System;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Reflect;
using Db4objects.Db4o.Reflect.Net;
using Db4objects.Db4o.Reflect.Generic;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Config;

namespace Db4objects.Db4odoc.Reflections
{
	public class ReflectorExample
	{
		private const string Db4oFileName = "reference.db4o";

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

		private static void SetCars()
		{
			File.Delete(Db4oFileName);     
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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

        private static void GetCars()
		{
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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

        private static void GetCarInfo()
		{
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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
				foreach (IReflectField field in fields)
					Console.WriteLine(field.GetName());
				
				// constructors
				Console.WriteLine("CONSTRUCTORS:");
				IReflectConstructor[] cons = carClass.GetDeclaredConstructors();
				foreach (IReflectConstructor constructor in cons)
                    Console.WriteLine(constructor);
				
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

        private static void GetReflectorInfo()
		{
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				Console.WriteLine("Reflector in use: " + db.Ext().Reflector());
				Console.WriteLine("Reflector delegate" +db.Ext().Reflector().GetDelegate());
				IReflectClass[] knownClasses = db.Ext().Reflector().KnownClasses();
				int count = knownClasses.Length;
				Console.WriteLine("Known classes: " + count);
				foreach (IReflectClass knownClass in knownClasses)
				{
					Console.WriteLine(knownClass);
				}
			} 
			finally 
			{
				db.Close();
			}
		}
		// end GetReflectorInfo

        private static void TestReflector()
		{
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            LoggingReflector logger = new LoggingReflector(); 
            configuration.ReflectWith(logger);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
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
