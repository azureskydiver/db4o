namespace com.db4o.db4ounit.common.assorted
{
	/// <exclude></exclude>
	public class UUIDTestItem
	{
		public string name;

		public UUIDTestItem()
		{
		}

		public UUIDTestItem(string name)
		{
			this.name = name;
		}

		public static void AssertItemsCanBeRetrievedByUUID(com.db4o.ext.ExtObjectContainer
			 container, com.db4o.foundation.Hashtable4 uuidCache)
		{
			com.db4o.query.Query q = container.Query();
			q.Constrain(typeof(com.db4o.db4ounit.common.assorted.UUIDTestItem));
			com.db4o.ObjectSet objectSet = q.Execute();
			while (objectSet.HasNext())
			{
				com.db4o.db4ounit.common.assorted.UUIDTestItem item = (com.db4o.db4ounit.common.assorted.UUIDTestItem
					)objectSet.Next();
				com.db4o.ext.Db4oUUID uuid = container.GetObjectInfo(item).GetUUID();
				Db4oUnit.Assert.IsNotNull(uuid);
				Db4oUnit.Assert.AreSame(item, container.GetByUUID(uuid));
				com.db4o.ext.Db4oUUID cached = (com.db4o.ext.Db4oUUID)uuidCache.Get(item.name);
				if (cached != null)
				{
					Db4oUnit.Assert.AreEqual(cached, uuid);
				}
				else
				{
					uuidCache.Put(item.name, uuid);
				}
			}
		}
	}
}
