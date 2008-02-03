/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Diagnostic;
using Db4objects.Db4o.TA;


namespace Db4objects.Db4odoc.TPClone
{
    public class TPCloneExample
    {

        private const string Db4oFileName = "reference.db4o";

        private static IObjectContainer _container = null;

        public static void Main(string[] args)
        {
            StoreCar();
            TestClone();
        }
        // end Main

        private static void StoreCar()
        {
            File.Delete(Db4oFileName);
            IObjectContainer container = Database(Db4oFactory.NewConfiguration());
            if (container != null)
            {
                try
                {
                    // create a car
                    Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
                    container.Store(car);
                    Car car1 = (Car)car.Clone();
                    container.Store(car1);
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end StoreCar

        private static IConfiguration ConfigureTP()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            // add TP support
            configuration.Add(new TransparentPersistenceSupport());
            return configuration;
        }
        // end ConfigureTP

        private static void TestClone()
        {
            IConfiguration configuration = ConfigureTP();

            IObjectContainer container = Database(configuration);
            if (container != null)
            {
                try
                {
                    IObjectSet result = container.QueryByExample(new Car(null, null));
                    ListResult(result);
                    Car car = null;
                    Car car1 = null;
                    if (result.Size() > 0)
                    {
                        car = (Car)result[0];
                        System.Console.WriteLine("Retrieved car: " + car);
                        car1 = (Car)car.Clone();
                        System.Console.WriteLine("Storing cloned car: " + car1);
                        container.Store(car1);
                        container.Commit();
                    }
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end TestClone


        private static IObjectContainer Database(IConfiguration configuration)
        {
            if (_container == null)
            {
                try
                {
                    _container = Db4oFactory.OpenFile(configuration, Db4oFileName);
                }
                catch (DatabaseFileLockedException ex)
                {
                    System.Console.WriteLine(ex.Message);
                }
            }
            return _container;
        }

        // end Database

        private static void CloseDatabase()
        {
            if (_container != null)
            {
                _container.Close();
                _container = null;
            }
        }

        // end CloseDatabase

        private static void ListResult(IObjectSet result)
        {
            System.Console.WriteLine(result.Size());
            while (result.HasNext())
            {
                System.Console.WriteLine(result.Next());
            }
        }
        // end ListResult
    }
}
