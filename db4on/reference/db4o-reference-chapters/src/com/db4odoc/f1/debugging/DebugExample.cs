/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using System;
using System.IO;
using com.db4o;
using com.db4o.query;
using com.db4odoc.f1;
using com.db4odoc.f1.evaluations;

namespace com.db4odoc.f1.debugging
{
	public class DebugExample: Util
	{
		public static void main(String[] args) 
		{
			SetCars();
		}

		public static void SetCars()
		{
			Db4o.Configure().MessageLevel(3);
			File.Delete(Util.YapFileName);
			ObjectContainer db=Db4o.OpenFile(Util.YapFileName);
			try 
			{
				Car car1 = new Car("BMW");
				db.Set(car1);
				Car car2 = new Car("Ferrari");
				db.Set(car2);
				db.Deactivate(car1,2);
				Query query = db.Query();
				query.Constrain(typeof(Car));
				ObjectSet results = query.Execute();
				ListResult(results);
			} 
			finally 
			{
				db.Close();
			}
			Db4o.Configure().MessageLevel(0);
		}
	}
}
