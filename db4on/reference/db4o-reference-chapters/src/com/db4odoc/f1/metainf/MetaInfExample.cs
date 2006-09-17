/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using com.db4o;
using com.db4o.ext;

namespace com.db4odoc.f1.metainf
{
	public class MetaInfExample: Util 
	{
		public static void main(String[] args) 
		{
			SetObjects();
			GetMetaObjects();
			GetMetaObjectsInfo();
		}

		public static void SetObjects()
		{
			File.Delete(Util.YapFileName);
			ObjectContainer oc = Db4o.OpenFile(Util.YapFileName);
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
		
		public static void GetMetaObjects()
		{
			ObjectContainer oc = Db4o.OpenFile(Util.YapFileName);
			try 
			{
				System.Console.WriteLine("Retrieve meta information for class: ");
				StoredClass sc = oc.Ext().StoredClass(typeof(Car));
				System.Console.WriteLine("Stored class:  "+ sc.ToString());
				
				System.Console.WriteLine("Retrieve meta information for all classes in database: ");
				StoredClass[] sclasses = oc.Ext().StoredClasses();
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
		
		public static void GetMetaObjectsInfo()
		{
			ObjectContainer oc = Db4o.OpenFile(Util.YapFileName);
			try 
			{
				System.Console.WriteLine("Retrieve meta information for field: ");
				StoredClass sc = oc.Ext().StoredClass(typeof(Car));
				StoredField sf = sc.StoredField("_pilot",typeof(Pilot));
				System.Console.WriteLine("Field info:  "+ sf.GetName()+"/"+sf.GetStoredType()+"/IsArray="+sf.IsArray());
				
				System.Console.WriteLine("Retrieve all fields: ");
				StoredField[] sfields = sc.GetStoredFields();
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
	}
}
