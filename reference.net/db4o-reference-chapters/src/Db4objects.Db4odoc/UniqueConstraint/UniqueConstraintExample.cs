/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Constraints;

namespace Db4objects.Db4odoc.UniqueConstraint
{
    class UniqueConstraintExample
    {
        private const string FileName = "test.db"; 

	public static void Main(string[] args) {
		Configure();
		StoreObjects();
	}
	// end Main

	private static IConfiguration Configure(){
		IConfiguration configuration = Db4oFactory.NewConfiguration();
		configuration.ObjectClass(typeof(Pilot)).ObjectField("_name").Indexed(true);
		configuration.Add(new UniqueFieldValueConstraint(typeof(Pilot), "_name"));
		return configuration;
	}
	// end Configure
	
	private static void StoreObjects(){
		File.Delete(FileName);
		IObjectServer server = Db4oFactory.OpenServer(Configure(), FileName, 0);
		Pilot pilot1 = null;
		Pilot pilot2 = null;
		try {
			IObjectContainer client1 = server.OpenClient();
			try {
				// creating and storing pilot1 to the database
				pilot1 = new Pilot("Rubens Barichello",99);
				client1.Set(pilot1);
				IObjectContainer client2 = server.OpenClient();
				try {
					// creating and storing pilot2 to the database
					pilot2 = new Pilot("Rubens Barichello",100);
					client2.Set(pilot2);
					// commit the changes
					client2.Commit();
				} catch (UniqueFieldValueConstraintViolationException ex){
					System.Console.WriteLine("Unique constraint violation in client2 saving: " + pilot2);
					client2.Rollback();
				} finally {
					client2.Close();
				}
				// Pilot Rubens Barichello is already in the database,
				// commit will fail
				client1.Commit();
			} catch (UniqueFieldValueConstraintViolationException ex){
                System.Console.WriteLine("Unique constraint violation in client1 saving: " + pilot1);
				client1.Rollback();
			} finally {
                client1.Close();
			}
		} finally {
            server.Close();
		}
	}
	// end StoreObjects
    }
}
