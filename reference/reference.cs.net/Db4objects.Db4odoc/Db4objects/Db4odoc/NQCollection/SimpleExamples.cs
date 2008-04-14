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
    class SimpleExamples
    {

        private readonly static string Db4oFileName = Path.Combine(
                               Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),
                               "formula1.db4o");  


        private const int ObjectCount = 10;

        private static IObjectContainer _container = null;

        public static void Main(string[] args)
        {
            StorePilots();
            SelectAllPilots();
            SelectAllPilotsNonGeneric();
            SelectPilot5Points();
            SelectTestPilots();
            SelectPilotsNumberX6();
            SelectTestPilots6PointsMore();
            SelectPilots6To12Points();
            SelectPilotsRandom();
            SelectPilotsEven();
            SelectAnyOnePilot();
            GetSortedPilots();
            GetPilotsSortByNameAndPoints();
            SelectAndChangePilots();
            StoreDuplicates();
            SelectDistinctPilots();
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

        private static void StoreDuplicates()
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
                        pilot = new Pilot("Test Pilot #" + i, i);
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

        // end StoreDuplicates

        private static void SelectAllPilots()
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

        // end SelectAllPilots

        private static void SelectPilot5Points()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Pilot> result = container.Query<Pilot>(delegate(Pilot pilot)
                    {
                        // pilots with 5 points are included in the
                        // result
                        return pilot.Points == 5;
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

        // end selectPilot5Points

        private class NonGenericPredicate : Predicate
        {
            public bool Match(object obj)
            {
                // each Pilot is included in the result
                if (obj is Pilot)
                {
                    return true;
                }
                return false;
            }
        }
        // end NonGenericPredicate 

        private static void SelectAllPilotsNonGeneric()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IObjectSet result = container.Query(new NonGenericPredicate());
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

        // end SelectAllPilotsNonGeneric

        private static void SelectTestPilots()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Pilot> result = container.Query<Pilot>(delegate(Pilot pilot)
                    {
                        // all Pilots containing "Test" in the name
                        // are included in the result
                        return pilot.Name.IndexOf("Test") >= 0;
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

        // end SelectTestPilots

        private static void SelectPilotsNumberX6()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Pilot> result = container.Query<Pilot>(delegate(Pilot pilot)
                    {
                        // all Pilots with the name ending with 6 will
                        // be included
                        return pilot.Name.EndsWith("6");
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

        // end SelectPilotsNumberX6

        private static void SelectTestPilots6PointsMore()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Pilot> result = container.Query<Pilot>(delegate(Pilot pilot)
                    {
                        // all Pilots containing "Test" in the name
                        // and 6 point are included in the result
                        bool b1 = pilot.Name.IndexOf("Test") >= 0;
                        bool b2 = pilot.Points > 6;
                        return b1 && b2;

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

        // end SelectTestPilots6PointsMore

        private static void SelectPilots6To12Points()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Pilot> result = container.Query<Pilot>(delegate(Pilot pilot)
                    {
                        // all Pilots having 6 to 12 point are
                        // included in the result
                        return ((pilot.Points >= 6) && (pilot.Points <= 12));

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

        // end SelectPilots6To12Points

        private class RandomPredicate : Db4objects.Db4o.Query.Predicate
        {
            private IList randomArray = null;

            private IList GetRandomArray()
            {
                if (randomArray == null)
                {
                    Random rand = new Random();
                    randomArray = new ArrayList();
                    for (int i = 0; i < 10; i++)
                    {
                        int random = (int)(rand.Next(10));
                        randomArray.Add(random);
                    }
                }
                return randomArray;
            }

            public bool Match(Pilot pilot)
            {
                // all Pilots having points in the values of
                // the randomArray
                return GetRandomArray().Contains(pilot.Points);
            }
        }
        // end RandomPredicate

        private static void SelectPilotsRandom()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IObjectSet result = container.Query(new RandomPredicate());
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

        // end SelectPilotsRandom

        private static void SelectPilotsEven()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Pilot> result = container.Query<Pilot>(delegate(Pilot pilot)
                    {
                        // all Pilots having even points
                        return pilot.Points % 2 == 0;
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

        // end SelectPilotsEven

        private class AnyPilotPredicate : Predicate
        {
            bool selected = false;

            public bool Match(Pilot pilot)
            {
                // return only first result (first result can
                // be any value from the resultset)
                if (!selected)
                {
                    selected = true;
                    return selected;
                }
                else
                {
                    return !selected;
                }

            }
        }
        // end AnyPilotPredicate

        private static void SelectAnyOnePilot()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IObjectSet result = container.Query(new AnyPilotPredicate());
                    SimpleExamples.ListResult(result);
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

        // end SelectAnyOnePilot

        public static void GetSortedPilots()
        {
            IObjectContainer container = Database();
            try
            {
                IList<Pilot> result = container.Query<Pilot>(delegate(Pilot pilot)
                {
                    return true;
                },
                    // sort by points
                    delegate(Pilot p1, Pilot p2)
                    {
                        return p2.Points - p1.Points;
                    });
                ListResult(result);
            }
            finally
            {
                CloseDatabase();
            }
        }
        // end GetSortedPilots

        public static void GetPilotsSortByNameAndPoints()
        {
            IObjectContainer container = Database();
            try
            {
                IList<Pilot> result = container.Query<Pilot>(delegate(Pilot pilot)
                {
                    return true;

                }, new System.Comparison<Pilot>(
                    // sort by name then by points: descending
                    delegate(Pilot p1, Pilot p2)
                    {
                        int compareResult = p1.Name.CompareTo(p2.Name);
                        if (compareResult == 0)
                        {
                            return p1.Points - p2.Points;
                        }
                        else
                        {
                            return -compareResult;
                        }

                    }));
                ListResult(result);
            }
            finally
            {
                CloseDatabase();
            }
        }

        // end GetPilotsSortByNameAndPoints

        public static void GetPilotsSortWithComparator()
        {
            IObjectContainer container = Database();
            try
            {
                IList <Pilot> result = container.Query<Pilot>(delegate(Pilot pilot)
                {
                    return true;

                }, new PilotComparator());
                ListResult(result);
            }
            finally
            {
                CloseDatabase();
            }
        }

        // end GetPilotsSortWithComparator

        private class PilotComparator : IComparer<Pilot>
        {
            public int Compare(Pilot p1, Pilot p2)
            {
                int result = p1.Name.CompareTo(p2.Name);
                if (result == 0)
                {
                    return p1.Points - p2.Points;
                }
                else
                {
                    return -result;
                }
            }
        }

        // end PilotComparator



        private class DistinctPilotsPredicate : Predicate
        {
            public Dictionary<Pilot, object> uniqueResult = new Dictionary<Pilot, object>();

            public bool Match(Pilot pilot)
            {
                // each Pilot is included in the result
                uniqueResult.Add(pilot, null);
                return false;
            }
        }
        // end DistinctPilotsPredicate

        private static void SelectDistinctPilots()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    DistinctPilotsPredicate predicate = new DistinctPilotsPredicate();
                    IObjectSet result = container.Query(predicate);
                    ListResult(predicate.uniqueResult);
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

        // end SelectDistinctPilots

        private static void SelectAndChangePilots()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Pilot> result = container.Query<Pilot>(delegate(Pilot pilot)
                    {
                        // Add ranking to the pilots during the query.
                        // Note: pilot records in the database won't
                        // be changed!!!
                        if (pilot.Points <= 5)
                        {
                            pilot.Name = pilot.Name + ": weak";
                        }
                        else if (pilot.Points > 5
                                && pilot.Points <= 15)
                        {
                            pilot.Name = pilot.Name + ": average";
                        }
                        else if (pilot.Points > 15)
                        {
                            pilot.Name = pilot.Name + ": strong";
                        }
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

        // end SelectAndChangePilots

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

        private static void ListResult(Dictionary<Pilot, object> result)
        {
            System.Console.WriteLine(result.Count);
            foreach (KeyValuePair<Pilot, object> kvp in result)
            {
                Console.WriteLine(kvp.Key);
            }
        }
        // end ListResult

    }
}

