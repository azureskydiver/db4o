namespace Db4oUnit.Extensions.tests
{
	public class MultipleDb4oTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public virtual void TestFirst()
		{
			Db4oUnit.Assert.Fail();
		}

		public virtual void TestSecond()
		{
			Db4oUnit.Assert.Fail();
		}
	}
}
