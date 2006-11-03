namespace Db4oUnit.Extensions.tests
{
	public class AllTests : Db4oUnit.TestCase
	{
		private sealed class ExcludingInMemoryFixture : Db4oUnit.Extensions.Fixtures.Db4oInMemory
		{
			public ExcludingInMemoryFixture(AllTests _enclosing, Db4oUnit.Extensions.Fixtures.ConfigurationSource
				 source) : base(source)
			{
				this._enclosing = _enclosing;
			}

			public override bool Accept(System.Type clazz)
			{
				return !typeof(Db4oUnit.Extensions.Fixtures.OptOutFromTestFixture).IsAssignableFrom
					(clazz);
			}

			private readonly AllTests _enclosing;
		}

		public static void Main(string[] args)
		{
			new Db4oUnit.TestRunner(typeof(Db4oUnit.Extensions.tests.AllTests)).Run();
		}

		public virtual void TestSingleTestWithDifferentFixtures()
		{
			Db4oUnit.Extensions.Fixtures.ConfigurationSource configSource = new Db4oUnit.Extensions.Fixtures.IndependentConfigurationSource
				();
			AssertSimpleDb4o(new Db4oUnit.Extensions.Fixtures.Db4oInMemory(configSource));
			AssertSimpleDb4o(new Db4oUnit.Extensions.Fixtures.Db4oSolo(configSource));
		}

		public virtual void TestMultipleTestsSingleFixture()
		{
			Db4oUnit.Extensions.tests.MultipleDb4oTestCase.ResetConfigureCalls();
			Db4oUnit.tests.FrameworkTestCase.RunTestAndExpect(new Db4oUnit.Extensions.Db4oTestSuiteBuilder
				(new Db4oUnit.Extensions.Fixtures.Db4oInMemory(new Db4oUnit.Extensions.Fixtures.IndependentConfigurationSource
				()), typeof(Db4oUnit.Extensions.tests.MultipleDb4oTestCase)).Build(), 2, false);
			Db4oUnit.Assert.AreEqual(2, Db4oUnit.Extensions.tests.MultipleDb4oTestCase.ConfigureCalls
				());
		}

		public virtual void TestSelectiveFixture()
		{
			Db4oUnit.Extensions.Db4oFixture fixture = new Db4oUnit.Extensions.tests.AllTests.ExcludingInMemoryFixture
				(this, new Db4oUnit.Extensions.Fixtures.IndependentConfigurationSource());
			Db4oUnit.TestSuite suite = new Db4oUnit.Extensions.Db4oTestSuiteBuilder(fixture, 
				new System.Type[] { typeof(Db4oUnit.Extensions.tests.AcceptedTestCase), typeof(Db4oUnit.Extensions.tests.NotAcceptedTestCase)
				 }).Build();
			Db4oUnit.Assert.AreEqual(1, suite.GetTests().Length);
			Db4oUnit.tests.FrameworkTestCase.RunTestAndExpect(suite, 0);
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
