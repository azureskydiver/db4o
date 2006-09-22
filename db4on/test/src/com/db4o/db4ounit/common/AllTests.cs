namespace com.db4o.db4ounit.common
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		protected override System.Type[] TestCases()
		{
			return new System.Type[] { typeof(com.db4o.db4ounit.common.assorted.AllTests), typeof(
				com.db4o.db4ounit.common.btree.AllTests), typeof(com.db4o.db4ounit.common.fieldindex.AllTests
				), typeof(com.db4o.db4ounit.common.foundation.AllTests), typeof(com.db4o.db4ounit.common.handlers.AllTests
				), typeof(com.db4o.db4ounit.common.header.AllTests) };
		}
	}
}
