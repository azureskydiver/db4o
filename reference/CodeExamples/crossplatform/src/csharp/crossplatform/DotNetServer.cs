using System;
using System.IO;
using System.Threading;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.CS;
using Db4objects.Db4o.CS.Config;

namespace Db4odoc.CrossPlatform.CrossPlatform
{
    /// <summary>
    /// A .NET server which also can serve Java clients
    /// </summary>
    public class DotNetServer
    {
        private const string DatabaseFileName = "database.db4o";

        public static void Main(string[] args) {
            CleanUp();

            IServerConfiguration configuration = Db4oClientServer.NewServerConfiguration();
            // #example: Add Java support 
            configuration.Common.Add(new JavaSupport());
            // #end example
            using(IObjectServer server = Db4oClientServer.OpenServer(configuration, 
                DatabaseFileName,1337))
            {
                server.GrantAccess("sa", "sa");

                Console.WriteLine("Server is running. Press any key to close it...");
                while (!Console.KeyAvailable)
                {
                    Thread.Sleep(1000);
                }
                Console.WriteLine("Closing..");
            }

        }

        private static void CleanUp()
        {
            File.Delete(DatabaseFileName);
        }
    }
}