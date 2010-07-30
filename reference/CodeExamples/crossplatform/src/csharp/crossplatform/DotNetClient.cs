using System;
using System.Threading;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.CS;
using Db4objects.Db4o.CS.Config;
using Db4objects.Db4o.Linq;

namespace Db4odoc.CrossPlatform.CrossPlatform
{
    /// <summary>
    /// A client in .NET which connects to a Java db4o database.
    /// </summary>
    public class DotNetClient
    {
        public static void Main(string[] args)
        {
            IClientConfiguration configuration = Db4oClientServer.NewClientConfiguration();
            configuration.Common.Add(new JavaSupport());

            // #example: You need to add aliases for your types
            configuration.Common.AddAlias(
                    new WildcardAlias("com.db4odoc.crossplatform.*",
                        "Db4odoc.CrossPlatform.CrossPlatform.*, Db4odoc.CrossPlatform"));
            // #end example

            using (IObjectContainer container = Db4oClientServer.OpenClient(configuration,
                "localhost", 1337, "sa", "sa"))
            {
                container.Store(new Person("Joe","Average"));
                container.Store(new Person("Joe","Johnson"));
                container.Store(new Person("Noel","Exceptional"));

                var persons = from Person p in container
                              where p.Firstname=="Joe"
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