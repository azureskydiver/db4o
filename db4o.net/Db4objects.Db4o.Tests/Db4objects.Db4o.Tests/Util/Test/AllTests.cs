namespace Db4objects.Db4o.Tests.Util.Test
{
	public class AllTests : Db4oUnit.Extensions.Db4oTestSuite
	{
		public static void Main(string[] args)
		{
			new Db4objects.Db4o.Tests.Util.Test.AllTests().RunSolo();
		}

		protected override System.Type[] TestCases()
		{
			return new System.Type[] { typeof(Db4objects.Db4o.Tests.Util.Test.PermutingTestConfigTestCase)
				 };
		}
	}
}
