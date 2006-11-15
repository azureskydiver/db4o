using System;
using System.IO;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.ClientServer
{
	public class DeepExample
    {
		public readonly static string YapFileName = "formula1.yap";

        public static void Main(string[] args)
        {
            File.Delete(YapFileName);
            IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
            try
            {
                StoreCar(db);
                db.Close();
                SetCascadeOnUpdate();
                db = Db4oFactory.OpenFile(YapFileName);
                TakeManySnapshots(db);
                db.Close();
                db = Db4oFactory.OpenFile(YapFileName);
                RetrieveAllSnapshots(db);
                db.Close();
                db = Db4oFactory.OpenFile(YapFileName);
                RetrieveSnapshotsSequentially(db);
                RetrieveSnapshotsSequentiallyImproved(db);
                db.Close();
                SetActivationDepth();
                db = Db4oFactory.OpenFile(YapFileName);
                RetrieveSnapshotsSequentially(db);
            }
            finally
            {
                db.Close();
            }
        }
		// end Main
        
        public static void StoreCar(IObjectContainer db)
        {
            Pilot pilot = new Pilot("Rubens Barrichello", 99);
            Car car = new Car("BMW");
            car.Pilot = pilot;
            db.Set(car);
        }
		// end StoreCar
        
        public static void SetCascadeOnUpdate()
        {
            Db4oFactory.Configure().ObjectClass(typeof(Car)).CascadeOnUpdate(true);
        }
		// end SetCascadeOnUpdate
        
        public static void TakeManySnapshots(IObjectContainer db)
        {
            IObjectSet result = db.Get(typeof(Car));
            Car car = (Car)result.Next();
            for (int i=0; i<5; i++)
            {
                car.Snapshot();
            }
            db.Set(car);
        }
		// end TakeManySnapshots
        
        public static void RetrieveAllSnapshots(IObjectContainer db)
        {
            IObjectSet result = db.Get(typeof(SensorReadout));
            while (result.HasNext())
            {
                Console.WriteLine(result.Next());
            }
        }
		// end RetrieveAllSnapshots
        
        public static void RetrieveSnapshotsSequentially(IObjectContainer db)
        {
            IObjectSet result = db.Get(typeof(Car));
            Car car = (Car)result.Next();
            SensorReadout readout = car.GetHistory();
            while (readout != null)
            {
                Console.WriteLine(readout);
                readout = readout.Next;
            }
        }
		// end RetrieveSnapshotsSequentially
        
        public static void RetrieveSnapshotsSequentiallyImproved(IObjectContainer db)
        {
            IObjectSet result = db.Get(typeof(Car));
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
            Db4oFactory.Configure().ObjectClass(typeof(TemperatureSensorReadout))
                .CascadeOnActivate(true);
        }
		// end SetActivationDepth        
    }
}
