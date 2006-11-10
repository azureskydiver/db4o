/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Debugging
{
	public class DebugExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public static void Main(string[] args) 
		{
			SetCars();
		}
		// end Main

		public static void SetCars()
		{
			Db4oFactory.Configure().MessageLevel(3);
			File.Delete(YapFileName);
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
			try 
			{
				Car car1 = new Car("BMW");
				db.Set(car1);
				Car car2 = new Car("Ferrari");
				db.Set(car2);
				db.Deactivate(car1,2);
				IQuery query = db.Query();
				query.Constrain(typeof(Car));
				IObjectSet results = query.Execute();
				ListResult(results);
			} 
			finally 
			{
				db.Close();
			}
			Db4oFactory.Configure().MessageLevel(0);
		}
		// end SetCars

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
