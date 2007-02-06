namespace com.db4o.db4ounit.common.regression
{
	/// <exclude></exclude>
	public class COR57TestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.regression.COR57TestCase().RunSolo();
		}

		public class Base
		{
			public string name;

			public Base()
			{
			}

			public Base(string name_)
			{
				name = name_;
			}

			public override string ToString()
			{
				return GetType() + ":" + name;
			}
		}

		public class BaseExt : com.db4o.db4ounit.common.regression.COR57TestCase.Base
		{
			public BaseExt()
			{
			}

			public BaseExt(string name_) : base(name_)
			{
			}
		}

		public class BaseExtExt : com.db4o.db4ounit.common.regression.COR57TestCase.BaseExt
		{
			public BaseExtExt()
			{
			}

			public BaseExtExt(string name_) : base(name_)
			{
			}
		}

		protected override void Configure(com.db4o.config.Configuration config)
		{
			config.ObjectClass(typeof(com.db4o.db4ounit.common.regression.COR57TestCase.Base)
				).ObjectField("name").Indexed(true);
		}

		protected override void Store()
		{
			for (int i = 0; i < 5; i++)
			{
				string name = i.ToString();
				Db().Set(new com.db4o.db4ounit.common.regression.COR57TestCase.Base(name));
				Db().Set(new com.db4o.db4ounit.common.regression.COR57TestCase.BaseExt(name));
				Db().Set(new com.db4o.db4ounit.common.regression.COR57TestCase.BaseExtExt(name));
			}
		}

		public virtual void TestQBE()
		{
			AssertQBE(1, new com.db4o.db4ounit.common.regression.COR57TestCase.BaseExtExt("1"
				));
			AssertQBE(2, new com.db4o.db4ounit.common.regression.COR57TestCase.BaseExt("1"));
			AssertQBE(3, new com.db4o.db4ounit.common.regression.COR57TestCase.Base("1"));
		}

		public virtual void TestSODA()
		{
			AssertSODA(1, new com.db4o.db4ounit.common.regression.COR57TestCase.BaseExtExt("1"
				));
			AssertSODA(2, new com.db4o.db4ounit.common.regression.COR57TestCase.BaseExt("1"));
			AssertSODA(3, new com.db4o.db4ounit.common.regression.COR57TestCase.Base("1"));
		}

		private void AssertSODA(int expectedCount, com.db4o.db4ounit.common.regression.COR57TestCase.Base
			 template)
		{
			AssertQueryResult(expectedCount, template, CreateSODA(template).Execute());
		}

		private com.db4o.query.Query CreateSODA(com.db4o.db4ounit.common.regression.COR57TestCase.Base
			 template)
		{
			com.db4o.query.Query q = NewQuery(template.GetType());
			q.Descend("name").Constrain(template.name);
			return q;
		}

		private void AssertQBE(int expectedCount, com.db4o.db4ounit.common.regression.COR57TestCase.Base
			 template)
		{
			AssertQueryResult(expectedCount, template, Db().Get(template));
		}

		private void AssertQueryResult(int expectedCount, com.db4o.db4ounit.common.regression.COR57TestCase.Base
			 expectedTemplate, com.db4o.ObjectSet result)
		{
			Db4oUnit.Assert.AreEqual(expectedCount, result.Size(), SimpleName(expectedTemplate
				.GetType()));
			while (result.HasNext())
			{
				com.db4o.db4ounit.common.regression.COR57TestCase.Base actual = (com.db4o.db4ounit.common.regression.COR57TestCase.Base
					)result.Next();
				Db4oUnit.Assert.AreEqual(expectedTemplate.name, actual.name);
				Db4oUnit.Assert.IsInstanceOf(expectedTemplate.GetType(), actual);
			}
		}

		private string SimpleName(System.Type c)
		{
			string name = c.FullName;
			return j4o.lang.JavaSystem.Substring(name, name.LastIndexOf('$') + 1);
		}
	}
}
