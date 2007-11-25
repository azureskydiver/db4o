/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Config;

namespace Db4objects.Db4odoc.ClientServer
{
	public class DeepExample
    {
		private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            File.Delete(Db4oFileName);
            IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                StoreCar(db);
                db.Close();
                IConfiguration configuration = ConfigureCascadeOnUpdate();
                db = Db4oFactory.OpenFile(configuration, Db4oFileName);
                TakeManySnapshots(db);
                db.Close();
                db = Db4oFactory.OpenFile(configuration, Db4oFileName);
                RetrieveAllSnapshots(db);
                db.Close();
                db = Db4oFactory.OpenFile(configuration, Db4oFileName);
                RetrieveSnapshotsSequentially(db);
                RetrieveSnapshotsSequentiallyImproved(db);
                db.Close();
                configuration = ConfigureActivationDepth();
                db = Db4oFactory.OpenFile(configuration, Db4oFileName);
                RetrieveSnapshotsSequentially(db);
            }
            finally
            {
                db.Close();
            }
        }
		// end Main
        
        private static void StoreCar(IObjectContainer db)
        {
            Pilot pilot = new Pilot("Rubens Barrichello", 99);
            Car car = new Car("BMW");
            car.Pilot = pilot;
            db.Set(car);
        }
		// end StoreCar

        private static IConfiguration ConfigureCascadeOnUpdate()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(Car)).CascadeOnUpdate(true);
            return configuration;
        }
		// end ConfigureCascadeOnUpdate

        private static void TakeManySnapshots(IObjectContainer db)
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

        private static void RetrieveAllSnapshots(IObjectContainer db)
        {
            IObjectSet result = db.Get(typeof(SensorReadout));
            while (result.HasNext())
            {
                Console.WriteLine(result.Next());
            }
        }
		// end RetrieveAllSnapshots

        private static void RetrieveSnapshotsSequentially(IObjectContainer db)
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

        private static void RetrieveSnapshotsSequentiallyImproved(IObjectContainer db)
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

        private static IConfiguration ConfigureActivationDepth()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(TemperatureSensorReadout))
                .CascadeOnActivate(true);
            return configuration;
        }
        // end ConfigureActivationDepth        
    }
}
