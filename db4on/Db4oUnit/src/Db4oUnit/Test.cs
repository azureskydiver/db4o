namespace Db4oUnit
{
	public interface Test
	{
		string GetLabel();

		void Run(Db4oUnit.TestResult result);
	}
}
