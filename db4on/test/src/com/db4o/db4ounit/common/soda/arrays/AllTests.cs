namespace com.db4o.db4ounit.common.soda.arrays
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		protected override System.Type[] TestCases()
		{
			return new System.Type[] { typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrMixedTestCase)
				, typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrStringOTestCase), typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrStringONTestCase)
				, typeof(com.db4o.db4ounit.common.soda.arrays.typed.STArrStringTTestCase), typeof(com.db4o.db4ounit.common.soda.arrays.typed.STArrStringTNTestCase)
				, typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrStringUTestCase), typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrStringUNTestCase)
				, typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerOTestCase), typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerONTestCase)
				, typeof(com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerTTestCase), typeof(com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerTNTestCase)
				, typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrIntegerUTestCase), typeof(com.db4o.db4ounit.common.soda.arrays.untyped.STArrIntegerUNTestCase)
				, typeof(com.db4o.db4ounit.common.soda.arrays.typed.STArrIntegerWTTestCase), typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerWTONTestCase)
				, typeof(com.db4o.db4ounit.common.soda.arrays.@object.STArrIntegerWUONTestCase) };
		}
	}
}
