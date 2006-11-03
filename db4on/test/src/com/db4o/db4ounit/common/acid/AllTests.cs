namespace com.db4o.db4ounit.common.acid
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		protected override System.Type[] TestCases()
		{
			return new System.Type[] { typeof(com.db4o.db4ounit.common.acid.CrashSimulatingTestCase)
				 };
		}
	}
}
