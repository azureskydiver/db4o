/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.ClientServer.BatchMode
{
    class BatchExample
    {
        private const string FILE = "test.yap";
        private const int PORT = 0xdb40;
        private const string USER = "db4o";
        private const string PASS = "db4o";
        private const string HOST = "localhost";

        private const int NO_OF_OBJECTS = 1000;

        public static void Main(string[] Args)
        {
            IObjectServer db4oServer = Db4oFactory.OpenServer(FILE, PORT);
            try
            {
                db4oServer.GrantAccess(USER, PASS);
                IObjectContainer container = Db4oFactory.OpenClient(HOST, PORT, USER,
                        PASS);
                try
                {
                    FillUpDb(container);
                    container.Ext().Configure().ClientServer().BatchMessages(true);
                    FillUpDb(container);
                }
                finally
                {
                    container.Close();
                }
            }
            finally
            {
                db4oServer.Close();
            }
        }
        // end Main


        private static void FillUpDb(IObjectContainer container)
        {
            Console.WriteLine("Testing inserts");
            DateTime dt1 = DateTime.UtcNow;
            for (int i = 0; i < NO_OF_OBJECTS; i++)
            {
                Pilot pilot = new Pilot("pilot #" + i, i);
                container.Set(pilot);
            }
            DateTime dt2 = DateTime.UtcNow;
            TimeSpan diff = dt2 - dt1;
            Console.WriteLine("Operation time: " + diff.Milliseconds + " ms.");
        }
        // end FillUpDb

    }
}
