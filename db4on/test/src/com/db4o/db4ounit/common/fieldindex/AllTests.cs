namespace com.db4o.db4ounit.common.fieldindex
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.fieldindex.AllTests().RunSolo();
		}

		protected override System.Type[] TestCases()
		{
			System.Type[] fieldBased = new System.Type[] { typeof(com.db4o.db4ounit.common.fieldindex.IndexedNodeTestCase)
				, typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexTestCase), typeof(com.db4o.db4ounit.common.fieldindex.FieldIndexProcessorTestCase)
				 };
			System.Type[] neutral = new System.Type[] { typeof(com.db4o.db4ounit.common.fieldindex.DoubleFieldIndexTestCase)
				, typeof(com.db4o.db4ounit.common.fieldindex.StringIndexTestCase), typeof(com.db4o.db4ounit.common.fieldindex.StringIndexCorruptionTestCase)
				 };
			System.Type[] tests = neutral;
			tests = new System.Type[fieldBased.Length + neutral.Length];
			System.Array.Copy(neutral, 0, tests, 0, neutral.Length);
			System.Array.Copy(fieldBased, 0, tests, neutral.Length, fieldBased.Length);
			return tests;
		}
	}
}
