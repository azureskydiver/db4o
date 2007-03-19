namespace Db4objects.Drs.Test
{
	public class Db4oDrsFixture : Db4objects.Drs.Test.IDrsFixture
	{
		internal static readonly Sharpen.IO.File RAM_DRIVE = new Sharpen.IO.File("w:");

		protected string _name;

		protected Db4objects.Db4o.Ext.IExtObjectContainer _db;

		protected Db4objects.Drs.Inside.ITestableReplicationProviderInside _provider;

		protected readonly Sharpen.IO.File testFile;

		public Db4oDrsFixture(string name)
		{
			_name = name;
			if (RAM_DRIVE.Exists())
			{
				testFile = new Sharpen.IO.File(RAM_DRIVE.GetPath() + "drs_cs_" + _name + ".yap");
			}
			else
			{
				testFile = new Sharpen.IO.File("drs_cs_" + _name + ".yap");
			}
		}

		public virtual Db4objects.Drs.Inside.ITestableReplicationProviderInside Provider(
			)
		{
			return _provider;
		}

		public virtual void Clean()
		{
			testFile.Delete();
		}

		public virtual void Close()
		{
			_provider.Destroy();
			_db.Close();
		}

		public virtual Db4objects.Db4o.Ext.IExtObjectContainer Db()
		{
			return _db;
		}

		public virtual void Open()
		{
			_db = Db4objects.Db4o.Db4oFactory.OpenFile(testFile.GetPath()).Ext();
			_provider = new Db4objects.Drs.Db4o.Db4oReplicationProvider(_db, _name);
		}
	}
}
