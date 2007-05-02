/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Ext;

namespace Db4objects.Db4odoc.MetaInfo
{
	public class MetaInfoExample 
	{
		private const string Db4oFileName = "reference.db4o";

		public static void Main(string[] args) 
		{
			SetObjects();
			GetMetaObjects();
			GetMetaObjectsInfo();
		}
		// end Main

		private static void SetObjects()
		{
			File.Delete(Db4oFileName);
			IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
				container.Set(car);
				car = new Car("Ferrari", new Pilot("Michael Schumacher"));
				container.Set(car);
			} 
			finally 
			{
				container.Close();
			}
		}
		// end SetObjects
		
		private static void GetMetaObjects()
		{
			IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				System.Console.WriteLine("Retrieve meta information for class: ");
				IStoredClass sc = container.Ext().StoredClass(typeof(Car));
				System.Console.WriteLine("Stored class:  "+ sc.ToString());
				
				System.Console.WriteLine("Retrieve meta information for all classes in database: ");
				IStoredClass[] sclasses = container.Ext().StoredClasses();
				for (int i=0; i< sclasses.Length; i++)
				{
					System.Console.WriteLine(sclasses[i].GetName());	
				}
			} 
			finally 
			{
				container.Close();
			}
		}
		// end GetMetaObjects

        private static void GetMetaObjectsInfo()
		{
			IObjectContainer container = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				System.Console.WriteLine("Retrieve meta information for field: ");
				IStoredClass sc = container.Ext().StoredClass(typeof(Car));
				IStoredField sf = sc.StoredField("_pilot",typeof(Pilot));
				System.Console.WriteLine("Field info:  "+ sf.GetName()+"/"+sf.GetStoredType()+"/IsArray="+sf.IsArray());
				
				System.Console.WriteLine("Retrieve all fields: ");
				IStoredField[] sfields = sc.GetStoredFields();
				for (int i=0; i< sfields.Length; i++)
				{
					System.Console.WriteLine("Stored field:  "+ sfields[i].GetName()+"/"+sfields[i].GetStoredType());
				}
			} 
			finally 
			{
				container.Close();
			}
		}
		// end GetMetaObjectsInfo
	}
}
