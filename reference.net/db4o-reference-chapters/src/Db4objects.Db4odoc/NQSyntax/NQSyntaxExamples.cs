/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.NQSyntax
{
    class NQSyntaxExamples
    {
        private const string Db4oFileName = "reference.db4o";

        private const int ObjectCount = 10;

        private static IObjectContainer _container = null;

        public static void Main(string[] args)
        {
            StorePilots();
            QuerySyntax1();
            QuerySyntax2();
            QuerySyntax3();
            QuerySyntax4();
            QuerySyntax5();
            QuerySyntax6();
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

        // end StorePilots

        private static void QuerySyntax1()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Pilot> result = container.Query<Pilot>(typeof(Pilot));
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
        // end QuerySyntax1

        private static void QuerySyntax2()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Pilot> result = container.Query<Pilot>();
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
        // end QuerySyntax2

        private class PilotComparer : IComparer<Pilot>
        {
            public int Compare(Pilot pilot1, Pilot pilot2)
            {
                return pilot1.Points - pilot2.Points;
            }
        }
        // end PilotComparer

        private static void QuerySyntax3()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Pilot> result = container.Query<Pilot>(new PilotComparer());
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
        // end QuerySyntax3


        private static void QuerySyntax4()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Pilot> result = container.Query<Pilot>(delegate(Pilot pilot)
                    {
                        // each Pilot is included in the result
                        return true;
                    });
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
        // end QuerySyntax4

        private static void QuerySyntax5()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Pilot> result = container.Query<Pilot>(delegate(Pilot pilot)
                    {
                        return pilot.Name.StartsWith("Test");
                    },
                    delegate(Pilot pilot1, Pilot pilot2)
                    {
                        return pilot1.Points - pilot2.Points;
                    });
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
        // end QuerySyntax5

        private static void QuerySyntax6()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Pilot> result = container.Query<Pilot>(delegate(Pilot pilot)
                    {
                        return pilot.Name.StartsWith("Test");
                    },
                    new PilotComparer());
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
        // end QuerySyntax6

        private class PilotPredicate : Db4objects.Db4o.Query.Predicate
        {
            public bool Match(object obj)
            {
                if (obj is Pilot)
                {
                    return true;
                }
                return false;
            }
        }
        // end PilotPredicate

        private class PilotPointsComparer : IComparer
        {
            public int Compare(object pilot1, object pilot2)
            {
                return ((Pilot)pilot1).Points - ((Pilot)pilot2).Points;
            }
        }
        // end PilotPointsComparer

        private static void QuerySyntax7()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IObjectSet result = container.Query(new PilotPredicate(), new PilotPointsComparer());
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
        // end QuerySyntax7

        private static void ListResult(IList<Pilot> result)
        {
            System.Console.WriteLine(result.Count);
            for (int i = 0; i < result.Count; i++)
            {
                System.Console.WriteLine(result[i]);
            }
        }

        // end ListResult

        private static void ListResult(IObjectSet result)
        {
            System.Console.WriteLine(result.Count);
            for (int i = 0; i < result.Count; i++)
            {
                System.Console.WriteLine(result[i]);
            }
        }

        // end ListResult
    }
}
