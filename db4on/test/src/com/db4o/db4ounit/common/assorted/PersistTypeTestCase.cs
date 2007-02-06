namespace com.db4o.db4ounit.common.assorted
{
	public class PersistTypeTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public sealed class Item
		{
			public System.Type type;

			public Item()
			{
			}

			public Item(System.Type type_)
			{
				type = type_;
			}
		}

		protected override void Store()
		{
			Store(new com.db4o.db4ounit.common.assorted.PersistTypeTestCase.Item(typeof(string)
				));
		}

		public virtual void Test()
		{
			Db4oUnit.Assert.AreEqual(typeof(string), ((com.db4o.db4ounit.common.assorted.PersistTypeTestCase.Item
				)RetrieveOnlyInstance(typeof(com.db4o.db4ounit.common.assorted.PersistTypeTestCase.Item)
				)).type);
		}
	}
}
