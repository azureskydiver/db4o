/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;

namespace Db4objects.Db4odoc.Aliases
{
    class AliasExample
    {
        private static readonly string Db4oFileName = "reference.db4o";
        private static TypeAlias tAlias;

        public static void Main(string[] args)
        {
            IConfiguration configuration = ConfigureClassAlias();
            SaveDrivers(configuration);
            RemoveClassAlias(configuration);
            GetPilots(configuration);
            SavePilots(configuration);
            configuration = ConfigureAlias();
            GetObjectsWithAlias(configuration);
        }
        // end Main

        private static IConfiguration ConfigureClassAlias()
        {
            // create a new alias
            tAlias = new TypeAlias("Db4objects.Db4odoc.Aliases.Pilot, Db4objects.Db4odoc", "Db4objects.Db4odoc.Aliases.Driver, Db4objects.Db4odoc");
            // add the alias to the db4o configuration 
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.AddAlias(tAlias);
            // check how does the alias resolve
            Console.WriteLine("Stored name for Db4objects.Db4odoc.Aliases.Driver: " + tAlias.ResolveRuntimeName("Db4objects.Db4odoc.Aliases.Driver, Db4objects.Db4odoc"));
            Console.WriteLine("Runtime name for Db4objects.Db4odoc.Aliases.Pilot: " + tAlias.ResolveStoredName("Db4objects.Db4odoc.Aliases.Pilot, Db4objects.Db4odoc"));
            return configuration;
        }
        // end ConfigureClassAlias


        private static void RemoveClassAlias(IConfiguration configuration)
        {
            configuration.RemoveAlias(tAlias);
        }
        // end RemoveClassAlias

        private static void SaveDrivers(IConfiguration configuration)
        {
            File.Delete(Db4oFileName);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
            try
            {
                Driver driver = new Driver("David Barrichello", 99);
                db.Set(driver);
                driver = new Driver("Kimi Raikkonen", 100);
                db.Set(driver);
            }
            finally
            {
                db.Close();
            }
        }
        // end SaveDrivers

        private static void SavePilots(IConfiguration configuration)
        {
            File.Delete(Db4oFileName);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
            try
            {
                Pilot pilot = new Pilot("David Barrichello", 99);
                db.Set(pilot);
                pilot = new Pilot("Kimi Raikkonen", 100);
                db.Set(pilot);
            }
            finally
            {
                db.Close();
            }
        }
        // end SavePilots

        private static void GetPilots(IConfiguration configuration)
        {
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
            try
            {
                IObjectSet result = db.Query(typeof(Pilot));
                ListResult(result);
            }
            finally
            {
                db.Close();
            }
        }
        // end GetPilots

        private static void GetObjectsWithAlias(IConfiguration configuration)
        {
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
            try
            {
                IObjectSet result = db.Query(typeof(Db4objects.Db4odoc.Aliases.NewAlias.Pilot));
                ListResult(result);
            }
            finally
            {
                db.Close();
            }
        }
        // end GetObjectsWithAlias

        private static void ListResult(IObjectSet result)
        {
            Console.WriteLine(result.Size());
            while (result.HasNext())
            {
                Console.WriteLine(result.Next());
            }
        }
        // end ListResult

        private static IConfiguration ConfigureAlias()
        {
            // Db4objects.Db4odoc.Aliases.* - namespace for the classes saved in the database
            // Db4objects.Db4odoc.Aliases.NewAlias.* - runtime namespace
            WildcardAlias wAlias = new WildcardAlias("Db4objects.Db4odoc.Aliases.*", "Db4objects.Db4odoc.Aliases.NewAlias.*");
            // add the alias to the configuration
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.AddAlias(wAlias);
            Console.WriteLine("Stored name for Db4objects.Db4odoc.Aliases.NewAlias.Pilot: " + wAlias.ResolveRuntimeName("Db4objects.Db4odoc.Aliases.NewAlias.Pilot"));
            Console.WriteLine("Runtime name for Db4objects.Db4odoc.Aliases.Pilot: " + wAlias.ResolveStoredName("Db4objects.Db4odoc.Aliases.Pilot"));
            return configuration;
        }
        // end ConfigureAlias

    }
}
