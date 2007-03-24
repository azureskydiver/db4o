/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;

namespace Db4objects.Db4odoc.marshal
{
    class CustomMarshallerExample
    {

	private const string DbFile = "test.db";
	private static ItemMarshaller marshaller = null;
	
	public static void Main(string[] args) {
		// store objects using standard mashaller
		StoreObjects();
		// retrieve objects using standard marshaller
		RetrieveObjects();
		// store and retrieve objects using the customized Item class marshaller
		//ConfigureMarshaller();
		StoreObjects();
		RetrieveObjects();
	}
	// end Main
	
	private static void ConfigureMarshaller(){
		marshaller = new ItemMarshaller();
		Db4oFactory.Configure().ObjectClass(typeof(Item)).MarshallWith(marshaller);
	}
	// end ConfigureMarshaller
	
	private static void StoreObjects(){
		File.Delete(DbFile);
		IObjectContainer container = Db4oFactory.OpenFile(DbFile);
		try {
			Item item;
            DateTime dt1 = DateTime.UtcNow;
			for (int i = 0; i < 50000; i++){
				item = new Item(0xFFAF, 0xFFFFFFF, 120);
				container.Set(item);
			}
            DateTime dt2 = DateTime.UtcNow;
            TimeSpan diff = dt2 - dt1;
			System.Console.WriteLine("Time to store the objects ="+ diff.Milliseconds + " ms");
		} finally {
			container.Close();
		}
	}
	// end StoreObjects
		
	private static void RetrieveObjects(){
		IObjectContainer container = Db4oFactory.OpenFile(DbFile);
		try {
            IObjectSet result = container.Get(typeof(Item));
            DateTime dt1 = DateTime.UtcNow;
            while (result.HasNext())
            {
                Item item = (Item)result.Next();
            }
            DateTime dt2 = DateTime.UtcNow;
            TimeSpan diff = dt2 - dt1;
            System.Console.WriteLine("Time to read the objects =" + diff.Milliseconds + " ms");
		} finally {
			container.Close();
		}
	}
	// end RetrieveObjects
		
	private static void ListResult(IObjectSet result) {
        System.Console.WriteLine(result.Size());
        // print only the first result
        if (result.HasNext())
            System.Console.WriteLine(result.Next());
    }
    // end ListResult
    }
}
