namespace Db4oUnit.Extensions.Fixtures
{
	public abstract class AbstractFileBasedDb4oFixture : Db4oUnit.Extensions.Fixtures.AbstractDb4oFixture
	{
		private readonly j4o.io.File _yap;

		public AbstractFileBasedDb4oFixture(string fileName)
		{
			_yap = new j4o.io.File(fileName);
		}

		public virtual string GetAbsolutePath()
		{
			return _yap.GetAbsolutePath();
		}

		public override void Clean()
		{
			_yap.Delete();
		}
	}
}
