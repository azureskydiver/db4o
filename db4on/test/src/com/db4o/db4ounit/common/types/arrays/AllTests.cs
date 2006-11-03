namespace com.db4o.db4ounit.common.types.arrays
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.types.arrays.AllTests().RunSolo();
		}

		protected override System.Type[] TestCases()
		{
			return new System.Type[] { typeof(com.db4o.db4ounit.common.types.arrays.ArrayNOrderTestCase)
				, typeof(com.db4o.db4ounit.common.types.arrays.ByteArrayTestCase), typeof(com.db4o.db4ounit.common.types.arrays.NestedArraysTestCase)
				, typeof(com.db4o.db4ounit.common.types.arrays.SimpleStringArrayTestCase), typeof(com.db4o.db4ounit.common.types.arrays.SimpleTypeArrayInUntypedVariableTestCase)
				, typeof(com.db4o.db4ounit.common.types.arrays.TypedArrayInObjectTestCase), typeof(com.db4o.db4ounit.common.types.arrays.TypedDerivedArrayTestCase)
				 };
		}
	}
}
