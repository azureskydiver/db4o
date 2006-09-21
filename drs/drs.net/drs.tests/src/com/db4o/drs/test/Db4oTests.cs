namespace com.db4o.drs.test
{
	public class Db4oTests : com.db4o.drs.test.DrsTestSuite
	{
		public static void Main(string[] args)
		{
			new com.db4o.drs.test.Db4oTests().RunDb4oDb4o();
		}

		public virtual void RunDb4oDb4o()
		{
			new Db4oUnit.TestRunner(new com.db4o.drs.test.DrsTestSuiteBuilder(new com.db4o.drs.test.Db4oDrsFixture
				("db4o-a"), new com.db4o.drs.test.Db4oDrsFixture("db4o-b"), GetType())).Run();
		}

		public virtual void RunCSCS()
		{
			new Db4oUnit.TestRunner(new com.db4o.drs.test.DrsTestSuiteBuilder(new com.db4o.drs.test.Db4oClientServerDrsFixture
				("db4o-cs-a", unchecked((int)(0xdb40))), new com.db4o.drs.test.Db4oClientServerDrsFixture
				("db4o-cs-b", 4455), GetType())).Run();
		}

		public virtual void Rundb4oCS()
		{
			new Db4oUnit.TestRunner(new com.db4o.drs.test.DrsTestSuiteBuilder(new com.db4o.drs.test.Db4oDrsFixture
				("db4o-a"), new com.db4o.drs.test.Db4oClientServerDrsFixture("db4o-cs-b", 4455), 
				GetType())).Run();
		}

		public virtual void RunCSdb4o()
		{
			new Db4oUnit.TestRunner(new com.db4o.drs.test.DrsTestSuiteBuilder(new com.db4o.drs.test.Db4oClientServerDrsFixture
				("db4o-cs-a", 4455), new com.db4o.drs.test.Db4oDrsFixture("db4o-b"), GetType()))
				.Run();
		}

		protected override System.Type[] TestCases()
		{
			return All();
		}

		protected override System.Type[] One()
		{
			return new System.Type[] { typeof(com.db4o.drs.test.TheSimplest) };
		}
	}
}
