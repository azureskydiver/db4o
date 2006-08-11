using System.IO;
using com.db4o.f1.chapter3;
using com.db4o.query;

namespace com.db4o.f1.chapter6
{
	public class EvaluationExample : Util
	{
		public static void Main(string[] args)
		{
			File.Delete(Util.YapFileName);
			ObjectContainer db = Db4o.OpenFile(Util.YapFileName);
			try
			{
				StoreCars(db);
				QueryWithEvaluation(db);
			}
			finally
			{
				db.Close();
			}
		}

		public static void StoreCars(ObjectContainer db)
		{
			Pilot pilot1 = new Pilot("Michael Schumacher", 100);
			Car car1 = new Car("Ferrari");
			car1.Pilot = pilot1;
			car1.Snapshot();
			db.Set(car1);
			Pilot pilot2 = new Pilot("Rubens Barrichello", 99);
			Car car2 = new Car("BMW");
			car2.Pilot = pilot2;
			car2.Snapshot();
			car2.Snapshot();
			db.Set(car2);
		}

		public static void QueryWithEvaluation(ObjectContainer db)
		{
			Query query = db.Query();
			query.Constrain(typeof (Car));
			query.Constrain(new EvenHistoryEvaluation());
			ObjectSet result = query.Execute();
			Util.ListResult(result);
		}
	}
}