/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;

using Db4objects.Db4o;
using Db4objects.Db4o.Config;

namespace Db4objects.Db4odoc.Marshal
{
    class CustomMarshallerExample
    {

	private const string Db4oFileName = "reference.db4o";
	private static ItemMarshaller marshaller = null;
	
	public static void Main(string[] args) {
        IConfiguration configuration = Db4oFactory.NewConfiguration();
		// store objects using standard mashaller
		StoreObjects(configuration);
		// retrieve objects using standard marshaller
		RetrieveObjects(configuration);
		// store and retrieve objects using the customized Item class marshaller
		configuration = ConfigureMarshaller();
        StoreObjects(configuration);
        RetrieveObjects(configuration);
	}
	// end Main
	
	private static IConfiguration ConfigureMarshaller(){
		marshaller = new ItemMarshaller();
        IConfiguration configuration = Db4oFactory.NewConfiguration();
        configuration.ObjectClass(typeof(Item)).MarshallWith(marshaller);
        return configuration;
	}
	// end ConfigureMarshaller

        private static void StoreObjects(IConfiguration configuration)
        {
		File.Delete(Db4oFileName);
        IObjectContainer container = Db4oFactory.OpenFile(configuration, Db4oFileName);
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

        private static void RetrieveObjects(IConfiguration configuration)
        {
            IObjectContainer container = Db4oFactory.OpenFile(configuration, Db4oFileName);
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
