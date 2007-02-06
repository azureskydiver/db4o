namespace com.db4o.db4ounit.common.reflect
{
	public class GenericReflectorStateTest : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		protected override void Store()
		{
		}

		public virtual void TestKnownClasses()
		{
			Db().Reflector().KnownClasses();
		}
	}
}
