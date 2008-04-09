/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using Db4oUnit;
using Db4oUnit.Extensions;
using Db4oUnit.Extensions.Tests;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Foundation;

namespace Db4oUnit.Extensions.Tests
{
	public class SimpleDb4oTestCase : AbstractDb4oTestCase
	{
		public static readonly DynamicVariable ExpectedFixtureVariable = new DynamicVariable
			();

		public class Data
		{
		}

		private bool[] _everythingCalled = new bool[3];

		protected override void Configure(IConfiguration config)
		{
			Assert.AreSame(ExpectedFixture(), Fixture());
			Assert.IsTrue(EverythingCalledBefore(0));
			_everythingCalled[0] = true;
		}

		private IDb4oFixture ExpectedFixture()
		{
			return (IDb4oFixture)ExpectedFixtureVariable.Value;
		}

		protected override void Store()
		{
			Assert.IsTrue(EverythingCalledBefore(1));
			_everythingCalled[1] = true;
			Fixture().Db().Store(new SimpleDb4oTestCase.Data());
		}

		public virtual void TestResultSize()
		{
			Assert.IsTrue(EverythingCalledBefore(2));
			_everythingCalled[2] = true;
			Assert.AreEqual(1, Fixture().Db().QueryByExample(typeof(SimpleDb4oTestCase.Data))
				.Size());
		}

		public virtual bool EverythingCalled()
		{
			return EverythingCalledBefore(_everythingCalled.Length);
		}

		public virtual bool EverythingCalledBefore(int idx)
		{
			for (int i = 0; i < idx; i++)
			{
				if (!_everythingCalled[i])
				{
					return false;
				}
			}
			for (int i = idx; i < _everythingCalled.Length; i++)
			{
				if (_everythingCalled[i])
				{
					return false;
				}
			}
			return true;
		}
	}
}
