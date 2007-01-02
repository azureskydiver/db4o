/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.Collections.Generic;

using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.NoClasses.Client
{
    class Client
    {
        private const int COUNT = 10;

        public static void Main(string[] args)
        {
            SavePilots();
            GetPilotsQBE();
            GetPilotsSODA();
            GetPilotsNative();
            GetPilotsNativeUnoptimized();
            GetPilotsEvaluation();
            SaveMultiArray();
            GetMultiArray();
        }
        // end Main

        private static void SavePilots()
        {
            Console.WriteLine("Saving Pilot objects without Pilot class on the server");
            IObjectContainer oc = Db4oFactory.OpenClient("localhost", 0xdb40, "db4o", "db4o");
            try
            {
                for (int i = 0; i < COUNT; i++)
                {
                    oc.Set(new Pilot("Pilot #" + i, i));
                }

            }
            finally
            {
                oc.Close();
            }
        }
        // end SavePilots

        private static void GetPilotsQBE()
        {
            Console.WriteLine("Retrieving Pilot objects: QBE");
            IObjectContainer oc = Db4oFactory.OpenClient("localhost", 0xdb40, "db4o", "db4o");
            try
            {
                IObjectSet result = oc.Get(new Pilot(null, 0));
                ListResult(result);
            }
            finally
            {
                oc.Close();
            }
        }
        // end GetPilotsQBE

        private static void GetPilotsSODA()
        {
            Console.WriteLine("Retrieving Pilot objects: SODA");
            IObjectContainer oc = Db4oFactory.OpenClient("localhost", 0xdb40, "db4o", "db4o");
            try
            {
                IQuery query = oc.Query();

                query.Constrain(typeof(Pilot));
                query.Descend("_points").Constrain(5);

                IObjectSet result = query.Execute();
                ListResult(result);
            }
            finally
            {
                oc.Close();
            }
        }
        // end GetPilotsSODA

        private static void GetPilotsEvaluation()
        {
            Console.WriteLine("Retrieving Pilot objects: Evaluation");
            IObjectContainer oc = Db4oFactory.OpenClient("localhost", 0xdb40, "db4o", "db4o");
            try
            {
                IQuery query = oc.Query();

                query.Constrain(typeof(Pilot));
                query.Constrain(new EvenPointsEvaluation());
                IObjectSet result = query.Execute();
                ListResult(result);
            }
            finally
            {
                oc.Close();
            }
        }
        // end GetPilotsEvaluation



        private static void GetPilotsNative()
        {
            Console.WriteLine("Retrieving Pilot objects: Native Query");
            IObjectContainer oc = Db4oFactory.OpenClient("localhost", 0xdb40, "db4o", "db4o");
            try
            {
                IList<Pilot> result = oc.Query<Pilot>(delegate(Pilot pilot)
                {
                    return pilot.Points == 5;
                });
                ListResult(result);
            }
            finally
            {
                oc.Close();
            }
        }
        // end GetPilotsNative

        private static void GetPilotsNativeUnoptimized()
        {
            Console.WriteLine("Retrieving Pilot objects: Native Query Unoptimized");
            IObjectContainer oc = Db4oFactory.OpenClient("localhost", 0xdb40, "db4o", "db4o");
            try
            {
                IList<Pilot> result = oc.Query<Pilot>(delegate(Pilot pilot)
                {
                    return pilot.Points % 2 == 0;
                });
                ListResult(result);
            }
            finally
            {
                oc.Close();
            }
        }
        // end GetPilotsNativeUnoptimized

        private static void SaveMultiArray()
        {
            Console.WriteLine("Testing saving an object with multidimentional array field");
            IObjectContainer oc = Db4oFactory.OpenClient("localhost", 0xdb40, "db4o", "db4o");
            try
            {
                RecordBook recordBook = new RecordBook();
                recordBook.AddRecord("September 2006", "Michael Schumacher", "last race");
                recordBook.AddRecord("September 2006", "Kimi Raikkonen", "no notes");
                oc.Set(recordBook);
            }
            finally
            {
                oc.Close();
            }
        }
        // end SaveMultiArray

        private static void GetMultiArray()
        {
            Console.WriteLine("Testing retrieving an object with multidimentional array field");
            IObjectContainer oc = Db4oFactory.OpenClient("localhost", 0xdb40, "db4o", "db4o");
            try
            {
                IObjectSet result = oc.Get(new RecordBook());
                ListResult(result);
            }
            finally
            {
                oc.Close();
            }
        }
        // end getMultiArray

        public static void ListResult(IObjectSet result)
        {
            Console.WriteLine(result.Size());
            while (result.HasNext())
            {
                Console.WriteLine(result.Next());
            }
        }
        // end ListResult

        public static void ListResult(IList<Pilot> result)
        {
            Console.WriteLine(result.Count);
            for (int x = 0; x < result.Count; x++)
            {
                Console.WriteLine(result[x]);
            }
        }
        // end ListResult

    }

    public class EvenPointsEvaluation : IEvaluation
    {
        public void Evaluate(ICandidate candidate)
        {
            Pilot pilot = (Pilot)candidate.GetObject();
            candidate.Include(pilot.Points % 2 == 0);
        }
    }
    // end EvenPointsEvaluation
}


