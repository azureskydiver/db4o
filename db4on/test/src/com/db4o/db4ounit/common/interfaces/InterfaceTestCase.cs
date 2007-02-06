namespace com.db4o.db4ounit.common.interfaces
{
	public class InterfaceTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		protected override void Store()
		{
			Store(new com.db4o.db4ounit.common.interfaces.ThreeSomeParent());
			Store(new com.db4o.db4ounit.common.interfaces.ThreeSomeLeftChild());
			Store(new com.db4o.db4ounit.common.interfaces.ThreeSomeRightChild());
		}

		public virtual void Test()
		{
			com.db4o.query.Query q = NewQuery(typeof(com.db4o.db4ounit.common.interfaces.ThreeSomeInterface)
				);
			Db4oUnit.Assert.AreEqual(2, q.Execute().Size());
		}
	}
}
