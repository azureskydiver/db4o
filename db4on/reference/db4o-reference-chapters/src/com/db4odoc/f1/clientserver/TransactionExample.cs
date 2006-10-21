using System;
using System.IO;
using com.db4o;
using com.db4odoc.f1;

namespace com.db4odoc.f1.clientserver
{
    public class TransactionExample 
    {
		public readonly static string YapFileName = "formula1.yap";

        public static void Main(string[] args)
        {
            File.Delete(YapFileName);
            ObjectContainer db=Db4o.OpenFile(YapFileName);
            try
            {
                StoreCarCommit(db);
                db.Close();
                db = Db4o.OpenFile(YapFileName);
                ListAllCars(db);
                StoreCarRollback(db);
                db.Close();
                db = Db4o.OpenFile(YapFileName);
                ListAllCars(db);
                CarSnapshotRollback(db);
                CarSnapshotRollbackRefresh(db);
            }
            finally
            {
                db.Close();
            }
        }
		// end Main
        
        public static void StoreCarCommit(ObjectContainer db)
        {
            Pilot pilot = new Pilot("Rubens Barrichello", 99);
            Car car = new Car("BMW");
            car.Pilot = pilot;
            db.Set(car);
            db.Commit();
        }
		// end StoreCarCommit
    
        public static void ListAllCars(ObjectContainer db)
        {
            ObjectSet result = db.Get(typeof(Car));
            ListResult(result);
        }
		// end ListAllCars
        
        public static void StoreCarRollback(ObjectContainer db)
        {
            Pilot pilot = new Pilot("Michael Schumacher", 100);
            Car car = new Car("Ferrari");
            car.Pilot = pilot;
            db.Set(car);
            db.Rollback();
        }
		// end StoreCarRollback
    
        public static void CarSnapshotRollback(ObjectContainer db)
        {
            ObjectSet result = db.Get(new Car("BMW"));
            Car car = (Car)result.Next();
            car.Snapshot();
            db.Set(car);
            db.Rollback();
            Console.WriteLine(car);
        }
		// end CarSnapshotRollback
    
        public static void CarSnapshotRollbackRefresh(ObjectContainer db)
        {
            ObjectSet result=db.Get(new Car("BMW"));
            Car car=(Car)result.Next();
            car.Snapshot();
            db.Set(car);
            db.Rollback();
            db.Ext().Refresh(car, int.MaxValue);
            Console.WriteLine(car);
        }
		// end CarSnapshotRollbackRefresh

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
