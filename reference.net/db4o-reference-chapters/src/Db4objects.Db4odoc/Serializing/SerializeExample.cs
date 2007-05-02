/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */
using System;
using System.IO;
using System.Xml.Serialization;
using Db4objects.Db4o;


namespace Db4objects.Db4odoc.Serializing
{
	public class SerializeExample
	{
		private const string XmlFileName = "reference.xml";
		private const string Db4oFileName = "reference.db4o";

		public static void Main(string[] args) 
		{
			SetObjects();
			ExportToXml();
			ImportFromXml();
		}
		// end Main

		private static void SetObjects()
		{
			File.Delete(Db4oFileName);
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
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

        private static void ExportToXml()
		{
			XmlSerializer carSerializer = new XmlSerializer(typeof(Car[]));
			StreamWriter xmlWriter = new StreamWriter(XmlFileName);
			IObjectContainer db = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IObjectSet result = db.Get(typeof(Car));
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

        private static void ImportFromXml()
		{
			File.Delete(Db4oFileName);
			XmlSerializer carSerializer = new XmlSerializer(typeof(Car[]));
			FileStream xmlFileStream = new FileStream(XmlFileName, FileMode.Open);
			Car[] cars = (Car[])carSerializer.Deserialize(xmlFileStream);
			IObjectContainer db;
			for (int i = 0; i < cars.Length; i++)
			{
				db = Db4oFactory.OpenFile(Db4oFileName);
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
			db = Db4oFactory.OpenFile(Db4oFileName);
			try 
			{
				IObjectSet result = db.Get(typeof(Pilot));
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

        private static void ListResult(IObjectSet result)
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
