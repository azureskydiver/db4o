/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using com.db4o;
using com.db4o.query;
using com.db4o.diagnostic;
using com.db4odoc.f1.evaluations;
using System;
using System.IO;

namespace com.db4odoc.f1.diagnostics
{
	public class DiagnosticExample : Util
	{
		 public static void TestEmpty() {
    		Db4o.Configure().Diagnostic().AddListener(new DiagnosticToConsole());
			File.Delete(Util.YapFileName);    
			ObjectContainer db=Db4o.OpenFile(Util.YapFileName);
			try {
        		SetEmptyObject(db);
			}
			finally {
				db.Close();
			}
		}
	    
		private static void SetEmptyObject(ObjectContainer db){
    		Empty empty = new Empty();
			db.Set(empty);
		}
	    	
		public static void TestArbitrary() {
    		Db4o.Configure().Diagnostic().AddListener(new DiagnosticToConsole());
    		File.Delete(Util.YapFileName);    
			ObjectContainer db=Db4o.OpenFile(Util.YapFileName);
			try {
        		Pilot pilot = new Pilot("Rubens Barrichello",99);
        		db.Set(pilot);
        		QueryPilot(db);
			}
			finally {
				db.Close();
			}
		}
		
		private static void QueryPilot(ObjectContainer db){
    		int[]  i = new int[]{19,100};
    		ObjectSet result = db.Query(new ArbitraryQuery(i));
    		ListResult(result);
		}

		public static void TestIndexDiagnostics() {
    		Db4o.Configure().Diagnostic().RemoveAllListeners();
    		Db4o.Configure().Diagnostic().AddListener(new IndexDiagListener());
    		Db4o.Configure().UpdateDepth(3);
			File.Delete(Util.YapFileName);    
			ObjectContainer db=Db4o.OpenFile(Util.YapFileName);
			try {
        		Pilot pilot1 = new Pilot("Rubens Barrichello",99);
        		db.Set(pilot1);
        		Pilot pilot2 = new Pilot("Michael Schumacher",100);
        		db.Set(pilot2);
        		QueryPilot(db);
        		SetEmptyObject(db);
        		Query query = db.Query();
        		query.Constrain(typeof(Pilot));
				query.Descend("_points").Constrain("99");
				ObjectSet  result = query.Execute();
				ListResult(result);
			}
			finally {
				db.Close();
			}
		}
	     
		public static void TestTranslatorDiagnostics() {
    		StoreTranslatedCars();
    		RetrieveTranslatedCars();
    		RetrieveTranslatedCarsNQ();
    		RetrieveTranslatedCarsNQUnopt();
    		RetrieveTranslatedCarsSODAEv();
		}
	    
		public static void StoreTranslatedCars() {
    		Db4o.Configure().ExceptionsOnNotStorable(true);
    		Db4o.Configure().ObjectClass(typeof(Car)).Translate(new CarTranslator());
    		Db4o.Configure().ObjectClass(typeof(Car)).CallConstructor(true);
    		File.Delete(Util.YapFileName);    
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try {
				Car car1 = new Car("BMW");
				System.Diagnostics.Trace.WriteLine("ORIGINAL: " + car1);
				db.Set(car1);
				Car car2 = new Car("Ferrari");
				System.Diagnostics.Trace.WriteLine("ORIGINAL: " + car2);
				db.Set(car2);
			} catch (Exception exc) {
				System.Diagnostics.Trace.WriteLine(exc.Message);
				return;
			} finally {
				db.Close();
			}
		}

		public static void RetrieveTranslatedCars() {
    		Db4o.Configure().Diagnostic().RemoveAllListeners();
    		Db4o.Configure().Diagnostic().AddListener(new TranslatorDiagListener());
    		Db4o.Configure().ExceptionsOnNotStorable(true);
    		Db4o.Configure().ObjectClass(typeof(Car)).Translate(new CarTranslator());
    		Db4o.Configure().ObjectClass(typeof(Car)).CallConstructor(true);
    		ObjectContainer db = Db4o.OpenFile(YapFileName);
			try {
				Query query = db.Query();
				query.Constrain(typeof(Car));
				ObjectSet  result = query.Execute();
				ListResult(result);
			} finally {
				db.Close();
			}
		}

		public static void RetrieveTranslatedCarsNQ() {
    		Db4o.Configure().Diagnostic().RemoveAllListeners();
    		Db4o.Configure().Diagnostic().AddListener(new TranslatorDiagListener());
    		Db4o.Configure().ExceptionsOnNotStorable(true);
    		Db4o.Configure().ObjectClass(typeof(Car)).Translate(new CarTranslator());
    		Db4o.Configure().ObjectClass(typeof(Car)).CallConstructor(true);
    		ObjectContainer db = Db4o.OpenFile(YapFileName);
			try {
				ObjectSet  result = db.Query(new NewCarModel());
				ListResult(result);
			} finally {
				db.Close();
			}
		}
	    
		public static void RetrieveTranslatedCarsNQUnopt() {
    		Db4o.Configure().OptimizeNativeQueries(false);
    		Db4o.Configure().Diagnostic().RemoveAllListeners();
    		Db4o.Configure().Diagnostic().AddListener(new TranslatorDiagListener());
    		Db4o.Configure().ExceptionsOnNotStorable(true);
    		Db4o.Configure().ObjectClass(typeof(Car)).Translate(new CarTranslator());
    		Db4o.Configure().ObjectClass(typeof(Car)).CallConstructor(true);
    		ObjectContainer db = Db4o.OpenFile(YapFileName);
			try {
				ObjectSet  result = db.Query(new NewCarModel());
				ListResult(result);
			} finally {
				Db4o.Configure().OptimizeNativeQueries(true);
				db.Close();
			}
		}

		public static void RetrieveTranslatedCarsSODAEv() {
    		Db4o.Configure().Diagnostic().RemoveAllListeners();
    		Db4o.Configure().Diagnostic().AddListener(new TranslatorDiagListener());
    		Db4o.Configure().ExceptionsOnNotStorable(true);
    		Db4o.Configure().ObjectClass(typeof(Car)).Translate(new CarTranslator());
    		Db4o.Configure().ObjectClass(typeof(Car)).CallConstructor(true);
    		ObjectContainer db = Db4o.OpenFile(YapFileName);
			try {
				Query query = db.Query();
				query.Constrain(typeof(Car));
				query.Constrain(new CarEvaluation());
				ObjectSet  result = query.Execute();
				ListResult(result);
			} finally {
				db.Close();
			}
		}
	}
}
