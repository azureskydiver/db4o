namespace com.db4o.test
{
	public class XTEAEncryption
	{
		private const int NUMSTORED = 100;

		public int id;

		public string name;

		public com.db4o.test.XTEAEncryption parent;

		public XTEAEncryption() : this(0, null, null)
		{
		}

		public XTEAEncryption(int id, string name, com.db4o.test.XTEAEncryption parent)
		{
			this.id = id;
			this.name = name;
			this.parent = parent;
		}

		public virtual void Test()
		{
			com.db4o.Db4o.Configure().BlockSize(1);
			com.db4o.Db4o.Configure().Io(new com.db4o.io.crypt.XTeaEncryptionFileAdapter("db4o"
				));
			new j4o.io.File("encrypted.yap").Delete();
			com.db4o.ObjectContainer db = com.db4o.Db4o.OpenFile("encrypted.yap");
			com.db4o.test.XTEAEncryption last = null;
			for (int i = 0; i < NUMSTORED; i++)
			{
				com.db4o.test.XTEAEncryption current = new com.db4o.test.XTEAEncryption(i, "X" + 
					i, last);
				db.Set(current);
				last = current;
			}
			db.Close();
			db = com.db4o.Db4o.OpenFile("encrypted.yap");
			com.db4o.query.Query query = db.Query();
			query.Constrain(j4o.lang.Class.GetClassForObject(this));
			query.Descend("id").Constrain(50);
			Tester.Ensure(query.Execute().Size() == 1);
			db.Close();
			com.db4o.Db4o.Configure().Io(new com.db4o.io.RandomAccessFileAdapter());
		}
	}
}
