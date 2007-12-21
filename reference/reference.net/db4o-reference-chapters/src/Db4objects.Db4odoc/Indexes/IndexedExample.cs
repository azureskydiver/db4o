/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Indexes
{	
	public class IndexedExample
	{
		private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            //FillUpDB();
            NoIndex();
            FullIndex();
            PilotIndex();
            PointsIndex();
        }
        // end Main
	
		private static void NoIndex() {
    		IObjectContainer db=Db4oFactory.OpenFile(Db4oFileName);
			try {
    			IQuery query = db.Query();
				query.Constrain(typeof(Car));
				query.Descend("_pilot").Descend("_points").Constrain("99");

				DateTime dt1 = DateTime.UtcNow;
				IObjectSet  result = query.Execute();
				DateTime dt2 = DateTime.UtcNow;
				TimeSpan  diff = dt2 - dt1;
				Console.WriteLine("Test 1: no indexes");
				Console.WriteLine("Execution time="+diff.Milliseconds + " ms");
				ListResult(result);
			}
			finally {
				db.Close();
			}
		}
		// end NoIndex

        private static void FillUpDB()
        {
			File.Delete(Db4oFileName);
			IObjectContainer db=Db4oFactory.OpenFile(Db4oFileName);
			try {
        		for (int i=0; i<10000;i++){
    				AddCar(db,i);
    			}
			}
			finally {
				db.Close();
			}
		}
		// end FillUpDB

        private static void PilotIndex()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(Car)).ObjectField("_pilot").Indexed(true);
            configuration.ObjectClass(typeof(Pilot)).ObjectField("_points").Indexed(false);
			IObjectContainer db=Db4oFactory.OpenFile(configuration, Db4oFileName);
			try {
    			IQuery query = db.Query();
				query.Constrain(typeof(Car));
				query.Descend("_pilot").Descend("_points").Constrain("99");

				DateTime dt1 = DateTime.UtcNow;
				IObjectSet  result = query.Execute();
				DateTime dt2 = DateTime.UtcNow;
				TimeSpan  diff = dt2 - dt1;
				Console.WriteLine("Test 3: index on pilot");
				Console.WriteLine("Execution time="+diff.Milliseconds + " ms");
				ListResult(result);
			}
			finally {
				db.Close();
			}
		}
		// end PilotIndex

        private static void PointsIndex()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(Car)).ObjectField("_pilot").Indexed(false);
            configuration.ObjectClass(typeof(Pilot)).ObjectField("_points").Indexed(true);
			IObjectContainer db=Db4oFactory.OpenFile(configuration, Db4oFileName);
			try {
    			IQuery query = db.Query();
				query.Constrain(typeof(Car));
				query.Descend("_pilot").Descend("_points").Constrain("99");

				DateTime dt1 = DateTime.UtcNow;
				IObjectSet  result = query.Execute();
				DateTime dt2 = DateTime.UtcNow;
				TimeSpan  diff = dt2 - dt1;
				Console.WriteLine("Test 4: index on points");
				Console.WriteLine("Execution time="+diff.Milliseconds + " ms");
				ListResult(result);
			}
			finally {
				db.Close();
			}
		}
	    // end PointsIndex

        private static void FullIndex()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(Car)).ObjectField("_pilot").Indexed(true);
            configuration.ObjectClass(typeof(Pilot)).ObjectField("_points").Indexed(true);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
			try {
    			IQuery query = db.Query();
				query.Constrain(typeof(Car));
				query.Descend("_pilot").Descend("_points").Constrain("99");

				DateTime dt1 = DateTime.UtcNow;
				IObjectSet  result = query.Execute();
				DateTime dt2 = DateTime.UtcNow;
				TimeSpan  diff = dt2 - dt1;
				Console.WriteLine("Test 2: index on pilot and points");
				Console.WriteLine("Execution time="+diff.Milliseconds + " ms");
				ListResult(result);
			}
			finally {
				db.Close();
			}
		}
		// end FullIndex
	    
		private static void AddCar(IObjectContainer db, int points)
		{
			Car car = new Car("BMW");
			car.Pilot= new Pilot("Tester", points);
			db.Set(car);
		}
		// end AddCar

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