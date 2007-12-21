/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using System.Collections.Generic;
using Db4objects.Db4o;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.TA;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Types;
using Db4objects.Db4o.Diagnostic;

namespace Db4objects.Db4odoc.Activating
{

    public class ActivationExample
    {
        private const string Db4oFileName = "reference.db4o";
        private static IObjectContainer _container;

        public static void Main(string[] args)
        {
            TestCollection();
        }
        // end Main
        
        private static void StoreCollection()
        {
            File.Delete(Db4oFileName);
            IObjectContainer db = Database(ConfigureTA());
            if (db != null)
            {
                try
                {
                    // create a linked list with length 10
                    SensorPanel sensorPanel = new SensorPanel().CreateList(10);
                    // store all elements with one statement, since all elements are new		
                    db.Set(sensorPanel);
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }
        // end StoreCollection

        private static IObjectContainer Database(IConfiguration configuration) {
		if (_container == null) {
			try {
				_container = Db4oFactory.OpenFile(configuration, Db4oFileName);
			} catch (DatabaseFileLockedException ex) {
				System.Console.WriteLine(ex.Message);
			}
		}
		return _container;
	}

        // end Database

        private static void CloseDatabase()
        {
            if (_container != null)
            {
                _container.Close();
                _container = null;
            }
        }

        // end CloseDatabase

        private static IConfiguration ConfigureTA() 
        {
		    IConfiguration configuration = Db4oFactory.NewConfiguration();
		    configuration.Add(new TransparentActivationSupport());
            ActivateDiagnostics(configuration);

		    return configuration;
	    }
	    // end configureTA

        private static void ActivateDiagnostics(IConfiguration configuration) 
        {
		    // Add diagnostic listener that will show all the classes that are not
		    // TA aware.
		    configuration.Diagnostic().AddListener(new TaDiagListener());
        }
        // end ActivateDiagnostics


        public class TaDiagListener: DiagnosticToConsole
	    {
		    override public void OnDiagnostic(Db4objects.Db4o.Diagnostic.IDiagnostic d) 
		    {
                if (d.GetType().Equals(typeof(Db4objects.Db4o.TA.NotTransparentActivationEnabled)))
                {
				    System.Console.WriteLine(d.ToString());
			    }
		    }
	    }
        // end TaDiagListener

        
        private static void TestCollection()
        {
            StoreCollection();
            IObjectContainer db = Database(ConfigureTA());
            if (db != null)
            {
                try
                {
                    IList<SensorPanel> result = db.Query<SensorPanel>(delegate(SensorPanel sensorPanel)
                    {
                        if (sensorPanel.Sensor.Equals(1))
                        {
                            return true;
                        }
                        return false;
                    });
                    Console.WriteLine(result.Count);
                    if (result.Count > 0)
                    {
                        SensorPanel sensor = (SensorPanel)result[0];
                        SensorPanel next = sensor.Next;
                        while (next != null)
                        {
                            Console.WriteLine(next);
                            next = next.Next;
                        }
                    }
                }
                finally
                {
                    CloseDatabase();
                }
            }
        }
        // end TestCollection


        private static void ListResult(IObjectSet result)
        {
            Console.WriteLine(result.Count);
            foreach (object item in result)
            {
                Console.WriteLine(item);
            }
        }
        // end ListResult
    }
}
