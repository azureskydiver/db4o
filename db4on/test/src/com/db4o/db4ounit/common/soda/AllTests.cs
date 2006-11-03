namespace com.db4o.db4ounit.common.soda
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		protected override System.Type[] TestCases()
		{
			return new System.Type[] { typeof(com.db4o.db4ounit.common.soda.arrays.AllTests), 
				typeof(com.db4o.db4ounit.common.soda.classes.simple.STBooleanTestCase), typeof(com.db4o.db4ounit.common.soda.wrapper.untyped.STBooleanWUTestCase)
				, typeof(com.db4o.db4ounit.common.soda.classes.simple.STByteTestCase), typeof(com.db4o.db4ounit.common.soda.wrapper.untyped.STByteWUTestCase)
				, typeof(com.db4o.db4ounit.common.soda.classes.simple.STCharTestCase), typeof(com.db4o.db4ounit.common.soda.wrapper.untyped.STCharWUTestCase)
				, typeof(com.db4o.db4ounit.common.soda.classes.simple.STDoubleTestCase), typeof(com.db4o.db4ounit.common.soda.wrapper.untyped.STDoubleWUTestCase)
				, typeof(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STETH1TestCase), typeof(com.db4o.db4ounit.common.soda.classes.simple.STFloatTestCase)
				, typeof(com.db4o.db4ounit.common.soda.wrapper.untyped.STFloatWUTestCase), typeof(com.db4o.db4ounit.common.soda.classes.simple.STIntegerTestCase)
				, typeof(com.db4o.db4ounit.common.soda.wrapper.untyped.STIntegerWUTestCase), typeof(com.db4o.db4ounit.common.soda.classes.simple.STLongTestCase)
				, typeof(com.db4o.db4ounit.common.soda.wrapper.untyped.STLongWUTestCase), typeof(com.db4o.db4ounit.common.soda.joins.typed.STOrTTestCase)
				, typeof(com.db4o.db4ounit.common.soda.joins.untyped.STOrUTestCase), typeof(com.db4o.db4ounit.common.soda.ordered.STOStringTestCase)
				, typeof(com.db4o.db4ounit.common.soda.ordered.STOIntegerTestCase), typeof(com.db4o.db4ounit.common.soda.ordered.STOIntegerWTTestCase)
				, typeof(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STRTH1TestCase), typeof(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STSDFT1TestCase)
				, typeof(com.db4o.db4ounit.common.soda.classes.simple.STShortTestCase), typeof(com.db4o.db4ounit.common.soda.wrapper.untyped.STShortWUTestCase)
				, typeof(com.db4o.db4ounit.common.soda.wrapper.untyped.STStringUTestCase), typeof(com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STRUH1TestCase)
				, typeof(com.db4o.db4ounit.common.soda.classes.typedhierarchy.STTH1TestCase), typeof(com.db4o.db4ounit.common.soda.classes.untypedhierarchy.STUH1TestCase)
				 };
		}

		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.soda.AllTests().RunSolo();
		}
	}
}
