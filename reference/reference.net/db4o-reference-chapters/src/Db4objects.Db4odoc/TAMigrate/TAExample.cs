/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Diagnostic;
using Db4objects.Db4o.TA;


namespace Db4objects.Db4odoc.TAMigrate
{
    public class TAExample
    {

        private const string FirstDbName = "reference.db4o";
        private const string SecondDbName = "migrate.db4o";


        public static void Main(string[] args)
        {
            //TestSwitchDatabases();
            TestSwitchDatabasesFixed();
        }
        // end Main

        private static void StoreSensorPanel()
        {
            File.Delete(FirstDbName);
            IObjectContainer container = Db4oFactory.OpenFile(FirstDbName);
            if (container != null)
            {
                try
                {
                    // create a linked list with length 10
                    SensorPanelTA list = new SensorPanelTA().CreateList(10);
                    container.Store(list);
                }
                finally
                {
                    container.Close();
                }
            }
        }
        // end StoreSensorPanel


        private static IConfiguration ConfigureTA()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            // add TA support
            configuration.Add(new TransparentActivationSupport());
            return configuration;
        }
        // end ConfigureTP

        private static void TestSwitchDatabases()
        {
            StoreSensorPanel();

            IObjectContainer firstDb = Db4oFactory.OpenFile(ConfigureTA(), FirstDbName);
            IObjectContainer secondDb = Db4oFactory.OpenFile(ConfigureTA(), SecondDbName);
            try
            {
                IObjectSet result = firstDb.QueryByExample(new SensorPanelTA(1));
                if (result.Count > 0)
                {
                    SensorPanelTA sensor = (SensorPanelTA)result[0];
                    firstDb.Close();
                    // Migrating an object from the first database
                    // into a second database
                    secondDb.Store(sensor);
                }
            }
            finally
            {
                firstDb.Close();
                secondDb.Close();
            }
        }
        // end TestSwitchDatabases


        private static void TestSwitchDatabasesFixed()
        {
            StoreSensorPanel();

            IObjectContainer firstDb = Db4oFactory.OpenFile(ConfigureTA(), FirstDbName);
            IObjectContainer secondDb = Db4oFactory.OpenFile(ConfigureTA(), SecondDbName);
            try
            {
                IObjectSet result = firstDb.QueryByExample(new SensorPanelTA(1));
                if (result.Count > 0)
                {
                    SensorPanelTA sensor = (SensorPanelTA)result[0];
                    // Unbind the object from the first database
                    sensor.Bind(null);
                    // Migrating the object into the second database
                    secondDb.Store(sensor);


                    System.Console.WriteLine("Retrieving previous query results from "
                            + FirstDbName + ":");
                    SensorPanelTA next = sensor.Next;
                    while (next != null)
                    {
                        System.Console.WriteLine(next);
                        next = next.Next;
                    }

                    System.Console.WriteLine("Retrieving previous query results from "
                            + FirstDbName + " with manual activation:");
                    firstDb.Activate(sensor, Int32.MaxValue);
                    next = sensor.Next;
                    while (next != null)
                    {
                        System.Console.WriteLine(next);
                        next = next.Next;
                    }

                    System.Console.WriteLine("Retrieving sensorPanel from " + SecondDbName + ":");
                    result = secondDb.QueryByExample(new SensorPanelTA(1));
                    next = sensor.Next;
                    while (next != null)
                    {
                        System.Console.WriteLine(next);
                        next = next.Next;
                    }
                }
            }
            finally
            {
                firstDb.Close();
                secondDb.Close();
            }
        }
        // end TestSwitchDatabasesFixed

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
