using System;
using System.IO;
using com.db4o;
using com.db4o.f1;
using com.db4o.query;

namespace com.db4o.f1.chapter4
{   
    public class InheritanceExample : Util
    {        
        public static void Main(string[] args)
        {
            File.Delete(Util.YapFileName);          
            ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
            try
            {
                StoreFirstCar(db);
                StoreSecondCar(db);
                RetrieveTemperatureReadoutsQBE(db);
                RetrieveAllSensorReadoutsQBE(db);
                RetrieveAllSensorReadoutsQBEAlternative(db);
                RetrieveAllSensorReadoutsQuery(db);
                RetrieveAllObjects(db);
            }
            finally
            {
                db.Close();
            }
        }
        
        public static void StoreFirstCar(ObjectContainer db)
        {
            Car car1 = new Car("Ferrari");
            Pilot pilot1 = new Pilot("Michael Schumacher", 100);
            car1.Pilot = pilot1;
            db.Set(car1);
        }
        
        public static void StoreSecondCar(ObjectContainer db)
        {
            Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
            Car car2 = new Car("BMW");
            car2.Pilot = pilot2;
            car2.Snapshot();
            car2.Snapshot();
            db.Set(car2);
        }
        
        public static void RetrieveAllSensorReadoutsQBE(ObjectContainer db)
        {
            SensorReadout proto = new SensorReadout(DateTime.MinValue, null, null);
            ObjectSet result = db.Get(proto);
            ListResult(result);
        }
        
        public static void RetrieveTemperatureReadoutsQBE(ObjectContainer db)
        {
            SensorReadout proto = new TemperatureSensorReadout(DateTime.MinValue, null, null, 0.0);
            ObjectSet result = db.Get(proto);
            ListResult(result);
        }
        
        public static void RetrieveAllSensorReadoutsQBEAlternative(ObjectContainer db)
        {
            ObjectSet result = db.Get(typeof(SensorReadout));
            ListResult(result);
        }
        
        public static void RetrieveAllSensorReadoutsQuery(ObjectContainer db)
        {
            Query query = db.Query();
            query.Constrain(typeof(SensorReadout));
            ObjectSet result = query.Execute();
            ListResult(result);
        }
        
        public static void RetrieveAllObjects(ObjectContainer db)
        {
            ObjectSet result = db.Get(new object());
            ListResult(result);
        }
    }
}
