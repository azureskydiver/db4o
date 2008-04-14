/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

using System;
using System.IO;
using System.Collections.Generic;

using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.NQCollection
{
    class MultiExamples
    {

        private const string Db4oFileName = "reference.db4o";

        private const int ObjectCount = 10;

        private static IObjectContainer _container = null;

        public static void Main(string[] args)
        {
            StorePilotsAndTrainees();
            SelectPilotsAndTrainees();
            StoreCars();
            SelectPilotsInRange();
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

        private static void StorePilotsAndTrainees()
        {
            File.Delete(Db4oFileName);
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    Pilot pilot;
                    Trainee trainee;
                    for (int i = 0; i < ObjectCount; i++)
                    {
                        pilot = new Pilot("Professional Pilot #" + i, i);
                        trainee = new Trainee("Trainee #" + i, pilot);
                        container.Set(trainee);
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

        // end StorePilotsAndTrainees

        private static void StoreCars()
        {
            File.Delete(Db4oFileName);
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    Car car;
                    for (int i = 0; i < ObjectCount; i++)
                    {
                        car = new Car("BMW", new Pilot("Test Pilot #" + i, i));
                        container.Set(car);
                    }
                    for (int i = 0; i < ObjectCount; i++)
                    {
                        car = new Car("Ferrari", new Pilot("Professional Pilot #"
                                + (i + 10), (i + 10)));
                        container.Set(car);
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

        // end StoreCars

        private static void SelectPilotsAndTrainees()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IList<Person> result = container.Query<Person>(delegate(Person person)
                    {
                        // all persons
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

        // end SelectPilotsAndTrainees

        private class CarPilotPredicate : Predicate
        {
            private IList<Pilot> pilots = null;

            private IList<Pilot> GetPilotsList()
            {
                if (pilots == null)
                {
                    pilots = Database().Query<Pilot>(
                            delegate(Pilot pilot)
                            {
                                return pilot.Name.StartsWith("Test");
                            });
                }
                return pilots;
            }

            public bool Match(Car car)
            {
                // all Cars that have pilot field in the
                // Pilots array
                return GetPilotsList().Contains(car.Pilot);
            }
        }
        // end CarPilotPredicate

        private static void SelectPilotsInRange()
        {
            IObjectContainer container = Database();
            if (container != null)
            {
                try
                {
                    IObjectSet result = container.Query(new CarPilotPredicate());
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

        // end SelectPilotsInRange

        private static void ListResult(IList<Person> result)
        {
            System.Console.WriteLine(result.Count);
            foreach (Person person in result)
            {
                System.Console.WriteLine(person);
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
