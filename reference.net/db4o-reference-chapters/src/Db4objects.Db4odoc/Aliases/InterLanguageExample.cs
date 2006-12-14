using System;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Aliases
{
    class InterLanguageExample
    {
        private static readonly string YapFileName = "formula1.yap";

        public static void Main(string[] args)
        {
            ConfigureAlias();
            GetObjects();
        }
        // end Main

        public static void ConfigureAlias()
        {
            Db4oFactory.Configure().AddAlias(new WildcardAlias("com.db4odoc.aliases.*", "Db4objects.Db4odoc.Aliases.*, Db4objects.Db4odoc"));
        }
        // end ConfigureAlias

        public static void GetObjects()
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
        // end GetObjects

        public static void ListResult(IObjectSet result)
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
