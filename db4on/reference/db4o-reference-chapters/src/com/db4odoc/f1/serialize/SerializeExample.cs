/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using System.Xml.Serialization;
using com.db4o;


namespace com.db4odoc.f1.serialize
{
	public class SerializeExample
	{
		public readonly static string XmlFileName = "formula1.xml";
		public readonly static string YapFileName = "formula1.yap";

		public static void Main(String[] args) 
		{
			SetObjects();
			ExportToXml();
			ImportFromXml();
		}
		// end Main

		public static void SetObjects()
		{
			File.Delete(YapFileName);
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				Car car = new Car("BMW", new Pilot("Rubens Barrichello"));
				db.Set(car);
				car = new Car("Ferrari", new Pilot("Michael Schumacher"));
				db.Set(car);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end SetObjects

		public static void ExportToXml()
		{
			XmlSerializer carSerializer = new XmlSerializer(typeof(Car[]));
			StreamWriter xmlWriter = new StreamWriter(XmlFileName);
			ObjectContainer db = Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet result = db.Get(typeof(Car));
				Car[] cars = new Car[result.Size()];
				for (int i = 0; i < result.Size(); i++)
				{
					Car car = (Car)result[i];
					cars.SetValue(car,i);
				}
				carSerializer.Serialize(xmlWriter, cars);
				xmlWriter.Close();
			}
			finally
			{
				db.Close();
			}
		}
		// end ExportToXml

		public static void ImportFromXml()
		{
			File.Delete(YapFileName);
			XmlSerializer carSerializer = new XmlSerializer(typeof(Car[]));
			FileStream xmlFileStream = new FileStream(XmlFileName, FileMode.Open);
			Car[] cars = (Car[])carSerializer.Deserialize(xmlFileStream);
			ObjectContainer db;
			for (int i = 0; i < cars.Length; i++)
			{
				db = Db4o.OpenFile(YapFileName);
				try 
				{
					Car car = (Car)cars[i];
					db.Set(car);
				} 
				finally 
				{
					db.Close();
				}
			}
			db = Db4o.OpenFile(YapFileName);
			try 
			{
				ObjectSet result = db.Get(typeof(Pilot));
				ListResult(result);
				result = db.Get(typeof(Car));
				ListResult(result);
			} 
			finally 
			{
				db.Close();
			}
		}
		// end ImportFromXml

		public static void ListResult(ObjectSet result)
		{
			Console.WriteLine(result.Count);
			foreach (object item in result)
			{
				Console.WriteLine(item);
			}
		}
		// end ListResult
	}
}
