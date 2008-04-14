/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.Collections.Generic;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Ext;

namespace Db4objects.Db4odoc.Equality
{
    class EqualityExample
    {
        private const string Db4oFileName = "reference.db4o";

        private static IObjectContainer _container = null;

        public static void Main(string[] args)
        {
            File.Delete(Db4oFileName);

            StorePilot();
            TestEquality();
            RetrieveEqual();
        }
        // end Main

        private static IObjectContainer Database()
        {
            if (_container == null)
            {
                try
                {
                    _container = Db4oFactory.OpenFile(Db4oFileName);
                }
                catch (DatabaseFileLockedException ex)
                {
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

        private static void StorePilot() 
        {
		IObjectContainer container = Database();
		if (container != null) {
			try {
				Pilot pilot = new Pilot("Kimi Raikkonnen", 100);
				container.Set(pilot);
			} catch (Exception ex) {
				System.Console.WriteLine("System Exception: " + ex.Message);
			} finally {
				CloseDatabase();
			}
		}
	}

	// end StorePilot

	private static void TestEquality() {
		IObjectContainer container = Database();
		if (container != null) {
			try {
				IList<Pilot> result = container.Query<Pilot>(delegate(Pilot p){
						return p.Name.Equals("Kimi Raikkonnen") &&
						   p.Points == 100;
				});
		         Pilot obj = result[0];
		         Pilot pilot = new Pilot("Kimi Raikkonnen", 100);
		         string equality = obj.Equals(pilot) ? "equal" : "not equal";
		         System.Console.WriteLine("Pilots are " + equality);
			} catch (Exception ex) {
				System.Console.WriteLine("System Exception: " + ex.Message);
			} finally {
				CloseDatabase();
			}
		}
	}

	// end TestEquality

	private static void RetrieveEqual() {
		IObjectContainer container = Database();
		if (container != null) {
			try {
				IObjectSet result = container.Get(new Pilot("Kimi Raikkonnen", 100));
				if (result.Count > 0){
					System.Console.WriteLine("Found equal object: " + result.Next().ToString());
				} else {
					System.Console.WriteLine("No equal object exist in the database");
				}
			} catch (Exception ex) {
				System.Console.WriteLine("System Exception: " + ex.Message);
			} finally {
				CloseDatabase();
			}
		}
	}

	// end RetrieveEqual

    }
}
