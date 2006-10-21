using System;
using System.IO;
using com.db4o;

namespace com.db4odoc.f1.clientserver
{
	public class DeepExample
    {
		public readonly static string YapFileName = "formula1.yap";

        public static void Main(string[] args)
        {
            File.Delete(YapFileName);
            ObjectContainer db = Db4o.OpenFile(YapFileName);
            try
            {
                StoreCar(db);
                db.Close();
                SetCascadeOnUpdate();
                db = Db4o.OpenFile(YapFileName);
                TakeManySnapshots(db);
                db.Close();
                db = Db4o.OpenFile(YapFileName);
                RetrieveAllSnapshots(db);
                db.Close();
                db = Db4o.OpenFile(YapFileName);
                RetrieveSnapshotsSequentially(db);
                RetrieveSnapshotsSequentiallyImproved(db);
                db.Close();
                SetActivationDepth();
                db = Db4o.OpenFile(YapFileName);
                RetrieveSnapshotsSequentially(db);
            }
            finally
            {
                db.Close();
            }
        }
		// end Main
        
        public static void StoreCar(ObjectContainer db)
        {
            Pilot pilot = new Pilot("Rubens Barrichello", 99);
            Car car = new Car("BMW");
            car.Pilot = pilot;
            db.Set(car);
        }
		// end StoreCar
        
        public static void SetCascadeOnUpdate()
        {
            Db4o.Configure().ObjectClass(typeof(Car)).CascadeOnUpdate(true);
        }
		// end SetCascadeOnUpdate
        
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
		// end TakeManySnapshots
        
        public static void RetrieveAllSnapshots(ObjectContainer db)
        {
            ObjectSet result = db.Get(typeof(SensorReadout));
            while (result.HasNext())
            {
                Console.WriteLine(result.Next());
            }
        }
		// end RetrieveAllSnapshots
        
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
		// end RetrieveSnapshotsSequentially
        
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
		// end RetrieveSnapshotsSequentiallyImproved
        
        public static void SetActivationDepth()
        {
            Db4o.Configure().ObjectClass(typeof(TemperatureSensorReadout))
                .CascadeOnActivate(true);
        }
		// end SetActivationDepth        
    }
}
