namespace com.db4o.db4ounit.common.defragment
{
	public abstract class AbstractDb4oDefragTestCase : Db4oUnit.Test
	{
		public virtual string GetLabel()
		{
			return "DefragAllTestCase: " + TestSuite().FullName;
		}

		public abstract System.Type TestSuite();

		public virtual void Run(Db4oUnit.TestResult result)
		{
			try
			{
				new Db4oUnit.Extensions.Db4oTestSuiteBuilder(new com.db4o.db4ounit.common.defragment.Db4oDefragSolo
					(new Db4oUnit.Extensions.Fixtures.IndependentConfigurationSource()), TestSuite()
					).Build().Run(result);
			}
			catch (System.Exception e)
			{
				result.TestFailed(this, e);
			}
		}
	}
}
