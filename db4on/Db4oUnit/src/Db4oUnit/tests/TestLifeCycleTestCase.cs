namespace Db4oUnit.tests
{
	public class TestLifeCycleTestCase : Db4oUnit.TestCase
	{
		public virtual void TestLifeCycle()
		{
			Db4oUnit.TestSuite suite = new Db4oUnit.ReflectionTestSuiteBuilder(typeof(Db4oUnit.tests.RunsLifeCycle
				)).Build();
			Db4oUnit.tests.FrameworkTestCase.RunTestAndExpect(suite, 1);
			Db4oUnit.Assert.IsTrue(GetTestSubject(suite).TearDownCalled());
		}

		private Db4oUnit.tests.RunsLifeCycle GetTestSubject(Db4oUnit.TestSuite suite)
		{
			return ((Db4oUnit.tests.RunsLifeCycle)((Db4oUnit.TestMethod)suite.GetTests()[0]).
				GetSubject());
		}
	}
}
