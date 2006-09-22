namespace Db4oUnit.Extensions
{
	public class AbstractDb4oTestCase : Db4oUnit.Extensions.Db4oTestCase
	{
		[com.db4o.Transient]
		private Db4oUnit.Extensions.Db4oFixture _fixture;

		public virtual void Fixture(Db4oUnit.Extensions.Db4oFixture fixture)
		{
			_fixture = fixture;
		}

		public virtual Db4oUnit.Extensions.Db4oFixture Fixture()
		{
			return _fixture;
		}

		protected virtual void Reopen()
		{
			Fixture().Close();
			Fixture().Open();
		}

		public virtual void SetUp()
		{
			_fixture.Clean();
			Configure();
			_fixture.Open();
			Store();
			_fixture.Close();
			_fixture.Open();
		}

		public virtual void TearDown()
		{
			_fixture.Close();
			_fixture.Clean();
		}

		protected virtual void Configure()
		{
		}

		protected virtual void Store()
		{
		}

		public virtual com.db4o.ext.ExtObjectContainer Db()
		{
			return Fixture().Db();
		}

		protected virtual System.Type[] TestCases()
		{
			return new System.Type[] { GetType() };
		}

		public virtual int RunSolo()
		{
			return new Db4oUnit.TestRunner(new Db4oUnit.Extensions.Db4oTestSuiteBuilder(new Db4oUnit.Extensions.Fixtures.Db4oSolo
				(), TestCases())).Run();
		}

		protected virtual com.db4o.YapStream Stream()
		{
			return (com.db4o.YapStream)Db();
		}

		protected virtual com.db4o.Transaction Trans()
		{
			return Stream().GetTransaction();
		}

		protected virtual com.db4o.Transaction SystemTrans()
		{
			return Stream().GetSystemTransaction();
		}

		protected virtual com.db4o.query.Query NewQuery()
		{
			return Db().Query();
		}

		protected virtual com.db4o.reflect.Reflector Reflector()
		{
			return Stream().Reflector();
		}

		protected virtual void IndexField(System.Type clazz, string fieldName)
		{
			com.db4o.Db4o.Configure().ObjectClass(clazz).ObjectField(fieldName).Indexed(true);
		}

		protected virtual com.db4o.Transaction NewTransaction()
		{
			return Stream().NewTransaction();
		}

		protected virtual com.db4o.query.Query NewQuery(System.Type clazz)
		{
			com.db4o.query.Query query = NewQuery();
			query.Constrain(clazz);
			return query;
		}
	}
}
