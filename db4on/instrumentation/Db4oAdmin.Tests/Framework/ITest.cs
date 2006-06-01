namespace Db4oAdmin.Tests.Framework
{
	public interface ITest
	{
		string Name { get; }
		void Run();
	}
}