namespace com.db4o.db4ounit.common.querying
{
	public class CascadeOnDelete : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class Item
		{
			public string item;
		}

		public com.db4o.db4ounit.common.querying.CascadeOnDelete.Item[] items;

		public virtual void Test()
		{
			NoAccidentalDeletes();
		}

		private void NoAccidentalDeletes()
		{
			NoAccidentalDeletes1(true, true);
			NoAccidentalDeletes1(true, false);
			NoAccidentalDeletes1(false, true);
			NoAccidentalDeletes1(false, false);
		}

		private void NoAccidentalDeletes1(bool cascadeOnUpdate, bool cascadeOnDelete)
		{
			DeleteAll(GetType());
			DeleteAll(typeof(com.db4o.db4ounit.common.querying.CascadeOnDelete.Item));
			com.db4o.config.ObjectClass oc = com.db4o.Db4o.Configure().ObjectClass(typeof(com.db4o.db4ounit.common.querying.CascadeOnDelete)
				);
			oc.CascadeOnDelete(cascadeOnDelete);
			oc.CascadeOnUpdate(cascadeOnUpdate);
			Reopen();
			com.db4o.db4ounit.common.querying.CascadeOnDelete.Item i = new com.db4o.db4ounit.common.querying.CascadeOnDelete.Item
				();
			com.db4o.db4ounit.common.querying.CascadeOnDelete cod = new com.db4o.db4ounit.common.querying.CascadeOnDelete
				();
			cod.items = new com.db4o.db4ounit.common.querying.CascadeOnDelete.Item[] { i };
			Db().Set(cod);
			Db().Commit();
			cod.items[0].item = "abrakadabra";
			Db().Set(cod);
			if (!cascadeOnDelete && !cascadeOnUpdate)
			{
				Db().Set(cod.items[0]);
			}
			Db4oUnit.Assert.AreEqual(1, CountOccurences(typeof(com.db4o.db4ounit.common.querying.CascadeOnDelete.Item)
				));
			Db().Commit();
			Db4oUnit.Assert.AreEqual(1, CountOccurences(typeof(com.db4o.db4ounit.common.querying.CascadeOnDelete.Item)
				));
		}
	}
}
