/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

using System;
using System.IO;
using System.Collections.Generic;
using System.Collections;

using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.NQCollection
{
    class ParameterizedExamples
    {

        private const string Db4oFileName = "reference.db4o";

        private const int ObjectCount = 10;

        private static IObjectContainer _container = null;


        public static void Main(string[] args)
        {
            StorePilots();
            GetTestPilots();
            GetProfessionalPilots();
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

        private static void StorePilots()
        {
            File.Delete(Db4oFileName);
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    Pilot pilot;
                    for (int i = 0; i < ObjectCount; i++)
                    {
                        pilot = new Pilot("Test Pilot #" + i, i);
                        container.Set(pilot);
                    }
                    for (int i = 0; i < ObjectCount; i++)
                    {
                        pilot = new Pilot("Professional Pilot #" + (i + 10), i + 10);
                        container.Set(pilot);
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

        // end storePilots

        private class PilotNamePredicate : Predicate
        {
            private string startsWith;

            public PilotNamePredicate(string startsWith)
            {
                this.startsWith = startsWith;
            }

            public bool Match(Pilot pilot)
            {
                return pilot.Name.StartsWith(startsWith);
            }
        }
        // end PilotNamePredicate

        private static void GetTestPilots()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IObjectSet result = container.Query(new PilotNamePredicate("Test"));
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

        // end GetTestPilots

        private static IList<Pilot> ByNameBeginning(string startsWith)
        {
            return Database().Query<Pilot>(delegate(Pilot pilot)
            {
                return pilot.Name.StartsWith(startsWith);
            });
        }
        // end ByNameBeginning

        private static void GetProfessionalPilots()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Pilot> result = ByNameBeginning("Professional");
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

        // end GetProfessionalPilots


        private static void ListResult(IList<Pilot> result)
        {
            System.Console.WriteLine(result.Count);
            foreach (Pilot pilot in result)
            {
                System.Console.WriteLine(pilot);
            }
        }

        // end ListResult

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
