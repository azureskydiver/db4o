/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.ClassMapping
{
    class MappingExample
    {
        private const string FileName = "test.db";
	
	public static void Main(string[] args) {
		StoreObjects();
		RetrieveObjects();
	}
	// end Main

	private static void StoreObjects(){
		File.Delete(FileName);
		IObjectContainer container = Db4oFactory.OpenFile(FileName);
		try {
			Pilot pilot = new Pilot("Michael Schumacher", 100);
			container.Set(pilot);
			pilot = new Pilot("Rubens Barichello", 99);
			container.Set(pilot);
		} finally {
			container.Close();
		}
	}
	// end StoreObjects
	
	private static void RetrieveObjects(){
		Db4oFactory.Configure().ObjectClass(typeof(Pilot)).ReadAs(typeof(PilotReplacement));
		IObjectContainer container = Db4oFactory.OpenFile(FileName);
		try {
			IQuery query = container.Query();
			query.Constrain(typeof(PilotReplacement));
			IObjectSet result = query.Execute();
			ListResult(result);
		} finally {
			container.Close();
		}
	}
	// end RetrieveObjects
	
    public static void ListResult(IObjectSet result) {
        Console.WriteLine(result.Count);
        while(result.HasNext()) {
            Console.WriteLine(result.Next());
        }
    }
    // end ListResult

    }
}
