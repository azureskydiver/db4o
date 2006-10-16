using System;

namespace com.db4o.db4ounit.cli1
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		protected override Type[] TestCases()
		{
			return new System.Type[]
				{
					typeof (ObjectSetAsListTestCase),
				};
		}
	}
}
