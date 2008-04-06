/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

using System;
using System.Linq;
using System.IO;
using System.Collections.Generic;
using System.Collections;

using Db4objects.Db4o;
using Db4objects.Db4o.Linq;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Diagnostic;
using Db4objects.Db4o.Config;

namespace Db4objects.Db4odoc.Linq
{
    class SimpleExamples
    {

        private readonly static string Db4oFileName = Path.Combine(
                               Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),
                               "reference.db4o");  

        private const int ObjectCount = 10;

        private static IObjectContainer _container = null;

        public static void Main(string[] args)
        {
            StoreObjects();
            SelectEverythingByName();
            SelectPilotByNameAndPoints();
            SelectUnoptimized();
        }

        // end Main

        private static IObjectContainer Database(IConfiguration config)
        {
            if (_container == null)
            {
                try
                {
                    _container = Db4oFactory.OpenFile(config, Db4oFileName);
                }
                catch (DatabaseFileLockedException ex)
                {
                    System.Console.WriteLine(ex.Message);
                }
            }
            return _container;
        }

        // end Database

        private static IObjectContainer Database()
        {
            if (_container == null)
            {
                try
                {
                    _container = Db4oFactory.OpenFile(Db4oFileName);
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

        private static void StoreObjects()
        {
            File.Delete(Db4oFileName);
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    Pilot pilot;
                    Car car;
                    for (int i = 0; i < ObjectCount; i++)
                    {
                        pilot = new Pilot("Test Pilot #" + i, i + 10);
                        car = new Car("Test model #" + i, pilot);
                        container.Store(car);
                    }
                    container.Commit();
                }
                catch (Db4oException ex)
                {
                    System.Console.WriteLine("Db4o Exception: " + ex.Message);
                }
                catch (Exception ex)
                {
                    System.Console.WriteLine("System Exception: " + ex.Message);
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end StoreObjects

        
        private static IConfiguration Configure()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.Diagnostic().AddListener(new NQDiagnostics());
            return configuration;
        }
        // end Configure

        private class NQDiagnostics : IDiagnosticListener
        {
            public void OnDiagnostic(IDiagnostic diagnostic)
            {
                if (diagnostic.GetType() == typeof(NativeQueryNotOptimized))
                {
                    System.Console.WriteLine(diagnostic.ToString());
                }              
            }
        }
        // end NQDiagnostics



        private static void SelectEverythingByName()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    var result = from object o in container
                                                where o.ToString().StartsWith("Test")
                                  select o;
                    IList objects = result.ToList();
                    ListResult(objects);
                }
                catch (Exception ex)
                {
                    System.Console.WriteLine("System Exception: " + ex.Message);
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end SelectEverythingByName



        private static void SelectUnoptimized()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IEnumerable<Pilot> result = from Pilot p in container
                                                where p.Points == p.Name.Length
                                  select p;

                    ListResult(result);
                }
                catch (Exception ex)
                {
                    System.Console.WriteLine("System Exception: " + ex.Message);
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end SelectUnoptimized


        private static void SelectPilotByNameAndPoints()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IEnumerable<Pilot> result = from Pilot p in container
                                                where p.Name.StartsWith("Test") && p.Points > 2
                                                select p;

                    ListResult(result);
                }
                catch (Exception ex)
                {
                    System.Console.WriteLine("System Exception: " + ex.Message);
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end SelectPilotByNameAndPoints

        private static void ListResult(IList<Car> result)
        {
            System.Console.WriteLine(result.Count);
            foreach (Car car in result)
            {
                System.Console.WriteLine(car);
            }
        }

        // end ListResult

        private static void ListResult(IList result)
        {
            System.Console.WriteLine(result.Count);
            foreach (object o in result)
            {
                System.Console.WriteLine(o);
            }
        }

        // end ListResult

        private static void ListResult(IEnumerable<Pilot> result)
        {
            System.Console.WriteLine(result.Count<Pilot>());
            foreach (Pilot pilot in result)
            {
                System.Console.WriteLine(pilot);
            }
        }

        // end ListResult


    }
}

