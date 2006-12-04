// Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com 
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Config;

namespace Db4objects.Db4odoc.Querymode
{
    class QueryModesExample
    {
        public readonly static string YapFileName = "formula1.yap";

        public static void Main(string[] args)
        {
            Db4oFactory.Configure().ObjectClass(typeof(Pilot)).ObjectField("_points").Indexed(true);
            TestImmediateQueries();
            TestLazyQueries();
            TestSnapshotQueries();
            TestLazyConcurrent();
            TestSnapshotConcurrent();
            TestImmediateChanged();
        }
        // end Main

        public static void FillUpDB(int pilotCount)
        {
            File.Delete(YapFileName);
            IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
            try
            {
                for (int i = 0; i < pilotCount; i++)
                {
                    AddPilot(db, i);
                }
            }
            finally
            {
                db.Close();
            }
        }
        // end FillUpDB

        private static void AddPilot(IObjectContainer db, int points)
        {
            Pilot pilot = new Pilot("Tester", points);
            db.Set(pilot);
        }
        // end AddPilot

        public static void TestImmediateQueries()
        {
            Console.WriteLine("Testing query performance on 10000 pilot objects in Immediate mode");
            FillUpDB(10000);
            IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
            try
            {
                db.Ext().Configure().Queries().EvaluationMode(QueryEvaluationMode.IMMEDIATE);
                IQuery query = db.Query();
                query.Constrain(typeof(Pilot));
                query.Descend("_points").Constrain(99).Greater();
                DateTime dt1 = DateTime.UtcNow;
                query.Execute();
                DateTime dt2 = DateTime.UtcNow;
                TimeSpan diff = dt2 - dt1;
                Console.WriteLine("Query execution time=" + diff.Milliseconds + " ms");
            }
            finally
            {
                db.Close();
            }
        }
        // end TestImmediateQueries

        public static void TestLazyQueries()
        {
            Console.WriteLine("Testing query performance on 10000 pilot objects in Lazy mode");
            FillUpDB(10000);
            IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
            try
            {
                db.Ext().Configure().Queries().EvaluationMode(QueryEvaluationMode.LAZY);
                //QueryStats stats = new QueryStats();       
                //stats.connect(db);
                IQuery query = db.Query();
                query.Constrain(typeof(Pilot));
                query.Descend("_points").Constrain(99).Greater();
                DateTime dt1 = DateTime.UtcNow;
                query.Execute();
                DateTime dt2 = DateTime.UtcNow;
                TimeSpan diff = dt2 - dt1;
                Console.WriteLine("Query execution time=" + diff.Milliseconds + " ms");
            }
            finally
            {
                db.Close();
            }
        }
        // end TestLazyQueries

        public static void TestLazyConcurrent()
        {
            Console.WriteLine("Testing lazy mode with concurrent modifications");
            FillUpDB(10);
            IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
            try
            {
                db.Ext().Configure().Queries().EvaluationMode(QueryEvaluationMode.LAZY);
                IQuery query1 = db.Query();
                query1.Constrain(typeof(Pilot));
                query1.Descend("_points").Constrain(5).Smaller();
                IObjectSet result1 = query1.Execute();

                IQuery query2 = db.Query();
                query2.Constrain(typeof(Pilot));
                query2.Descend("_points").Constrain(1);
                IObjectSet result2 = query2.Execute();
                Pilot pilotToDelete = (Pilot)result2[0];
                Console.WriteLine("Pilot to be deleted: " + pilotToDelete);
                db.Delete(pilotToDelete);
                Pilot pilot = new Pilot("Tester", 2);
                Console.WriteLine("Pilot to be added: " + pilot);
                db.Set(pilot);

                Console.WriteLine("Query result after changing from the same transaction");
                ListResult(result1);
            }
            finally
            {
                db.Close();
            }
        }
        // end TestLazyConcurrent

        public static void ListResult(IObjectSet result)
        {
            while (result.HasNext())
            {
                Console.WriteLine(result.Next());
            }
        }
        // end ListResult

        public static void TestSnapshotQueries()
        {
            Console.WriteLine("Testing query performance on 10000 pilot objects in Snapshot mode");
            FillUpDB(10000);
            IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
            try
            {
                db.Ext().Configure().Queries().EvaluationMode(QueryEvaluationMode.SNAPSHOT);
                IQuery query = db.Query();
                query.Constrain(typeof(Pilot));
                query.Descend("_points").Constrain(99).Greater();
                DateTime dt1 = DateTime.UtcNow;
                query.Execute();
                DateTime dt2 = DateTime.UtcNow;
                TimeSpan diff = dt2 - dt1;
                Console.WriteLine("Query execution time=" + diff.Milliseconds + " ms");
            }
            finally
            {
                db.Close();
            }
        }
        // end TestSnapshotQueries

        public static void TestSnapshotConcurrent()
        {
            Console.WriteLine("Testing snapshot mode with concurrent modifications");
            FillUpDB(10);
            IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
            try
            {
                db.Ext().Configure().Queries().EvaluationMode(QueryEvaluationMode.SNAPSHOT);
                IQuery query1 = db.Query();
                query1.Constrain(typeof(Pilot));
                query1.Descend("_points").Constrain(5).Smaller();
                IObjectSet result1 = query1.Execute();

                IQuery query2 = db.Query();
                query2.Constrain(typeof(Pilot));
                query2.Descend("_points").Constrain(1);
                IObjectSet result2 = query2.Execute();
                Pilot pilotToDelete = (Pilot)result2[0];
                Console.WriteLine("Pilot to be deleted: " + pilotToDelete);
                db.Delete(pilotToDelete);
                Pilot pilot = new Pilot("Tester", 2);
                Console.WriteLine("Pilot to be added: " + pilot);
                db.Set(pilot);

                Console.WriteLine("Query result after changing from the same transaction");
                ListResult(result1);
            }
            finally
            {
                db.Close();
            }
        }
        // end TestSnapshotConcurrent

        public static void TestImmediateChanged()
        {
            Console.WriteLine("Testing immediate mode with field changes");
            FillUpDB(10);
            IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
            try
            {
                db.Ext().Configure().Queries().EvaluationMode(QueryEvaluationMode.IMMEDIATE);
                IQuery query1 = db.Query();
                query1.Constrain(typeof(Pilot));
                query1.Descend("_points").Constrain(5).Smaller();
                IObjectSet result1 = query1.Execute();

                // change field
                IQuery query2 = db.Query();
                query2.Constrain(typeof(Pilot));
                query2.Descend("_points").Constrain(2);
                IObjectSet result2 = query2.Execute();
                Pilot pilot2 = (Pilot)result2[0];
                pilot2.AddPoints(22);
                db.Set(pilot2);
                ListResult(result1);
            }
            finally
            {
                db.Close();
            }
        }
        // end TestImmediateChanged
    }
}
