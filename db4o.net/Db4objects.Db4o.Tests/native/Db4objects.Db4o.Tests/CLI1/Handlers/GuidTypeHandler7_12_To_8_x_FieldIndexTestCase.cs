/* Copyright (C) 2012 Versant Inc.   http://www.db4o.com */
using System;
using System.IO;
using System.Linq;
using Db4oUnit;
using Db4objects.Db4o.Defragment;
using Db4objects.Db4o.Tests.Common.Migration;

namespace Db4objects.Db4o.Tests.CLI1.Handlers
{
	public class GuidTypeHandler7_12_To_8_x_FieldIndexTestCase : ITestLifeCycle
	{
#if !CF && !SILVERLIGHT
		class Item
		{
			public Guid id;
			public string name;
		}

		public void SetUp()
		{
			db4oFilePath = Path.GetTempFileName();
		}

		public void TearDown()
		{
			testsFinished++;
			if (testsFinished == testMethodCount)
			{
				library.environment.Dispose();	
			}

			if (db4oFilePath != null)
			{
				File.Delete(db4oFilePath);
			}
		}

		public void TestDefragWorksAfterReopening()
		{
			Library().environment.InvokeInstanceMethod(GetType(), "CreateDatabase", db4oFilePath, true);

			using(var db = Db4oEmbedded.OpenFile(db4oFilePath))
			{
			}

			var config = new DefragmentConfig(db4oFilePath);
			config.ForceBackupDelete(true);
			Defragment.Defragment.Defrag(config);
		}

		public void TestStoreWorksAfterReopening()
		{
			db4oFilePath = Path.GetTempFileName();
			Library().environment.InvokeInstanceMethod(GetType(), "CreateDatabase", db4oFilePath, true);

			using (var db = Db4oEmbedded.OpenFile(db4oFilePath))
			{
			}

			using(var db = Db4oEmbedded.OpenFile(db4oFilePath))
			{
				db.Store(new Item { id = Guid.NewGuid(), name= "Foo " + Db4oVersion.Name});
			}
		}
		
		public void CreateDatabase(string path, bool indexed)
		{
			var config = Db4oFactory.NewConfiguration();
			config.ObjectClass(typeof (Item)).ObjectField("id").Indexed(indexed);

			using (var db = Db4oFactory.OpenFile(config, path))
			{
				db.Store(new Item { id = Guid.NewGuid(), name = "Foo" });
				db.Store(new Item { id = Guid.NewGuid(), name = "Bar" });
				db.Store(new Item { id = Guid.NewGuid(), name = "Baz" });
			}
		}

		private Db4oLibrary Library()
		{
			return library ?? (library = Librarian().ForVersion("7.1"));
		}

		private Db4oLibrarian Librarian()
		{
			return new Db4oLibrarian(new Db4oLibraryEnvironmentProvider(PathProvider.TestCasePath()));
		}

		static GuidTypeHandler7_12_To_8_x_FieldIndexTestCase()
		{
			testMethodCount = TestMethodCountFor(typeof(GuidTypeHandler7_12_To_8_x_FieldIndexTestCase));
		}

		private static int TestMethodCountFor(Type type)
		{
			return type.GetMethods().Count(method => method.Name.StartsWith("Test") && method.GetParameters().Length == 0);
		}

		private string db4oFilePath;
		private static Db4oLibrary library;
		private static int testMethodCount;
		private static int testsFinished;
#else
		public void SetUp()
		{
		}

		public void TearDown()
		{
		}
#endif
	}
}
