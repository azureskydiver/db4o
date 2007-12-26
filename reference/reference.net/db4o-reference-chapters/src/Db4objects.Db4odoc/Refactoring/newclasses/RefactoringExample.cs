/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Ext;

namespace Db4objects.Db4odoc.Refactoring.NewClasses
{
	public class RefactoringExample
	{
		private const string Db4oFileName = "reference.db4o";
	
		public static void Main(string[] args) 
		{
			ReopenDB();
			TransferValues();
		}
		// end Main

		private static void ReopenDB()
		{
            IConfiguration configuration = Db4oFactory.NewConfiguration();
            configuration.DetectSchemaChanges(false);
            IObjectContainer container = Db4oFactory.OpenFile(configuration, Db4oFileName);
			container.Close();
		}
		// end ReopenDB

        private static void TransferValues()
		{
			IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IStoredClass sc = container.Ext().StoredClass(typeof(Pilot));
				System.Console.WriteLine("Stored class:  "+ sc.ToString());
				IStoredField sfOld = sc.StoredField("_name",typeof(string));
				System.Console.WriteLine("Old field:  "+ sfOld.ToString()+";"+sfOld.GetStoredType());
				IQuery q = container.Query();
				q.Constrain(typeof(Pilot));
				IObjectSet result = q.Execute();
				foreach (object obj in result)
				{
					Pilot pilot = (Pilot)obj;
					pilot.Name = new Identity(sfOld.Get(pilot).ToString(),"");
					System.Console.WriteLine("Pilot="+ pilot);
					container.Set(pilot);
				}
			} 
			finally 
			{
				container.Close();
			}
		}
		// end TransferValues
	}
}
