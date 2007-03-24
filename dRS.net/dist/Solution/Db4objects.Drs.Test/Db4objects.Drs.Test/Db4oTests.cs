namespace Db4objects.Drs.Test
{
	public class Db4oTests : Db4objects.Drs.Test.DrsTestSuite
	{
		public static void Main(string[] args)
		{
			new Db4objects.Drs.Test.Db4oTests().Rundb4oCS();
			new Db4objects.Drs.Test.Db4oTests().RunCSCS();
		}

		public virtual void RunDb4oDb4o()
		{
			new Db4oUnit.TestRunner(new Db4objects.Drs.Test.DrsTestSuiteBuilder(new Db4objects.Drs.Test.Db4oDrsFixture
				("db4o-a"), new Db4objects.Drs.Test.Db4oDrsFixture("db4o-b"), GetType())).Run();
		}

		public virtual void RunCSCS()
		{
			new Db4oUnit.TestRunner(new Db4objects.Drs.Test.DrsTestSuiteBuilder(new Db4objects.Drs.Test.Db4oClientServerDrsFixture
				("db4o-cs-a", unchecked((int)(0xdb40))), new Db4objects.Drs.Test.Db4oClientServerDrsFixture
				("db4o-cs-b", 4455), GetType())).Run();
		}

		public virtual void Rundb4oCS()
		{
			new Db4oUnit.TestRunner(new Db4objects.Drs.Test.DrsTestSuiteBuilder(new Db4objects.Drs.Test.Db4oDrsFixture
				("db4o-a"), new Db4objects.Drs.Test.Db4oClientServerDrsFixture("db4o-cs-b", 4455
				), GetType())).Run();
		}

		public virtual void RunCSdb4o()
		{
			new Db4oUnit.TestRunner(new Db4objects.Drs.Test.DrsTestSuiteBuilder(new Db4objects.Drs.Test.Db4oClientServerDrsFixture
				("db4o-cs-a", 4455), new Db4objects.Drs.Test.Db4oDrsFixture("db4o-b"), GetType()
				)).Run();
		}

		protected override System.Type[] TestCases()
		{
			return Shared();
		}

		protected virtual System.Type[] One()
		{
			return new System.Type[] { typeof(Db4objects.Drs.Test.ByteArrayTest) };
		}
	}
}
