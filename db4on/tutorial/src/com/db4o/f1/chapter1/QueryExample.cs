namespace com.db4o.f1.chapter1
{
    using System;
    using com.db4o;
    using com.db4o.query;
    using com.db4o.f1;

    public class QueryExample : Util
    {
        public static void Main(string[] args)
        {
            ObjectContainer db = Db4o.openFile(Util.YapFileName);
            try
            {
                storeFirstPilot(db);
                storeSecondPilot(db);
                retrieveAllPilots(db);
                retrievePilotByName(db);
                retrievePilotByExactPoints(db);
                retrieveByNegation(db);
                retrieveByConjunction(db);
                retrieveByDisjunction(db);
                retrieveByComparison(db);
                retrieveByDefaultFieldValue(db);
                retrieveSorted(db); 
                clearDatabase(db);
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
    
        public static void retrieveAllPilots(ObjectContainer db)
        {
            Query query = db.query();
            query.constrain(typeof(Pilot));
            ObjectSet result = query.execute();
            listResult(result);
        }
    
        public static void retrievePilotByName(ObjectContainer db)
        {
            Query query = db.query();
            query.constrain(typeof(Pilot));
            query.descend("_name").constrain("Michael Schumacher");
            ObjectSet result = query.execute();
            listResult(result);
        }
        
        public static void retrievePilotByExactPoints(ObjectContainer db)
        {
            Query query = db.query();
            query.constrain(typeof(Pilot));
            query.descend("_points").constrain(100);
            ObjectSet result = query.execute();
            listResult(result);
        }
    
        public static void retrieveByNegation(ObjectContainer db)
        {
            Query query = db.query();
            query.constrain(typeof(Pilot));
            query.descend("_name").constrain("Michael Schumacher").not();
            ObjectSet result = query.execute();
            listResult(result);
        }
    
        public static void retrieveByConjunction(ObjectContainer db)
        {
            Query query = db.query();
            query.constrain(typeof(Pilot));
            Constraint constr = query.descend("_name")
                    .constrain("Michael Schumacher");
            query.descend("_points")
                    .constrain(99).and(constr);
            ObjectSet result = query.execute();
            listResult(result);
        }
    
        public static void retrieveByDisjunction(ObjectContainer db)
        {
            Query query = db.query();
            query.constrain(typeof(Pilot));
            Constraint constr = query.descend("_name")
                    .constrain("Michael Schumacher");
            query.descend("_points")
                    .constrain(99).or(constr);
            ObjectSet result = query.execute();
            listResult(result);
        }
    
        public static void retrieveByComparison(ObjectContainer db)
        {
            Query query = db.query();
            query.constrain(typeof(Pilot));
            query.descend("_points")
                    .constrain(99).greater();
            ObjectSet result = query.execute();
            listResult(result);
        }
    
        public static void retrieveByDefaultFieldValue(ObjectContainer db)
        {
            Pilot somebody = new Pilot("Somebody else", 0);
            db.set(somebody);
            Query query = db.query();
            query.constrain(typeof(Pilot));
            query.descend("_points").constrain(0);
            ObjectSet result = query.execute();
            listResult(result);
            db.delete(somebody);
        }
        
        public static void retrieveSorted(ObjectContainer db)
        {
            Query query = db.query();
            query.constrain(typeof(Pilot));
            query.descend("_name").orderAscending();
            ObjectSet result = query.execute();
            listResult(result);
            query.descend("_name").orderDescending();
            result = query.execute();
            listResult(result);
        }
    
        public static void clearDatabase(ObjectContainer db)
        {
            ObjectSet result = db.get(new Pilot(null, 0));
            while (result.hasNext())
            {
                db.delete(result.next());
            }
        }
    }
}
