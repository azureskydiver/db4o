namespace com.db4o.db4ounit.common.querying
{
	public class CascadeDeleteArray : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		public class ArrayElem
		{
			public string name;

			public ArrayElem(string name)
			{
				this.name = name;
			}
		}

		public com.db4o.db4ounit.common.querying.CascadeDeleteArray.ArrayElem[] array;

		protected override void Configure(com.db4o.config.Configuration config)
		{
			config.ObjectClass(this).CascadeOnDelete(true);
		}

		protected override void Store()
		{
			com.db4o.db4ounit.common.querying.CascadeDeleteArray cda = new com.db4o.db4ounit.common.querying.CascadeDeleteArray
				();
			cda.array = new com.db4o.db4ounit.common.querying.CascadeDeleteArray.ArrayElem[] 
				{ new com.db4o.db4ounit.common.querying.CascadeDeleteArray.ArrayElem("one"), new 
				com.db4o.db4ounit.common.querying.CascadeDeleteArray.ArrayElem("two"), new com.db4o.db4ounit.common.querying.CascadeDeleteArray.ArrayElem
				("three") };
			Db().Set(cda);
		}

		public virtual void Test()
		{
			com.db4o.db4ounit.common.querying.CascadeDeleteArray cda = (com.db4o.db4ounit.common.querying.CascadeDeleteArray
				)RetrieveOnlyInstance(GetType());
			Db4oUnit.Assert.AreEqual(3, CountOccurences(typeof(com.db4o.db4ounit.common.querying.CascadeDeleteArray.ArrayElem)
				));
			Db().Delete(cda);
			Db4oUnit.Assert.AreEqual(0, CountOccurences(typeof(com.db4o.db4ounit.common.querying.CascadeDeleteArray.ArrayElem)
				));
			Db().Rollback();
			Db4oUnit.Assert.AreEqual(3, CountOccurences(typeof(com.db4o.db4ounit.common.querying.CascadeDeleteArray.ArrayElem)
				));
			Db().Delete(cda);
			Db4oUnit.Assert.AreEqual(0, CountOccurences(typeof(com.db4o.db4ounit.common.querying.CascadeDeleteArray.ArrayElem)
				));
			Db().Commit();
			Db4oUnit.Assert.AreEqual(0, CountOccurences(typeof(com.db4o.db4ounit.common.querying.CascadeDeleteArray.ArrayElem)
				));
		}
	}
}
