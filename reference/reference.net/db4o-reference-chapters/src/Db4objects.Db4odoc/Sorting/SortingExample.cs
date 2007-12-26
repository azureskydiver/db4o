/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using System.Collections.Generic;
using System.Collections;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Sorting
{
    class SortingExample
    {
        private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            Db4oFactory.Configure().ObjectClass(typeof(Pilot)).ObjectField("_name").Indexed(true);
            Db4oFactory.Configure().ObjectClass(typeof(Pilot)).ObjectField("_points").Indexed(true);
            SetObjects();
            GetObjectsNQ();
            GetObjectsSODA();
            GetObjectsEval();
        }
        // end Main

        private static void SetObjects()
        {
            File.Delete(Db4oFileName);
            IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                for (int i = 0; i < 10; i++)
                {
                    for (int j = 0; j < 5; j++)
                    {
                        Pilot pilot = new Pilot("Pilot #" + i, j + 1);
                        db.Set(pilot);
                    }
                }
            }
            finally
            {
                db.Close();
            }
        }
        // end SetObjects

        private class PilotComparer : IComparer
        {
            public int Compare(object p1, object p2)
            {
                if (p1 is Pilot && p2 is Pilot)
                {
                    Pilot pilot1 = (Pilot)p1;
                    Pilot pilot2 = (Pilot)p2;
                    return pilot1.Name.CompareTo(pilot2.Name);
                }
                return 0;
            }
        }
        // end PilotComparer

        private class PilotEvaluation : IEvaluation
        {
            public void Evaluate(ICandidate candidate)
            {
                Pilot pilot = (Pilot)candidate.GetObject();
                candidate.Include(pilot.Points % 2 == 0);
            }
        }
        // end PilotEvaluation

        private static void GetObjectsEval()
        {
            IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                DateTime dt1 = DateTime.UtcNow;
                IQuery query = db.Query();
                query.Constrain(typeof(Pilot));
                query.Constrain(new PilotEvaluation());
                ArrayList result = new ArrayList(query.Execute());
                result.Sort(new PilotComparer());
                DateTime dt2 = DateTime.UtcNow;
                TimeSpan diff = dt2 - dt1;
                Console.WriteLine("Time to execute with Evaluation query and collection sorting: " + diff.Milliseconds + " ms.");
                ListResult(result);
            }
            finally
            {
                db.Close();
            }
        }
        // end getObjectsEval

        private static void GetObjectsSODA()
        {
            IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                IQuery query = db.Query();
                query.Constrain(typeof(Pilot));
                query.Descend("_name").OrderAscending();
                query.Descend("_points").OrderDescending();
                DateTime dt1 = DateTime.UtcNow;
                IObjectSet result = query.Execute();
                DateTime dt2 = DateTime.UtcNow;
                TimeSpan diff = dt2 - dt1;
                Console.WriteLine("Time to query and sort with  SODA: " + diff.Milliseconds + " ms.");
                ListResult(result);
            }
            finally
            {
                db.Close();
            }
        }
        // end getObjectsSODA

        private static void GetObjectsNQ()
        {
            IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                DateTime dt1 = DateTime.UtcNow;
                IList<Pilot> result = db.Query<Pilot>(delegate(Pilot pilot) {
                    return true;
                },
                new System.Comparison<Pilot>(delegate(Pilot p1, Pilot p2)
                {
                    Pilot pilot1 = (Pilot)p1;
                    Pilot pilot2 = (Pilot)p2;
                    int res = pilot1.Points - pilot2.Points;
                    if (res == 0)
                    {
                        return pilot1.Name.CompareTo(pilot2.Name);
                    }
                    else
                    {
                        return -res;
                    }
                }));
                DateTime dt2 = DateTime.UtcNow;
                TimeSpan diff = dt2 - dt1;
                Console.WriteLine("Time to execute with NQ and comparator: " + diff.Milliseconds + " ms.");
                ListResult(result);
            }
            finally
            {
                db.Close();
            }
        }
        // end getObjectsNQ

        private static void ListResult(IObjectSet result)
        {
            Console.WriteLine(result.Count);
            while (result.HasNext())
            {
                Console.WriteLine(result.Next());
            }
        }
        // end ListResult

        private static void ListResult<Pilot>(IList<Pilot> result)
        {
            Console.WriteLine(result.Count);
            foreach (Pilot pilot in result)
            {
                Console.WriteLine(pilot);
            }
        }
        // end ListResult

        private static void ListResult(ArrayList result)
        {
            Console.WriteLine(result.Count);
            foreach (object obj in result)
            {
                Console.WriteLine(obj);
            }
        }
        // end ListResult
    }
}
