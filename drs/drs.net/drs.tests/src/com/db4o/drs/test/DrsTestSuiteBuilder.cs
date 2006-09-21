namespace com.db4o.drs.test
{
	public class DrsTestSuiteBuilder : Db4oUnit.ReflectionTestSuiteBuilder
	{
		private com.db4o.drs.test.DrsFixture _a;

		private com.db4o.drs.test.DrsFixture _b;

		public DrsTestSuiteBuilder(com.db4o.drs.test.DrsFixture a, com.db4o.drs.test.DrsFixture
			 b, System.Type clazz) : base(clazz)
		{
			A(a);
			B(b);
		}

		public DrsTestSuiteBuilder(com.db4o.drs.test.DrsFixture a, com.db4o.drs.test.DrsFixture
			 b, System.Type[] classes) : base(classes)
		{
			A(a);
			B(b);
		}

		private void A(com.db4o.drs.test.DrsFixture fixture)
		{
			if (null == fixture)
			{
				throw new System.ArgumentException("fixture");
			}
			_a = fixture;
		}

		private void B(com.db4o.drs.test.DrsFixture fixture)
		{
			if (null == fixture)
			{
				throw new System.ArgumentException("fixture");
			}
			_b = fixture;
		}

		protected override object NewInstance(System.Type clazz)
		{
			object instance = base.NewInstance(clazz);
			if (instance is com.db4o.drs.test.DrsTestCase)
			{
				com.db4o.drs.test.DrsTestCase testCase = (com.db4o.drs.test.DrsTestCase)instance;
				testCase.A(_a);
				testCase.B(_b);
			}
			return instance;
		}
	}
}
