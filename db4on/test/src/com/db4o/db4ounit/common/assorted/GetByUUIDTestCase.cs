namespace com.db4o.db4ounit.common.assorted
{
	public class GetByUUIDTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.assorted.GetByUUIDTestCase().RunSolo();
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			config.ObjectClass(typeof(com.db4o.db4ounit.common.assorted.UUIDTestItem)).GenerateUUIDs
				(true);
		}

		protected override void Store()
		{
			Db().Set(new com.db4o.db4ounit.common.assorted.UUIDTestItem("one"));
			Db().Set(new com.db4o.db4ounit.common.assorted.UUIDTestItem("two"));
		}

		public virtual void Test()
		{
			com.db4o.foundation.Hashtable4 uuidCache = new com.db4o.foundation.Hashtable4();
			AssertItemsCanBeRetrievedByUUID(uuidCache);
			Reopen();
			AssertItemsCanBeRetrievedByUUID(uuidCache);
		}

		private void AssertItemsCanBeRetrievedByUUID(com.db4o.foundation.Hashtable4 uuidCache
			)
		{
			com.db4o.db4ounit.common.assorted.UUIDTestItem.AssertItemsCanBeRetrievedByUUID(Db
				(), uuidCache);
		}
	}
}
