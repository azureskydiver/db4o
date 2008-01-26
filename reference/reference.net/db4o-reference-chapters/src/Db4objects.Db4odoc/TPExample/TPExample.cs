/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Diagnostic;
using Db4objects.Db4o.TA;


namespace Db4objects.Db4odoc.TPExample
{
    public class TPExample
    {

        private const string Db4oFileName = "reference.db4o";

        private static IObjectContainer _container = null;

        public static void Main(string[] args)
        {
            TestTransparentPersistence();
        }
        // end Main

        private static void StoreSensorPanel()
        {
            File.Delete(Db4oFileName);
            IObjectContainer container = Database(Db4oFactory.NewConfiguration());
            if (container != null)
            {
                try
                {
                    // create a linked list with length 10
                    SensorPanel list = new SensorPanel().CreateList(10);
                    container.Store(list);
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end StoreSensorPanel

        private static IConfiguration ConfigureTP()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            // add TP support
            configuration.Add(new TransparentPersistenceSupport());
            return configuration;
        }
        // end ConfigureTP

        private static void TestTransparentPersistence()
        {
            StoreSensorPanel();
            IConfiguration configuration = ConfigureTP();

            IObjectContainer container = Database(configuration);
            if (container != null)
            {
                try
                {
                    IObjectSet result = container.QueryByExample(new SensorPanel(1));
                    ListResult(result);
                    SensorPanel sensor = null;
                    if (result.Size() > 0)
                    {
                        System.Console.WriteLine("Before modification: ");
                        sensor = (SensorPanel)result[0];
                        // the object is a linked list, so each call to next()
                        // will need to activate a new object
                        SensorPanel next = sensor.Next;
                        while (next != null)
                        {
                            System.Console.WriteLine(next);
                            // modify the next sensor
                            next.Sensor = (object)(10 + (int)next.Sensor);
                            next = next.Next;
                        }
                        // Explicit commit stores and commits the changes at any time
                        container.Commit();
                    }
                }
                finally
                {
                    // If there are unsaved changes to activatable objects, they 
                    // will be implicitly saved and committed when the database 
                    // is closed
                    CloseDatabase();
                }
            }
            // reopen the database and check the modifications
            container = Database(configuration);
            if (container != null)
            {
                try
                {
                    IObjectSet result = container.QueryByExample(new SensorPanel(1));
                    ListResult(result);
                    SensorPanel sensor = null;
                    if (result.Size() > 0)
                    {
                        System.Console.WriteLine("After modification: ");
                        sensor = (SensorPanel)result[0];
                        SensorPanel next = sensor.Next;
                        while (next != null)
                        {
                            System.Console.WriteLine(next);
                            next = next.Next;
                        }
                    }
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end TestTransparentPersistence

        
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
