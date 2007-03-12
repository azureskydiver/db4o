namespace com.db4o.db4ounit.common.btree
{
	public class DebugBTreeNodeMarshalledLength : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class Item
		{
			public int _int;

			public string _string;
		}

		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.btree.DebugBTreeNodeMarshalledLength().RunSolo();
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			base.Configure(config);
			config.ObjectClass(typeof(com.db4o.db4ounit.common.btree.DebugBTreeNodeMarshalledLength.Item)
				).ObjectField("_int").Indexed(true);
			config.ObjectClass(typeof(com.db4o.db4ounit.common.btree.DebugBTreeNodeMarshalledLength.Item)
				).ObjectField("_string").Indexed(true);
		}

		protected override void Store()
		{
			for (int i = 0; i < 50000; i++)
			{
				Store(new com.db4o.db4ounit.common.btree.DebugBTreeNodeMarshalledLength.Item());
			}
		}

		public virtual void Test()
		{
			com.db4o.@internal.btree.BTree btree = Btree().DebugLoadFully(SystemTrans());
			Store(new com.db4o.db4ounit.common.btree.DebugBTreeNodeMarshalledLength.Item());
			btree.Write(SystemTrans());
		}

		private com.db4o.@internal.btree.BTree Btree()
		{
			com.db4o.@internal.ClassMetadata clazz = Stream().GetYapClass(Db4oUnit.Extensions.Db4oUnitPlatform.GetReflectClass
				(Reflector(), typeof(com.db4o.db4ounit.common.btree.DebugBTreeNodeMarshalledLength.Item)
				));
			com.db4o.@internal.classindex.ClassIndexStrategy index = clazz.Index();
			return ((com.db4o.@internal.classindex.BTreeClassIndexStrategy)index).Btree();
		}
	}
}
