using System;
using System.Text;
using System.IO;
using System.Collections.Generic;
using System.Collections;
using System.Linq;

using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Linq;

namespace Db4objects.Db4odoc.LinqCollection
{
    class LinqCollection
    {
        private readonly static string Db4oFileName = Path.Combine(
                       Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),
                       "reference.db4o");

        private const int ObjectCount = 20;

        private static IObjectContainer _container = null;


        public static void Main(string[] args)
        {
            //StoreObjects();
            //SelectJoin();
            //SelectFromSelection();
            //SelectEverythingByName();
            //SelectClone();
            //SelectPilotByNameAndPoints();
            //SelectByNameAndPoints();
            //SelectOrdered();
            StoreForSorting();
            SelectGroupByName();
            //SelectComplexOrdered();
            //StorePilots();
            //SelectWithModifiedResult();
            //SelectAggregate();
            //SelectAverage();
            //SelectAny();
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
                        container.Store(pilot);
                    }
                    for (int i = 0; i < ObjectCount; i++)
                    {
                        pilot = new Pilot("Professional Pilot #" + (i + 10), i + 10);
                        container.Store(pilot);
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

        private static void StoreForSorting()
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
                        container.Store(pilot);
                    }
                    for (int i = 0; i < ObjectCount; i++)
                    {
                        pilot = new Pilot("Test Pilot #" + i, (i + 10));
                        container.Store(pilot);
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
        // end StoreForSorting

        
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

        // end SelectEverythingByName


        private static void SelectOrdered()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    var result = from Pilot p in container
                                 where p.Points < 15
                                 orderby p.Name descending
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

        // end SelectOrdered


        private static void SelectComplexOrdered()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    var result = from Pilot p in container
                                 orderby p.Name descending,
                                 p.Points ascending
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

        // end SelectComplexOrdered

        private static void SelectGroupByName()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    var result = (from Pilot p in container
                                  orderby p.Points descending
                                  select p).GroupBy(value => value.Points, value => new {value.Name, value.Points});
                    foreach (var group in result)
                    {
                        Console.WriteLine("Pilots with {0} points:", group.Key);

                        foreach (var tuple in group)
                            Console.WriteLine("  {0}", tuple);
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

        // end SelectGroupByName

        private static void SelectPilotByNameAndPoints()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IEnumerable<Pilot> result = from Pilot p in container
                                                where p.Name.StartsWith("Test") && p.Points > 12
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

        private static void SelectByNameAndPoints()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    var result = from Pilot p in container
                                 where p.Name.StartsWith("Test") && p.Points > 12
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
        // end SelectByNameAndPoints

        private static void SelectClone()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    var result = from Pilot p in container
                                 where p.Name.StartsWith("Test") && p.Points > 12
                                 select new Pilot(p.Name, p.Points);

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

        // end SelectClone

        private static void SelectFromSelection()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    var allObjects = from object o in container select o;
                    var listObjects = allObjects.ToList();
                    var pilots = from object p in listObjects 
                                 where p.GetType().FullName.Equals("Db4objects.Db4odoc.LinqCollection.Pilot") 
                                 && ((Pilot)p).Points > 25
                                 select (Pilot)p;
                    ListResult(pilots);
                    var cars = from object car in allObjects
                               where car.GetType().FullName.Equals("Db4objects.Db4odoc.LinqCollection.Car")
                               && pilots.Contains(((Car)car).Pilot) 
                               select (Car)car;
                    ListResult(cars);
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

        // end SelectFromSelection

        private static void SelectWithModifiedResult()
        {
            IObjectContainer container = Database();
            int maxPoints = 100;
            if (container != null)
            {
                try
                {
                    /*Select percentage*/
                    var result = from Pilot p in container
                                 where p.Name.StartsWith("Test")
                                 select String.Format("{0}: {1}%", p.Name, (p.Points * 100 / maxPoints));

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
        // end SelectWithModifiedResult

        private static void SelectAggregate()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    /*Select pilot names separated by semicolon*/
                    var result = (from Pilot p in container
                                  where p.Name.StartsWith("Test")
                                  select p.Name).Aggregate(new StringBuilder(), 
                                  (acc, value) => acc.AppendFormat("{0}; ", value));

                    System.Console.WriteLine(result);
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
        // end SelectAggregate

        private static void SelectAverage()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    /*Find the average of pilot points*/
                    var result = (from Pilot p in container
                                  where p.Name.StartsWith("Test")
                                  select p.Points).Average();

                    System.Console.WriteLine(result);
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
        // end SelectAverage

        private static void SelectAny()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    /*Checks if query returns any results*/
                    var result = (from Pilot p in container
                                  where p.Name.EndsWith("Test")
                                  select p).Any();

                    System.Console.WriteLine("The query returns any results: " + result);
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
        // end SelectAny

        private static void SelectJoin()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    var result = from Pilot p in container
                                  from Car c in container
                                  where p.Points > 25
                                  && c.Pilot.Equals(p) 
                                  select c;

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
        // end SelectJoin


        private static void ListResult<T>(IEnumerable<T> result)
        {
            System.Console.WriteLine(result.Count<T>());
            foreach (T t in result)
            {
                System.Console.WriteLine(t);
            }
        }

        // end ListResult

        private static void ListResult(IEnumerable result)
        {
            foreach (object o in result)
            {
                System.Console.WriteLine(o);
            }
        }

        // end ListResult

    }
}
