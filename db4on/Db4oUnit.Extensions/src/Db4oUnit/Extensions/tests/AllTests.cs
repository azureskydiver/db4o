namespace Db4oUnit.Extensions.tests
{
	public class AllTests : Db4oUnit.TestCase
	{
		public static void Main(string[] args)
		{
			new Db4oUnit.TestRunner(typeof(Db4oUnit.Extensions.tests.AllTests)).Run();
		}

		public virtual void TestSingleTestWithDifferentFixtures()
		{
			AssertSimpleDb4o(new Db4oUnit.Extensions.Fixtures.Db4oInMemory());
			AssertSimpleDb4o(new Db4oUnit.Extensions.Fixtures.Db4oSolo());
		}

		public virtual void TestMultipleTestsSingleFixture()
		{
			Db4oUnit.tests.FrameworkTestCase.RunTestAndExpect(new Db4oUnit.Extensions.Db4oTestSuiteBuilder
				(new Db4oUnit.Extensions.Fixtures.Db4oInMemory(), typeof(Db4oUnit.Extensions.tests.MultipleDb4oTestCase
				)).Build(), 2, false);
		}

		private void AssertSimpleDb4o(Db4oUnit.Extensions.Db4oFixture fixture)
		{
			Db4oUnit.TestSuite suite = new Db4oUnit.Extensions.Db4oTestSuiteBuilder(fixture, 
				typeof(Db4oUnit.Extensions.tests.SimpleDb4oTestCase)).Build();
			Db4oUnit.Extensions.tests.SimpleDb4oTestCase subject = GetTestSubject(suite);
			subject.ExpectedFixture(fixture);
			Db4oUnit.tests.FrameworkTestCase.RunTestAndExpect(suite, 0);
			Db4oUnit.Assert.IsTrue(subject.EverythingCalled());
		}

		private Db4oUnit.Extensions.tests.SimpleDb4oTestCase GetTestSubject(Db4oUnit.TestSuite
			 suite)
		{
			return ((Db4oUnit.Extensions.tests.SimpleDb4oTestCase)((Db4oUnit.TestMethod)suite
				.GetTests()[0]).GetSubject());
		}
	}
}
