/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.QBE
{
    class QBEExample
    {
        private const string Db4oFileName = "reference.db4o";

        private const int ObjectCount = 10;

        private static IObjectContainer _container = null;

        public static void Main(string[] args)
        {
            File.Delete(Db4oFileName);
         
            Test();
            Test1();
            Test2();
            Test3();
            Test4();
        }
        // end Main

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

        private static void Test()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    Pilot pilot = new Pilot("Kimi Raikonnen", 100);
                    container.Set(pilot);
                    container.Commit();
                    IObjectSet result = container.Get(new Pilot("Kimi Raikonnen", 100));
                    ListResult(result);
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

        // end Test

        private static void Test1()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    // Pilot1 contains re-initialisation in the constructor
                    Pilot1 pilot = new Pilot1("Kimi Raikonnen", 100);
                    container.Set(pilot);
                    container.Commit();
                    // QBE returns result with wrong points
                    IObjectSet result = container.Get(new Pilot1("Kimi Raikonnen", 100));
                    System.Console.WriteLine("Test QBE on class with member re-initialization in constructor");
                    ListResult(result);
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

        // end Test1

        private static void Test2()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    // Pilot1Derived derives the constructor with re-initialisation
                    Pilot1Derived pilot = new Pilot1Derived("Kimi Raikonnen", 100);
                    container.Set(pilot);
                    container.Commit();
                    // QBE returns result with wrong points
                    IObjectSet result = container.Get(new Pilot1Derived("Kimi Raikonnen", 100));
                    System.Console.WriteLine("Test QBE on class with member re-initialization in ancestor constructor");
                    ListResult(result);
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

        // end Test2

        private static void Test3()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    // Pilot2 uses static value to initializ points 
                    Pilot2 pilot = new Pilot2("Kimi Raikonnen", 100);
                    container.Set(pilot);
                    container.Commit();
                    // QBE returns result with wrong points
                    IObjectSet result = container.Get(new Pilot2("Kimi Raikonnen", 100));
                    System.Console.WriteLine("Test QBE on class with member initialization using static value");
                    ListResult(result);
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

        // end Test3

        private static void Test4()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    // Pilot2Derived is derived from class with initialization of points member using static value
                    Pilot2Derived pilot = new Pilot2Derived("Kimi Raikonnen", 100);
                    container.Set(pilot);
                    container.Commit();
                    // QBE returns result with wrong points
                    IObjectSet result = container.Get(new Pilot2Derived("Kimi Raikonnen", 100));
                    System.Console.WriteLine("Test QBE on class derived from a class with member initialization using static member");
                    ListResult(result);
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

        // end Test4

        private static void ListResult(IObjectSet result)
        {
            System.Console.WriteLine(result.Count);
            foreach (object obj in result)
            {
                System.Console.WriteLine(obj);
            }
        }

        // end ListResult
    }
}
