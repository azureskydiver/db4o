namespace Db4objects.Drs.Test
{
	/// <exclude></exclude>
	public abstract class DrsTestSuite : Db4objects.Drs.Test.DrsTestCase, Db4oUnit.ITestSuiteBuilder
	{
		public virtual Db4oUnit.TestSuite Build()
		{
			return new Db4objects.Drs.Test.DrsTestSuiteBuilder(A(), B(), TestCases()).Build();
		}

		protected abstract System.Type[] TestCases();

		protected virtual System.Type[] Shared()
		{
			return new System.Type[] { typeof(Db4objects.Drs.Test.TheSimplest), typeof(Db4objects.Drs.Test.ReplicationEventTest)
				, typeof(Db4objects.Drs.Test.ReplicationProviderTest), typeof(Db4objects.Drs.Test.ReplicationAfterDeletionTest)
				, typeof(Db4objects.Drs.Test.SimpleArrayTest), typeof(Db4objects.Drs.Test.SimpleParentChild)
				, typeof(Db4objects.Drs.Test.ByteArrayTest), typeof(Db4objects.Drs.Test.ListTest)
				, typeof(Db4objects.Drs.Test.Db4oListTest), typeof(Db4objects.Drs.Test.R0to4Runner)
				, typeof(Db4objects.Drs.Test.ReplicationFeaturesMain) };
		}
	}
}
