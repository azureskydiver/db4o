/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.Evaluations
{
	public class EvaluationExample 
	{
		private const string Db4oFileName = "reference.db4o";

		public static void Main(string[] args)
		{
			File.Delete(Db4oFileName);
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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
		// end Main

        private static void StoreCars(IObjectContainer db)
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
		// end StoreCars

        private static void QueryWithEvaluation(IObjectContainer db)
		{
			IQuery query = db.Query();
			query.Constrain(typeof (Car));
			query.Constrain(new EvenHistoryEvaluation());
			IObjectSet result = query.Execute();
			ListResult(result);
		}
		// end QueryWithEvaluation

        private static void ListResult(IObjectSet result)
		{
			System.Console.WriteLine(result.Count);
			foreach (object item in result)
			{
				System.Console.WriteLine(item);
			}
		}
		// end ListResult
	}
}