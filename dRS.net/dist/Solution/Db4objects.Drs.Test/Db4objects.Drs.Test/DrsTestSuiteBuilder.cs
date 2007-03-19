namespace Db4objects.Drs.Test
{
	public class DrsTestSuiteBuilder : Db4oUnit.ReflectionTestSuiteBuilder
	{
		private Db4objects.Drs.Test.IDrsFixture _a;

		private Db4objects.Drs.Test.IDrsFixture _b;

		public DrsTestSuiteBuilder(Db4objects.Drs.Test.IDrsFixture a, Db4objects.Drs.Test.IDrsFixture
			 b, System.Type clazz) : base(clazz)
		{
			A(a);
			B(b);
		}

		public DrsTestSuiteBuilder(Db4objects.Drs.Test.IDrsFixture a, Db4objects.Drs.Test.IDrsFixture
			 b, System.Type[] classes) : base(classes)
		{
			A(a);
			B(b);
		}

		private void A(Db4objects.Drs.Test.IDrsFixture fixture)
		{
			if (null == fixture)
			{
				throw new System.ArgumentException("fixture");
			}
			_a = fixture;
		}

		private void B(Db4objects.Drs.Test.IDrsFixture fixture)
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
			if (instance is Db4objects.Drs.Test.DrsTestCase)
			{
				Db4objects.Drs.Test.DrsTestCase testCase = (Db4objects.Drs.Test.DrsTestCase)instance;
				testCase.A(_a);
				testCase.B(_b);
			}
			return instance;
		}
	}
}
