/* Copyright (C) 2004 - 2009  db4objects Inc.  http://www.db4o.com */

using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using Db4objects.Db4o;
using NUnit.Framework;
using OManager.BusinessLayer.Login;
using OManager.DataLayer.Connection;
using OManager.DataLayer.Modal;
using Sharpen.Lang;

namespace OMNUnitTest
{
	[TestFixture]
	public class DbInformationTestCase
	{
		[SetUp]
		public void Setup()
		{
			GenerateDatabase();
		}

		[TearDown]
		public void TearDown()
		{
			IObjectContainer client = Db4oClient.Client;
			if (null != client)
			{
				string connection = Db4oClient.conn.Connection;
				Db4oClient.CloseConnection();
				File.Delete(connection);
			}
		}

		private void GenerateDatabase()
		{
			string databaseFile = Path.GetTempFileName();
			Db4oClient.conn = new ConnParams(databaseFile);

			Store(new Item("foo"), new Element("bar"), new Item("baz"), new ArrayList(new[] {1, 2, 3}));
		}

		protected virtual void Store(params object[] items)
		{
			foreach (object item in items)
			{
				Db4oClient.Client.Store(item);
			}
		}

		[Test]
		public void TestGetAllClasses()
		{
			Hashtable classes = new DbInformation().StoredClasses();
			CollectionAssert.AreEqual(ClassesCollection(typeof(Item), typeof(Element), typeof(ArrayList)), classes);
		}

		[Test]
		public void TestClassesCollectionByAssembly()
		{
			Hashtable classesByAssembly = new DbInformation().StoredClassesByAssembly();

			foreach (DictionaryEntry entry in ClassesCollectionByAssembly(typeof(Item), typeof(Element), typeof(ArrayList)))
			{
				Assert.IsTrue(classesByAssembly.ContainsKey(entry.Key));
				CollectionAssert.AreEqual((IEnumerable) entry.Value, (IEnumerable) classesByAssembly[entry.Key]);
			}
		}

		private static Hashtable ClassesCollectionByAssembly(params Type[] types)
		{
			Hashtable classesByAssembly = new Hashtable();
			foreach (var type in types)
			{
				string assemblyName = type.Assembly.GetName().Name;
				if (!classesByAssembly.ContainsKey(assemblyName))
				{
					classesByAssembly[assemblyName] = new List<string>();
				}

				((List<string>)classesByAssembly[assemblyName]).Add(TypeReference.FromType(type).GetUnversionedName());
			}

			return classesByAssembly;
		}

		private static IEnumerable ClassesCollection(params Type[] types)
		{
			Hashtable coll = new Hashtable();

			foreach (var type in types)
			{
				TypeReference reference = TypeReference.FromType(type);
				coll.Add(reference.GetUnversionedName(), type.FullName);
			}

			return coll;
		}
	}

	internal class Element : Item
	{
		public Element(string name) : base(name)
		{
		}
	}

	public class Item
	{
		private readonly string _name;

		public Item(string name)
		{
			_name = name;
		}

		public string Name
		{
			get { return _name; }
		}
	}
}
