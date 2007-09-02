/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using Db4oUnit;
using Db4oUnit.Extensions;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Tests.Common.Set;

namespace Db4objects.Db4o.Tests.Common.Set
{
	public class DeepSetClientServerTestCase : Db4oClientServerTestCase
	{
		public static void Main(string[] args)
		{
			new DeepSetClientServerTestCase().RunAll();
		}

		public class Item
		{
			public DeepSetClientServerTestCase.Item child;

			public string name;
		}

		protected override void Store()
		{
			DeepSetClientServerTestCase.Item item = new DeepSetClientServerTestCase.Item();
			item.name = "1";
			item.child = new DeepSetClientServerTestCase.Item();
			item.child.name = "2";
			item.child.child = new DeepSetClientServerTestCase.Item();
			item.child.child.name = "3";
			Store(item);
		}

		public virtual void Test()
		{
			IExtObjectContainer oc1 = OpenNewClient();
			IExtObjectContainer oc2 = OpenNewClient();
			IExtObjectContainer oc3 = OpenNewClient();
			DeepSetClientServerTestCase.Item example = new DeepSetClientServerTestCase.Item();
			example.name = "1";
			try
			{
				DeepSetClientServerTestCase.Item item1 = (DeepSetClientServerTestCase.Item)oc1.Get
					(example).Next();
				Assert.AreEqual("1", item1.name);
				Assert.AreEqual("2", item1.child.name);
				Assert.AreEqual("3", item1.child.child.name);
				DeepSetClientServerTestCase.Item item2 = (DeepSetClientServerTestCase.Item)oc2.Get
					(example).Next();
				Assert.AreEqual("1", item2.name);
				Assert.AreEqual("2", item2.child.name);
				Assert.AreEqual("3", item2.child.child.name);
				item1.child.name = "12";
				item1.child.child.name = "13";
				oc1.Set(item1, 2);
				oc1.Commit();
				DeepSetClientServerTestCase.Item item = (DeepSetClientServerTestCase.Item)oc1.Get
					(example).Next();
				Assert.AreEqual("1", item.name);
				Assert.AreEqual("12", item.child.name);
				Assert.AreEqual("13", item.child.child.name);
				item = (DeepSetClientServerTestCase.Item)oc2.Get(example).Next();
				oc2.Refresh(item, 3);
				Assert.AreEqual("1", item.name);
				Assert.AreEqual("12", item.child.name);
				Assert.AreEqual("3", item.child.child.name);
				item = (DeepSetClientServerTestCase.Item)oc3.Get(example).Next();
				Assert.AreEqual("1", item.name);
				Assert.AreEqual("12", item.child.name);
				Assert.AreEqual("3", item.child.child.name);
			}
			finally
			{
				oc1.Close();
				oc2.Close();
				oc3.Close();
			}
		}
	}
}
