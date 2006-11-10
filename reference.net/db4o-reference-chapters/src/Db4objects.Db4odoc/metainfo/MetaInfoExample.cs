/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using Db4objects.Db4o;
using Db4objects.Db4o.Ext;

namespace Db4objects.Db4odoc.MetaInfo
{
	public class MetaInfoExample 
	{
		public readonly static string YapFileName = "formula1.yap";

		public static void Main(string[] args) 
		{
			SetObjects();
			GetMetaObjects();
			GetMetaObjectsInfo();
		}
		// end Main

		public static void SetObjects()
		{
			File.Delete(YapFileName);
			IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
				oc.Set(car);
				car = new Car("Ferrari", new Pilot("Michael Schumacher"));
				oc.Set(car);
			} 
			finally 
			{
				oc.Close();
			}
		}
		// end SetObjects
		
		public static void GetMetaObjects()
		{
			IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				System.Console.WriteLine("Retrieve meta information for class: ");
				IStoredClass sc = oc.Ext().StoredClass(typeof(Car));
				System.Console.WriteLine("Stored class:  "+ sc.ToString());
				
				System.Console.WriteLine("Retrieve meta information for all classes in database: ");
				IStoredClass[] sclasses = oc.Ext().StoredClasses();
				for (int i=0; i< sclasses.Length; i++)
				{
					System.Console.WriteLine(sclasses[i].GetName());	
				}
			} 
			finally 
			{
				oc.Close();
			}
		}
		// end GetMetaObjects
		
		public static void GetMetaObjectsInfo()
		{
			IObjectContainer oc = Db4oFactory.OpenFile(YapFileName);
			try 
			{
				System.Console.WriteLine("Retrieve meta information for field: ");
				IStoredClass sc = oc.Ext().StoredClass(typeof(Car));
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
				oc.Close();
			}
		}
		// end GetMetaObjectsInfo
	}
}
