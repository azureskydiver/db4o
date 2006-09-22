namespace Db4oUnit.Extensions.Fixtures
{
	public abstract class AbstractDb4oFixture : Db4oUnit.Extensions.Db4oFixture
	{
		private com.db4o.ext.ExtObjectContainer _db;

		public virtual void Close()
		{
			_db.Close();
			_db = null;
		}

		public virtual com.db4o.ext.ExtObjectContainer Db()
		{
			return _db;
		}

		protected virtual void Db(com.db4o.ext.ExtObjectContainer container)
		{
			Db4oUnit.Assert.IsNull(_db);
			_db = container;
		}

		public abstract void Clean();

		public abstract void Open();
	}
}
