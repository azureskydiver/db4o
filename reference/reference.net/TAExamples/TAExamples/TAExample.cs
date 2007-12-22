/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Diagnostic;
using Db4objects.Db4o.TA;


namespace Db4ojects.Db4odoc.TAExamples
{
    public class TAExample
    {

        private const string Db4oFileName = "reference.db4o";

        private static IObjectContainer _container = null;

        public static void Main(string[] args)
        {
            TestActivation();
            TestCollectionActivation();
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
                    SensorPanelTA list = new SensorPanelTA().CreateList(10);
                    container.Set(list);
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end StoreSensorPanel

        private class TADiagnostics : IDiagnosticListener
        {
            public void OnDiagnostic(IDiagnostic diagnostic)
            {
                if (!(diagnostic is NotTransparentActivationEnabled))
                {
                    return;
                }
                System.Console.WriteLine(diagnostic.ToString());
            }
        }
        // end TADiagnostics

        private static void ActivateDiagnostics(IConfiguration configuration)
        {
            // Add diagnostic listener that will show all the classes that are not
            // TA aware.
            configuration.Diagnostic().AddListener(new TADiagnostics());
        }
        // end ActivateDiagnostics

        private static IConfiguration ConfigureTA()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            // add TA support
            configuration.Add(new TransparentActivationSupport());
            // activate TA diagnostics to reveal the classes that are not TA-enabled.
            // ActivateDiagnostics(configuration);
            return configuration;
        }
        // end ConfigureTA

        private static void TestActivation()
        {
            StoreSensorPanel();
            IConfiguration configuration = ConfigureTA();

            IObjectContainer container = Database(configuration);
            if (container != null)
            {
                try
                {
                    System.Console.WriteLine("Zero activation depth");
                    IObjectSet result = container.Get(new SensorPanelTA(1));
                    ListResult(result);
                    if (result.Size() > 0)
                    {
                        SensorPanelTA sensor = (SensorPanelTA)result[0];
                        // the object is a linked list, so each call to next()
                        // will need to activate a new object
                        SensorPanelTA next = sensor.Next;
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

        // end TestActivation

        private static void StoreCollection()
        {
            File.Delete(Db4oFileName);
            IObjectContainer container = Database(ConfigureTA());
            if (container != null)
            {
                try
                {
                    Team team = new Team();
                    for (int i = 0; i < 10; i++)
                    {
                        team.AddPilot(new Pilot("Pilot #" + i));
                    }
                    container.Set(team);
                    container.Commit();
                }
                catch (Exception ex)
                {
                    System.Console.WriteLine(ex.StackTrace);
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end StoreCollection

        private static void TestCollectionActivation()
        {
            StoreCollection();
            IObjectContainer container = Database(ConfigureTA());
            if (container != null)
            {
                try
                {
                    Team team = (Team)container.Get(new Team()).Next();
                    for (int j = 0; j < team.Size(); j++)
                    {
                        System.Console.WriteLine(team.Pilots[j]);
                    }
                }
                catch (Exception ex)
                {
                    System.Console.WriteLine(ex.StackTrace);
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end TestCollectionActivation

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
