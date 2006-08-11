using System;
using System.IO;
using com.db4o;
using com.db4odoc.f1;

namespace com.db4odoc.f1.clientserver
{
    public class TransactionExample : Util
    {
        public static void Main(string[] args)
        {
            File.Delete(Util.YapFileName);
            ObjectContainer db=Db4o.OpenFile(Util.YapFileName);
            try
            {
                StoreCarCommit(db);
                db.Close();
                db = Db4o.OpenFile(Util.YapFileName);
                ListAllCars(db);
                StoreCarRollback(db);
                db.Close();
                db = Db4o.OpenFile(Util.YapFileName);
                ListAllCars(db);
                CarSnapshotRollback(db);
                CarSnapshotRollbackRefresh(db);
            }
            finally
            {
                db.Close();
            }
        }
        
        public static void StoreCarCommit(ObjectContainer db)
        {
            Pilot pilot = new Pilot("Rubens Barrichello", 99);
            Car car = new Car("BMW");
            car.Pilot = pilot;
            db.Set(car);
            db.Commit();
        }
    
        public static void ListAllCars(ObjectContainer db)
        {
            ObjectSet result = db.Get(typeof(Car));
            ListResult(result);
        }
        
        public static void StoreCarRollback(ObjectContainer db)
        {
            Pilot pilot = new Pilot("Michael Schumacher", 100);
            Car car = new Car("Ferrari");
            car.Pilot = pilot;
            db.Set(car);
            db.Rollback();
        }
    
        public static void CarSnapshotRollback(ObjectContainer db)
        {
            ObjectSet result = db.Get(new Car("BMW"));
            Car car = (Car)result.Next();
            car.Snapshot();
            db.Set(car);
            db.Rollback();
            Console.WriteLine(car);
        }
    
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
    }
}
