namespace Db4oUnit.tests
{
	public class AllTests : Db4oUnit.TestSuiteBuilder
	{
		public virtual Db4oUnit.TestSuite Build()
		{
			return new Db4oUnit.ReflectionTestSuiteBuilder(new System.Type[] { typeof(Db4oUnit.tests.FrameworkTestCase
				), typeof(Db4oUnit.tests.AssertTestCase), typeof(Db4oUnit.tests.TestLifeCycleTestCase
				), typeof(Db4oUnit.tests.ReflectionTestSuiteBuilderTestCase) }).Build();
		}

		public static void Main(string[] args)
		{
			new Db4oUnit.TestRunner(typeof(Db4oUnit.tests.AllTests)).Run();
		}
	}
}
