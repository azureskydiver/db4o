using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;

namespace Db4objects.Db4odoc.Aliases
{
    class AliasExample
    {
        private static readonly string YapFileName = "formula1.yap";
        private static TypeAlias tAlias;

        public static void Main(string[] args)
        {
            ConfigureClassAlias();
            SaveDrivers();
            RemoveClassAlias();
            GetPilots();
            SavePilots();
            ConfigureAlias();
            GetObjectsWithAlias();
        }
        // end Main

        public static void ConfigureClassAlias()
        {
            // create a new alias
            tAlias = new TypeAlias("Db4objects.Db4odoc.Aliases.Pilot, Db4objects.Db4odoc", "Db4objects.Db4odoc.Aliases.Driver, Db4objects.Db4odoc");
            // add the alias to the db4o configuration 
            Db4oFactory.Configure().AddAlias(tAlias);
            // check how does the alias resolve
            Console.WriteLine("Stored name for Db4objects.Db4odoc.Aliases.Driver: " + tAlias.ResolveRuntimeName("Db4objects.Db4odoc.Aliases.Driver, Db4objects.Db4odoc"));
            Console.WriteLine("Runtime name for Db4objects.Db4odoc.Aliases.Pilot: " + tAlias.ResolveStoredName("Db4objects.Db4odoc.Aliases.Pilot, Db4objects.Db4odoc"));
        }
        // end ConfigureClassAlias


        public static void RemoveClassAlias()
        {
            Db4oFactory.Configure().RemoveAlias(tAlias);
        }
        // end RemoveClassAlias

        public static void SaveDrivers()
        {
            File.Delete(YapFileName);
            IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
            try
            {
                Driver driver = new Driver("David Barrichello", 99);
                db.Set(driver);
                driver = new Driver("Finn Kimi Raikkonen", 100);
                db.Set(driver);
            }
            finally
            {
                db.Close();
            }
        }
        // end SaveDrivers

        public static void SavePilots()
        {
            File.Delete(YapFileName);
            IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
            try
            {
                Pilot pilot = new Pilot("David Barrichello", 99);
                db.Set(pilot);
                pilot = new Pilot("Finn Kimi Raikkonen", 100);
                db.Set(pilot);
            }
            finally
            {
                db.Close();
            }
        }
        // end SavePilots

        public static void GetPilots()
        {
            IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
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

        public static void GetObjectsWithAlias()
        {
            IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
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

        public static void ListResult(IObjectSet result)
        {
            Console.WriteLine(result.Size());
            while (result.HasNext())
            {
                Console.WriteLine(result.Next());
            }
        }
        // end ListResult

        public static void ConfigureAlias()
        {
            // Db4objects.Db4odoc.Aliases.* - namespace for the classes saved in the database
            // Db4objects.Db4odoc.Aliases.NewAlias.* - runtime namespace
            WildcardAlias wAlias = new WildcardAlias("Db4objects.Db4odoc.Aliases.*", "Db4objects.Db4odoc.Aliases.NewAlias.*");
            Db4oFactory.Configure().AddAlias(wAlias);
            Console.WriteLine("Stored name for Db4objects.Db4odoc.Aliases.NewAlias.Pilot: " + wAlias.ResolveRuntimeName("Db4objects.Db4odoc.Aliases.NewAlias.Pilot"));
            Console.WriteLine("Runtime name for Db4objects.Db4odoc.Aliases.Pilot: " + wAlias.ResolveStoredName("Db4objects.Db4odoc.Aliases.Pilot"));
        }
        // end ConfigureAlias

    }
}
