/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using System.Collections;
using com.db4o;
using com.db4o.query;

namespace com.db4odoc.f1.selpersist
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
			Db4o.Configure().MarkTransient("com.db4odoc.f1.selpersist.FieldTransient");
		}
		// end ConfigureTransient

		public static void SaveObjects()
		{
			File.Delete(YapFileName);
			ObjectContainer oc = Db4o.OpenFile(YapFileName);
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
			ObjectContainer oc = Db4o.OpenFile(YapFileName);
			try 
			{
				Query query = oc.Query();
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
