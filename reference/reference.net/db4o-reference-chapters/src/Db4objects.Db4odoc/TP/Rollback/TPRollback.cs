/* Copyright (C) 2004 - 2008 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Diagnostic;
using Db4objects.Db4o.TA;


namespace Db4objects.Db4odoc.TP.Rollback
{
    public class TPCloneExample
    {

        private const string Db4oFileName = "reference.db4o";

        private static IObjectContainer _container = null;

        public static void Main(string[] args)
        {
            StoreCar();
            ModifyAndRollback();
            ModifyRollbackAndCheck();
            ModifyWithRollbackStrategy();
        }
        // end Main

        private static void StoreCar()
        {
            File.Delete(Db4oFileName);
            IObjectContainer container = Database(Db4oFactory.NewConfiguration());
            if (container != null)
            {
                try
                {
                    // create a car
                    Car car = new Car("BMW", new Pilot("Rubens Barrichello", 1));
                    container.Store(car);
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end StoreCar

        private static IConfiguration ConfigureTP()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            // add TP support
            configuration.Add(new TransparentPersistenceSupport());
            return configuration;
        }
        // end ConfigureTP

        private static IConfiguration ConfigureTPForRollback()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            // add TP support and rollback strategy
            configuration.Add(new TransparentPersistenceSupport(
                    new RollbackDeactivateStrategy()));
            return configuration;
        }

        // end ConfigureTPForRollback


        private class RollbackDeactivateStrategy : IRollbackStrategy
        {
            public void Rollback(IObjectContainer container, Object obj)
            {
                container.Ext().Deactivate(obj);
            }
        }

        // end RollbackDeactivateStrategy


        private static void ModifyAndRollback()
        {
            IObjectContainer container = Database(ConfigureTP());
            if (container != null)
            {
                try
                {
                    // create a car
                    Car car = (Car)container.QueryByExample(new Car(null, null))
                            [0];
                    System.Console.WriteLine("Initial car: " + car + "("
                            + container.Ext().GetID(car) + ")");
                    car.Model = "Ferrari";
                    car.Pilot = new Pilot("Michael Schumacher", 123);
                    container.Rollback();
                    System.Console.WriteLine("Car after rollback: " + car + "("
                            + container.Ext().GetID(car) + ")");
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end ModifyAndRollback

        private static void ModifyRollbackAndCheck()
        {
            IObjectContainer container = Database(ConfigureTP());
            if (container != null)
            {
                try
                {
                    // create a car
                    Car car = (Car)container.QueryByExample(new Car(null, null))
                            [0];
                    Pilot pilot = car.Pilot;
                    System.Console.WriteLine("Initial car: " + car + "("
                            + container.Ext().GetID(car) + ")");
                    System.Console.WriteLine("Initial pilot: " + pilot + "("
                            + container.Ext().GetID(pilot) + ")");
                    car.Model = "Ferrari";
                    car.ChangePilot("Michael Schumacher", 123);
                    container.Rollback();
                    container.Deactivate(car, Int32.MaxValue);
                    System.Console.WriteLine("Car after rollback: " + car + "("
                            + container.Ext().GetID(car) + ")");
                    System.Console.WriteLine("Pilot after rollback: " + pilot + "("
                            + container.Ext().GetID(pilot) + ")");
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }

        // end ModifyRollbackAndCheck

        private static void ModifyWithRollbackStrategy()
        {
            IObjectContainer container = Database(ConfigureTPForRollback());
            if (container != null)
            {
                try
                {
                    // create a car
                    Car car = (Car)container.QueryByExample(new Car(null, null))
                            [0];
                    Pilot pilot = car.Pilot;
                    System.Console.WriteLine("Initial car: " + car + "("
                            + container.Ext().GetID(car) + ")");
                    System.Console.WriteLine("Initial pilot: " + pilot + "("
                            + container.Ext().GetID(pilot) + ")");
                    car.Model = "Ferrari";
                    car.ChangePilot("Michael Schumacher", 123);
                    container.Rollback();
                    System.Console.WriteLine("Car after rollback: " + car + "("
                            + container.Ext().GetID(car) + ")");
                    System.Console.WriteLine("Pilot after rollback: " + pilot + "("
                            + container.Ext().GetID(pilot) + ")");
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }
        // end ModifyWithRollbackStrategy

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

    }
}
