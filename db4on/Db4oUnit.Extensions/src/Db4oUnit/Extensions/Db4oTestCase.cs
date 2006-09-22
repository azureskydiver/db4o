namespace Db4oUnit.Extensions
{
	public interface Db4oTestCase : Db4oUnit.TestCase, Db4oUnit.TestLifeCycle
	{
		void Fixture(Db4oUnit.Extensions.Db4oFixture fixture);

		Db4oUnit.Extensions.Db4oFixture Fixture();

		com.db4o.ext.ExtObjectContainer Db();
	}
}
