namespace com.db4o.f1.chapter5
{
    using System;
    using System.IO;
    using com.db4o;
    using com.db4o.f1;

    public class TransactionExample : Util
    {
        public static void Main(string[] args)
        {
            File.Delete(Util.YapFileName);
            ObjectContainer db=Db4o.openFile(Util.YapFileName);
            try
            {
                storeCarCommit(db);
                db.close();
                db = Db4o.openFile(Util.YapFileName);
                listAllCars(db);
                storeCarRollback(db);
                db.close();
                db = Db4o.openFile(Util.YapFileName);
                listAllCars(db);
                carSnapshotRollback(db);
                carSnapshotRollbackRefresh(db);
                deleteAllObjects(db);
            }
            finally
            {
                db.close();
            }
        }
        
        public static void storeCarCommit(ObjectContainer db)
        {
            Pilot pilot = new Pilot("Rubens Barrichello", 99);
            Car car = new Car("BMW");
            car.Pilot = pilot;
            db.set(car);
            db.commit();
        }
    
        public static void listAllCars(ObjectContainer db)
        {
            ObjectSet result = db.get(new Car(null));
            listResult(result);
        }
        
        public static void storeCarRollback(ObjectContainer db)
        {
            Pilot pilot = new Pilot("Michael Schumacher", 100);
            Car car = new Car("Ferrari");
            car.Pilot = pilot;
            db.set(car);
            db.rollback();
        }
    
        public static void deleteAllObjects(ObjectContainer db)
        {
            ObjectSet result = db.get(new object());
            while (result.hasNext())
            {
                db.delete(result.next());
            }
        }
    
        public static void carSnapshotRollback(ObjectContainer db)
        {
            ObjectSet result = db.get(new Car("BMW"));
            Car car = (Car)result.next();
            car.Snapshot();
            db.set(car);
            db.rollback();
            Console.WriteLine(car);
        }
    
        public static void carSnapshotRollbackRefresh(ObjectContainer db)
        {
            ObjectSet result=db.get(new Car("BMW"));
            Car car=(Car)result.next();
            car.Snapshot();
            db.set(car);
            db.rollback();
            db.ext().refresh(car, int.MaxValue);
            Console.WriteLine(car);
        }
    }
}
