using System;
using System.IO;
using com.db4o;

namespace com.db4o.f1.chapter5
{
	public class DeepExample : Util
    {
        public static void Main(string[] args)
        {
            File.Delete(Util.YapFileName);
            ObjectContainer db = Db4o.openFile(Util.YapFileName);
            try
            {
                storeCar(db);
                db.close();
                setCascadeOnUpdate();
                db = Db4o.openFile(Util.YapFileName);
                takeManySnapshots(db);
                db.close();
                db = Db4o.openFile(Util.YapFileName);
                retrieveAllSnapshots(db);
                db.close();
                db = Db4o.openFile(Util.YapFileName);
                retrieveSnapshotsSequentially(db);
                retrieveSnapshotsSequentiallyImproved(db);
                db.close();
                setActivationDepth();
                db = Db4o.openFile(Util.YapFileName);
                retrieveSnapshotsSequentially(db);
            }
            finally
            {
                db.close();
            }
        }
        
        public static void storeCar(ObjectContainer db)
        {
            Pilot pilot = new Pilot("Rubens Barrichello", 99);
            Car car = new Car("BMW");
            car.Pilot = pilot;
            db.set(car);
        }
        
        public static void setCascadeOnUpdate()
        {
            Db4o.configure().objectClass(typeof(Car)).cascadeOnUpdate(true);
        }
        
        public static void takeManySnapshots(ObjectContainer db)
        {
            ObjectSet result = db.get(typeof(Car));
            Car car = (Car)result.next();
            for (int i=0; i<5; i++)
            {
                car.Snapshot();
            }
            db.set(car);
        }
        
        public static void retrieveAllSnapshots(ObjectContainer db)
        {
            ObjectSet result = db.get(typeof(SensorReadout));
            while (result.hasNext())
            {
                Console.WriteLine(result.next());
            }
        }
        
        public static void retrieveSnapshotsSequentially(ObjectContainer db)
        {
            ObjectSet result = db.get(typeof(Car));
            Car car = (Car)result.next();
            SensorReadout readout = car.GetHistory();
            while (readout != null)
            {
                Console.WriteLine(readout);
                readout = readout.Next;
            }
        }
        
        public static void retrieveSnapshotsSequentiallyImproved(ObjectContainer db)
        {
            ObjectSet result = db.get(typeof(Car));
            Car car = (Car)result.next();
            SensorReadout readout = car.GetHistory();
            while (readout != null)
            {
                db.activate(readout, 1);
                Console.WriteLine(readout);
                readout = readout.Next;
            }
        }
        
        public static void setActivationDepth()
        {
            Db4o.configure().objectClass(typeof(TemperatureSensorReadout))
                .cascadeOnActivate(true);
        }
        
    }
}
