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

		public virtual void test()
		{
			com.db4o.Db4o.configure().blockSize(1);
			com.db4o.Db4o.configure().io(new com.db4o.io.crypt.XTeaEncryptionFileAdapter("db4o"
				));
			new j4o.io.File("encrypted.yap").delete();
			com.db4o.ObjectContainer db = com.db4o.Db4o.openFile("encrypted.yap");
			com.db4o.test.XTEAEncryption last = null;
			for (int i = 0; i < NUMSTORED; i++)
			{
				com.db4o.test.XTEAEncryption current = new com.db4o.test.XTEAEncryption(i, "X" + 
					i, last);
				db.set(current);
				last = current;
			}
			db.close();
			db = com.db4o.Db4o.openFile("encrypted.yap");
			com.db4o.query.Query query = db.query();
			query.constrain(j4o.lang.Class.getClassForObject(this));
			query.descend("id").constrain(50);
			Tester.ensure(query.execute().size() == 1);
			db.close();
			com.db4o.Db4o.configure().io(new com.db4o.io.RandomAccessFileAdapter());
		}
	}
}
