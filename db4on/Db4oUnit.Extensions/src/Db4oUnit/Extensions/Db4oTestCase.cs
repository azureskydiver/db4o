namespace Db4oUnit.Extensions
{
	public interface Db4oTestCase : Db4oUnit.TestCase, Db4oUnit.TestLifeCycle
	{
		/// <summary>returns an ExtObjectContainer as a parameter for test method.</summary>
		/// <remarks>returns an ExtObjectContainer as a parameter for test method.</remarks>
		/// <returns>ExtObjectContainer</returns>
		com.db4o.ext.ExtObjectContainer Db();
	}
}
