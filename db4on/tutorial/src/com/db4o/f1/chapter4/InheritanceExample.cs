namespace com.db4o.f1.chapter4
{
    using System;
    using System.IO;
    using com.db4o;
    using com.db4o.f1;
    using com.db4o.query;
    
    public class InheritanceExample : Util
    {        
        public static void main(string[] args)
        {
            File.Delete(Util.YapFileName);          
            ObjectContainer db = Db4o.openFile(Util.YapFileName);
            try
            {
                storeFirstCar(db);
                storeSecondCar(db);
                retrieveTemperatureReadoutsQBE(db);
                retrieveAllSensorReadoutsQBE(db);
                retrieveAllSensorReadoutsQBEAlternative(db);
                retrieveAllSensorReadoutsQuery(db);
                retrieveAllObjects(db);
                deleteAllObjects(db);
            }
            finally
            {
                db.close();
            }
        }
        
        public static void storeFirstCar(ObjectContainer db)
        {
            Car car1 = new Car("Ferrari");
            Pilot pilot1 = new Pilot("Michael Schumacher", 100);
            car1.Pilot = pilot1;
            db.set(car1);
        }
        
        public static void storeSecondCar(ObjectContainer db)
        {
            Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
            Car car2 = new Car("BMW");
            car2.Pilot = pilot2;
            car2.Snapshot();
            car2.Snapshot();
            db.set(car2);
        }
        
        public static void retrieveAllSensorReadoutsQBE(ObjectContainer db)
        {
            SensorReadout proto = new SensorReadout(DateTime.MinValue, null, null);
            ObjectSet result = db.get(proto);
            listResult(result);
        }
        
        public static void retrieveTemperatureReadoutsQBE(ObjectContainer db)
        {
            SensorReadout proto = new TemperatureSensorReadout(DateTime.MinValue, null, null, 0.0);
            ObjectSet result = db.get(proto);
            listResult(result);
        }
        
        public static void retrieveAllSensorReadoutsQBEAlternative(ObjectContainer db)
        {
            ObjectSet result = db.get(typeof(SensorReadout));
            listResult(result);
        }
        
        public static void retrieveAllSensorReadoutsQuery(ObjectContainer db)
        {
            Query query = db.query();
            query.constrain(typeof(SensorReadout));
            ObjectSet result = query.execute();
            listResult(result);
        }
        
        public static void retrieveAllObjects(ObjectContainer db)
        {
            ObjectSet result = db.get(new object());
            listResult(result);
        }
        
        public static void deleteAllObjects(ObjectContainer db)
        {
            ObjectSet result=db.get(new object());
            while (result.hasNext())
            {
                db.delete(result.next());
            }
        }
    }
}
