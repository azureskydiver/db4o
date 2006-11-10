using System;
using System.IO;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.Clientserver
{
    public class TransactionExample 
    {
		public readonly static string YapFileName = "formula1.yap";

        public static void Main(string[] args)
        {
            File.Delete(YapFileName);
            IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
            try
            {
                StoreCarCommit(db);
                db.Close();
                db = Db4oFactory.OpenFile(YapFileName);
                ListAllCars(db);
                StoreCarRollback(db);
                db.Close();
                db = Db4oFactory.OpenFile(YapFileName);
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
        
        public static void StoreCarCommit(IObjectContainer db)
        {
            Pilot pilot = new Pilot("Rubens Barrichello", 99);
            Car car = new Car("BMW");
            car.Pilot = pilot;
            db.Set(car);
            db.Commit();
        }
		// end StoreCarCommit
    
        public static void ListAllCars(IObjectContainer db)
        {
            IObjectSet result = db.Get(typeof(Car));
            ListResult(result);
        }
		// end ListAllCars
        
        public static void StoreCarRollback(IObjectContainer db)
        {
            Pilot pilot = new Pilot("Michael Schumacher", 100);
            Car car = new Car("Ferrari");
            car.Pilot = pilot;
            db.Set(car);
            db.Rollback();
        }
		// end StoreCarRollback
    
        public static void CarSnapshotRollback(IObjectContainer db)
        {
            IObjectSet result = db.Get(new Car("BMW"));
            Car car = (Car)result.Next();
            car.Snapshot();
            db.Set(car);
            db.Rollback();
            Console.WriteLine(car);
        }
		// end CarSnapshotRollback
    
        public static void CarSnapshotRollbackRefresh(IObjectContainer db)
        {
            IObjectSet result=db.Get(new Car("BMW"));
            Car car=(Car)result.Next();
            car.Snapshot();
            db.Set(car);
            db.Rollback();
            db.Ext().Refresh(car, int.MaxValue);
            Console.WriteLine(car);
        }
		// end CarSnapshotRollbackRefresh

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
