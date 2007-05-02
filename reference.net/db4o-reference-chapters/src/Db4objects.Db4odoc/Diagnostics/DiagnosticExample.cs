/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Diagnostic;

namespace Db4objects.Db4odoc.Diagnostics
{
	public class DiagnosticExample 
	{
		private const string Db4oFileName = "reference.db4o";

        public static void Main(string[] args)
        {
            TestEmpty();
            TestArbitrary();
            TestIndexDiagnostics();
            TestTranslatorDiagnostics();
        }
        // end Main

		 private static void TestEmpty() {
            File.Delete(Db4oFileName);
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.Diagnostic().AddListener(new DiagnosticToConsole());
			IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
			try {
        		SetEmptyObject(db);
			}
			finally {
				db.Close();
			}
		}
		// end TestEmpty
	    
		private static void SetEmptyObject(IObjectContainer db){
    		Empty empty = new Empty();
			db.Set(empty);
		}
		// end SetEmptyObject
	    	
		private static void TestArbitrary() {
            File.Delete(Db4oFileName);    
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.Diagnostic().AddListener(new DiagnosticToConsole());
    		IObjectContainer db=Db4oFactory.OpenFile(configuration, Db4oFileName);
			try {
        		Pilot pilot = new Pilot("Rubens Barrichello",99);
        		db.Set(pilot);
        		QueryPilot(db);
			}
			finally {
				db.Close();
			}
		}
		// end TestArbitrary
		
		private static void QueryPilot(IObjectContainer db){
    		int[]  i = new int[]{19,100};
    		IObjectSet result = db.Query(new ArbitraryQuery(i));
    		ListResult(result);
		}
		// end QueryPilot

        private static void TestIndexDiagnostics()
        {
			File.Delete(Db4oFileName);
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.Diagnostic().RemoveAllListeners();
            configuration.Diagnostic().AddListener(new IndexDiagListener());
            configuration.UpdateDepth(3);
			IObjectContainer db=Db4oFactory.OpenFile(configuration, Db4oFileName);
			try {
        		Pilot pilot1 = new Pilot("Rubens Barrichello",99);
        		db.Set(pilot1);
        		Pilot pilot2 = new Pilot("Michael Schumacher",100);
        		db.Set(pilot2);
        		QueryPilot(db);
        		SetEmptyObject(db);
        		IQuery query = db.Query();
        		query.Constrain(typeof(Pilot));
				query.Descend("_points").Constrain("99");
				IObjectSet  result = query.Execute();
				ListResult(result);
			}
			finally {
				db.Close();
			}
		}
		// end TestIndexDiagnostics

        private static void TestTranslatorDiagnostics()
        {
    		StoreTranslatedCars();
    		RetrieveTranslatedCars();
    		RetrieveTranslatedCarsNQ();
    		RetrieveTranslatedCarsNQUnopt();
    		RetrieveTranslatedCarsSODAEv();
		}
		// end TestTranslatorDiagnostics

        private static void StoreTranslatedCars()
        {
    		File.Delete(Db4oFileName);
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ExceptionsOnNotStorable(true);
            configuration.ObjectClass(typeof(Car)).Translate(new CarTranslator());
            configuration.ObjectClass(typeof(Car)).CallConstructor(true);
    		
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
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
		// end StoreTranslatedCars

        private static void RetrieveTranslatedCars()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.Diagnostic().RemoveAllListeners();
            configuration.Diagnostic().AddListener(new TranslatorDiagListener());
            configuration.ExceptionsOnNotStorable(true);
            configuration.ObjectClass(typeof(Car)).Translate(new CarTranslator());
            configuration.ObjectClass(typeof(Car)).CallConstructor(true);
    		IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
			try {
				IQuery query = db.Query();
				query.Constrain(typeof(Car));
				IObjectSet  result = query.Execute();
				ListResult(result);
			} finally {
				db.Close();
			}
		}
		// end RetrieveTranslatedCars

        private static void RetrieveTranslatedCarsNQ()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.Diagnostic().RemoveAllListeners();
            configuration.Diagnostic().AddListener(new TranslatorDiagListener());
            configuration.ExceptionsOnNotStorable(true);
            configuration.ObjectClass(typeof(Car)).Translate(new CarTranslator());
            configuration.ObjectClass(typeof(Car)).CallConstructor(true);
    		IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
			try {
				IObjectSet  result = db.Query(new NewCarModel());
				ListResult(result);
			} finally {
				db.Close();
			}
		}
		// end RetrieveTranslatedCarsNQ

        private static void RetrieveTranslatedCarsNQUnopt()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.OptimizeNativeQueries(false);
            configuration.Diagnostic().RemoveAllListeners();
            configuration.Diagnostic().AddListener(new TranslatorDiagListener());
            configuration.ExceptionsOnNotStorable(true);
            configuration.ObjectClass(typeof(Car)).Translate(new CarTranslator());
            configuration.ObjectClass(typeof(Car)).CallConstructor(true);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
			try {
				IObjectSet  result = db.Query(new NewCarModel());
				ListResult(result);
			} finally {
				Db4oFactory.Configure().OptimizeNativeQueries(true);
				db.Close();
			}
		}
		// end RetrieveTranslatedCarsNQUnopt

        private static void RetrieveTranslatedCarsSODAEv()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.Diagnostic().RemoveAllListeners();
            configuration.Diagnostic().AddListener(new TranslatorDiagListener());
            configuration.ExceptionsOnNotStorable(true);
            configuration.ObjectClass(typeof(Car)).Translate(new CarTranslator());
            configuration.ObjectClass(typeof(Car)).CallConstructor(true);
            IObjectContainer db = Db4oFactory.OpenFile(configuration, Db4oFileName);
			try {
				IQuery query = db.Query();
				query.Constrain(typeof(Car));
				query.Constrain(new CarEvaluation());
				IObjectSet  result = query.Execute();
				ListResult(result);
			} finally {
				db.Close();
			}
		}
		// end RetrieveTranslatedCarsSODAEv

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
