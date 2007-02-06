namespace com.db4o.db4ounit.common.assorted
{
	public class AliasesTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase, Db4oUnit.Extensions.Fixtures.OptOutDefragSolo
	{
		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.assorted.AliasesTestCase().RunSolo();
		}

		private int id;

		private com.db4o.config.Alias alias;

		public class AFoo
		{
			public string foo;
		}

		public class ABar : com.db4o.db4ounit.common.assorted.AliasesTestCase.AFoo
		{
			public string bar;
		}

		public class BFoo
		{
			public string foo;
		}

		public class BBar : com.db4o.db4ounit.common.assorted.AliasesTestCase.BFoo
		{
			public string bar;
		}

		public class CFoo
		{
			public string foo;
		}

		public class CBar : com.db4o.db4ounit.common.assorted.AliasesTestCase.CFoo
		{
			public string bar;
		}

		protected override void Store()
		{
			AddACAlias();
			com.db4o.db4ounit.common.assorted.AliasesTestCase.CBar bar = new com.db4o.db4ounit.common.assorted.AliasesTestCase.CBar
				();
			bar.foo = "foo";
			bar.bar = "bar";
			Store(bar);
			id = (int)Db().GetID(bar);
		}

		public virtual void TestAccessByChildClass()
		{
			AddABAlias();
			com.db4o.db4ounit.common.assorted.AliasesTestCase.BBar bar = (com.db4o.db4ounit.common.assorted.AliasesTestCase.BBar
				)RetrieveOnlyInstance(typeof(com.db4o.db4ounit.common.assorted.AliasesTestCase.BBar)
				);
			AssertInstanceOK(bar);
		}

		public virtual void TestAccessByParentClass()
		{
			AddABAlias();
			com.db4o.db4ounit.common.assorted.AliasesTestCase.BBar bar = (com.db4o.db4ounit.common.assorted.AliasesTestCase.BBar
				)RetrieveOnlyInstance(typeof(com.db4o.db4ounit.common.assorted.AliasesTestCase.BFoo)
				);
			AssertInstanceOK(bar);
		}

		public virtual void TestAccessById()
		{
			AddABAlias();
			com.db4o.db4ounit.common.assorted.AliasesTestCase.BBar bar = (com.db4o.db4ounit.common.assorted.AliasesTestCase.BBar
				)Db().GetByID(id);
			Db().Activate(bar, 2);
			AssertInstanceOK(bar);
		}

		public virtual void TestAccessWithoutAlias()
		{
			RemoveAlias();
			com.db4o.db4ounit.common.assorted.AliasesTestCase.ABar bar = (com.db4o.db4ounit.common.assorted.AliasesTestCase.ABar
				)RetrieveOnlyInstance(typeof(com.db4o.db4ounit.common.assorted.AliasesTestCase.ABar)
				);
			AssertInstanceOK(bar);
		}

		private void AssertInstanceOK(com.db4o.db4ounit.common.assorted.AliasesTestCase.BBar
			 bar)
		{
			Db4oUnit.Assert.AreEqual("foo", bar.foo);
			Db4oUnit.Assert.AreEqual("bar", bar.bar);
		}

		private void AssertInstanceOK(com.db4o.db4ounit.common.assorted.AliasesTestCase.ABar
			 bar)
		{
			Db4oUnit.Assert.AreEqual("foo", bar.foo);
			Db4oUnit.Assert.AreEqual("bar", bar.bar);
		}

		private void AddABAlias()
		{
			AddAlias("A", "B");
		}

		private void AddACAlias()
		{
			AddAlias("A", "C");
		}

		private void AddAlias(string storedLetter, string runtimeLetter)
		{
			RemoveAlias();
			alias = CreateAlias(storedLetter, runtimeLetter);
			Db().Configure().AddAlias(alias);
		}

		private void RemoveAlias()
		{
			if (alias != null)
			{
				Db().Configure().RemoveAlias(alias);
				alias = null;
			}
		}

		private com.db4o.config.WildcardAlias CreateAlias(string storedLetter, string runtimeLetter
			)
		{
			string className = Reflector().ForObject(new com.db4o.db4ounit.common.assorted.AliasesTestCase.ABar
				()).GetName();
			string storedPattern = className.Replace("ABar", storedLetter + "*");
			string runtimePattern = className.Replace("ABar", runtimeLetter + "*");
			return new com.db4o.config.WildcardAlias(storedPattern, runtimePattern);
		}
	}
}
