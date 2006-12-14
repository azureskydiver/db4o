/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using System.Collections;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;

namespace Db4objects.Db4odoc.SelectivePersistence
{
	public class MarkTransientExample
	{
		public readonly static string YapFileName = "formula1.yap";

		public static void Main(string[] args)
		{
			ConfigureTransient();
			SaveObjects();
			RetrieveObjects();
		}
		// end main

		public static void ConfigureTransient()
		{
			Db4oFactory.Configure().MarkTransient("Db4objects.Db4odoc.SelectivePersistence.FieldTransient");
		}
		// end ConfigureTransient

		public static void SaveObjects()
		{
			File.Delete(YapFileName);
			IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				Test test = new Test("Transient string","Persistent string");
				oc.Set(test);
				TestCusomized testc = new TestCusomized("Transient string","Persistent string");
				oc.Set(testc);
			} 
			finally 
			{
				oc.Close();
			}
		}
		// end SaveObjects

		public static void RetrieveObjects()
		{
			IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IQuery query = oc.Query();
				query.Constrain(typeof(object));
				IList result = query.Execute();
				ListResult(result);
			} 
			finally 
			{
				oc.Close();
			}
		}
		// end RetrieveObjects

		
		public static void ListResult(IList result)
		{
			Console.WriteLine(result.Count);
			for(int x = 0; x < result.Count; x++)
				Console.WriteLine(result[x]);
		}
		// end ListResult
	}
}
