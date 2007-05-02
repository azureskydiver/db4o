/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.Transactions
{
    public class TransactionExample 
    {
		private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            File.Delete(Db4oFileName);
            IObjectContainer db=Db4oFactory.OpenFile(Db4oFileName);
            try
            {
                StoreCarCommit(db);
                db.Close();
                db = Db4oFactory.OpenFile(Db4oFileName);
                ListAllCars(db);
                StoreCarRollback(db);
                db.Close();
                db = Db4oFactory.OpenFile(Db4oFileName);
                ListAllCars(db);
                CarSnapshotRollback(db);
                CarSnapshotRollbackRefresh(db);
            }
            finally
            {
                db.Close();
            }
        }
		// end Main
        
        private static void StoreCarCommit(IObjectContainer db)
        {
            Pilot pilot = new Pilot("Rubens Barrichello", 99);
            Car car = new Car("BMW");
            car.Pilot = pilot;
            db.Set(car);
            db.Commit();
        }
		// end StoreCarCommit
    
        private static void ListAllCars(IObjectContainer db)
        {
            IObjectSet result = db.Get(typeof(Car));
            ListResult(result);
        }
		// end ListAllCars

        private static void StoreCarRollback(IObjectContainer db)
        {
            Pilot pilot = new Pilot("Michael Schumacher", 100);
            Car car = new Car("Ferrari");
            car.Pilot = pilot;
            db.Set(car);
            db.Rollback();
        }
		// end StoreCarRollback

        private static void CarSnapshotRollback(IObjectContainer db)
        {
            IObjectSet result = db.Get(new Car("BMW"));
            Car car = (Car)result.Next();
            car.Snapshot();
            db.Set(car);
            db.Rollback();
            Console.WriteLine(car);
        }
		// end CarSnapshotRollback

        private static void CarSnapshotRollbackRefresh(IObjectContainer db)
        {
            IObjectSet result=db.Get(new Car("BMW"));
            Car car=(Car)result.Next();
            car.Snapshot();
            db.Set(car);
            db.Rollback();
            db.Ext().Refresh(car, int.MaxValue);
            Console.WriteLine(car);
        }
		// end CarSnapshotRollbackRefresh

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
