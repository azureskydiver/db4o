using System;
using Db4oUnit.Extensions;

namespace com.db4o.db4ounit
{
	public class AllTests : Db4oTestSuite
	{
		protected override Type[] TestCases()
		{
			return new System.Type[]
			{
				typeof(com.db4o.db4ounit.common.AllTests),
				typeof(com.db4o.db4ounit.cli1.AllTests),
				typeof(com.db4o.db4ounit.cli2.AllTests),
			};
		}
	}
}
