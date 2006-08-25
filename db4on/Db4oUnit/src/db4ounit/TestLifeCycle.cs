namespace Db4oUnit
{
	/// <summary>For test cases that need setUp/tearDown support.</summary>
	/// <remarks>For test cases that need setUp/tearDown support.</remarks>
	public interface TestLifeCycle
	{
		void SetUp();

		void TearDown();
	}
}
