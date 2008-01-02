/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;

using Db4objects.Db4o;
using Db4objects.Db4o.Config;

namespace Db4objects.Db4odoc.ClientServer.BatchMode
{
    class BatchExample
    {
        private const string Db4oFileName = "reference.db4o";
        private const int Port = 0xdb40;
        private const string User = "db4o";
        private const string Password = "db4o";
        private const string Host = "localhost";

        private const int NoOfObjects = 1000;

        public static void Main(string[] Args)
        {
            IObjectServer db4oServer = Db4oFactory.OpenServer(Db4oFileName, Port);
            try
            {
                db4oServer.GrantAccess(User, Password);
                IConfiguration configuration = Db4oFactory.NewConfiguration();
                FillUpDb(configuration);
                configuration.ClientServer().BatchMessages(true);
                FillUpDb(configuration);
            }
            finally
            {
                db4oServer.Close();
            }
        }
        // end Main


        private static void FillUpDb(IConfiguration configuration)
        {
            IObjectContainer container = Db4oFactory.OpenClient(configuration, Host, Port, User,
                        Password);
            try
            {
                Console.WriteLine("Testing inserts");
                DateTime dt1 = DateTime.UtcNow;
                for (int i = 0; i < NoOfObjects; i++)
                {
                    Pilot pilot = new Pilot("pilot #" + i, i);
                    container.Set(pilot);
                }
                DateTime dt2 = DateTime.UtcNow;
                TimeSpan diff = dt2 - dt1;
                Console.WriteLine("Operation time: " + diff.TotalMilliseconds + " ms.");
            }
            finally
            {
                container.Close();
            }
        }
        // end FillUpDb

    }
}
