namespace com.db4o.f1.chapter1
{
    using System;
    using System.IO;
    using com.db4o;
    using com.db4o.f1;

    public class FirstStepsExample : Util
    {    
        public static void Main(string[] args)
        {
            File.Delete(Util.YapFileName);
            accessDb4o();
            File.Delete(Util.YapFileName);
            ObjectContainer db = Db4o.openFile(Util.YapFileName);
            try
            {
                storeFirstPilot(db);
                storeSecondPilot(db);
                retrieveAllPilots(db);
                retrievePilotByName(db);
                retrievePilotByExactPoints(db);
                updatePilot(db);
                deleteFirstPilotByName(db);
                deleteSecondPilotByName(db);
            }
            finally
            {
                db.close();
            }
        }
        
        public static void accessDb4o()
        {
        	ObjectContainer db=Db4o.openFile(Util.YapFileName);
        	try
        	{
            	// do something with db4o
        	}
        	finally
        	{
            	db.close();
        	}
    	}
        
        public static void storeFirstPilot(ObjectContainer db)
        {
            Pilot pilot1 = new Pilot("Michael Schumacher", 100);
            db.set(pilot1);
            Console.WriteLine("Stored " + pilot1);
        }
    
        public static void storeSecondPilot(ObjectContainer db)
        {
            Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
            db.set(pilot2);
            Console.WriteLine("Stored " + pilot2);
        }
    
        public static void retrieveAllPilotQBE(ObjectContainer db) {
            Pilot proto = new Pilot(null, 0);
            ObjectSet result = db.get(proto);
            listResult(result);
        }
    
        public static void retrieveAllPilots(ObjectContainer db) {
            ObjectSet result = db.get(typeof(Pilot));
            listResult(result);
        }
    
        public static void retrievePilotByName(ObjectContainer db)
        {
            Pilot proto = new Pilot("Michael Schumacher", 0);
            ObjectSet result = db.get(proto);
            listResult(result);
        }
        
        public static void retrievePilotByExactPoints(ObjectContainer db)
        {
            Pilot proto = new Pilot(null, 100);
            ObjectSet result = db.get(proto);
            listResult(result);
        }
    
        public static void updatePilot(ObjectContainer db)
        {
            ObjectSet result = db.get(new Pilot("Michael Schumacher", 0));
            Pilot found = (Pilot)result.next();
            found.AddPoints(11);
            db.set(found);
            Console.WriteLine("Added 11 points for " + found);
            retrieveAllPilots(db);
        }
    
        public static void deleteFirstPilotByName(ObjectContainer db)
        {
            ObjectSet result = db.get(new Pilot("Michael Schumacher", 0));
            Pilot found = (Pilot)result.next();
            db.delete(found);
            Console.WriteLine("Deleted " + found);
            retrieveAllPilots(db);
        }
    
        public static void deleteSecondPilotByName(ObjectContainer db)
        {
            ObjectSet result = db.get(new Pilot("Rubens Barrichello", 0));
            Pilot found = (Pilot)result.next();
            db.delete(found);
            Console.WriteLine("Deleted " + found);
            retrieveAllPilots(db);
        }
    }
}
