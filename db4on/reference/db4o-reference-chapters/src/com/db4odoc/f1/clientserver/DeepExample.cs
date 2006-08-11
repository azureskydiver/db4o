using System;
using System.IO;
using com.db4o;

namespace com.db4odoc.f1.clientserver
{
	public class DeepExample : Util
    {
        public static void Main(string[] args)
        {
            File.Delete(Util.YapFileName);
            ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
            try
            {
                StoreCar(db);
                db.Close();
                SetCascadeOnUpdate();
                db = Db4o.OpenFile(Util.YapFileName);
                TakeManySnapshots(db);
                db.Close();
                db = Db4o.OpenFile(Util.YapFileName);
                RetrieveAllSnapshots(db);
                db.Close();
                db = Db4o.OpenFile(Util.YapFileName);
                RetrieveSnapshotsSequentially(db);
                RetrieveSnapshotsSequentiallyImproved(db);
                db.Close();
                SetActivationDepth();
                db = Db4o.OpenFile(Util.YapFileName);
                RetrieveSnapshotsSequentially(db);
            }
            finally
            {
                db.Close();
            }
        }
        
        public static void StoreCar(ObjectContainer db)
        {
            Pilot pilot = new Pilot("Rubens Barrichello", 99);
            Car car = new Car("BMW");
            car.Pilot = pilot;
            db.Set(car);
        }
        
        public static void SetCascadeOnUpdate()
        {
            Db4o.Configure().ObjectClass(typeof(Car)).CascadeOnUpdate(true);
        }
        
        public static void TakeManySnapshots(ObjectContainer db)
        {
            ObjectSet result = db.Get(typeof(Car));
            Car car = (Car)result.Next();
            for (int i=0; i<5; i++)
            {
                car.Snapshot();
            }
            db.Set(car);
        }
        
        public static void RetrieveAllSnapshots(ObjectContainer db)
        {
            ObjectSet result = db.Get(typeof(SensorReadout));
            while (result.HasNext())
            {
                Console.WriteLine(result.Next());
            }
        }
        
        public static void RetrieveSnapshotsSequentially(ObjectContainer db)
        {
            ObjectSet result = db.Get(typeof(Car));
            Car car = (Car)result.Next();
            SensorReadout readout = car.GetHistory();
            while (readout != null)
            {
                Console.WriteLine(readout);
                readout = readout.Next;
            }
        }
        
        public static void RetrieveSnapshotsSequentiallyImproved(ObjectContainer db)
        {
            ObjectSet result = db.Get(typeof(Car));
            Car car = (Car)result.Next();
            SensorReadout readout = car.GetHistory();
            while (readout != null)
            {
                db.Activate(readout, 1);
                Console.WriteLine(readout);
                readout = readout.Next;
            }
        }
        
        public static void SetActivationDepth()
        {
            Db4o.Configure().ObjectClass(typeof(TemperatureSensorReadout))
                .CascadeOnActivate(true);
        }
        
    }
}
