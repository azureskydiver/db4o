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
    class CalculationExamples
    {

        private const string Db4oFileName = "reference.db4o";

        private const int ObjectCount = 10;

        private static IObjectContainer _container = null;

        public static void Main(string[] args)
        {
            StorePilots();
            SumPilotPoints();
            SelectMinPointsPilot();
            AveragePilotPoints();
            CountSubGroups();
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

        private static void SumPilotPoints()
        {
            IObjectContainer container = Database();

            if (container != null)
            {
                try
                {
                    SumPredicate sumPredicate = new SumPredicate();
                    IObjectSet result = container.Query(sumPredicate);
                    ListResult(result);
                    System.Console.WriteLine("Sum of pilots points: " + sumPredicate.sum);
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

        // end SumPilotPoints

        private class SumPredicate : Predicate
        {
            public int sum = 0;

            public bool Match(Pilot pilot)
            {
                // return all pilots
                sum += pilot.Points;
                return true;
            }
        }

        // end SumPredicate

        private static void SelectMinPointsPilot()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Pilot> result = container.Query<Pilot>(delegate(Pilot pilot)
                    {
                        // return all pilots
                        return true;

                    }, new System.Comparison<Pilot>(delegate(Pilot p1, Pilot p2)
                    {
                        // sort by points then by name
                        return p1.Points - p2.Points;
                    }
                    ));
                    if (result.Count > 0)
                    {
                        System.Console.WriteLine("The min points result is: "
                                + result[0]);
                    }
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

        // end SelectMinPointsPilot

        private static void AveragePilotPoints()
        {
            IObjectContainer container = Database();

            if (container != null)
            {
                try
                {
                    AveragePredicate averagePredicate = new AveragePredicate();
                    IObjectSet result = container.Query(averagePredicate);
                    if (averagePredicate.count > 0)
                    {
                        System.Console.WriteLine("Average points for professional pilots: "
                                        + averagePredicate.sum
                                        / averagePredicate.count);
                    }
                    else
                    {
                        System.Console.WriteLine("No results");
                    }
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

        // end AveragePilotPoints

        private class AveragePredicate : Predicate
        {
            public int sum = 0;

            public int count = 0;

            public bool Match(Pilot pilot)
            {
                // return professional pilots
                if (pilot.Name.StartsWith("Professional"))
                {
                    sum += pilot.Points;
                    count++;
                    return true;
                }
                return false;
            }
        }

        // end AveragePredicate

        private class CountPredicate : Predicate
        {

            public Hashtable countTable = new Hashtable();

            public bool Match(Pilot pilot)
            {
                // return all Professional and Test pilots and count in
                // each category
                String[] keywords = { "Professional", "Test" };
                foreach (string keyword in keywords)
                {
                    if (pilot.Name.StartsWith(keyword))
                    {
                        if (countTable.ContainsKey(keyword))
                        {
                            countTable[keyword] = ((int)countTable[keyword]) + 1;
                        }
                        else
                        {
                            countTable.Add(keyword, 1);
                        }
                        return true;
                    }
                }
                return false;
            }
        }

        // end CountPredicate

        private static void CountSubGroups()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    CountPredicate predicate = new CountPredicate();
                    IObjectSet result = container.Query(predicate);
                    ListResult(result);
                    IDictionaryEnumerator enumerator = predicate.countTable.GetEnumerator();
                    while (enumerator.MoveNext())
                    {
                        System.Console.WriteLine(enumerator.Key + ": " + enumerator.Value);
                    }
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

        // end CountSubGroups

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
