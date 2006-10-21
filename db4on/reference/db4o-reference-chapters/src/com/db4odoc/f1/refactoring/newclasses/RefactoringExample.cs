/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using com.db4o;
using com.db4o.query;
using com.db4o.ext;

namespace com.db4odoc.f1.refactoring.newclasses
{
	public class RefactoringExample
	{
		public readonly static string YapFileName = "formula1.yap";
	
		public static void Main(string[] args) 
		{
			ReopenDB();
			TransferValues();
		}
		// end Main

		public static void ReopenDB()
		{
			Db4o.Configure().DetectSchemaChanges(false);
			ObjectContainer oc = Db4o.OpenFile(YapFileName);
			oc.Close();
		}
		// end ReopenDB

		public static void TransferValues()
		{
			ObjectContainer oc = Db4o.OpenFile(YapFileName);
			try 
			{
				StoredClass sc = oc.Ext().StoredClass(typeof(Pilot));
				System.Console.WriteLine("Stored class:  "+ sc.ToString());
				StoredField sfOld = sc.StoredField("_name",typeof(string));
				System.Console.WriteLine("Old field:  "+ sfOld.ToString()+";"+sfOld.GetStoredType());
				Query q = oc.Query();
				q.Constrain(typeof(Pilot));
				ObjectSet result = q.Execute();
				for (int i = 0; i< result.Size(); i++)
				{
					Pilot pilot = (Pilot)result[i];
					pilot.Name = new Identity(sfOld.Get(pilot).ToString(),"");
					System.Console.WriteLine("Pilot="+ pilot);
					oc.Set(pilot);
				}
			} 
			finally 
			{
				oc.Close();
			}
		}
		// end TransferValues
	}
}
