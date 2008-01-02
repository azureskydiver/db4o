/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;

using Db4objects.Db4o.Config;
using Db4objects.Db4o.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.CachedIO
{
    public class CachedIOExample
    {
        private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            SetObjects(configuration);
            GetObjects(configuration);
            configuration = ConfigureCache();
            SetObjects(configuration);
            GetObjects(configuration);
        }
        // end Main

        private static IConfiguration ConfigureCache(){
		    System.Console.WriteLine("Setting up cached io adapter");
            // new cached IO adapter with 256 pages 1024 bytes each
		    CachedIoAdapter adapter = new CachedIoAdapter(new RandomAccessFileAdapter(), 1024, 256);
            IConfiguration configuration = Db4oFactory.NewConfiguration();
		    configuration.Io(adapter);
            return configuration;
	    }
        // end ConfigureCache

        private static IConfiguration ConfigureRandomAccessAdapter()
        {
            System.Console.WriteLine("Setting up random access io adapter");
            IConfiguration configuration = Db4oFactory.NewConfiguration();
		    configuration.Io(new RandomAccessFileAdapter());
            return configuration;
	    }
        // end ConfigureRandomAccessAdapter

        private static void SetObjects(IConfiguration configuration)
        {
            File.Delete(Db4oFileName);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
            try
            {
                DateTime dt1 = DateTime.UtcNow;
			    for (int i = 0; i< 50000; i++){
				    Pilot pilot = new Pilot("Pilot #"+i);
				    db.Set(pilot);
			    }
			    DateTime dt2 = DateTime.UtcNow;
			    TimeSpan  diff = dt2 - dt1;
			    System.Console.WriteLine("Time elapsed for setting objects ="+ diff.TotalMilliseconds + " ms");
			    dt1 = DateTime.UtcNow;
                db.Commit(); ;
			    dt2 = DateTime.UtcNow;
			    diff = dt2 - dt1;
                System.Console.WriteLine("Time elapsed for commit =" + diff.TotalMilliseconds + " ms");

            }
            finally
            {
                db.Close();
            }       }
        // end SetObjects

        private static void GetObjects(IConfiguration configuration)
        {
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
            try
            {
                DateTime dt1 = DateTime.UtcNow;
                IObjectSet result = db.Get(null);
                DateTime dt2 = DateTime.UtcNow;
                TimeSpan diff = dt2 - dt1;
                System.Console.WriteLine("Time elapsed for the query =" + diff.TotalMilliseconds + " ms");
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