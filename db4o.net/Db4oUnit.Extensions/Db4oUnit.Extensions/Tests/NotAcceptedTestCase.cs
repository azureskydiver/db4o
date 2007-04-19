using Db4oUnit;
using Db4oUnit.Extensions;
using Db4oUnit.Extensions.Fixtures;

namespace Db4oUnit.Extensions.Tests
{
	public class NotAcceptedTestCase : AbstractDb4oTestCase, IOptOutFromTestFixture
	{
		public virtual void Test()
		{
			Assert.Fail("Opted out test should not be run.");
		}
	}
}
