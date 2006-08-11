using System;
using System.IO;
using com.db4o;
using com.db4o.query;

namespace com.db4odoc.f1.indexes
{	
	public class IndexedExample: Util {
		
		public static void noIndex() {
    		ObjectContainer db=Db4o.OpenFile(Util.YapFileName);
			try {
    			Query query = db.Query();
				query.Constrain(typeof(Car));
				query.Descend("_pilot").Descend("_points").Constrain("99");

				DateTime dt1 = DateTime.UtcNow;
				ObjectSet  result = query.Execute();
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
	    
		public static void fillUpDB(){
			File.Delete(Util.YapFileName);
			ObjectContainer db=Db4o.OpenFile(Util.YapFileName);
			try {
        		for (int i=0; i<10000;i++){
    				AddCar(db,i);
    			}
			}
			finally {
				db.Close();
			}
		}
	  
		public static void pilotIndex() {
    		Db4o.Configure().ObjectClass(typeof(Car)).ObjectField("_pilot").Indexed(true);
    		Db4o.Configure().ObjectClass(typeof(Pilot)).ObjectField("_points").Indexed(false);
			ObjectContainer db=Db4o.OpenFile(Util.YapFileName);
			try {
    			Query query = db.Query();
				query.Constrain(typeof(Car));
				query.Descend("_pilot").Descend("_points").Constrain("99");

				DateTime dt1 = DateTime.UtcNow;
				ObjectSet  result = query.Execute();
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
	   
		public static void pointsIndex() {
    		Db4o.Configure().ObjectClass(typeof(Car)).ObjectField("_pilot").Indexed(false);
    		Db4o.Configure().ObjectClass(typeof(Pilot)).ObjectField("_points").Indexed(true);
			ObjectContainer db=Db4o.OpenFile(Util.YapFileName);
			try {
    			Query query = db.Query();
				query.Constrain(typeof(Car));
				query.Descend("_pilot").Descend("_points").Constrain("99");

				DateTime dt1 = DateTime.UtcNow;
				ObjectSet  result = query.Execute();
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
	    
	    
		public static void fullIndex() {
    		Db4o.Configure().ObjectClass(typeof(Car)).ObjectField("_pilot").Indexed(true);
    		Db4o.Configure().ObjectClass(typeof(Pilot)).ObjectField("_points").Indexed(true);
			ObjectContainer db=Db4o.OpenFile(Util.YapFileName);
			try {
    			Query query = db.Query();
				query.Constrain(typeof(Car));
				query.Descend("_pilot").Descend("_points").Constrain("99");

				DateTime dt1 = DateTime.UtcNow;
				ObjectSet  result = query.Execute();
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

	    
		private static void AddCar(ObjectContainer db, int points)
		{
			Car car = new Car("BMW");
			car.Pilot= new Pilot("Tester", points);
			db.Set(car);
		}
	    
	    
	}
}