using com.db4o;
using com.db4o.query;
using com.db4o.f1;

namespace com.db4o.f1.chapter1
{
	public class NQExample : Util
	{
		public static void Main(string[] args)
		{
			ObjectContainer db = Db4o.openFile(Util.YapFileName);
			try
			{
				storePilots(db);
				retrieveComplexSODA(db);
				retrieveComplexNQ(db);
				retrieveArbitraryCodeNQ(db);
				clearDatabase(db);
			}
			finally
			{
				db.close();
			}
		}
    
		public static void storePilots(ObjectContainer db)
		{
			db.set(new Pilot("Michael Schumacher", 100));
			db.set(new Pilot("Rubens Barrichello", 99));
		}
    
		public static void retrieveComplexSODA(ObjectContainer db)
		{
			Query query=db.query();
			query.constrain(typeof(Pilot));
			Query pointQuery=query.descend("_points");
			query.descend("_name").constrain("Rubens Barrichello")
				.or(pointQuery.constrain(99).greater()
				.and(pointQuery.constrain(199).smaller()));
			ObjectSet result=query.execute();
			listResult(result);
		}

		public static void retrieveComplexNQ(ObjectContainer db)
		{
			ObjectSet result = db.query(new ComplexQuery());
			listResult(result);
		}

		public static void retrieveArbitraryCodeNQ(ObjectContainer db)
		{
			ObjectSet result = db.query(new ArbitraryQuery(new int[]{1,100}));
			listResult(result);
		}
    
		public static void clearDatabase(ObjectContainer db)
		{
			ObjectSet result = db.get(typeof(Pilot));
			while (result.hasNext())
			{
				db.delete(result.next());
			}
		}
	}
}
