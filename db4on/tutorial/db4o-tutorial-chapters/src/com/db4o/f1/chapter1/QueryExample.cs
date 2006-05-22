using System;
using com.db4o;
using com.db4o.query;
using com.db4o.f1;

namespace com.db4o.f1.chapter1
{
    public class QueryExample : Util
    {
        public static void Main(string[] args)
        {
            ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
            try
            {
                StoreFirstPilot(db);
                StoreSecondPilot(db);
                RetrieveAllPilots(db);
                RetrievePilotByName(db);
                RetrievePilotByExactPoints(db);
                RetrieveByNegation(db);
                RetrieveByConjunction(db);
                RetrieveByDisjunction(db);
                RetrieveByComparison(db);
                RetrieveByDefaultFieldValue(db);
                RetrieveSorted(db); 
                ClearDatabase(db);
            }
            finally
            {
                db.Close();
            }
        }
    
        public static void StoreFirstPilot(ObjectContainer db)
        {
            Pilot pilot1 = new Pilot("Michael Schumacher", 100);
            db.Set(pilot1);
            Console.WriteLine("Stored {0}", pilot1);
        }
    
        public static void StoreSecondPilot(ObjectContainer db)
        {
            Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
            db.Set(pilot2);
            Console.WriteLine("Stored {0}", pilot2);
        }
    
        public static void RetrieveAllPilots(ObjectContainer db)
        {
            Query query = db.Query();
            query.Constrain(typeof(Pilot));
            ObjectSet result = query.Execute();
            ListResult(result);
        }
    
        public static void RetrievePilotByName(ObjectContainer db)
        {
            Query query = db.Query();
            query.Constrain(typeof(Pilot));
            query.Descend("_name").Constrain("Michael Schumacher");
            ObjectSet result = query.Execute();
            ListResult(result);
        }
        
        public static void RetrievePilotByExactPoints(ObjectContainer db)
        {
            Query query = db.Query();
            query.Constrain(typeof(Pilot));
            query.Descend("_points").Constrain(100);
            ObjectSet result = query.Execute();
            ListResult(result);
        }
    
        public static void RetrieveByNegation(ObjectContainer db)
        {
            Query query = db.Query();
            query.Constrain(typeof(Pilot));
            query.Descend("_name").Constrain("Michael Schumacher").Not();
            ObjectSet result = query.Execute();
            ListResult(result);
        }
    
        public static void RetrieveByConjunction(ObjectContainer db)
        {
            Query query = db.Query();
            query.Constrain(typeof(Pilot));
            Constraint constr = query.Descend("_name")
                    .Constrain("Michael Schumacher");
            query.Descend("_points")
                    .Constrain(99).And(constr);
            ObjectSet result = query.Execute();
            ListResult(result);
        }
    
        public static void RetrieveByDisjunction(ObjectContainer db)
        {
            Query query = db.Query();
            query.Constrain(typeof(Pilot));
            Constraint constr = query.Descend("_name")
                    .Constrain("Michael Schumacher");
            query.Descend("_points")
                    .Constrain(99).Or(constr);
            ObjectSet result = query.Execute();
            ListResult(result);
        }
    
        public static void RetrieveByComparison(ObjectContainer db)
        {
            Query query = db.Query();
            query.Constrain(typeof(Pilot));
            query.Descend("_points")
                    .Constrain(99).Greater();
            ObjectSet result = query.Execute();
            ListResult(result);
        }
    
        public static void RetrieveByDefaultFieldValue(ObjectContainer db)
        {
            Pilot somebody = new Pilot("Somebody else", 0);
            db.Set(somebody);
            Query query = db.Query();
            query.Constrain(typeof(Pilot));
            query.Descend("_points").Constrain(0);
            ObjectSet result = query.Execute();
            ListResult(result);
            db.Delete(somebody);
        }
        
        public static void RetrieveSorted(ObjectContainer db)
        {
            Query query = db.Query();
            query.Constrain(typeof(Pilot));
            query.Descend("_name").OrderAscending();
            ObjectSet result = query.Execute();
            ListResult(result);
            query.Descend("_name").OrderDescending();
            result = query.Execute();
            ListResult(result);
        }
    
        public static void ClearDatabase(ObjectContainer db)
        {
            ObjectSet result = db.Get(typeof(Pilot));
            foreach (object item in result)
            {
                db.Delete(item);
            }
        }
    }
}
