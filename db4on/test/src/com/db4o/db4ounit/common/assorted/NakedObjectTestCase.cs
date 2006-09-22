namespace com.db4o.db4ounit.common.assorted
{
	public class NakedObjectTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class Item
		{
			public object field = new object();
		}

		public virtual void TestStoreNakedObjects()
		{
			try
			{
				Db().Set(new com.db4o.db4ounit.common.assorted.NakedObjectTestCase.Item());
				Db4oUnit.Assert.Fail("Naked objects can't be stored");
			}
			catch (com.db4o.ext.ObjectNotStorableException e)
			{
			}
		}
	}
}
