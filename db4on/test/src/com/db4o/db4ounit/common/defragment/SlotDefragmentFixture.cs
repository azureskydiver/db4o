namespace com.db4o.db4ounit.common.defragment
{
	public class SlotDefragmentFixture
	{
		public static readonly string PRIMITIVE_FIELDNAME = "_id";

		public static readonly string WRAPPER_FIELDNAME = "_wrapper";

		public static readonly string TYPEDOBJECT_FIELDNAME = "_next";

		public class Data
		{
			public int _id;

			public int _wrapper;

			public com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.Data _next;

			public Data(int id, com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.Data
				 next)
			{
				_id = id;
				_wrapper = id;
				_next = next;
			}
		}

		public const int VALUE = 42;

		public static com.db4o.defragment.DefragmentConfig DefragConfig(bool forceBackupDelete
			)
		{
			com.db4o.defragment.DefragmentConfig defragConfig = new com.db4o.defragment.DefragmentConfig
				(com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants.FILENAME, com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants
				.BACKUPFILENAME);
			defragConfig.ForceBackupDelete(forceBackupDelete);
			return defragConfig;
		}

		public static void CreateFile(string fileName)
		{
			com.db4o.config.Configuration config = com.db4o.Db4o.NewConfiguration();
			com.db4o.ObjectContainer db = com.db4o.Db4o.OpenFile(config, fileName);
			com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.Data data = null;
			for (int value = VALUE - 1; value <= VALUE + 1; value++)
			{
				data = new com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.Data(value, 
					data);
				db.Set(data);
			}
			db.Close();
		}

		public static void ForceIndex()
		{
			com.db4o.config.Configuration config = com.db4o.Db4o.NewConfiguration();
			config.ObjectClass(typeof(com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.Data)
				).ObjectField(PRIMITIVE_FIELDNAME).Indexed(true);
			config.ObjectClass(typeof(com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.Data)
				).ObjectField(WRAPPER_FIELDNAME).Indexed(true);
			config.ObjectClass(typeof(com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.Data)
				).ObjectField(TYPEDOBJECT_FIELDNAME).Indexed(true);
			com.db4o.ObjectContainer db = com.db4o.Db4o.OpenFile(config, com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants
				.FILENAME);
			Db4oUnit.Assert.IsTrue(db.Ext().StoredClass(typeof(com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.Data)
				).StoredField(PRIMITIVE_FIELDNAME, typeof(int)).HasIndex());
			Db4oUnit.Assert.IsTrue(db.Ext().StoredClass(typeof(com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.Data)
				).StoredField(WRAPPER_FIELDNAME, typeof(int)).HasIndex());
			Db4oUnit.Assert.IsTrue(db.Ext().StoredClass(typeof(com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.Data)
				).StoredField(TYPEDOBJECT_FIELDNAME, typeof(com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.Data)
				).HasIndex());
			db.Close();
		}

		public static void AssertIndex(string fieldName)
		{
			ForceIndex();
			com.db4o.defragment.Defragment.Defrag(com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants
				.FILENAME, com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants.BACKUPFILENAME
				);
			com.db4o.ObjectContainer db = com.db4o.Db4o.OpenFile(com.db4o.Db4o.NewConfiguration
				(), com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants.FILENAME);
			com.db4o.query.Query query = db.Query();
			query.Constrain(typeof(com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.Data)
				);
			query.Descend(fieldName).Constrain(VALUE);
			com.db4o.ObjectSet result = query.Execute();
			Db4oUnit.Assert.AreEqual(1, result.Size());
			db.Close();
		}

		public static void AssertDataClassKnown(bool expected)
		{
			com.db4o.ObjectContainer db = com.db4o.Db4o.OpenFile(com.db4o.Db4o.NewConfiguration
				(), com.db4o.db4ounit.common.defragment.SlotDefragmentTestConstants.FILENAME);
			try
			{
				com.db4o.ext.StoredClass storedClass = db.Ext().StoredClass(typeof(com.db4o.db4ounit.common.defragment.SlotDefragmentFixture.Data)
					);
				if (expected)
				{
					Db4oUnit.Assert.IsNotNull(storedClass);
				}
				else
				{
					Db4oUnit.Assert.IsNull(storedClass);
				}
			}
			finally
			{
				db.Close();
			}
		}
	}
}
