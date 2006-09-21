namespace com.db4o.drs.test
{
	public class Db4oDrsFixture : com.db4o.drs.test.DrsFixture
	{
		internal string _name;

		internal com.db4o.ext.ExtObjectContainer _db;

		internal com.db4o.drs.inside.TestableReplicationProviderInside _provider;

		public Db4oDrsFixture(string name)
		{
			_name = name;
		}

		private string YapFileName()
		{
			return "drs" + _name + ".yap";
		}

		public virtual com.db4o.drs.inside.TestableReplicationProviderInside Provider()
		{
			return _provider;
		}

		public virtual void Clean()
		{
			new j4o.io.File(YapFileName()).Delete();
		}

		public virtual void Close()
		{
			_provider.Destroy();
			_db.Close();
		}

		public virtual com.db4o.ext.ExtObjectContainer Db()
		{
			return _db;
		}

		public virtual void Open()
		{
			_db = com.db4o.Db4o.OpenFile(new j4o.io.File(YapFileName()).GetPath()).Ext();
			_provider = new com.db4o.drs.db4o.Db4oReplicationProvider(_db, _name);
		}
	}
}
