namespace Db4oUnit.tests
{
	public class ReinstantiatePerMethodTest : Db4oUnit.TestCase
	{
		private int a = 0;

		public virtual void Test1()
		{
			Db4oUnit.Assert.AreEqual(0, a);
			a = 1;
		}

		public virtual void Test2()
		{
			Db4oUnit.Assert.AreEqual(0, a);
			a = 2;
		}
	}
}
