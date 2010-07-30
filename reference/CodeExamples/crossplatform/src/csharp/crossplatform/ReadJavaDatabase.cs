using System;
using System.Threading;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Linq;

namespace Db4odoc.CrossPlatform.CrossPlatform
{
    public class ReadJavaDatabase
    {
        private const string DatabaseFile = "C:\\temp\\database.db4o";

        public static void Main(string[] args)
        {
            IEmbeddedConfiguration configuration = Db4oEmbedded.NewConfiguration();
            configuration.Common.Add(new JavaSupport());

            configuration.Common.AddAlias(
                new WildcardAlias("Db4odoc.CrossPlatform.CrossPlatform.*, Db4odoc.CrossPlatform",
                                  "com.db4odoc.crossplatform.*"));

            using (IObjectContainer container = Db4oEmbedded.OpenFile(configuration, DatabaseFile))
            {
                container.Store(new Person("Joe", "Average"));
                container.Store(new Person("Joe", "Johnson"));
                container.Store(new Person("Noel", "Exceptional"));

                var persons = from Person p in container
                              where p.Firstname == "Joe"
                              select p;
                foreach (var person in persons)
                {
                    Console.Out.WriteLine(person);
                }
            }
            Thread.Sleep(5000);
        }
    }
}