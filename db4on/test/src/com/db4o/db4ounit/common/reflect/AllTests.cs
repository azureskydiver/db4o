namespace com.db4o.db4ounit.common.reflect
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		protected override System.Type[] TestCases()
		{
			return new System.Type[] { typeof(com.db4o.db4ounit.common.reflect.GenericReflectorStateTest)
				, typeof(com.db4o.db4ounit.common.reflect.ReflectArrayTestCase) };
		}

		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.reflect.AllTests().RunSolo();
		}
	}
}
