/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Collections;

using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Query;


namespace Db4objects.Db4odoc.ListOperations
{
    class ListOperationsExample
    {
        public const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            FillUpDb(2);
            RemoveInsert();
            CheckResults();
            UpdateObject();
            CheckResults();
        }
        // end Main

        private static void FillUpDb(int listCount)
        {
            int dataCount = 50000;
            Stopwatch sw = new Stopwatch();
            File.Delete(Db4oFileName);
            IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                sw.Start();

                for (int i = 0; i < listCount; i++)
                {
                    ListObject lo = new ListObject();
                    lo.Name = "list" + i.ToString("00");
                    for (int j = 0; j < dataCount; j++)
                    {
                        DataObject dataObject = new DataObject();
                        dataObject.Name = "data" + j.ToString("00000");
                        dataObject.Data = DateTime.Now.ToString() + " ---- Data Object " + j.ToString("00000");
                        lo.Data.Add(dataObject);
                    }
                    db.Set(lo);
                }
                sw.Stop();
            }
            finally
            {
                db.Close();
            }
            Console.WriteLine("Completed {0} lists of {1} objects each.", listCount, dataCount);
            Console.WriteLine("Elapsed time: {0}", sw.Elapsed.ToString());
        }
        // end FillUpDb

        private static void CheckResults()
        {
            Stopwatch sw = new Stopwatch();
            // activation depth should be enough to activate 
            // ListObject, DataObject and its list members
            int activationDepth = 3;
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ActivationDepth(activationDepth);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
            try
            {
                IList<ListObject> result = db.Query<ListObject>();
                if (result.Count > 0)
                {
                    Console.WriteLine("Result count was {0}, looping with activation depth {1}", result.Count, activationDepth);
                    sw.Start();
                    foreach (ListObject lo in result)
                    {
                        Console.WriteLine("ListObj {0} has {1} objects", lo.Name,
                            ((lo.Data == null) ? "<null>" : lo.Data.Count.ToString()));
                        Console.WriteLine(" --- {0} at index 0",
                            ((lo.Data != null && lo.Data.Count > 0) ? lo.Data[0].ToString() : "<null>"));
                    }
                    sw.Stop();
                }
            }
            finally
            {
                db.Close();
            }
            Console.WriteLine("Activation took {0}", sw.Elapsed.ToString());
        }
        // end CheckResults


        private static void RemoveInsert()
        {
            Stopwatch sw = new Stopwatch();

            // set update depth to 1 for the quickest execution
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.UpdateDepth(1);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
            try
            {
                IList<ListObject> result = db.Query<ListObject>();
                if (result.Count == 2)
                {
                    // retrieve 2 ListObjects
                    ListObject lo1 = result[0];
                    ListObject lo2 = result[1];
                    DataObject dataObject = lo1.Data[0];
                    // move the first object from the first
                    // ListObject to the second ListObject
                    lo1.Data.Remove(dataObject);
                    lo2.Data.Add(dataObject);

                    Console.WriteLine("Removed from the first list, count is {0}, setting data...", lo1.Data.Count);
                    Console.WriteLine("Added to the second list, count is {0}, setting data...", lo2.Data.Count);
                    sw.Start();
                    db.Set(lo1);
                    db.Set(lo2);
                    db.Commit();
                    sw.Stop();
                }
            }
            finally
            {
                db.Close();
            }
            Console.WriteLine("Storing took {0}", sw.Elapsed.ToString());
        }
        // end RemoveInsert

        private static void UpdateObject()
        {
            Stopwatch sw = new Stopwatch();

            // we can set update depth to 0 
            // as we update only the current object
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.UpdateDepth(0);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
            try
            {
                IList<ListObject> result = db.Query<ListObject>();
                if (result.Count == 2)
                {
                    ListObject lo1 = result[0];
                    DataObject dataobject = lo1.Data[0];
                    dataobject.Name = "Updated";
                    dataobject.Data = DateTime.Now.ToString() + " ---- Updated Object ";

                    Console.WriteLine("Updated list {0} dataobject {1}", lo1.Name, lo1.Data[0]);
                    sw.Start();
                    db.Set(dataobject);
                    db.Commit();
                    sw.Stop();
                }
            }
            finally
            {
                db.Close();
            }
            Console.WriteLine("Storing took {0}", sw.Elapsed.ToString());
        }
        // end UpdateObject
    }
}


