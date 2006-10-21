/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using System.Threading;
using com.db4o;

namespace com.db4odoc.f1.persist
{
	public class PeekPersistedExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public static void Main(String[] args) 
		{
			MeasureCarTemperature();
		}
		// end Main
		
		public static void SetObjects()
		{
			File.Delete(YapFileName);
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				Car car = new Car("BMW");
				db.Set(car);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end SetObjects

		public static void MeasureCarTemperature()
		{
			SetObjects();
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet result = db.Get(typeof(Car));
				if (result.Size() > 0)
				{
					Car car = (Car)result[0];
					Car car1  = (Car)db.Ext().PeekPersisted(car, 5, true);
					Change1 ch1 = new Change1();
					ch1.Init(car1);
					Car car2  = (Car)db.Ext().PeekPersisted(car, 5, true);
					Change2 ch2 = new Change2();
					ch2.Init(car2);
					Thread.Sleep(300);
					// We can work on the database object at the same time
					car.Model = "BMW M3Coupe";
					db.Set(car);
					ch1.Stop();
					ch2.Stop();
					System.Console.WriteLine("car1 saved to the database: " + db.Ext().IsStored(car1));
					System.Console.WriteLine("car2 saved to the database: " + db.Ext().IsStored(car1));
					int temperature = (int)((car1.Temperature + car2.Temperature)/2);
					car.Temperature = temperature;
					db.Set(car);
				}
			} 
			finally 
			{
				db.Close();
			}
			ÑheckCar();
		}
		// end MeasureCarTemperature
	
		public static void ÑheckCar()
		{
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet result = db.Get(typeof(Car));
				ListResult(result);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end ÑheckCar

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
