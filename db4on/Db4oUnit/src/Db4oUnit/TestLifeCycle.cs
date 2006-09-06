namespace Db4oUnit
{
	/// <summary>For test cases that need setUp/tearDown support.</summary>
	/// <remarks>For test cases that need setUp/tearDown support.</remarks>
	public interface TestLifeCycle : Db4oUnit.TestCase
	{
		void SetUp();

		void TearDown();
	}
}
