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

namespace Db4objects.Db4odoc.ListDeleting
{
    class ListDeletingExample
    {
        public const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            FillUpDb(1);
            DeleteTest();
            FillUpDb(1);
            RemoveAndDeleteTest();
            FillUpDb(1);
            RemoveTest();
        }
        // end Main

        private static void RemoveTest(){
            // set update depth to 1 as we only 
            // modify List field
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(ListObject)).UpdateDepth(1);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
            try
            {
                IList<ListObject> result = db.Query<ListObject>(typeof(ListObject));
                if (result.Count > 0)
                {
                	// retrieve a ListObject
                    ListObject lo1 = result[0];
                    // remove all the objects from the list
                    lo1.Data.RemoveRange(0, lo1.Data.Count);
                    db.Set(lo1);
                }
            } finally {
            	db.Close();
            }
            // check DataObjects in the list
            // and DataObjects in the database
            db = Db4oFactory.OpenFile(Db4oFileName);
            try {
            	IList<ListObject> result = db.Query<ListObject>(typeof(ListObject));
                if (result.Count > 0) {
                	ListObject lo1 = result[0];
                	Console.WriteLine("DataObjects in the list:  " + lo1.Data.Count);
                }
                IList<DataObject> removedObjects = db.Query<DataObject>(typeof(DataObject));
                Console.WriteLine("DataObjects in the database: " + removedObjects.Count);
        	} finally {
                db.Close();
            }
        } 
        // end RemoveTest

        private static void RemoveAndDeleteTest()
        {
            // set update depth to 1 as we only 
            // modify List field
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(ListObject)).UpdateDepth(1);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
            try
            {
                IList<ListObject> result = db.Query<ListObject>(typeof(ListObject));
                if (result.Count > 0)
                {
                    // retrieve a ListObject
                    ListObject lo1 = result[0];
                    // create a copy of the objects list
                    // to memorize the objects to be deleted
                    List <DataObject>tempList = new List<DataObject>(lo1.Data);
                    // remove all the objects from the list
                    lo1.Data.RemoveRange(0, lo1.Data.Count);
                    db.Set(lo1);
                    // and delete them from the database
                    foreach (DataObject obj in tempList)
                    {
                       db.Delete(obj);
                    }
                }
            }
            finally
            {
                db.Close();
            }
            // check DataObjects in the list
            // and DataObjects in the database
            db = Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                IList<ListObject> result = db.Query<ListObject>(typeof(ListObject));
                if (result.Count > 0)
                {
                    ListObject lo1 = result[0];
                    Console.WriteLine("DataObjects in the list:  " + lo1.Data.Count);
                }
                IList<DataObject> removedObjects = db.Query<DataObject>(typeof(DataObject));
                Console.WriteLine("DataObjects in the database: " + removedObjects.Count);
            }
            finally
            {
                db.Close();
            }
        }
        // end RemoveAndDeleteTest
        
        private static void DeleteTest(){
            // set cascadeOnDelete in order to delete member objects
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(ListObject)).CascadeOnDelete(true);
            IObjectContainer db = Db4oFactory.OpenFile(configuration , Db4oFileName);
            try
            {
                IList<ListObject> result = db.Query<ListObject>(typeof(ListObject));
                if (result.Count > 0)
                {
                	// retrieve a ListObject
                    ListObject lo1 = result[0];
                    // delete the ListObject with all the field objects
                    db.Delete(lo1);
                }
            } finally {
            	db.Close();
            }
            // check ListObjects and DataObjects in the database
            db = Db4oFactory.OpenFile(Db4oFileName);
            try {
            	IList<ListObject> listObjects = db.Query<ListObject>(typeof(ListObject));
            	Console.WriteLine("ListObjects in the database:  " + listObjects.Count);
                IList<DataObject> dataObjects = db.Query<DataObject>(typeof(DataObject));
                Console.WriteLine("DataObjects in the database: " + dataObjects.Count);
        	} finally {
                db.Close();
            }
        } 
        // end DeleteTest
        
        private static void FillUpDb(int listCount)
        {
            int dataCount = 50;
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
    }
}


