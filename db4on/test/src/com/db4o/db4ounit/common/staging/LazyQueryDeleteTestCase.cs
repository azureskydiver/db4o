namespace com.db4o.db4ounit.common.staging
{
	public class LazyQueryDeleteTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		private const int COUNT = 3;

		public class Item
		{
			public string _name;

			public Item(string name)
			{
				_name = name;
			}
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			config.Queries().EvaluationMode(com.db4o.config.QueryEvaluationMode.LAZY);
		}

		protected override void Store()
		{
			for (int i = 0; i < COUNT; i++)
			{
				Store(new com.db4o.db4ounit.common.staging.LazyQueryDeleteTestCase.Item(i.ToString
					()));
				Db().Commit();
			}
		}

		public virtual void Test()
		{
			com.db4o.ObjectSet objectSet = NewQuery(typeof(com.db4o.db4ounit.common.staging.LazyQueryDeleteTestCase.Item)
				).Execute();
			for (int i = 0; i < COUNT; i++)
			{
				Db().Delete(objectSet.Next());
				Db().Commit();
			}
		}

		public static void Main(string[] arguments)
		{
			new com.db4o.db4ounit.common.staging.LazyQueryDeleteTestCase().RunSolo();
		}
	}
}
