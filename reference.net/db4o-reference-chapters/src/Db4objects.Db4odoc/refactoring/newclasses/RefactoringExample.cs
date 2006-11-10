/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Ext;

namespace Db4objects.Db4odoc.Refactoring.NewClasses
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
			Db4oFactory.Configure().DetectSchemaChanges(false);
			IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
			oc.Close();
		}
		// end ReopenDB

		public static void TransferValues()
		{
			IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				IStoredClass sc = oc.Ext().StoredClass(typeof(Pilot));
				System.Console.WriteLine("Stored class:  "+ sc.ToString());
				IStoredField sfOld = sc.StoredField("_name",typeof(string));
				System.Console.WriteLine("Old field:  "+ sfOld.ToString()+";"+sfOld.GetStoredType());
				IQuery q = oc.Query();
				q.Constrain(typeof(Pilot));
				IObjectSet result = q.Execute();
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
