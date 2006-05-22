using com.db4o;
using com.db4o.query;
using com.db4o.f1;

namespace com.db4o.f1.chapter1
{
	public class NQExample : Util
	{
		public static void Main(string[] args)
		{
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			try
			{
				StorePilots(db);
				RetrieveComplexSODA(db);
				RetrieveComplexNQ(db);
				RetrieveArbitraryCodeNQ(db);
				ClearDatabase(db);
			}
			finally
			{
				db.Close();
			}
		}
    
		public static void StorePilots(ObjectContainer db)
		{
			db.Set(new Pilot("Michael Schumacher", 100));
			db.Set(new Pilot("Rubens Barrichello", 99));
		}
    
		public static void RetrieveComplexSODA(ObjectContainer db)
		{
			Query query=db.Query();
			query.Constrain(typeof(Pilot));
			Query pointQuery=query.Descend("_points");
			query.Descend("_name").Constrain("Rubens Barrichello")
				.Or(pointQuery.Constrain(99).Greater()
				.And(pointQuery.Constrain(199).Smaller()));
			ObjectSet result=query.Execute();
			ListResult(result);
		}

		public static void RetrieveComplexNQ(ObjectContainer db)
		{
			ObjectSet result = db.Query(new ComplexQuery());
			ListResult(result);
		}

		public static void RetrieveArbitraryCodeNQ(ObjectContainer db)
		{
			ObjectSet result = db.Query(new ArbitraryQuery(new int[]{1,100}));
			ListResult(result);
		}
    
		public static void ClearDatabase(ObjectContainer db)
		{
			ObjectSet result = db.Get(typeof(Pilot));
			while (result.HasNext())
			{
				db.Delete(result.Next());
			}
		}
	}
}
