/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Aliases
{
    class InterLanguageExample
    {
        private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            GetObjects(ConfigureAlias());
        }
        // end Main

        private static IConfiguration ConfigureAlias()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.AddAlias(new WildcardAlias("com.db4odoc.aliases.*", "Db4objects.Db4odoc.Aliases.*, Db4objects.Db4odoc"));
            configuration.AddAlias(new TypeAlias("com.db4o.ext.Db4oDatabase", "Db4objects.Db4o.Ext.Db4oDatabase, Db4objects.Db4o"));
            return configuration;
        }
        // end ConfigureAlias

        private static void GetObjects(IConfiguration configuration)
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
        // end GetObjects

        private static void ListResult(IObjectSet result)
        {
            Console.WriteLine(result.Count);
            foreach (object item in result)
            {
                Console.WriteLine(item);
            }
        }
        // end ListResult
    }
}
