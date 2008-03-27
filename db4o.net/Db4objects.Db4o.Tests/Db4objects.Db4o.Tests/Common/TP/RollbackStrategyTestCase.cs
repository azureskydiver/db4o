/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using System;
using Db4oUnit.Extensions;
using Db4oUnit.Mocking;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.TA;
using Db4objects.Db4o.Tests.Common.TP;

namespace Db4objects.Db4o.Tests.Common.TP
{
	public class RollbackStrategyTestCase : AbstractDb4oTestCase
	{
		private readonly RollbackStrategyMock _mock = new RollbackStrategyMock();

		/// <exception cref="Exception"></exception>
		protected override void Configure(IConfiguration config)
		{
			config.Add(new TransparentPersistenceSupport(_mock));
		}

		public virtual void TestRollbackStrategyIsCalledForChangedObjects()
		{
			Item item1 = StoreItem("foo");
			Item item2 = StoreItem("bar");
			StoreItem("baz");
			Change(item1);
			Change(item2);
			_mock.Verify(new MethodCall[0]);
			Db().Rollback();
			_mock.Verify(new MethodCall[] { new MethodCall("rollback", Db(), item2), new MethodCall
				("rollback", Db(), item1) });
		}

		private void Change(Item item)
		{
			item.SetName(item.GetName() + "*");
		}

		private Item StoreItem(string name)
		{
			Item item = new Item(name);
			Store(item);
			return item;
		}

		public static void Main(string[] args)
		{
			new RollbackStrategyTestCase().RunAll();
		}
	}
}
