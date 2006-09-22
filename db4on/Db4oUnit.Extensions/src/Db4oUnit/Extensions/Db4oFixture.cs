namespace Db4oUnit.Extensions
{
	public interface Db4oFixture
	{
		void Open();

		void Close();

		void Clean();

		com.db4o.ext.ExtObjectContainer Db();
	}
}
