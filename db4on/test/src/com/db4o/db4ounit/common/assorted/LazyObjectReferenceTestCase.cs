namespace com.db4o.db4ounit.common.assorted
{
	public class LazyObjectReferenceTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public static void Main(string[] arguments)
		{
			new com.db4o.db4ounit.common.assorted.LazyObjectReferenceTestCase().RunSolo();
		}

		public class Item
		{
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			base.Configure(config);
			config.ObjectClass(typeof(com.db4o.db4ounit.common.assorted.LazyObjectReferenceTestCase.Item)
				).GenerateUUIDs(true);
		}

		protected override void Store()
		{
			for (int i = 0; i < 10; i++)
			{
				Store(new com.db4o.db4ounit.common.assorted.LazyObjectReferenceTestCase.Item());
			}
		}

		public virtual void Test()
		{
			com.db4o.query.Query q = Db().Query();
			q.Constrain(typeof(com.db4o.db4ounit.common.assorted.LazyObjectReferenceTestCase.Item)
				);
			com.db4o.ObjectSet objectSet = q.Execute();
			long[] ids = objectSet.Ext().GetIDs();
			com.db4o.ext.ObjectInfo[] infos = new com.db4o.ext.ObjectInfo[ids.Length];
			com.db4o.db4ounit.common.assorted.LazyObjectReferenceTestCase.Item[] items = new 
				com.db4o.db4ounit.common.assorted.LazyObjectReferenceTestCase.Item[ids.Length];
			for (int i = 0; i < items.Length; i++)
			{
				items[i] = (com.db4o.db4ounit.common.assorted.LazyObjectReferenceTestCase.Item)Db
					().GetByID(ids[i]);
				infos[i] = new com.db4o.@internal.LazyObjectReference(Stream(), (int)ids[i]);
			}
			AssertInfosAreConsistent(ids, infos);
			for (int i = 0; i < items.Length; i++)
			{
				Db().Purge(items[i]);
			}
			Db().Purge();
			AssertInfosAreConsistent(ids, infos);
		}

		private void AssertInfosAreConsistent(long[] ids, com.db4o.ext.ObjectInfo[] infos
			)
		{
			for (int i = 0; i < infos.Length; i++)
			{
				com.db4o.ext.ObjectInfo info = Db().GetObjectInfo(Db().GetByID(ids[i]));
				Db4oUnit.Assert.AreEqual(info.GetInternalID(), infos[i].GetInternalID());
				Db4oUnit.Assert.AreEqual(info.GetUUID().GetLongPart(), infos[i].GetUUID().GetLongPart
					());
				Db4oUnit.Assert.AreSame(info.GetObject(), infos[i].GetObject());
			}
		}
	}
}
