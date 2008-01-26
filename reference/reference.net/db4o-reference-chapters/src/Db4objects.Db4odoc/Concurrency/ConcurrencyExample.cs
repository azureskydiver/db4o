/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using System.Threading;

using Db4objects.Db4o;
using Db4objects.Db4o.Config;

namespace Db4objects.Db4odoc.Concurrency
{
    class ConcurrencyExample
    {
        private const string Db4oFileName = "reference.db4o";
        private static IObjectServer _server;

        public static void Main(string[] args)
        {
            Connect();
            try
            {
                SavePilots();
                ModifyPilotsOptimistic();
                ModifyPilotsPessimistic();
            }
            finally
            {
                Disconnect();
            }
        }
        // end Main

        private static void Connect()
        {
            if (_server == null)
            {
                File.Delete(Db4oFileName);
                IConfiguration configuration = Db4oFactory.NewConfiguration();
                configuration.GenerateVersionNumbers(ConfigScope.Globally);
                _server = Db4oFactory.OpenServer(configuration, Db4oFileName, 0);
            }
        }
        // end Connect

        private static void Disconnect()
        {
            _server.Close();
        }
        // end Disconnect

        private static void SavePilots()
        {
            IObjectContainer db = _server.OpenClient();
            try
            {
                Pilot pilot = new Pilot("Kimi Raikkonnen", 0);
                db.Set(pilot);
                pilot = new Pilot("David Barrichello", 0);
                db.Set(pilot);
                pilot = new Pilot("David Coulthard", 0);
                db.Set(pilot);
            }
            finally
            {
                db.Close();
            }
        }
        // end SavePilots

        private static void ModifyPilotsOptimistic()
        {
            Console.WriteLine("Optimistic locking example");
            // create threads for concurrent modifications
            OptimisticThread t1 = new OptimisticThread("t1: ", _server);
            OptimisticThread t2 = new OptimisticThread("t2: ", _server);
            Thread thread1 = new Thread(new ThreadStart(t1.Run));
            Thread thread2 = new Thread(new ThreadStart(t2.Run));
            RunThreads(thread1, thread2);
        }
        // end ModifyPilotsOptimistic

        private static void ModifyPilotsPessimistic()
        {
            Console.WriteLine();
            Console.WriteLine("Pessimistic locking example");
            // create threads for concurrent modifications
            PessimisticThread t1 = new PessimisticThread("t1: ", _server);
            PessimisticThread t2 = new PessimisticThread("t2: ", _server);
            Thread thread1 = new Thread(new ThreadStart(t1.Run));
            Thread thread2 = new Thread(new ThreadStart(t2.Run));
            RunThreads(thread1, thread2);
        }
        // end ModifyPilotsPessimistic

        private static void RunThreads(Thread thread1, Thread thread2)
        {
            thread1.Start();
            thread2.Start();

            bool thread1IsAlive = true;
            bool thread2IsAlive = true;

            do
            {
                if (thread1IsAlive && !thread1.IsAlive)
                {
                    thread1IsAlive = false;
                    Console.WriteLine("t1 is dead.");
                }

                if (thread2IsAlive && !thread2.IsAlive)
                {
                    thread2IsAlive = false;
                    Console.WriteLine("t2 is dead.");
                }
            } while (thread1IsAlive || thread2IsAlive);
        }
        // end RunThreads
    }
}
