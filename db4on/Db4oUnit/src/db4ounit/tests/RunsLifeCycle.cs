namespace Db4oUnit.tests
{
	public class RunsLifeCycle : Db4oUnit.TestCase, Db4oUnit.TestLifeCycle
	{
		private bool _setupCalled = false;

		private bool _tearDownCalled = false;

		public virtual void SetUp()
		{
			_setupCalled = true;
		}

		public virtual void TearDown()
		{
			_tearDownCalled = true;
		}

		public virtual bool SetupCalled()
		{
			return _setupCalled;
		}

		public virtual bool TearDownCalled()
		{
			return _tearDownCalled;
		}

		public virtual void TestMethod()
		{
			Db4oUnit.Assert.IsTrue(_setupCalled);
			Db4oUnit.Assert.IsTrue(!_tearDownCalled);
			throw Db4oUnit.tests.FrameworkTestCase.EXCEPTION;
		}
	}
}
