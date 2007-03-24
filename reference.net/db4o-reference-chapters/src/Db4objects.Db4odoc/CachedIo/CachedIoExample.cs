/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

using System;
using System.IO;
using Db4objects.Db4o.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.CachedIO
{

    public class CachedIOExample
    {
        public readonly static string YapFileName = "formula1.yap";

        public static void Main(string[] args)
        {
            SetObjects();
            GetObjects();
            ConfigureCache();
            SetObjects();
            GetObjects();
        }
        // end Main

        public static void ConfigureCache(){
		    System.Console.WriteLine("Setting up cached io adapter");
            // new cached IO adapter with 256 pages 1024 bytes each
		    CachedIoAdapter adapter = new CachedIoAdapter(new RandomAccessFileAdapter(), 1024, 256);
		    Db4oFactory.Configure().Io(adapter);
	    }
        // end ConfigureCache

        public static void ConfigureRandomAccessAdapter(){
            System.Console.WriteLine("Setting up random access io adapter");
            Db4oFactory.Configure().Io(new RandomAccessFileAdapter());
	    }
        // end ConfigureRandomAccessAdapter

        public static void SetObjects()
        {
            File.Delete(YapFileName);
            IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
            try
            {
                DateTime dt1 = DateTime.UtcNow;
			    for (int i = 0; i< 50000; i++){
				    Pilot pilot = new Pilot("Pilot #"+i);
				    db.Set(pilot);
			    }
			    DateTime dt2 = DateTime.UtcNow;
			    TimeSpan  diff = dt2 - dt1;
			    System.Console.WriteLine("Time elapsed for setting objects ="+ diff.Milliseconds + " ms");
			    dt1 = DateTime.UtcNow;
                db.Commit(); ;
			    dt2 = DateTime.UtcNow;
			    diff = dt2 - dt1;
                System.Console.WriteLine("Time elapsed for commit =" + diff.Milliseconds + " ms");

            }
            finally
            {
                db.Close();
            }
        }
        // end SetObjects

        public static void GetObjects()
        {
            Db4oFactory.Configure().Io(new RandomAccessFileAdapter());
            IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
            try
            {
                DateTime dt1 = DateTime.UtcNow;
                IObjectSet result = db.Get(null);
                DateTime dt2 = DateTime.UtcNow;
                TimeSpan diff = dt2 - dt1;
                System.Console.WriteLine("Time elapsed for the query =" + diff.Milliseconds + " ms");
                Console.WriteLine("Objects in the database: " + result.Count);
            }
            finally
            {
                db.Close();
            }
        }
        // end GetObjects
    }
}