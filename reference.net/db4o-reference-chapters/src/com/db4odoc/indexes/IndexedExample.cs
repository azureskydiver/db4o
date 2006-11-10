using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Indexes
{	
	public class IndexedExample
	{
		public readonly static string YapFileName = "formula1.yap";

        public static void Main(string[] args)
        {
            FillUpDB();
            NoIndex();
            FullIndex();
            PilotIndex();
            PointsIndex();
        }
        // end Main
	
		public static void NoIndex() {
    		IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
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
	    
		public static void FillUpDB(){
			File.Delete(YapFileName);
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
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
	  
		public static void PilotIndex() {
    		Db4oFactory.Configure().ObjectClass(typeof(Car)).ObjectField("_pilot").Indexed(true);
    		Db4oFactory.Configure().ObjectClass(typeof(Pilot)).ObjectField("_points").Indexed(false);
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
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
	   
		public static void PointsIndex() {
    		Db4oFactory.Configure().ObjectClass(typeof(Car)).ObjectField("_pilot").Indexed(false);
    		Db4oFactory.Configure().ObjectClass(typeof(Pilot)).ObjectField("_points").Indexed(true);
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
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
	    
		public static void FullIndex() {
    		Db4oFactory.Configure().ObjectClass(typeof(Car)).ObjectField("_pilot").Indexed(true);
    		Db4oFactory.Configure().ObjectClass(typeof(Pilot)).ObjectField("_points").Indexed(true);
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
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