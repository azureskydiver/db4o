/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using System.Collections;

using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.SelectivePersistence
{
	public class MarkTransientExample
	{
		private const string Db4oFileName = "reference.db4o";

		public static void Main(string[] args)
		{
			IConfiguration configuration = ConfigureTransient();
            SaveObjects(configuration);
            RetrieveObjects();
		}
		// end main

		private static IConfiguration ConfigureTransient()
		{
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.MarkTransient("Db4objects.Db4odoc.SelectivePersistence.FieldTransient");
            return configuration;
		}
		// end ConfigureTransient

        private static IConfiguration ConfigureSaveTransient()
        {
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.ObjectClass(typeof(Test)).StoreTransientFields(true);
            return configuration;
        }
        // end ConfigureSaveTransient

        private static void SaveObjects(IConfiguration configuration)
		{
			File.Delete(Db4oFileName);
            IObjectContainer container = Db4oFactory.OpenFile(configuration, Db4oFileName);
			try 
			{
				Test test = new Test("Transient string","Persistent string");
				container.Set(test);
				TestCustomized testc = new TestCustomized("Transient string","Persistent string");
				container.Set(testc);
			} 
			finally 
			{
				container.Close();
			}
		}
		// end SaveObjects

        private static void RetrieveObjects()
		{
            IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IQuery query = container.Query();
				query.Constrain(typeof(object));
				IList result = query.Execute();
				ListResult(result);
			} 
			finally 
			{
				container.Close();
			}
		}
		// end RetrieveObjects


        private static void ListResult(IList result)
		{
			Console.WriteLine(result.Count);
			for(int x = 0; x < result.Count; x++)
				Console.WriteLine(result[x]);
		}
		// end ListResult
	}
}
