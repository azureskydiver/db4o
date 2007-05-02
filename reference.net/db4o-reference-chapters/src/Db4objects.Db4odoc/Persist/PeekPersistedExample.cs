/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using System.Threading;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.Persist
{
	public class PeekPersistedExample
	{
		private const string Db4oFileName = "reference.db4o";

		public static void Main(string[] args) 
		{
			MeasureCarTemperature();
		}
		// end Main
		
		private static void SetObjects()
		{
			File.Delete(Db4oFileName);
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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

		private static void MeasureCarTemperature()
		{
			SetObjects();
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IObjectSet result = db.Get(typeof(Car));
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

        private static void ÑheckCar()
		{
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IObjectSet result = db.Get(typeof(Car));
				ListResult(result);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end ÑheckCar

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
