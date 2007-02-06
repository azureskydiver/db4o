namespace com.db4o.db4ounit.common.io
{
	public class AllTests : Db4oUnit.TestSuiteBuilder
	{
		public virtual Db4oUnit.TestSuite Build()
		{
			return new Db4oUnit.ReflectionTestSuiteBuilder(new System.Type[] { typeof(com.db4o.db4ounit.common.io.IoAdapterTest)
				 }).Build();
		}

		public static void Main(string[] args)
		{
			new Db4oUnit.TestRunner(typeof(com.db4o.db4ounit.common.io.AllTests)).Run();
		}
	}
}
