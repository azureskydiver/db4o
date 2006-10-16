namespace com.db4o.db4ounit.cli2.collections
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.cli2.collections.AllTests().RunSolo();
		}

		protected override System.Type[] TestCases()
		{
#if NET_2_0 || CF_2_0
			return new System.Type[] { typeof(GenericDictionary) };
#else
			return new System.Type[0];
#endif
		}
	}
}
