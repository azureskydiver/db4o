namespace Db4oUnit.Extensions.tests
{
	public class SimpleDb4oTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class Data
		{
		}

		private bool[] _everythingCalled = new bool[3];

		private Db4oUnit.Extensions.Db4oFixture _expectedFixture;

		protected override void Configure(com.db4o.config.Configuration config)
		{
			Db4oUnit.Assert.AreSame(_expectedFixture, Fixture());
			Db4oUnit.Assert.IsTrue(EverythingCalledBefore(0));
			_everythingCalled[0] = true;
		}

		protected override void Store()
		{
			Db4oUnit.Assert.IsTrue(EverythingCalledBefore(1));
			_everythingCalled[1] = true;
			Fixture().Db().Set(new Db4oUnit.Extensions.tests.SimpleDb4oTestCase.Data());
		}

		public virtual void TestResultSize()
		{
			Db4oUnit.Assert.IsTrue(EverythingCalledBefore(2));
			_everythingCalled[2] = true;
			Db4oUnit.Assert.AreEqual(1, Fixture().Db().Get(typeof(Db4oUnit.Extensions.tests.SimpleDb4oTestCase.Data)
				).Size());
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

		public virtual void ExpectedFixture(Db4oUnit.Extensions.Db4oFixture fixture)
		{
			_expectedFixture = fixture;
		}
	}
}
